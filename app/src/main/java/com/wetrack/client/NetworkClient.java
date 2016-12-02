package com.wetrack.client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.wetrack.BaseApplication;
import com.wetrack.client.json.ChatSerializer;
import com.wetrack.client.json.LocalDateTimeTypeAdapter;
import com.wetrack.client.json.LocalDateTypeAdapter;
import com.wetrack.database.WeTrackDatabaseHelper;
import com.wetrack.model.Chat;
import com.wetrack.model.ChatMessage;
import com.wetrack.model.CreatedMessage;
import com.wetrack.model.Location;
import com.wetrack.model.Message;
import com.wetrack.model.User;
import com.wetrack.model.UserToken;
import com.wetrack.utils.Config;
import com.wetrack.utils.CryptoUtils;
import com.wetrack.utils.Tags;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.Scheduler;

class NetworkClient {
    private static final String TAG = Tags.Client.NETWORK;

    private final Gson gson;

    private final String baseUrl;
    private final OkHttpClient client;

    private final Scheduler subscribeScheduler;
    private final Scheduler observeScheduler;

    private final UserService userService;
    private final ChatService chatService;
    private final FriendService friendService;
    private final LocationService locationService;

    NetworkClient(String baseUrl, int timeoutSeconds,
                  Scheduler subscribeScheduler, Scheduler observeScheduler) {
        this.subscribeScheduler = subscribeScheduler;
        this.observeScheduler = observeScheduler;

        this.baseUrl = baseUrl;
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(timeoutSeconds, TimeUnit.SECONDS);
        client = clientBuilder.build();

        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Chat.class, new ChatSerializer())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();

