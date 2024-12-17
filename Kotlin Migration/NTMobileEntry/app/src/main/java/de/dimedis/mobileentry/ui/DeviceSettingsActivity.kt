package de.dimedis.mobileentry.ui
/**
 * Created by Softeq Development Corporation
 * http://www.softeq.com
 */
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.backend.BackendServiceUtil
import de.dimedis.mobileentry.backend.response.GetTicketHistoryResponse
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DeviceSettingsActivity : StubActivity() {
    companion object {
        var TAG = "DeviceSettingsActivity"
        fun start(context: Context) {
            val intent = Intent(context, DeviceSettingsActivity::class.java)
            context.startActivity(intent)
        }
    }

    var sta: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_phone)
        sta = findViewById(R.id.status)
        sta!!.text = "OFF"
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        BackendServiceUtil.getTicketHistory("123-good")
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: GetTicketHistoryResponse) {
        Log.i(TAG, "event:" + event.content.toString())
        //Log.i(TAG, "event:" + event.content);
    } //    @Click(R.id.ok)    TODO

    //            void onOKclick(){
    //        BackendService.getTicketHistory("123-good");
    //    }
}