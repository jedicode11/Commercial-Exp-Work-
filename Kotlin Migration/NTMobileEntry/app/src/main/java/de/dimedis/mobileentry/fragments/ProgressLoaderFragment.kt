package de.dimedis.mobileentry.fragments

import android.widget.RelativeLayout
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import de.dimedis.mobileentry.R

abstract class ProgressLoaderFragment : BaseFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate(...)")
        super.onCreate(savedInstanceState)
    }

    var mProgressBar: ProgressBar? = null
    var relativeLayout: RelativeLayout? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // if(relativeLayout==null)initRelativeLayout(view);
        (view as ViewGroup).addView(relativeLayout)
        setVisibilityProgressBar(false)
    }

    fun initRelativeLayout(inflater: LayoutInflater, container: ViewGroup?) {
        relativeLayout = inflater.inflate(R.layout.fragment_progresbar, container, false) as RelativeLayout?
        mProgressBar = relativeLayout!!.findViewById<View>(R.id.progressBar1) as ProgressBar
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initRelativeLayout(inflater, container)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    fun setVisibilityProgressBar(isVisible: Boolean) {
        mProgressBar!!.visibility = if (isVisible) View.VISIBLE else View.GONE
        relativeLayout!!.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}