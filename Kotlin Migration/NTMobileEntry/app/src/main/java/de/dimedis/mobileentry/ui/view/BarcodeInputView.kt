package de.dimedis.mobileentry.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.LinearLayout
import android.widget.EditText
import kotlin.jvm.JvmOverloads
import android.widget.TextView
import androidx.core.content.ContextCompat
import android.os.Parcelable
import android.os.Bundle
import androidx.annotation.ColorRes
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import de.dimedis.mobileentry.R
import de.dimedis.mobileentry.util.SoundPlayerUtil
import de.dimedis.mobileentry.util.SoundType
import de.dimedis.mobileentry.util.UIUtils

class BarcodeInputView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyle: Int = 0)
    : LinearLayout(context, attrs, defStyle) {
    private val mBorderPaint = Paint()
    private var mShouldDrawBorder = true
    private val mKeyboardLayout: View
    private val mBarcodeEditText: EditText
    private var mOnBarcodeEnteredListener: OnBarcodeEnteredListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_barcode_input, this, true)
        mKeyboardLayout = findViewById(R.id.frame_keyboard)
        mBarcodeEditText = findViewById(R.id.et_barcode)
        mBarcodeEditText.setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (mOnBarcodeEnteredListener != null) {
                    mOnBarcodeEnteredListener!!.onBarcodeEntered(v.text.toString().trim { it <= ' ' })
                }
                SoundPlayerUtil.getInstance().playSoundAsync(SoundType.CODE_SCAN_OK_OR_MANUAL_ACCEPTED)
            }
            false
        }
        findViewById<View>(R.id.btn_keyboard).setOnClickListener {
            clear()
            UIUtils.showSoftKeyboard(mBarcodeEditText)
        }
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.strokeWidth = resources.getDimensionPixelSize(R.dimen.border_line_width).toFloat()
        mBorderPaint.color = ContextCompat.getColor(context!!, R.color.grey_dark)
    }

    fun setOnBarcodeEnteredListener(listener: OnBarcodeEnteredListener?) {
        mOnBarcodeEnteredListener = listener
    }

    fun getBarcode(): String {
        return mBarcodeEditText.text.toString().trim { it <= ' ' }
    }

    fun setBarcode(barcode: String?) {
        mBarcodeEditText.setText(barcode)
    }

    fun clear() {
        mBarcodeEditText.setText("")
    }

    fun setShouldDrawBorder(should: Boolean) {
        if (mShouldDrawBorder != should) {
            mShouldDrawBorder = should
            invalidate()
        }
    }

    fun setKeyboardButtonBackgroundColor(@ColorRes resId: Int) {
        mKeyboardLayout.setBackgroundColor(ContextCompat.getColor(context, resId))
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (mShouldDrawBorder) {
            val borderWidthHalf = mBorderPaint.strokeWidth / 2
            // noinspection SuspiciousNameCombination
            canvas.drawRect(borderWidthHalf, borderWidthHalf, width - borderWidthHalf, height - borderWidthHalf, mBorderPaint)
        }
    }

    public override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(STATE_VIEW, super.onSaveInstanceState())
        bundle.putBoolean(STATE_SHOULD_DRAW_BORDER, mShouldDrawBorder)
        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val bundle = state as Bundle
        super.onRestoreInstanceState(bundle.getParcelable(STATE_VIEW))
        setShouldDrawBorder(bundle.getBoolean(STATE_SHOULD_DRAW_BORDER))
    }

    interface OnBarcodeEnteredListener {
        fun onBarcodeEntered(barcode: String?)
    }

    companion object {
        private const val STATE_VIEW = "state_view"
        private const val STATE_SHOULD_DRAW_BORDER = "should_draw_border"
    }
}