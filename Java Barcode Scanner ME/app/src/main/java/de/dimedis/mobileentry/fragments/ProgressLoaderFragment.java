package de.dimedis.mobileentry.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import de.dimedis.mobileentry.R;

public abstract class ProgressLoaderFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate(...)");
        super.onCreate(savedInstanceState);
    }

    ProgressBar mProgressBar;
    RelativeLayout relativeLayout;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // if(relativeLayout==null)initRelativeLayout(view);
        ((ViewGroup) view).addView(relativeLayout);
        setVisibilityProgressBar(false);
    }

    void initRelativeLayout(LayoutInflater inflater, ViewGroup container) {
        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_progresbar, container, false);
        mProgressBar = (ProgressBar) relativeLayout.findViewById(R.id.progressBar1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initRelativeLayout(inflater, container);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setVisibilityProgressBar(boolean isVisible) {
        mProgressBar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        relativeLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }


}
