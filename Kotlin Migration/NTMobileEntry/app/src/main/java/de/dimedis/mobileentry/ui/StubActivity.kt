package de.dimedis.mobileentry.ui

import android.os.Bundle
import android.view.View
import android.view.ViewStub
import de.dimedis.mobileentry.R

open class StubActivity : BaseActivity() {
    var stub: ViewStub? = null
    var rootView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(R.layout.activity_base)
        initStub(layoutResID)
    }

    fun initStub(layoutResID: Int) {
        stub = super.findViewById(R.id.viewStub)
        stub?.layoutResource = layoutResID
        rootView = stub?.inflate()
    }
}