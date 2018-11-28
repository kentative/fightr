package com.bytes.fightr.client.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

/**
 * Created by Kent on 10/27/2015.
 */
public class GraphicUtils {


    /**
     * Changes the color of the specified bitmap.
     * @param source the original bitmap
     * @param color the new color
     * @return the original bitmap with the new color
     */
    public static Bitmap changeColor(Bitmap source, int color) {

        // Create a mutable bitmap
        Bitmap dest = Bitmap.createBitmap(source, 0, 0, source.getWidth() - 1, source.getHeight() - 1);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));

        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(dest, 0, 0, paint);
        return dest;
    }
}
