package de.dimedis.mobileentry.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.dimedis.mobileentry.ConfigPref;
import de.dimedis.mobileentry.MobileEntryApplication;
import de.dimedis.mobileentry.backend.response.BaseResponse;
import de.dimedis.mobileentry.backend.response.BaseResponseContent;
import de.dimedis.mobileentry.ui.BaseFragmentActivity;
import de.dimedis.mobileentry.util.dynamicres.DynamicString;

public class BaseFragment extends Fragment {
    static final String TAG = "BaseFragment";

    Activity mActivity;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) mActivity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        DynamicString.scan(view);
        super.onViewCreated(view, savedInstanceState);
    }

    public Activity getMainActivity() {
        return ((super.getActivity() != null ? getActivity() : mActivity));
    }

    public void fragment(Fragment fragment) {
        ((BaseFragmentActivity) (getActivity() != null ? getActivity() : mActivity)).fragment(fragment, true);
    }

    public void replaceFragment(Fragment fragment) {
        ((BaseFragmentActivity) getActivity()).fragment(fragment, false);
    }

    MobileEntryApplication getApp() {
        BaseFragmentActivity ba = (BaseFragmentActivity) getActivity();
        return (MobileEntryApplication) (ba.getApplication());
    }

    public ConfigPref getConfigPreferences() {
        return MobileEntryApplication.getConfigPreferences();
    }

    public void back() {
        getActivity().onBackPressed();
    }

    public interface Response<T extends BaseResponse> {
        void resultOK(T event);
        boolean onError(T event);
    }

    public static class ResponseImpl<T extends BaseResponse> implements Response<T> {
        @Override
        public void resultOK(T event) {

        }

        @Override
        public boolean onError(T event) {
            return true;
        }
    }

    protected void responseProcess(final BaseResponse event, final Response response) {
        if (event.isResultOk()) {
            if (event.content instanceof BaseResponseContent) {
                BaseResponseContent brc = (BaseResponseContent) event.content;
                if (brc.isStatusSuccess()) {
                    response.resultOK(event);
                }
            }
        }
    }

    void onErrorCancelClick() {
    }

    // get localized string
    public String getLocalizedString(int resId) {
        return DynamicString.getInstance().getString(resId);
    }
}
