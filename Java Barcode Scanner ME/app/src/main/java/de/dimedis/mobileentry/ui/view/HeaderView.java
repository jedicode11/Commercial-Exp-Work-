package de.dimedis.mobileentry.ui.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.model.StatusManager;
import de.dimedis.mobileentry.util.BarcodeUtil;
import de.dimedis.mobileentry.util.CommonUtils;

public class HeaderView extends RelativeLayout {
    private static final String STATE_VIEW = "state_view";
    private static final String STATE_DEVICE_ID = "state_device_id";
    private static final String STATE_BORDER_NAME = "state_border_name";
    private final ImageButton mCameraReaderToolBarButton;

    private final TextView mStatusTextView;
    private final TextView mBorderNameTextView;
    private final ImageButton mMenuButton;
    private String mDeviceId;
    private String mBorderName;
    private OnMenuButtonClickedListener mOnMenuButtonClickedListener;
    public static final int REQUEST = 112;

    public HeaderView(Context context) {
        this(context, null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.view_header, this, true);
        setBackgroundResource(R.color.grey_light);

        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.HeaderView, defStyle, 0);
        boolean showStatus = styledAttrs.getBoolean(R.styleable.HeaderView_hv_showStatus, true);
        boolean showBorderName = styledAttrs.getBoolean(R.styleable.HeaderView_hv_showBorderName, true);
        boolean showMenuButton = styledAttrs.getBoolean(R.styleable.HeaderView_hv_showMenuButton, true);
        styledAttrs.recycle();

        mStatusTextView = findViewById(R.id.tv_status);
        mStatusTextView.setVisibility(showStatus ? VISIBLE : GONE);
        mBorderNameTextView = findViewById(R.id.tv_border_name);
        mBorderNameTextView.setVisibility(showBorderName ? VISIBLE : GONE);
        mMenuButton = findViewById(R.id.btn_menu);
        mMenuButton.setVisibility(showMenuButton ? VISIBLE : GONE);
        mMenuButton.setOnClickListener(v -> {
            if (mOnMenuButtonClickedListener != null) {
                mOnMenuButtonClickedListener.onMenuButtonClicked();
            }
        });

        mCameraReaderToolBarButton = findViewById(R.id.btn_menu_camera);
        mCameraReaderToolBarButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // TODO Version
                String[] PERMISSIONS = {Manifest.permission.CAMERA};
                if (!hasPermissions(context, PERMISSIONS)) {
                    ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, REQUEST);
                } else {
                    openBarCodeReader(context);
                }
            } else {
                openBarCodeReader(context);
            }
        });
        updateStatus();
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void openBarCodeReader(Context context) {
        BarcodeUtil.openCamReader((Activity) context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    private void updateStatus() {
        if (isInEditMode()) {
            return;
        }

        StatusManager.Status status = StatusManager.getInstance().getStatus();
        String statusText = status.getText(status);
        if (!TextUtils.isEmpty(mDeviceId)) {
            statusText += " - " + mDeviceId;
        }
        mStatusTextView.setText(statusText);

        int statusIconId = -1;
        switch (status) {
            case OFFLINE:
            case LOCAL_SCAN:
                statusIconId = R.drawable.status_offline;
                break;
            case ONLINE:
                statusIconId = R.drawable.status_online;
        }
        Drawable statusIcon = ContextCompat.getDrawable(getContext(), statusIconId);
        mStatusTextView.setCompoundDrawablesWithIntrinsicBounds(statusIcon, null, null, null);
    }

    public void setOnMenuButtonClickedListener(OnMenuButtonClickedListener listener) {
        mOnMenuButtonClickedListener = listener;
    }

    public void setDeviceId(String deviceId) {
        if (!TextUtils.equals(mDeviceId, deviceId)) {
            mDeviceId = deviceId;
            updateStatus();
        }
    }

    public void setBorderName(String name) {
        if (!TextUtils.equals(mDeviceId, name)) {
            mBorderName = name;
            mBorderNameTextView.setText(name);
        }
    }

    public void setMenuButtonVisible(boolean isVisible) {
        mMenuButton.setVisibility(isVisible ? VISIBLE : GONE);
    }

    public void setCameraButtonVisible(boolean isVisible) {
        if (!isVisible && !CommonUtils.hasBBApi()) {
            isVisible = true;
        }

        mCameraReaderToolBarButton.setVisibility(isVisible ? VISIBLE : GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(StatusManager.StatusChangedEvent event) {
        updateStatus();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_VIEW, super.onSaveInstanceState());
        bundle.putString(STATE_DEVICE_ID, mDeviceId);
        bundle.putString(STATE_BORDER_NAME, mBorderName);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable(STATE_VIEW));
        setDeviceId(bundle.getString(STATE_DEVICE_ID));
        setBorderName(bundle.getString(STATE_BORDER_NAME));
    }

    public interface OnMenuButtonClickedListener {
        void onMenuButtonClicked();
    }
}
