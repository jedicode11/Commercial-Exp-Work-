package bg.next.restconnected

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import bg.next.restconnected.api.ApiService
import bg.next.restconnected.response.LoginPostData
import bg.next.restconnected.ui.main.MainFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
        mContext = this
        loginApiCall("user-001", "pass1221") // login post request
        userApiCall("user-001") // user get request
    }

    // login post request
    @SuppressLint("CheckResult")
    private fun loginApiCall(userID: String, userPassword: String) {
        if (UtilMethods.isConnectedToInternet(mContext)) {
            UtilMethods.showLoading(mContext)
            val observable = ApiService.loginApiCall().doLogin(LoginPostData(userID, userPassword))
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ res ->
                    val msg = res.message
                    UtilMethods.hideLoading()

                    /** loginResponse is response data class*/

                }, { error ->
                    UtilMethods.hideLoading()
                    UtilMethods.showLongToast(mContext, error.message.toString())
                })
        } else {
            UtilMethods.showLongToast(mContext, "No Internet Connection!")
        }
    }

    // user info get request
    @SuppressLint("CheckResult")
    private fun userApiCall(userID: String) {
        if (UtilMethods.isConnectedToInternet(mContext)) {
            UtilMethods.showLoading(mContext)
            val observable =
                ApiService.userApiCall().getUser(Constants.API_AUTHORIZATION_KEY, userID)
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    UtilMethods.hideLoading()

                    /** userResponse is response data class*/

                }, { error ->
                    UtilMethods.hideLoading()
                    UtilMethods.showLongToast(mContext, error.message.toString())
                })
        } else {
            UtilMethods.showLongToast(mContext, "No Internet Connection!")
        }
    }
}