package com.bytes.fightr.client.widget.layout;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

/**
 * Created by Kent on 5/27/2017.
 */

public class SmoothScrollLayout extends LinearLayoutManager {
    private static final float MILLISECONDS_PER_INCH = 300f;
    private Context mContext;

    public SmoothScrollLayout(Context context) {
        super(context);
        mContext = context;
        setStackFromEnd(true);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView,
                                       RecyclerView.State state, final int position) {

        LinearSmoothScroller smoothScroller =
                new LinearSmoothScroller(mContext) {

                    //This controls the direction in which smoothScroll looks for your view
                    @Override
                    public PointF computeScrollVectorForPosition
                    (int targetPosition) {
                        return SmoothScrollLayout.this
                                .computeScrollVectorForPosition(targetPosition);
                    }

                    //This returns the milliseconds it takes to scroll one pixel.
                    @Override
                    protected float calculateSpeedPerPixel
                    (DisplayMetrics displayMetrics) {
                        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                    }
                };

        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }
}