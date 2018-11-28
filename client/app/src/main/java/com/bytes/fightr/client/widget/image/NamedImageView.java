package com.bytes.fightr.client.widget.image;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Add a name attribute to the {@code ImageView}
 */
public class NamedImageView extends FighterActionView {

    public NamedImageView(Context context) {
        this(context, null);
    }

    public NamedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NamedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

}
