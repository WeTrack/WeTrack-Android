package com.wetrack.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wetrack.client.json.ChatSerializer;
import com.wetrack.client.json.LocalDateTimeTypeAdapter;
import com.wetrack.client.json.LocalDateTypeAdapter;
import com.wetrack.model.Chat;
import com.wetrack.model.CreatedMessage;
import com.wetrack.model.Location;
import com.wetrack.model.Message;
import com.wetrack.model.User;
import com.wetrack.model.UserToken;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Client classes for Android client of WeTrack, with all the necessary methods for network connection.
 * <p>
 * All methods of this class executes asynchronous network request and handle the response
 * with the given callback object in the Android main thread. Essentially, the class subscribes
 * on {@link Schedulers#io()} and observes on {@link AndroidSchedulers#mainThread()}.
 */
public class WeTrackClient {
    private final Gson gson;

    private final Scheduler subscribeScheduler;
    private final Scheduler observeScheduler;

    private final UserService userService;
    private final ChatService chatService;
    private final FriendService friendService;
    private final LocationService locationService;

    /**
     * Creates a {@code WeTrackClient} connected to the given base URL with given timeout in seconds.
     *
     * @param baseUrl the given base URL.
     * @param timeoutSeconds the given timeout in seconds.
     */
    public WeTrackClient(String baseUrl, int timeoutSeconds) {
        this(baseUrl, timeoutSeconds, Schedulers.io(), AndroidSchedulers.mainThread());
    }

    WeTrackClient(String baseUrl, int timeoutSeconds,
                  Scheduler subscribeScheduler, Scheduler observeScheduler) {
        this.subscribeScheduler = subscribeScheduler;
        this.observeScheduler = observeScheduler;

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(timeoutSeconds, TimeUnit.SECONDS);

        this.gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Chat.class, new ChatSerializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();

        userService = retrofit.create(UserService.class);
        chatService = retrofit.create(ChatService.class);
        friendService = retrofit.create(FriendService.class);
        locationService = retrofit.create(LocationService.class);
    }

    /**
     * Checks if the given username and token is still valid.
     * <p>
     * The {@link EntityCallback#onReceive(Object)} method will be invoked if the provided token is still valid,
     * provided with the token value and expired time sent from the server;
     * otherwise the {@link EntityCallback#onErrorMessage(Message)} will be invoked with status code {@code 401}
     * and empty response body.
     *
     * @param username the given username.
     * @param token the given token to be verified.
     * @param callback callback object which defines how to handle different result.
     */
    public void tokenVerify(String username, String token, final EntityCallback<UserToken> callback) {
        userService.tokenValidate(username, RequestBody.create(MediaType.parse("text/plain"), token))
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    /**
     * Creates a user with the fields provided in the given {@link User} instance.
     * <p>
     * The {@link CreatedMessageCallback#onSuccess(String, String)} method will be invoked if the creation is
     * successful; otherwise, the {@link CreatedMessageCallback#onFail(String, int)} method will be invoked.
     * <p>
     * Possible error response status code includes:
     *
     * <table>
     *     <tr><th>Status Code</th><th>Meaning</th></tr>
     *     <tr><td>{@code 400}</td><td>Some fields in the provided {@code User} instance are invalid.</td></tr>
     *     <tr><td>{@code 403}</td><td>User with the same username already exist.</td></tr>
     * </table>
     *
     * @param newUser the given {@code User} instance.
     * @param callback callback object which defines how to handle different result.
     */
    public void createUser(User newUser, final CreatedMessageCallback callback) {
        userService.createUser(newUser)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    /**
     * Updates the user with the given username with the fields provided in the given {@link User} instance.
     * <p>
     * The {@link MessageCallback#onSuccess(String)} method will be invoked if the update is successful;
     * otherwise, the {@link MessageCallback#onFail(String, int)} method will be invoked.
     * <p>
     * Possible error response status code includes:
     *
     * <table>
     *     <tr><th>Status Code</th><th>Meaning</th></tr>
     *     <tr><td>{@code 400}</td><td>Some fields in the provided {@code User} instance are invalid.</td></tr>
     *     <tr>
     *         <td>{@code 401}</td>
     *         <td>
     *             The provided token is invalid or has expired; or the logged-in user has no permission for
     *             this operation.
     *         </td>
     *     </tr>
     *     <tr><td>{@code 404}</td><td>User with the given username does not exist.</td></tr>
     * </table>
     *
     * @param username the given username.
     * @param token given token for authentication and permission authorization.
     * @param updatedUser the given {@code User} instance.
     * @param callback callback object which defines how to handle different result.
     */
    public void updateUser(String username, String token, User updatedUser, final MessageCallback callback) {
        updatedUser.setPassword(null);
        userService.updateUser(username, token, updatedUser)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    /**
     * Updates the password of the user with the given username.
     * <p>
     * The user's original password and new password must be provided in plain text.
     * <p>
     * The {@link MessageCallback#onSuccess(String)} method will be invoked if the update is successful, and any
     * formerly logged-in token of this user will be invalidated. Otherwise, the
     * {@link MessageCallback#onFail(String, int)} method will be invoked.
     * <p>
     * Possible error response status code includes:
     *
     * <table>
     *     <tr><th>Status Code</th><th>Meaning</th></tr>
     *     <tr><td>{@code 400}</td><td>Given old or/and new password is/are empty or invalid.</td></tr>
     *     <tr><td>{@code 401}</td><td>Given old password is incorrect.</td></tr>
     *     <tr><td>{@code 404}</td><td>User with the given username does not exist.</td></tr>
     * </table>
     *
     * @param username the given username.
     * @param oldPassword the provided original password of the user in plain text.
     * @param newPassword the new password of the user in plain text.
     * @param callback callback object which defines how to handle different result.
     */
    public void updateUserPassword(String username, String oldPassword, String newPassword,
                                   final MessageCallback callback) {
        oldPassword = CryptoUtils.md5Digest(oldPassword);
        userService.updateUserPassword(username, new UserService.PasswordUpdateRequest(oldPassword, newPassword))
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    /**
     * Fetches the list of locations of the designated user since the given time.
     *
     * @param username the given username of the designated user.
     * @param sinceTime the given since time.
     * @param callback callback object which defines how to handle different result.
     */
    public void getUserLocationsSince(String username, LocalDateTime sinceTime,
                                      final EntityCallback<List<Location>> callback) {
        locationService.getLocationSince(username, sinceTime.toString())
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    /**
     * Uploads the given list of locations for the designated user.
     *
     * @param username the given username of the designated user.
     * @param token token of the current logged-in user.
     * @param locations the given list of locations.
     * @param callback callback object which defines how to handle different result.
     */
    public void uploadLocations(String username, String token, List<Location> locations,
                                final MessageCallback callback) {
        for (Location location : locations)
            location.setUsername(username);
        locationService.uploadLocations(username, new LocationService.LocationsUploadRequest(token, locations))
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    /**
     * Gets the latest location of the designated user.
     *
     * @param username the given username of the designated user.
     * @param callback callback object which defines how to handle different result.
     */
    public void getUserLatestLocation(String username, final EntityCallback<Location> callback) {
        locationService.getLatestLocation(username)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    /**
     * Creates a chat group with the fields of the given {@link Chat} object.
     *
     * @param token token of the current logged-in user.
     * @param chat the given {@code Chat}.
     * @param callback callback object which defines how to handle different result.
     */
    public void createChat(String token, Chat chat, final CreatedMessageCallback callback) {
        chatService.createChat(token, chat)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    /**
     * Adds the given list of user to the chat group with the given id.
     *
     * @param chatId the given chat id.
     * @param token token of the current logged-in user.
     * @param newMembers the given list of new members.
     * @param callback callback object which defines how to handle different result.
     */
    public void addChatMembers(String chatId, String token, List<User> newMembers,
                               final MessageCallback callback) {
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

    /**
     * Gets the list of members of the chat with given id.
     *
     * @param chatId the given chat id.
     * @param token token of the current logged-in user.
     * @param callback callback object which defines how to handle different result.
     */
    public void getChatMembers(String chatId, String token, final EntityCallback<List<User>> callback) {
        chatService.getChatMembers(chatId, token)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    /**
     * Removes the designated member from the chat with the given id.
     *
     * @param chatId the given chat id.
     * @param token token of the current logged-in user.
     * @param memberName the given name of the designated member.
     * @param callback callback object which defines how to handle different result.
     */
    public void removeChatMember(String chatId, String token, String memberName, final MessageCallback callback) {
        chatService.removeChatMember(chatId, memberName, token)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    /**
     * Fetches the list of chats of the designated user.
     *
     * @param username the given username of the designated user.
     * @param token token of the current logged-in user.
     * @param callback callback object which defines how to handle different result.
     */
    public void getUserChatList(String username, String token, final EntityCallback<List<Chat>> callback) {
        chatService.getUserChatList(username, token)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    /**
     * Exits the chat with the given id.
     *
     * @param username username of the current logged-in user.
     * @param token token of the current logged-in user.
     * @param chatId the given chat id.
     * @param callback callback object which defines how to handle different result.
     */
    public void exitChat(String username, String token, String chatId, final MessageCallback callback) {
        chatService.exitChat(username, chatId, token)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    /**
     * Fetches the list of friend of the designated user.
     *
     * @param username the given username of the designated user.
     * @param token token of the current logged-in user.
     * @param callback callback object which defines how to handle different result.
     */
    public void getUserFriendList(String username, String token, final EntityCallback<List<User>> callback) {
        friendService.getUserFriendList(username, token)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void addFriend(String username, String token, String friendName,
                          final MessageCallback callback) {
        friendService.addFriend(username, friendName, token)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void deleteFriend(String username, String token, String friendName,
                             final MessageCallback callback) {
        friendService.deleteFriend(username, friendName, token)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    public void isFriend(String username, String token, String friendName,
                         final ResultCallback callback) {
        friendService.isFriend(username, friendName, token)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    /**
     * Checks if a user with the given username exists.
     * <p>
     * The {@link ResultCallback#onSuccess()} method will be invoked if there is such user,
     * otherwise the {@link ResultCallback#onFail(int)} will be invoked.
     *
     * @param username the given username
     * @param callback callback object which defines how to handle different result
     */
    public void userExists(String username, final ResultCallback callback) {
        userService.userExists(username)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    /**
     * Gets the information of the user with the given username.
     * <p>
     * The {@link EntityCallback#onReceive(Object)} method will be invoked when successfully
     * received the response entity as a {@link User}.
     *
     * @param username the given username of the designated user.
     * @param callback callback object which defines how to handle different response.
     */
    public void getUserInfo(String username, final EntityCallback<User> callback) {
        userService.getUserInfo(username)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
    }

    /**
     * Logs in with the given username and password.
     * <p>
     * The {@link EntityCallback#onReceive(Object)} method will be invoked with the received {@link UserToken}
     * if the log in is successful.
     *
     * @param username the given username to be used for logging in.
     * @param password the given password to be used for logging in.
     * @param callback callback object which defines how to handle different response.
     */
    public void userLogin(String username, String password, final EntityCallback<UserToken> callback) {
        password = CryptoUtils.md5Digest(password);
        userService.userLogin(new UserService.UserLoginRequest(username, password))
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe(observer(callback));
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
                else {
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
