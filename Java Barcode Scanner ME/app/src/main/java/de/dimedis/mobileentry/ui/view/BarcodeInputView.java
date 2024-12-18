package de.dimedis.mobileentry.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import de.dimedis.mobileentry.R;
import de.dimedis.mobileentry.util.SoundPlayerUtil;
import de.dimedis.mobileentry.util.SoundType;
import de.dimedis.mobileentry.util.UIUtils;

public class BarcodeInputView extends LinearLayout {
    private static final String STATE_VIEW = "state_view";
    private static final String STATE_SHOULD_DRAW_BORDER = "should_draw_border";

    private final Paint mBorderPaint = new Paint();
    private boolean mShouldDrawBorder = true;
    private View mKeyboardLayout;
    private EditText mBarcodeEditText;
    private OnBarcodeEnteredListener mOnBarcodeEnteredListener;

    public BarcodeInputView(Context context) {
        this(context, null);
    }

    public BarcodeInputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarcodeInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.view_barcode_input, this, true);
        mKeyboardLayout = findViewById(R.id.frame_keyboard);
        mBarcodeEditText = findViewById(R.id.et_barcode);
        mBarcodeEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (mOnBarcodeEnteredListener != null) {
                    mOnBarcodeEnteredListener.onBarcodeEntered(v.getText().toString().trim());
                }
                SoundPlayerUtil.getInstance().playSoundAsync(SoundType.CODE_SCAN_OK_OR_MANUAL_ACCEPTED);
            }
            return false;
        });

        findViewById(R.id.btn_keyboard).setOnClickListener(v -> {
            clear();
            UIUtils.showSoftKeyboard(mBarcodeEditText);
        });

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.border_line_width));
        mBorderPaint.setColor(ContextCompat.getColor(context, R.color.grey_dark));
    }

    public void setOnBarcodeEnteredListener(OnBarcodeEnteredListener listener) {
        mOnBarcodeEnteredListener = listener;
    }

    @NonNull
    public String getBarcode() {
        return mBarcodeEditText.getText().toString().trim();
    }

    public void setBarcode(String barcode) {
        mBarcodeEditText.setText(barcode);
    }

    public void clear() {
        mBarcodeEditText.setText("");
    }

    public void setShouldDrawBorder(boolean should) {
        if (mShouldDrawBorder != should) {
            mShouldDrawBorder = should;
            invalidate();
        }
    }

    public void setKeyboardButtonBackgroundColor(@ColorRes int resId) {
        mKeyboardLayout.setBackgroundColor(ContextCompat.getColor(getContext(), resId));
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);

        if (mShouldDrawBorder) {
            float borderWidthHalf = mBorderPaint.getStrokeWidth() / 2;
            // noinspection SuspiciousNameCombination
            canvas.drawRect(borderWidthHalf, borderWidthHalf, getWidth() - borderWidthHalf, getHeight() - borderWidthHalf, mBorderPaint);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_VIEW, super.onSaveInstanceState());
        bundle.putBoolean(STATE_SHOULD_DRAW_BORDER, mShouldDrawBorder);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable(STATE_VIEW));
        setShouldDrawBorder(bundle.getBoolean(STATE_SHOULD_DRAW_BORDER));
    }

    public interface OnBarcodeEnteredListener {
        void onBarcodeEntered(String barcode);
    }
}
