package de.dimedis.mobileentry.ui.view;

import static de.dimedis.mobileentry.util.SoftKeyboard.showKeyboard;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.dimedis.mobileentry.R;

public class FMEditView extends LinearLayout {
    EditText mEditText;
    ImageButton mImageButton;

    public FMEditView(Context context) {
        super(context, null);
    }

    public FMEditView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        inflate(getContext(), R.layout.view_custedit, this);
        mEditText = findViewById(R.id.editText);
        mImageButton = findViewById(R.id.imageButton);
        mImageButton.setOnClickListener(v -> softKeyboard());
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener l) {
        mEditText.setOnEditorActionListener(l);
    }

    public Editable getText() {
        return mEditText.getText();
    }

    void softKeyboard() {
        mEditText.requestFocus();
        showKeyboard(mEditText);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
