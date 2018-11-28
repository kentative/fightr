package com.bytes.fightr.client.widget.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.bytes.fightr.R;
import com.bytes.fightr.client.widget.image.shader.CircleShader;
import com.bytes.fightr.client.widget.image.shader.ShaderHelper;

/**
 * Created by Kent on 4/29/2017.
 * An awesome round image view with custom attributes adapted for Fighters
 */
public abstract class FighterActionView extends ShaderImageView {

    // The name of the view
    protected String name;

    // TODO add more fighter specific data
    // TODO remove CircularImageView and NamedImageView, this one does both

    public FighterActionView(Context context) {
        this(context, null);
    }

    public FighterActionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FighterActionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) return;
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.FighterActionView);
            this.name = array.getString(R.styleable.FighterActionView_name);
            array.recycle();
        }
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        return new CircleShader();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
