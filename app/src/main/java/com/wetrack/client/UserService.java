package com.wetrack.client;

import com.wetrack.model.CreatedMessage;
import com.wetrack.model.Message;
import com.wetrack.model.User;
import com.wetrack.model.UserToken;

import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

interface UserService {

    @POST("/login")
    Observable<Response<UserToken>> userLogin(@Body UserLoginRequest request);

    @HEAD("/users/{username}")
    Observable<Response<Void>> userExists(@Path("username") String username);

    @Headers({
            "Content-Type: text/plain"
    })
    @POST("/users/{username}/tokenVerify")
    Observable<Response<UserToken>> tokenValidate(@Path("username") String username,
                                                  @Body RequestBody token);

    @GET("/users/{username}")
    Observable<Response<User>> getUserInfo(@Path("username") String username);

    @PUT("/users/{username}")
    Observable<Response<Message>> updateUser(@Path("username") String username,
                                             @Query("token") String token,
                                             @Body User updatedUser);

    @POST("/users")
    Observable<Response<CreatedMessage>> createUser(@Body User newUser);

    @PUT("/users/{username}/password")
    Observable<Response<Message>> updateUserPassword(@Path("username") String username,
                                                     @Body PasswordUpdateRequest request);

    class UserLoginRequest {
        private String username;
        private String password;

        UserLoginRequest() {}

        UserLoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        String getUsername() {
            return username;
        }
        void setUsername(String username) {
            this.username = username;
        }
        String getPassword() {
            return password;
        }
        void setPassword(String password) {
            this.password = password;
        }
    }

    class PasswordUpdateRequest {
        private String oldPassword;
        private String newPassword;

        public PasswordUpdateRequest() {}

        public PasswordUpdateRequest(String oldPassword, String newPassword) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
        }

        public static PasswordUpdateRequest of(String oldPassword, String newPassword) {
            return new PasswordUpdateRequest(oldPassword, newPassword);
        }

        public String getOldPassword() {
            return oldPassword;
        }
        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }
        public String getNewPassword() {
            return newPassword;
        }
        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

}
