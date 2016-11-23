package com.wetrack.client;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.wetrack.BaseApplication;
import com.wetrack.database.Friend;
import com.wetrack.database.UserChat;
import com.wetrack.database.WeTrackDatabaseHelper;
import com.wetrack.model.Chat;
import com.wetrack.model.Location;
import com.wetrack.model.Message;
import com.wetrack.model.User;

import org.joda.time.LocalDateTime;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Response;

public class WeTrackClientWithDbCache extends WeTrackClient {
    private static final String TAG = WeTrackClientWithDbCache.class.getCanonicalName();

    private static WeTrackClientWithDbCache instance = null;

    public static synchronized WeTrackClientWithDbCache singleton() {
        if (instance == null)
            instance = new WeTrackClientWithDbCache("http://www.robertshome.com.cn/", 5);
        return instance;
    }

    private final Context context;
    private final WeTrackDatabaseHelper helper;

    WeTrackClientWithDbCache(String baseUrl, int timeoutSeconds) {
        super(baseUrl, timeoutSeconds);
        context = BaseApplication.getContext();
        helper = OpenHelperManager.getHelper(context, WeTrackDatabaseHelper.class);
    }

    @Override
    public void createUser(final User newUser, final CreatedMessageCallback callback) {
        super.createUser(newUser, new DelegatedCreatedMessageCallback(callback) {
            @Override
            protected void onSuccess(String newEntityId, String message) {
                helper.getUserDao().createOrUpdate(newUser);
                callback.onSuccess(newEntityId, message);
            }
        });
    }

    @Override
    public void updateUser(String username, String token, final User updatedUser, final MessageCallback callback) {
        super.updateUser(username, token, updatedUser, new DelegatedMessageCallback(callback) {
            @Override
            protected void onSuccess(String message) {
                helper.getUserDao().createOrUpdate(updatedUser);
                callback.onSuccess(message);
            }
        });
    }

    @Override
    public void getUserLocationsSince(String username, LocalDateTime sinceTime, EntityCallback<List<Location>> callback) {
        try {
            PreparedQuery<Location> query =
                    helper.getLocationDao().queryBuilder()
                          .where().gt("time", sinceTime)
                          .prepare();
            List<Location> fetchedLocations = helper.getLocationDao().query(query);
            if (fetchedLocations != null && !fetchedLocations.isEmpty())
                callback.onReceive(fetchedLocations);
        } catch (SQLException ex) {
            Log.e(TAG, "Failed to query for cached locations from local database: ", ex);
        }
        super.getUserLocationsSince(username, sinceTime, callback);
    }

    @Override
    public void uploadLocations(String username, String token, List<Location> locations, MessageCallback callback) {
        for (Location location : locations)
            helper.getLocationDao().create(location);
        super.uploadLocations(username, token, locations, callback);
    }

    @Override
    public void getUserLatestLocation(String username, EntityCallback<Location> callback) {
        try {
            PreparedQuery<Location> query =
                    helper.getLocationDao().queryBuilder()
                            .orderBy("time", false)
                            .where().eq("username", username)
                            .prepare();
            Location fetchedLocation = helper.getLocationDao().queryForFirst(query);
            if (fetchedLocation != null)
                callback.onReceive(fetchedLocation);
        } catch (SQLException ex) {
            Log.e(TAG, "Failed to query for cached latest location from local database: ", ex);
        }
        super.getUserLatestLocation(username, callback);
    }

    @Override
    public void createChat(String token, final Chat chat, final CreatedMessageCallback callback) {
        super.createChat(token, chat, new DelegatedCreatedMessageCallback(callback) {
            @Override
            protected void onSuccess(String newEntityId, String message) {
                helper.getChatDao().create(chat);
                callback.onSuccess(newEntityId, message);
            }
        });
    }

    @Override
    public void addChatMembers(final String chatId, String token, final List<User> newMembers, final MessageCallback callback) {
        super.addChatMembers(chatId, token, newMembers, new DelegatedMessageCallback(callback) {
            @Override
            protected void onSuccess(String message) {
                Chat fetchedChat = helper.getChatDao().queryForId(chatId);
                if (fetchedChat != null) {
                    for (User newMember : newMembers)
                        fetchedChat.getMemberNames().add(newMember.getUsername());
                    helper.getChatDao().update(fetchedChat);
                }
                callback.onSuccess(message);
            }
        });
    }

    @Override
    public void getUserChatList(String username, String token, final EntityCallback<List<Chat>> callback) {
        final User userInDb = helper.getUserDao().queryForId(username);
        if (userInDb != null) {
            final List<Chat> cachedChats = new ArrayList<>(helper.getUserChatDao().getUserChatList(userInDb));
            callback.onReceive(cachedChats);
            super.getUserChatList(username, token, new DelegatedEntityCallback<List<Chat>>(callback) {
                @Override
                protected void onReceive(List<Chat> chatsFromServer) {
                    callback.onReceive(chatsFromServer);
                    for (Chat chat : chatsFromServer)
                        if (!cachedChats.contains(chat))
                            helper.getUserChatDao().create(new UserChat(userInDb, chat));
                }
            });
        } else
            super.getUserChatList(username, token, callback);
    }

    @Override
    public void getUserInfo(String username, final EntityCallback<User> callback) {
        final User userInDb = helper.getUserDao().queryForId(username);
        if (userInDb != null)
            callback.onReceive(userInDb);
        super.getUserInfo(username, new DelegatedEntityCallback<User>(callback) {
            @Override
            protected void onReceive(User userFromServer) {
                callback.onReceive(userFromServer);
                helper.getUserDao().createOrUpdate(userFromServer);
            }
        });
    }

    @Override
    public void getUserFriendList(String username, String token, final EntityCallback<List<User>> callback) {
        final User userInDb = helper.getUserDao().queryForId(username);
        if (userInDb != null) {
            final List<User> cachedFriends =
                    new ArrayList<>(helper.getFriendDao().getUserFriendList(userInDb));
            callback.onReceive(cachedFriends);
            super.getUserFriendList(username, token, new DelegatedEntityCallback<List<User>>(callback) {
                @Override
                protected void onReceive(List<User> usersFromServer) {
                    callback.onReceive(usersFromServer);
                    for (User user : usersFromServer)
                        if (!cachedFriends.contains(user))
                            helper.getFriendDao().create(new Friend(userInDb, user));
                }
            });
        } else
            super.getUserFriendList(username, token, callback);
    }

    @Override
    public void getChatMembers(String chatId, String token, EntityCallback<List<User>> callback) {
        super.getChatMembers(chatId, token, callback);
    }

    private static abstract class DelegatedEntityCallback<T> extends EntityCallback<T> {
        private final EntityCallback<T> delegate;

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
        private final MessageCallback delegate;

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
        private final CreatedMessageCallback delegate;

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
}
