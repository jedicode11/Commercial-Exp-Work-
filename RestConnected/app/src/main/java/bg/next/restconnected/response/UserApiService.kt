package bg.next.restconnected.response

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface UserApiService {
    @Headers("Content-Type: application/json")
    @GET("user")
    fun getUser(
        @Query("Authorization") authorizationKey: String, // authentication header
        @Query("UserID") userID: String // authentication header
    ): Observable<UserResponse>
}