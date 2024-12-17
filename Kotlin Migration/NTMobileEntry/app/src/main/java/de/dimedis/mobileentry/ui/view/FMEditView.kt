package de.dimedis.mobileentry.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView.OnEditorActionListener
import android.text.Editable
import android.view.View
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.util.SoftKeyboard

class FMEditView : LinearLayout {
    var mEditText: EditText? = null
    var mImageButton: ImageButton? = null

    constructor(context: Context?) : super(context, null) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs, 0) {
        init(context, attrs)
    }

    fun init(context: Context?, attrs: AttributeSet?) {
        inflate(getContext(), R.layout.view_custedit, this)
        mEditText = findViewById(R.id.editText)
        mImageButton = findViewById(R.id.imageButton)
        mImageButton?.setOnClickListener(OnClickListener {
            softKeyboard() })
    }

    fun setOnEditorActionListener(l: OnEditorActionListener?) {
        mEditText!!.setOnEditorActionListener(l)
    }

    fun getText(): Editable {
        return mEditText!!.text
    }

    fun softKeyboard() {
        mEditText!!.requestFocus()
        SoftKeyboard.showKeyboard(mEditText)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
}