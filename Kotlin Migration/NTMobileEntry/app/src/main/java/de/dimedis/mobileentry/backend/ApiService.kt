package de.dimedis.mobileentry.backend


import android.util.Base64
import com.google.gson.GsonBuilder
import de.dimedis.mobileentry.Config
import de.dimedis.mobileentry.ConfigPref
import de.dimedis.mobileentry.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit

object ApiService {
    private val TAG = "--ApiService"

    private var mClient: OkHttpClient? = null
    private var mGsonConverter: GsonConverterFactory? = null
    var sBackendApi: BackendApi? = null

    fun createBackendApi() = Retrofit.Builder()
        .baseUrl(getRpcUrl())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(gsonConverter)
        .client(client)
        .build()
        .create(BackendApi::class.java)

    private fun getRpcUrl(): String {
        val url = (ConfigPref.rpcUrl?: Constants.API_BASE_PATH)
        if (url.endsWith("/")) {
            return url
        } else {
            return "$url/"
        }
    }

    fun backendApi(): BackendApi {
        if (sBackendApi == null) {
            sBackendApi = createBackendApi()
        }
        return sBackendApi!!
    }

    fun setupRestAdapter() {
        sBackendApi = createBackendApi()
    }

    /**
     * Don't forget to remove Interceptors (or change Logging Level to NONE)
     * in production! Otherwise people will be able to see your request and response on Log Cat.
     */
    private val client: OkHttpClient
        @Throws(NoSuchAlgorithmException::class, KeyManagementException::class)
        get() {
            if (mClient == null) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                val httpBuilder = OkHttpClient.Builder()
                httpBuilder
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor(
                        Interceptor { chain: Interceptor.Chain ->
                        chain.proceed(chain.request().newBuilder().addHeader(
                            "Authorization",
                            "Basic " + Base64.encodeToString(
                                (Config.USERNAME + ":" + Config.PASSWORD).toByteArray(), Base64.NO_WRAP
                            )).build())
                    })
                    .addInterceptor(interceptor)  /// show all JSON in logCat
                mClient = httpBuilder.build()

            }
            return mClient!!
        }

    private val gsonConverter: GsonConverterFactory
        get() {
            if (mGsonConverter == null) {
                mGsonConverter = GsonConverterFactory.create(
                    GsonBuilder().setLenient().disableHtmlEscaping().create()
                )
            }
            return mGsonConverter!!
        }
}