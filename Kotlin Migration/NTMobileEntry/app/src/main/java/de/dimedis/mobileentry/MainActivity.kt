package de.dimedis.mobileentry

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import de.dimedis.mobileentry.backend.ApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @SuppressLint("CheckResult")
    private fun connectServer2() {
        if (UtilMethods.isConnectedToInternet(this.applicationContext)) {
            UtilMethods.showLoading(this.applicationContext)
            val observable = ApiService.backendApi().serverConnect("GhPqKkbxSyIHKkM7")
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ res ->
                    if (ConfigPref.rpcUrl != res.content?.rpcUrl) {
                        ConfigPref.rpcUrl = res.content?.rpcUrl
                        if (ConfigPref.rpcUrl?.endsWith("/") == false) {
                            ConfigPref.rpcUrl += "/"
                        }
                        ApiService.setupRestAdapter()
                    }
                    ConfigPref.serverName = res.content?.serverName
                    ConfigPref.languages = Gson().toJson(res.content?.languages)

                    UtilMethods.hideLoading()

                    EventBus.getDefault().post(res)

                }, { error -> UtilMethods.hideLoading()
                    UtilMethods.showLongToast(this.applicationContext, error.message.toString())
                })
        } else {
            UtilMethods.showLongToast(this.applicationContext, "No Internet Connection!")
        }
    }
}