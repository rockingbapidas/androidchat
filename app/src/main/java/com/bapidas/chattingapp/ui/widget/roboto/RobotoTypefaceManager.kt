package com.bapidas.chattingapp.ui.widget.roboto

import android.content.Context
import android.util.SparseArray

/**
 * Created by ILM-Rahul on 29/01/16.
 */
object RobotoTypefaceManager {
    /**
     * Array of created typefaces for later reused.
     */
    private val mTypefaces = SparseArray<android.graphics.Typeface?>(22)

    /**
     * Obtain typeface.
     *
     * @param context       The Context the widget is running in, through which it can access the current theme, resources, etc.
     * @param typefaceValue The value of "typeface" attribute
     * @return specify [android.graphics.Typeface]
     * @throws IllegalArgumentException if unknown `typeface` attribute value.
     */
    @Throws(IllegalArgumentException::class)
    fun obtainTypeface(context: Context, typefaceValue: Int): android.graphics.Typeface? {
        var typeface = mTypefaces[typefaceValue]
        if (typeface == null) {
            typeface = createTypeface(context, typefaceValue)
            mTypefaces.put(typefaceValue, typeface)
        }
        return typeface
    }

    /**
     * Obtain typeface.
     *
     * @param context    The Context the widget is running in, through which it can access the current theme, resources, etc.
     * @param fontFamily The value of "fontFamily" attribute
     * @param textWeight The value of "textWeight" attribute
     * @param textStyle  The value of "textStyle" attribute
     * @return specify [android.graphics.Typeface]
     * @throws IllegalArgumentException if unknown `typeface` attribute value.
     */
    @Throws(IllegalArgumentException::class)
    fun obtainTypeface(
            context: Context, fontFamily: Int, textWeight: Int, textStyle: Int): android.graphics.Typeface? {
        val typefaceValue = getTypefaceValue(fontFamily, textWeight, textStyle)
        return obtainTypeface(context, typefaceValue)
    }

