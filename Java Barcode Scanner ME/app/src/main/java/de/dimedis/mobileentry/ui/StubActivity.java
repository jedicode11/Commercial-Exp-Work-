package de.dimedis.mobileentry.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

import de.dimedis.mobileentry.R;

public class StubActivity extends BaseActivity {
    ViewStub stub;
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_base);
        initStub(layoutResID);
    }

    void initStub(int layoutResID) {
        stub = super.findViewById(R.id.viewStub);
        stub.setLayoutResource(layoutResID);
        rootView = stub.inflate();
    }

    @Override
    public View findViewById(int id) {
        return rootView.findViewById(id);
    }
}