        userService = retrofit.create(UserService.class);
        chatService = retrofit.create(ChatService.class);
        friendService = retrofit.create(FriendService.class);
        locationService = retrofit.create(LocationService.class);
    }

    public void tokenVerify(String username, String token, final EntityCallback<UserToken> callback) {
        userService.tokenValidate(username, RequestBody.create(MediaType.parse("text/plain"), token))
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void createUser(User newUser, final CreatedMessageCallback callback) {
        userService.createUser(newUser)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void updateUser(String username, String token, User updatedUser, final MessageCallback callback) {
        updatedUser.setPassword(null);
        userService.updateUser(username, token, updatedUser)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void updateUserPassword(String username, String oldPassword, String newPassword,
                                   final MessageCallback callback
    ) {
        oldPassword = CryptoUtils.md5Digest(oldPassword);
        userService.updateUserPassword(username, new UserService.PasswordUpdateRequest(oldPassword, newPassword))
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void getUserLocationsSince(String username, LocalDateTime sinceTime,
                                      final EntityCallback<List<Location>> callback
    ) {
        locationService.getLocationSince(username, sinceTime.toString())
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void uploadLocations(String username, String token, List<Location> locations,
                                final MessageCallback callback
    ) {
        for (Location location : locations)
            location.setUsername(username);
        locationService.uploadLocations(username, new LocationService.LocationsUploadRequest(token, locations))
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void getUserLatestLocation(String username, final EntityCallback<Location> callback) {
        locationService.getLatestLocation(username)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void createChat(String token, Chat chat, final CreatedMessageCallback callback) {
        chatService.createChat(token, chat)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void addChatMembers(String chatId, String token, List<User> newMembers,
                               final MessageCallback callback
    ) {
        chatService.addChatMembers(chatId, token, usersToUsernames(newMembers))
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    private List<String> usersToUsernames(List<User> users) {
        List<String> usernames = new ArrayList<>(users.size());
        for (User user : users)
            usernames.add(user.getUsername());
        return usernames;
    }

    public void getChatMembers(String chatId, String token, final EntityCallback<List<User>> callback) {
        chatService.getChatMembers(chatId, token)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void removeChatMember(String chatId, String token, String memberName, final MessageCallback callback) {
        chatService.removeChatMember(chatId, memberName, token)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void getUserChatList(String username, String token, final EntityCallback<List<Chat>> callback) {
        chatService.getUserChatList(username, token)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void exitChat(String username, String token, String chatId, final MessageCallback callback) {
        chatService.exitChat(username, chatId, token)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void getUserFriendList(String username, String token, final EntityCallback<List<User>> callback) {
        friendService.getUserFriendList(username, token)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void addFriend(String username, String token, String friendName,
                          final MessageCallback callback
    ) {
        friendService.addFriend(username, friendName, token)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void deleteFriend(String username, String token, String friendName,
                             final MessageCallback callback
    ) {
        friendService.deleteFriend(username, friendName, token)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void isFriend(String username, String token, String friendName,
                         final ResultCallback callback
    ) {
        friendService.isFriend(username, friendName, token)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void userExists(String username, final ResultCallback callback) {
        userService.userExists(username)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void getUserInfo(String username, final EntityCallback<User> callback) {
        userService.getUserInfo(username)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void userLogin(String username, String password, final EntityCallback<UserToken> callback) {
        password = CryptoUtils.md5Digest(password);
        userService.userLogin(new UserService.UserLoginRequest(username, password))
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void getChatInfo(String chatId, String token, final EntityCallback<Chat> callback) {
        chatService.getChatInfo(chatId, token)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void getChatMessagesBefore(String chatId, String token, LocalDateTime before, int limit,
                                      final EntityCallback<List<ChatMessage>> callback
    ) {
        chatService.getMessagesBefore(chatId, token, before.toString(), limit)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void getChatMessagesSince(String chatId, String token, LocalDateTime since, LocalDateTime before,
                                     final EntityCallback<List<ChatMessage>> callback
    ) {
        chatService.getMessagesSince(chatId, token, since.toString(), before == null ? null : before.toString())
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    private static final DateTimeFormatter RFC1123_DATE_TIME_FORMATTER =
            DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'")
                    .withZoneUTC().withLocale(Locale.US);

    public void getUserPortrait(String username, DateTime lastUpdateTime, final EntityCallback<Bitmap> callback) {
        userService.getUserPortrait(username, lastUpdateTime == null ? null : lastUpdateTime.toString(RFC1123_DATE_TIME_FORMATTER))
                .subscribeOn(subscribeScheduler).observeOn(observeScheduler)
                .subscribe(bitmapObserver(callback));
    }

    public void uploadUserPortrait(String username, String token, File portrait, final CreatedMessageCallback callback) {
        byte[] data;
        try {
            data = FileUtils.readFileToByteArray(portrait);
        } catch (IOException ex) {
            callback.onError(ex);
            return;
        }
        RequestBody part = RequestBody.create(MediaType.parse("image/*"), data);
        userService.uploadUserPortrait(username, token, MultipartBody.Part.createFormData("data", portrait.getName(), part))
                .subscribeOn(subscribeScheduler).observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    Gson getGson() {
        return gson;
    }

    OkHttpClient getClient() {
        return client;
    }

    String getBaseUrl() {
        return baseUrl;
    }

    private Observer<Response<CreatedMessage>> observer(final CreatedMessageCallback callback) {
        return new Observer<Response<CreatedMessage>>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                callback.onError(e);
            }

            @Override
            public void onNext(Response<CreatedMessage> response) {
                if (response.code() == 201) { // Created
                    String newEntityUrl = response.body().getEntityUrl();
                    String[] splitResult = newEntityUrl.split("/");
                    callback.onSuccess(splitResult[splitResult.length - 1], response.body().getMessage());
                } else {
                    ResponseBody errorBody = response.errorBody();
                    try {
                        String errorResponse = errorBody.string();
                        Message message = gson.fromJson(errorResponse, Message.class);
                        callback.onFail(message.getMessage(), response.code());
                    } catch (IOException e) {
                        callback.onFail(response.message(), response.code());
                    } finally {
                        errorBody.close();
                    }
                }
            }
        };
    }

    private <T extends Message> Observer<Response<T>> observer(final MessageCallback callback) {
        return new Observer<Response<T>>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                callback.onError(e);
            }

            @Override
            public void onNext(Response<T> response) {
                if (response.code() == callback.getSuccessfulStatusCode()) {
                    callback.onSuccess(response.body().getMessage());
                } else {
                    if (response.body() != null) {
                        callback.onFail(response.body().getMessage(), response.code());
                    } else {
                        ResponseBody errorBody = response.errorBody();
                        try {
                            String errorResponse = errorBody.string();
                            Message message = gson.fromJson(errorResponse, Message.class);
                            callback.onFail(message.getMessage(), response.code());
                        } catch (IOException e) {
                            callback.onFail(response.message(), response.code());
                        } finally {
                            errorBody.close();
                        }
                    }
                }
            }
        };
    }

    private Observer<Response> observer(final ResultCallback callback) {
        return new Observer<Response>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                callback.onError(e);
            }

            @Override
            public void onNext(Response response) {
                if (response.code() == callback.getSuccessfulStatusCode())
                    callback.onSuccess();
                else
                    callback.onFail(response.code());
            }
        };
    }

    @SuppressWarnings("unchecked")
    private Observer<Response<ResponseBody>> bitmapObserver(final EntityCallback<Bitmap> callback) {
        return new Observer<Response<ResponseBody>>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                callback.onException(e);
            }

            @Override
            public void onNext(Response<ResponseBody> response) {
                callback.onResponse((Response) response);
                if (response.code() == 200)
                    callback.onReceive(BitmapFactory.decodeStream(response.body().byteStream()));
                else if (response.code() >= 400) {
                    try {
                        Message receivedMessage = gson.fromJson(response.errorBody().string(), Message.class);
                        callback.onErrorMessage(receivedMessage);
                    } catch (IOException e) {
                        callback.onException(e);
                    }
                }
            }
        };
    }

    ;

    private <T> Observer<Response<T>> observer(final EntityCallback<T> callback) {
        return new Observer<Response<T>>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                callback.onException(e);
            }

            @Override
            public void onNext(Response<T> response) {
                callback.onResponse(response);
                if (response.code() >= 200 && response.code() < 300)
                    callback.onReceive(response.body());
                else if (response.code() >= 400) {
                    try {
                        Message receivedMessage = gson.fromJson(response.errorBody().string(), Message.class);
                        callback.onErrorMessage(receivedMessage);
                    } catch (IOException e) {
                        callback.onException(e);
                    }
                }
            }
        };
    }

}
