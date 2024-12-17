package bg.next.restconnected.response

import com.google.gson.annotations.SerializedName
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginApiService {
    @Headers("Content-Type: application/json")
    @POST("login")
    fun doLogin(
        //@Query("Authorization") authorizationKey: String, // authentication header
        @Body loginPostData: LoginPostData
    ): Observable<LoginResponse> // body data
}

data class LoginPostData(
    @SerializedName("UserId") var userID: String,
    @SerializedName("Password") var userPassword: String
)