    /**
     * @param fontFamily The value of "fontFamily" attribute
     * @param textWeight The value of "textWeight" attribute
     * @param textStyle  The value of "textStyle" attribute
     * @return typeface value
     */
    private fun getTypefaceValue(fontFamily: Int, textWeight: Int, textStyle: Int): Int {
        return if (fontFamily == FontFamily.ROBOTO) {
            when (textWeight) {
                TextWeight.NORMAL -> {
                    when (textStyle) {
                        TextStyle.NORMAL -> Typeface.ROBOTO_REGULAR
                        TextStyle.ITALIC -> Typeface.ROBOTO_ITALIC
                        else -> throw IllegalArgumentException("`textStyle` attribute value " + textStyle +
                                " is not supported for this fontFamily " + fontFamily +
                                " and textWeight " + textWeight)
                    }
                }
                TextWeight.THIN -> {
                    when (textStyle) {
                        TextStyle.NORMAL -> Typeface.ROBOTO_THIN
                        TextStyle.ITALIC -> Typeface.ROBOTO_THIN_ITALIC
                        else -> throw IllegalArgumentException("`textStyle` attribute value " + textStyle +
                                " is not supported for this fontFamily " + fontFamily +
                                " and textWeight " + textWeight)
                    }
                }
                TextWeight.LIGHT -> {
                    when (textStyle) {
                        TextStyle.NORMAL -> Typeface.ROBOTO_LIGHT
                        TextStyle.ITALIC -> Typeface.ROBOTO_LIGHT_ITALIC
                        else -> throw IllegalArgumentException("`textStyle` attribute value " + textStyle +
                                " is not supported for this fontFamily " + fontFamily +
                                " and textWeight " + textWeight)
                    }
                }
                TextWeight.MEDIUM -> {
                    when (textStyle) {
                        TextStyle.NORMAL -> Typeface.ROBOTO_MEDIUM
                        TextStyle.ITALIC -> Typeface.ROBOTO_MEDIUM_ITALIC
                        else -> throw IllegalArgumentException("`textStyle` attribute value " + textStyle +
                                " is not supported for this fontFamily " + fontFamily +
                                " and textWeight " + textWeight)
                    }
                }
                TextWeight.BOLD -> {
                    when (textStyle) {
                        TextStyle.NORMAL -> Typeface.ROBOTO_BOLD
                        TextStyle.ITALIC -> Typeface.ROBOTO_BOLD_ITALIC
                        else -> throw IllegalArgumentException("`textStyle` attribute value " + textStyle +
                                " is not supported for this fontFamily " + fontFamily +
                                " and textWeight " + textWeight)
                    }
                }
                TextWeight.ULTRA_BOLD -> {
                    when (textStyle) {
                        TextStyle.NORMAL -> Typeface.ROBOTO_BLACK
                        TextStyle.ITALIC -> Typeface.ROBOTO_BLACK_ITALIC
                        else -> throw IllegalArgumentException("`textStyle` attribute value " + textStyle +
                                " is not supported for this fontFamily " + fontFamily +
                                " and textWeight " + textWeight)
                    }
                }
                else -> {
                    throw IllegalArgumentException("`textWeight` attribute value " + textWeight +
                            " is not supported for this font family " + fontFamily)
                }
            }
        } else if (fontFamily == FontFamily.ROBOTO_CONDENSED) {
            when (textWeight) {
                TextWeight.NORMAL -> {
                    when (textStyle) {
                        TextStyle.NORMAL -> Typeface.ROBOTO_CONDENSED_REGULAR
                        TextStyle.ITALIC -> Typeface.ROBOTO_CONDENSED_ITALIC
                        else -> throw IllegalArgumentException("`textStyle` attribute value " + textStyle +
                                " is not supported for this fontFamily " + fontFamily +
                                " and textWeight " + textWeight)
                    }
                }
                TextWeight.LIGHT -> {
                    when (textStyle) {
                        TextStyle.NORMAL -> Typeface.ROBOTO_CONDENSED_LIGHT
                        TextStyle.ITALIC -> Typeface.ROBOTO_CONDENSED_LIGHT_ITALIC
                        else -> throw IllegalArgumentException("`textStyle` attribute value " + textStyle +
                                " is not supported for this fontFamily " + fontFamily +
                                " and textWeight " + textWeight)
                    }
                }
                TextWeight.BOLD -> {
                    when (textStyle) {
                        TextStyle.NORMAL -> Typeface.ROBOTO_CONDENSED_BOLD
                        TextStyle.ITALIC -> Typeface.ROBOTO_CONDENSED_BOLD_ITALIC
                        else -> throw IllegalArgumentException("`textStyle` attribute value " + textStyle +
                                " is not supported for this fontFamily " + fontFamily +
                                " and textWeight " + textWeight)
                    }
                }
                else -> {
                    throw IllegalArgumentException("`textWeight` attribute value " + textWeight +
                            " is not supported for this font family " + fontFamily)
                }
            }
        } else if (fontFamily == FontFamily.ROBOTO_SLAB) {

            // roboto slab does not support 'textStyle'
            require(textStyle == TextStyle.NORMAL) {
                "`textStyle` attribute value " + textStyle +
                        " is not supported for this fontFamily " + fontFamily
            }
            if (textWeight == TextWeight.NORMAL) {
                Typeface.ROBOTO_SLAB_REGULAR
            } else if (textWeight == TextWeight.THIN) {
                Typeface.ROBOTO_SLAB_THIN
            } else if (textWeight == TextWeight.LIGHT) {
                Typeface.ROBOTO_SLAB_LIGHT
            } else if (textWeight == TextWeight.BOLD) {
                Typeface.ROBOTO_SLAB_BOLD
            } else {
                throw IllegalArgumentException("`textWeight` attribute value " + textWeight +
                        " is not supported for this font family " + fontFamily)
            }
        } else {
            throw IllegalArgumentException("Unknown `fontFamily` attribute value $fontFamily")
        }
    }

