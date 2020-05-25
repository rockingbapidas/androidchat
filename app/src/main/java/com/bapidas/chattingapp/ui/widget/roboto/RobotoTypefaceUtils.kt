package com.bapidas.chattingapp.ui.widget.roboto

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView
import com.bapidas.chattingapp.R

/**
 * Created by ILM-Rahul on 29/01/16.
 */
object RobotoTypefaceUtils {
    /**
     * Typeface initialization using the attributes. Used in RobotoTextView constructor.
     *
     * @param textView The roboto text view
     * @param context  The context the widget is running in, through which it can
     * access the current theme, resources, etc.
     * @param attrs    The attributes of the XML tag that is inflating the widget.
     */
    fun initView(textView: TextView, context: Context, attrs: AttributeSet?) {
        val typeface: Typeface?
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.RobotoTextView)
            typeface = typefaceFromAttrs(context, a)
            a.recycle()
        } else {
            typeface = RobotoTypefaceManager.obtainTypeface(context,
                    RobotoTypefaceManager.Typeface.ROBOTO_REGULAR)
        }
        typeface?.let { setup(textView, it) }
    }

    fun typefaceFromAttrs(context: Context, a: TypedArray): Typeface? {
        val typeface: Typeface?
        typeface = if (a.hasValue(R.styleable.RobotoTextView_typeface)) {
            val typefaceValue = a.getInt(R.styleable.RobotoTextView_typeface,
                    RobotoTypefaceManager.Typeface.ROBOTO_REGULAR)
            RobotoTypefaceManager.obtainTypeface(context, typefaceValue)
        } else {
            val fontFamily = a.getInt(R.styleable.RobotoTextView_fontFamily,
                    RobotoTypefaceManager.FontFamily.ROBOTO)
            val textWeight = a.getInt(R.styleable.RobotoTextView_textWeight,
                    RobotoTypefaceManager.TextWeight.NORMAL)
            val textStyle = a.getInt(R.styleable.RobotoTextView_textStyle,
                    RobotoTypefaceManager.TextStyle.NORMAL)
            RobotoTypefaceManager.obtainTypeface(context, fontFamily, textWeight, textStyle)
        }
        return typeface
    }

    /**
     * Setup typeface for TextView. Wrapper over [TextView.setTypeface]
     * for making the font anti-aliased.
     *
     * @param textView The text view
     * @param typeface The specify typeface
     */
    fun setup(textView: TextView, typeface: Typeface) {
        textView.paintFlags = textView.paintFlags or Paint.SUBPIXEL_TEXT_FLAG or
                Paint.ANTI_ALIAS_FLAG
        textView.typeface = typeface
    }

    /**
     * Setup typeface for Paint.
     *
     * @param paint    The paint
     * @param typeface The specify typeface
     */
    fun setup(paint: Paint, typeface: Typeface) {
        paint.flags = paint.flags or Paint.SUBPIXEL_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
        paint.typeface = typeface
    }
}