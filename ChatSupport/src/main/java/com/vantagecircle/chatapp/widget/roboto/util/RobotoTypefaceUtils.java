package com.vantagecircle.chatapp.widget.roboto.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.vantagecircle.chatapp.R;

/**
 * Created by ILM-Rahul on 29/01/16.
 */
public final class RobotoTypefaceUtils {

    private RobotoTypefaceUtils() {
    }

    /**
     * Typeface initialization using the attributes. Used in RobotoTextView constructor.
     *
     * @param textView The roboto text view
     * @param context  The context the widget is running in, through which it can
     *                 access the current theme, resources, etc.
     * @param attrs    The attributes of the XML tag that is inflating the widget.
     */
    public static void initView(@NonNull TextView textView, @NonNull Context context, @Nullable AttributeSet attrs) {
        Typeface typeface;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RobotoTextView);
            typeface = typefaceFromAttrs(context, a);
            a.recycle();
        } else {
            typeface = RobotoTypefaceManager.obtainTypeface(context, RobotoTypefaceManager.Typeface.ROBOTO_REGULAR);
        }

        setup(textView, typeface);
    }

    @NonNull
    public static Typeface typefaceFromAttrs(@NonNull Context context, @NonNull TypedArray a) {
        Typeface typeface;
        if (a.hasValue(R.styleable.RobotoTextView_typeface)) {
            int typefaceValue = a.getInt(R.styleable.RobotoTextView_typeface, RobotoTypefaceManager.Typeface.ROBOTO_REGULAR);
            typeface = RobotoTypefaceManager.obtainTypeface(context, typefaceValue);
        } else {
            int fontFamily = a.getInt(R.styleable.RobotoTextView_fontFamily, RobotoTypefaceManager.FontFamily.ROBOTO);
            int textWeight = a.getInt(R.styleable.RobotoTextView_textWeight, RobotoTypefaceManager.TextWeight.NORMAL);
            int textStyle = a.getInt(R.styleable.RobotoTextView_textStyle, RobotoTypefaceManager.TextStyle.NORMAL);
            typeface = RobotoTypefaceManager.obtainTypeface(context, fontFamily, textWeight, textStyle);
        }
        return typeface;
    }

    /**
     * Setup typeface for TextView. Wrapper over {@link TextView#setTypeface(Typeface)}
     * for making the font anti-aliased.
     *
     * @param textView The text view
     * @param typeface The specify typeface
     */
    public static void setup(@NonNull TextView textView, @NonNull Typeface typeface) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        textView.setTypeface(typeface);
    }

    /**
     * Setup typeface for Paint.
     *
     * @param paint    The paint
     * @param typeface The specify typeface
     */
    public static void setup(@NonNull Paint paint, @NonNull Typeface typeface) {
        paint.setFlags(paint.getFlags() | Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(typeface);
    }
}
