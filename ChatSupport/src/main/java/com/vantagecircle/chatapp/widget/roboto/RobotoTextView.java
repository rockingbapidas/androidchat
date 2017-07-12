package com.vantagecircle.chatapp.widget.roboto;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.vantagecircle.chatapp.widget.roboto.util.RobotoTypefaceUtils;

/**
 * Created by ILM-Rahul on 29/01/16.
 */
public class RobotoTextView extends AppCompatTextView {

    /**
     * Simple constructor to use when creating a widget from code.
     *
     * @param context The Context the widget is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public RobotoTextView(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a widget from XML. This is called
     * when a widget is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p>
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the widget is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the widget.
     * @see #RobotoTextView(Context, AttributeSet, int)
     */
    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            RobotoTypefaceUtils.initView(this, context, attrs);
        }
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of View allows subclasses to use their own base style when
     * they are inflating.
     *
     * @param context  The Context the widget is running in, through which it can
     *                 access the current theme, resources, etc.
     * @param attrs    The attributes of the XML tag that is inflating the widget.
     * @param defStyle The default style to apply to this widget. If 0, no style
     *                 will be applied (beyond what is included in the theme). This may
     *                 either be an attribute resource, whose value will be retrieved
     *                 from the current theme, or an explicit style resource.
     * @see #RobotoTextView(Context, AttributeSet)
     */
    public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (!isInEditMode()) {
            RobotoTypefaceUtils.initView(this, context, attrs);
        }
    }
}
