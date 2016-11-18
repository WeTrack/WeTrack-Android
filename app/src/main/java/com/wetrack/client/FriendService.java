package com.wetrack.client;

import com.wetrack.model.Message;
import com.wetrack.model.User;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

interface FriendService {

    @GET("/users/{username}/friends")
    Observable<Response<List<User>>> getUserFriendList(@Path("username") String username,
                                                       @Query("token") String token);

    @POST("/users/{username}/friends/{friendName}")
    Observable<Response<Message>> addFriend(@Path("username") String username,
                                            @Path("friendName") String friendName,
                                            @Query("token") String token);

    @DELETE("/users/{username}/friends/{friendName}")
    Observable<Response<Message>> deleteFriend(@Path("username") String username,
                                               @Path("friendName") String friendName,
                                               @Query("token") String token);

    @HEAD("/users/{username}/friends/{friendName}")
    Observable<Response<Void>> isFriend(@Path("username") String username,
                                        @Path("friendName") String friendName,
                                        @Query("token") String token);

}
