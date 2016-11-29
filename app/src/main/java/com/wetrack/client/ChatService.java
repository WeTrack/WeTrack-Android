package com.wetrack.client;

import com.wetrack.model.Chat;
import com.wetrack.model.ChatMessage;
import com.wetrack.model.CreatedMessage;
import com.wetrack.model.Message;
import com.wetrack.model.User;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

interface ChatService {

    @POST("/chats")
    Observable<Response<CreatedMessage>> createChat(@Query("token") String token, @Body Chat chatEntity);

    @POST("/chats/{chatId}/members")
    Observable<Response<Message>> addChatMembers(@Path("chatId") String chatId,
                                                 @Query("token") String token,
                                                 @Body List<String> newMemberNames);

    @GET("/chats/{chatId}")
    Observable<Response<Chat>> getChatInfo(@Path("chatId") String chatId,
                                           @Query("token") String token);

    @GET("/chats/{chatId}/messages")
    Observable<Response<List<ChatMessage>>> getMessagesBefore(
            @Path("chatId") String chatId,
            @Query("token") String token,
            @Query("before") String beforeTime,
            @Query("limit") int limit
    );

    @GET("/chats/{chatId}/messages")
    Observable<Response<List<ChatMessage>>> getMessagesSince(
            @Path("chatId") String chatId,
            @Query("token") String token,
            @Query("since") String sinceTime,
            @Query("before") String beforeTime
    );

    @GET("/chats/{chatId}/members")
    Observable<Response<List<User>>> getChatMembers(@Path("chatId") String chatId,
                                                    @Query("token") String token);

    @DELETE("/chats/{chatId}/members/{memberName}")
    Observable<Response<Message>> removeChatMember(@Path("chatId") String chatId,
                                                   @Path("memberName") String memberName,
                                                   @Query("token") String token);

    @GET("/users/{username}/chats")
    Observable<Response<List<Chat>>> getUserChatList(@Path("username") String username,
                                                     @Query("token") String token);

    @DELETE("/users/{username}/chats/{chatId}")
    Observable<Response<Message>> exitChat(@Path("username") String username,
                                           @Path("chatId") String chatId,
                                           @Query("token") String token);

}
