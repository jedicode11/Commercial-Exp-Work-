package de.dimedis.mobileentry.ui
/**
 * Created by Softeq Development Corporation
 * http://www.softeq.com
 */
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.zxing.client.android.Intents
import com.google.zxing.integration.android.IntentIntegrator
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.bbapi.BarcodeManager
import de.dimedis.mobileentry.util.dynamicres.DynamicString
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

open class BaseFragmentActivity : BaseActivity() {
    companion object {
        const val TAG = "BaseActivity"
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        DynamicString.scan(window.decorView)
    }

    override fun onBackPressed() {
        Log.i(TAG, "onBackPressed():" + supportFragmentManager.backStackEntryCount)
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    @JvmOverloads
    fun addFragment(fragment: Fragment, tag: String? = null) {
        var tag = tag
        tag = tag ?: fragment.javaClass.simpleName
        Log.i(TAG, "---- addFragment([ " + tag + " ]- isVisible: " + fragment.isVisible + " isAdded: " + fragment.isAdded + " isRemoving:" + fragment.isRemoving)
        supportFragmentManager.beginTransaction().add(R.id.content, fragment, tag).commit()
    }

    fun fragmentToStack(fragment: Fragment) {
        fragment(fragment, true)
    }

    @JvmOverloads
    fun fragment(fragment: Fragment, isAddtoStack: Boolean = true) {
        var tag = fragment.tag
        tag = tag ?: fragment.javaClass.simpleName
        Log.i(TAG, "==== fragment -[ " + tag + " ]- isVisible: " + fragment.isVisible + " isAdded: " + fragment.isAdded + " isRemoving:" + fragment.isRemoving)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val fr = supportFragmentManager.findFragmentByTag(tag)
        if (fr != null) {
            Log.i(TAG, "fragment fr isVisible: " + fr.isVisible + " isAdded: " + fr.isAdded + " isRemoving:" + fr.isRemoving)
            val status = supportFragmentManager.popBackStackImmediate(tag, 0)
            fragmentTransaction.show(fr)
            fragmentTransaction.commit()
            Log.i(TAG, "in stack> tag:$tag status:$status")
            return
        }
        if (fragment.isRemoving) {
            Log.i(TAG, "isRemoving")
        }
        if (fragment.isVisible || fragment.isAdded) {
            fragmentTransaction.show(fragment)
            fragmentTransaction.commit()
            return
        }
        fragmentTransaction.replace(R.id.content, fragment, tag)
        if (isAddtoStack) fragmentTransaction.addToBackStack(tag)
        fragmentTransaction.commit()
        Log.i(TAG, "fragment:" + supportFragmentManager.backStackEntryCount)
    }

    override fun onDestroy() {
        super.onDestroy()
        //handler=null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            return
        }
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            var dataStr: String?
            if (data.getStringExtra(Intents.Scan.RESULT).also { dataStr = it } != null) {
                EventBus.getDefault().postSticky(BarcodeManager.BarcodeScannedEvent(dataStr))
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

