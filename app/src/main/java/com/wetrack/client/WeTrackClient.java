package com.wetrack.client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.wetrack.BaseApplication;
import com.wetrack.database.Friend;
import com.wetrack.database.UserChat;
import com.wetrack.database.WeTrackDatabaseHelper;
import com.wetrack.model.Chat;
import com.wetrack.model.ChatMessage;
import com.wetrack.model.Location;
import com.wetrack.model.Message;
import com.wetrack.model.User;
import com.wetrack.model.UserPortrait;
import com.wetrack.model.UserToken;
import com.wetrack.utils.Tags;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WeTrackClient {
    private static final String TAG = Tags.Client.CACHED;

    private static WeTrackClient instance = null;

    public static synchronized WeTrackClient singleton() {
        if (instance == null)
            instance = new WeTrackClient("http://www.robertshome.com.cn/", 5);
        return instance;
    }

    private static final String PORTRAIT_FOLDER = "portrait";

    private final NetworkClient client;
    private final WeTrackDatabaseHelper helper;

    private WeTrackClient(String baseUrl, int timeoutSeconds) {
        client = new NetworkClient(baseUrl, timeoutSeconds, Schedulers.io(), AndroidSchedulers.mainThread());
        helper = OpenHelperManager.getHelper(BaseApplication.getContext(), WeTrackDatabaseHelper.class);
    }

    public void userLogin(String username, String password, EntityCallback<UserToken> callback) {
        client.userLogin(username, password, callback);
    }

    public void getChatMessagesBefore(String chatId, String token, LocalDateTime before, int limit,
                                      EntityCallback<List<ChatMessage>> callback
    ) {
        List<ChatMessage> messagesInDB = helper.getChatMessageDao().getMessageBefore(chatId, before, (long) limit);
        if (messagesInDB != null && !messagesInDB.isEmpty())
            callback.onReceive(messagesInDB);
        client.getChatMessagesBefore(chatId, token, before, limit, callback);
        // TODO Update cache database with the received messages
    }

    public void getChatMessagesSince(String chatId, String token, LocalDateTime since, LocalDateTime before,
                                     EntityCallback<List<ChatMessage>> callback
    ) {
        List<ChatMessage> messagesInDB = helper.getChatMessageDao().getMessageSince(chatId, since, before);
        if (messagesInDB != null && !messagesInDB.isEmpty())
            callback.onReceive(messagesInDB);
        client.getChatMessagesSince(chatId, token, since, before, callback);
        // TODO Update cache database with the received messages
    }

    public void getNewChatMessages(String chatId, String token,
                                   EntityCallback<List<ChatMessage>> callback
    ) {
        LocalDateTime latestReceivedTime = helper.getChatMessageDao().getLatestReceivedMessageTime();

        EntityCallback<List<ChatMessage>> realCallback =
                new DelegatedEntityCallback<List<ChatMessage>>(callback) {
                    @Override
                    protected void onReceive(List<ChatMessage> messagesFromServer) {
                        delegate.onReceive(messagesFromServer);
                        for (ChatMessage message : messagesFromServer)
                            helper.getChatMessageDao().create(message);
                    }
                };

        if (latestReceivedTime == null) { // No message in local database
            Log.d(TAG, "Latest receiving time cannot be fetched. Querying for the latest 20 messages from server...");
            getChatMessagesBefore(chatId, token, LocalDateTime.now(), 20, realCallback);
        } else {
            Log.d(TAG, "Fetched latest receiving time " + latestReceivedTime.toString());
            getChatMessagesSince(chatId, token, latestReceivedTime.plusMillis(1), null, realCallback);
        }
    }

    public void createUser(final User newUser, CreatedMessageCallback callback) {
        client.createUser(newUser, new DelegatedCreatedMessageCallback(callback) {
            @Override
            protected void onSuccess(String newEntityId, String message) {
                Log.d(TAG, "User created. Saving user information to local database.");
                helper.getUserDao().createOrUpdate(newUser);
                Log.d(TAG, "Invoking original callback#onSuccess");
                delegate.onSuccess(newEntityId, message);
            }
        });
    }

    public void updateUser(String username, String token, final User updatedUser, MessageCallback callback) {
        client.updateUser(username, token, updatedUser, new DelegatedMessageCallback(callback) {
            @Override
            protected void onSuccess(String message) {
                Log.d(TAG, "User updated. Saving updated user information to local database.");
                helper.getUserDao().createOrUpdate(updatedUser);
                Log.d(TAG, "Invoking original callback#onSuccess");
                delegate.onSuccess(message);
            }
        });
    }

    public void getUserLocationsSince(String username, LocalDateTime sinceTime, EntityCallback<List<Location>> callback) {
        final List<Location> fetchedLocations = helper.getLocationDao().getUserLocationsSince(username, sinceTime);
        if (fetchedLocations != null && !fetchedLocations.isEmpty())
            callback.onReceive(fetchedLocations);
        client.getUserLocationsSince(username, sinceTime, callback);
    }

    public void uploadLocations(String username, String token, List<Location> locations, MessageCallback callback) {
        for (Location location : locations)
            helper.getLocationDao().create(location);
        client.uploadLocations(username, token, locations, callback);
    }

    public void getUserLatestLocation(String username, EntityCallback<Location> callback) {
        try {
            PreparedQuery<Location> query =
                    helper.getLocationDao().queryBuilder().orderBy("time", false)
                            .where().eq("username", username).prepare();
            Location fetchedLocation = helper.getLocationDao().queryForFirst(query);
            if (fetchedLocation != null)
                callback.onReceive(fetchedLocation);
        } catch (SQLException ex) {
            Log.e(TAG, "Failed to query for cached latest location from local database: ", ex);
        }
        client.getUserLatestLocation(username, callback);
    }

    public void createChat(String token, final Chat chat, CreatedMessageCallback callback) {
        client.createChat(token, chat, new DelegatedCreatedMessageCallback(callback) {
            @Override
            protected void onSuccess(String newEntityId, String message) {
                delegate.onSuccess(newEntityId, message);
                Log.d(TAG, "Chat created. Saving chat information to local database.");
                helper.getChatDao().create(chat);
                Log.d(TAG, "Invoking original callback#onSuccess");
            }
        });
    }

    public void addChatMembers(final String chatId, String token, final List<User> newMembers, MessageCallback callback) {
        client.addChatMembers(chatId, token, newMembers, new DelegatedMessageCallback(callback) {
            @Override
            protected void onSuccess(String message) {
                Chat fetchedChat = helper.getChatDao().queryForId(chatId);
                if (fetchedChat != null) {
                    for (User newMember : newMembers)
                        fetchedChat.getMemberNames().add(newMember.getUsername());
                    helper.getChatDao().update(fetchedChat);
                }
                delegate.onSuccess(message);
            }
        });
    }

    public void getUserChatList(String username, String token, EntityCallback<List<Chat>> callback) {
        final User userInDb = helper.getUserDao().queryForId(username);
        if (userInDb != null) {
            final List<Chat> cachedChats = new ArrayList<>(helper.getUserChatDao().getUserChatList(userInDb));
            if (!cachedChats.isEmpty()) {
                Log.d(TAG, "Fetched cached chat list for user `" + username + "`, invoking callback#onReceive");
                callback.onReceive(cachedChats);
            }
            client.getUserChatList(username, token, new DelegatedEntityCallback<List<Chat>>(callback) {
                @Override
                protected void onReceive(List<Chat> chatsFromServer) {
                    delegate.onReceive(chatsFromServer);
                    for (Chat chat : chatsFromServer) {
                        if (!cachedChats.contains(chat)) {
                            Log.d(TAG, "Received new chat `" + chat.getChatId() + "` from server. Saving to database.");
                            helper.getChatDao().createOrUpdate(chat);
                            helper.getUserChatDao().create(new UserChat(userInDb, chat));
                        }
                    }
                }
            });
        } else {
            Log.w(TAG, "Local database does not have record for user `" + username + "`. Something's not right.");
            client.getUserChatList(username, token, callback);
        }
    }

    public void getUserInfo(String username, EntityCallback<User> callback) {
        final User userInDb = helper.getUserDao().queryForId(username);
        if (userInDb != null) {
            Log.d(TAG, "Invoking callback#onReceive with cached user information of `" + username + "`");
            callback.onReceive(userInDb);
        }
        Log.d(TAG, "Sending network request for user information of `" + username + "`");
        client.getUserInfo(username, new DelegatedEntityCallback<User>(callback) {
            @Override
            protected void onReceive(User userFromServer) {
                delegate.onReceive(userFromServer);
                helper.getUserDao().createOrUpdate(userFromServer);
            }
        });
    }

    public void getChatInfo(String chatId, String token, EntityCallback<Chat> callback) {
        final Chat chatInDb = helper.getChatDao().queryForId(chatId);
        if (chatInDb != null) {
            Log.d(TAG, "Invoking callback#onReceive with cached chat information of `" + chatId + "`");
            callback.onReceive(chatInDb);
        }
        Log.d(TAG, "Sending network request for chat information of `" + chatId + "`");
        client.getChatInfo(chatId, token, new DelegatedEntityCallback<Chat>(callback) {
            @Override
            protected void onReceive(Chat chatFromServer) {
                delegate.onReceive(chatFromServer);
                helper.getChatDao().createOrUpdate(chatFromServer);
            }
        });
    }

    public void getUserFriendList(String username, String token, EntityCallback<List<User>> callback) {
        final User userInDb = helper.getUserDao().queryForId(username);
        if (userInDb != null) {
            final List<User> cachedFriends =
                    new ArrayList<>(helper.getFriendDao().getUserFriendList(userInDb));
            if (!cachedFriends.isEmpty()) {
                Log.d(TAG, "Fetched cached friend list for user `" + username + "`. Invoking callback#onReceive");
                callback.onReceive(cachedFriends);
            }
            client.getUserFriendList(username, token, new DelegatedEntityCallback<List<User>>(callback) {
                @Override
                protected void onReceive(List<User> usersFromServer) {
                    delegate.onReceive(usersFromServer);
                    for (User user : usersFromServer) {
                        if (!cachedFriends.contains(user)) {
                            Log.d(TAG, "Received new friend `" + user.getUsername() + "` from server. Saving to database.");
                            helper.getUserDao().createOrUpdate(user);
                            helper.getFriendDao().create(new Friend(userInDb, user));
                        }
                    }
                }
            });
        } else {
            Log.w(TAG, "Local database does not have record for user `" + username + "`. Something's not right.");
            client.getUserFriendList(username, token, callback);
        }
    }

    public void addFriend(final String username, String token, final String friendName,
                          MessageCallback callback
    ) {
        client.addFriend(username, token, friendName, new DelegatedMessageCallback(callback) {
            @Override
            protected void onSuccess(String message) {
                Log.d(TAG, "Friend added. Saving friend information to local database.");
                User owner = helper.getUserDao().queryForId(username);
                User friend = helper.getUserDao().queryForId(friendName);
                if (owner != null && friend != null)
                    helper.getFriendDao().create(new Friend(owner, friend));
                else if (owner == null)
                    Log.w(TAG, "Local database does not have record for user `" + username + "`. Something's not right.");
                else
                    Log.w(TAG, "Local database does not have record for user `" + friendName + "`. Something's not right.");
                Log.d(TAG, "Invoking original callback#onSuccess");
                delegate.onSuccess(message);
            }
        });
    }

    public void getChatMembers(String chatId, String token, EntityCallback<List<User>> callback) {
        Chat chat = helper.getChatDao().queryForId(chatId);
        if (chat != null && chat.getMemberNames() != null && !chat.getMemberNames().isEmpty()) {
            List<User> members = new ArrayList<>(chat.getMemberNames().size());
            User userInDB;
            for (String memberName : chat.getMemberNames()) {
                userInDB = helper.getUserDao().queryForId(memberName);
                if (userInDB != null)
                    members.add(userInDB);
            }
            callback.onReceive(members);
        }
        client.getChatMembers(chatId, token, new DelegatedEntityCallback<List<User>>(callback) {
            @Override
            protected void onReceive(List<User> membersFromServer) {
                delegate.onReceive(membersFromServer);
                for (User user : membersFromServer)
                    helper.getUserDao().createOrUpdate(user);
            }
        });
    }

    public void uploadUserPortrait(final String username, String token, final File portrait, CreatedMessageCallback callback) {
        File cachedDir = BaseApplication.getContext().getCacheDir();
        final File cachedPortraitPath = FileUtils.getFile(cachedDir, PORTRAIT_FOLDER, username);
        client.uploadUserPortrait(username, token, portrait, new DelegatedCreatedMessageCallback(callback) {
            @Override
            protected void onSuccess(String newEntityId, String message) {
                delegate.onSuccess(newEntityId, message);
                try {
                    FileUtils.forceMkdirParent(cachedPortraitPath);
                    FileUtils.copyFile(portrait, cachedPortraitPath);
                    UserPortrait portrait = new UserPortrait(username, DateTime.now());
                    helper.getPortraitDao().createOrUpdate(portrait);
                } catch (IOException ex) {
                    Log.w(TAG, "Failed to copy the new portrait to cache folder: ", ex);
                }
            }
        });
    }

    public void getUserPortrait(final String username, boolean forceNetworkRequest, EntityCallback<Bitmap> callback) {
        File cachedDir = BaseApplication.getContext().getCacheDir();
        final File cachedPortraitPath = FileUtils.getFile(cachedDir, PORTRAIT_FOLDER, username);

        final EntityCallback<Bitmap> realCallback = new DelegatedEntityCallback<Bitmap>(callback) {
            @Override
            protected void onReceive(Bitmap bitmap) {
                delegate.onReceive(bitmap);

                Log.d(TAG, "Received new portrait for user `" + username + "` from server. Caching it...");
                // Cache the received Bitmap
                OutputStream out = null;
                try {
                    FileUtils.forceMkdirParent(cachedPortraitPath);
                    out = new FileOutputStream(cachedPortraitPath);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    UserPortrait portrait = new UserPortrait(username, DateTime.now());
                    helper.getPortraitDao().createOrUpdate(portrait);
                } catch (IOException ex) {
                    Log.w(TAG, "Failed to write received portrait to designated cache directory: ", ex);
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {}
                    }
                }
            }
        };

        UserPortrait cachedRecord = helper.getPortraitDao().queryForId(username);
        if (cachedRecord != null) {
            if (cachedPortraitPath.exists()) {
                Log.d(TAG, "Found cached portrait for user `" + username + "`.");
                callback.onReceive(BitmapFactory.decodeFile(cachedPortraitPath.toString()));
                if (forceNetworkRequest)
                    client.getUserPortrait(username, cachedRecord.getUpdateTime(), realCallback);
            } else {
                Log.d(TAG, "User `" + username + "`'s portrait cached record is found, but cached file does not exist. "
                        + "Deleting cached record...");
                helper.getPortraitDao().delete(cachedRecord);
            }
        } else
            client.getUserPortrait(username, null, realCallback);
    }

    private static abstract class DelegatedEntityCallback<T> extends EntityCallback<T> {
        protected final EntityCallback<T> delegate;

        DelegatedEntityCallback(EntityCallback delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onException(Throwable ex) {
            delegate.onException(ex);
        }

        @Override
        protected void onResponse(Response<T> response) {
            delegate.onResponse(response);
        }

        @Override
        protected void onErrorMessage(Message response) {
            delegate.onErrorMessage(response);
        }

        protected abstract void onReceive(T value);
    }

    private static abstract class DelegatedMessageCallback extends MessageCallback {
        protected final MessageCallback delegate;

        DelegatedMessageCallback(MessageCallback delegate) {
            super(delegate.getSuccessfulStatusCode());
            this.delegate = delegate;
        }

        @Override
        public int getSuccessfulStatusCode() {
            return delegate.getSuccessfulStatusCode();
        }

        @Override
        protected void onFail(String message, int failedStatusCode) {
            delegate.onFail(message, failedStatusCode);
        }

        @Override
        protected void onError(Throwable ex) {
            delegate.onError(ex);
        }

        protected abstract void onSuccess(String message);
    }

    private static abstract class DelegatedCreatedMessageCallback extends CreatedMessageCallback {
        protected final CreatedMessageCallback delegate;

        DelegatedCreatedMessageCallback(CreatedMessageCallback delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onFail(String message, int failedStatusCode) {
            delegate.onFail(message, failedStatusCode);
        }

        @Override
        protected void onError(Throwable ex) {
            delegate.onError(ex);
        }

        protected abstract void onSuccess(String newEntityId, String message);
    }

    public String getBaseUrl() { return client.getBaseUrl(); }
}