    /**
     * Create typeface from assets.
     *
     * @param context       The Context the widget is running in, through which it can
     * access the current theme, resources, etc.
     * @param typefaceValue The value of "typeface" attribute
     * @return Roboto [android.graphics.Typeface]
     * @throws IllegalArgumentException if unknown `typeface` attribute value.
     */
    @Throws(IllegalArgumentException::class)
    private fun createTypeface(context: Context, typefaceValue: Int): android.graphics.Typeface {
        val typefacePath: String = when (typefaceValue) {
            Typeface.ROBOTO_THIN -> "fonts/roboto_thin.ttf"
            Typeface.ROBOTO_THIN_ITALIC -> "fonts/roboto_thin_italic.ttf"
            Typeface.ROBOTO_LIGHT -> "fonts/roboto_light.ttf"
            Typeface.ROBOTO_LIGHT_ITALIC -> "fonts/roboto_light_italic.ttf"
            Typeface.ROBOTO_REGULAR -> "fonts/roboto_regular.ttf"
            Typeface.ROBOTO_ITALIC -> "fonts/roboto_italic.ttf"
            Typeface.ROBOTO_MEDIUM -> "fonts/roboto_medium.ttf"
            Typeface.ROBOTO_MEDIUM_ITALIC -> "fonts/roboto_medium_italic.ttf"
            Typeface.ROBOTO_BOLD -> "fonts/roboto_bold.ttf"
            Typeface.ROBOTO_BOLD_ITALIC -> "fonts/roboto_bold_italic.ttf"
            Typeface.ROBOTO_BLACK -> "fonts/roboto_black.ttf"
            Typeface.ROBOTO_BLACK_ITALIC -> "fonts/roboto_black_italic.ttf"
            Typeface.ROBOTO_CONDENSED_LIGHT -> "fonts/roboto_condensed_light.ttf"
            Typeface.ROBOTO_CONDENSED_LIGHT_ITALIC -> "fonts/roboto_condensed_light_italic.ttf"
            Typeface.ROBOTO_CONDENSED_REGULAR -> "fonts/roboto_condensed_regular.ttf"
            Typeface.ROBOTO_CONDENSED_ITALIC -> "fonts/roboto_condensed_italic.ttf"
            Typeface.ROBOTO_CONDENSED_BOLD -> "fonts/roboto_condensed_bold.ttf"
            Typeface.ROBOTO_CONDENSED_BOLD_ITALIC -> "fonts/roboto_condensed_bold_italic.ttf"
            Typeface.ROBOTO_SLAB_THIN -> "fonts/roboto_slab_thin.ttf"
            Typeface.ROBOTO_SLAB_LIGHT -> "fonts/roboto_slab_light.ttf"
            Typeface.ROBOTO_SLAB_REGULAR -> "fonts/roboto_slab_regular.ttf"
            Typeface.ROBOTO_SLAB_BOLD -> "fonts/roboto_slab_bold.ttf"
            else -> throw IllegalArgumentException("Unknown `typeface` attribute value $typefaceValue")
        }
        return android.graphics.Typeface.createFromAsset(context.assets, typefacePath)
    }

    /**
     * Available values ​​for the "typeface" attribute.
     */
    object Typeface {
        const val ROBOTO_THIN = 0
        const val ROBOTO_THIN_ITALIC = 1
        const val ROBOTO_LIGHT = 2
        const val ROBOTO_LIGHT_ITALIC = 3
        const val ROBOTO_REGULAR = 4
        const val ROBOTO_ITALIC = 5
        const val ROBOTO_MEDIUM = 6
        const val ROBOTO_MEDIUM_ITALIC = 7
        const val ROBOTO_BOLD = 8
        const val ROBOTO_BOLD_ITALIC = 9
        const val ROBOTO_BLACK = 10
        const val ROBOTO_BLACK_ITALIC = 11
        const val ROBOTO_CONDENSED_LIGHT = 12
        const val ROBOTO_CONDENSED_LIGHT_ITALIC = 13
        const val ROBOTO_CONDENSED_REGULAR = 14
        const val ROBOTO_CONDENSED_ITALIC = 15
        const val ROBOTO_CONDENSED_BOLD = 16
        const val ROBOTO_CONDENSED_BOLD_ITALIC = 17
        const val ROBOTO_SLAB_THIN = 18
        const val ROBOTO_SLAB_LIGHT = 19
        const val ROBOTO_SLAB_REGULAR = 20
        const val ROBOTO_SLAB_BOLD = 21
    }

    /**
     * Available values ​​for the "fontFamily" attribute.
     */
    object FontFamily {
        const val ROBOTO = 0
        const val ROBOTO_CONDENSED = 1
        const val ROBOTO_SLAB = 2
    }

    /**
     * Available values ​​for the "textWeight" attribute.
     */
    object TextWeight {
        const val NORMAL = 0
        const val THIN = 1
        const val LIGHT = 2
        const val MEDIUM = 3
        const val BOLD = 4
        const val ULTRA_BOLD = 5
    }

    /**
     * Available values ​​for the "textStyle" attribute.
     */
    object TextStyle {
        const val NORMAL = 0
        const val ITALIC = 1
    }
}