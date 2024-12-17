package bg.next.restconnected.api

import bg.next.restconnected.Constants
import bg.next.restconnected.response.LoginApiService
import bg.next.restconnected.response.UserApiService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory

object ApiService {
    private val TAG = "--ApiService"

    fun loginApiCall() = Retrofit.Builder()
        .baseUrl(Constants.API_BASE_PATH)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(ApiWorker.gsonConverter)
        .client(ApiWorker.client)
        .build()
        .create(LoginApiService::class.java)

    // get request builder
    fun userApiCall() = Retrofit.Builder()
        .baseUrl(Constants.API_BASE_PATH)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(ApiWorker.gsonConverter)
        .client(ApiWorker.client)
        .build()
        .create(UserApiService::class.java)
}