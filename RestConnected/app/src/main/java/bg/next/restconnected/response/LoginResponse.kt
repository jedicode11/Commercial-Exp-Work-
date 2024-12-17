package bg.next.restconnected.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("status") val status: Int = 0,
    @SerializedName("error") val error: String = "",
    @SerializedName("error_type") val errorType: String = "",
    @Expose(deserialize = false) // deserialize if this field is not required
    @SerializedName("message") val message: String = ""
)

