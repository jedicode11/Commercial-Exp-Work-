package de.dimedis.mobileentry.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class CustTextView extends AppCompatTextView {
    public CustTextView(Context context) {
        super(context);
    }

    public CustTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }
}
