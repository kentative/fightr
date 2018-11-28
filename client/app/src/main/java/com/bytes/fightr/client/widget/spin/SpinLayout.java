package com.bytes.fightr.client.widget.spin;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.bytes.fightr.R;

import java.security.InvalidParameterException;

/**
 * Created by Kent on 10/16/2016.
 */

public class SpinLayout extends ViewGroup {


    public enum InitialPosition {
        EAST(0), SOUTH(90), WEST(180), NORTH(270);

        private int angle;

        InitialPosition(int angle) {
            this.angle = angle;
        }

        public int getAngle() {
            return angle;
        }
    }

    private GestureDetector gestureDetector;
    private boolean[] quadrantTouched;

    // SettingsController of the ViewGroup
    private int speed;
    private float angle;
    private InitialPosition initialPosition;
    private boolean isRotating;

    // Sizes of the ViewGroup
    private int circleWidth, circleHeight;
    private float radius;

    // Tapped and selected child
    private View selectedView;

    // Child sizes
    private int maxChildWidth = 0;
    private int maxChildHeight = 0;

    // SpinLayout event listener
    private EventListener listener;

    // Rotation animator
    private ObjectAnimator animator;

    // Touch helpers
    private double touchStartAngle;
    private boolean didMove = false;


    /**
     * Required constructors
     *
     * @param context
     */
    public SpinLayout(Context context) {
        this(context, null);
    }

    public SpinLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpinLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }


    /**
     * Initializes the ViewGroup using specified attributes
     *
     * @param attrs the attributes used to modify default settings
     */
    private void init(AttributeSet attrs) {

        // Initialize default values
        speed = 25;
        angle = 90;
        initialPosition = InitialPosition.SOUTH;
        isRotating = true;
        radius = -1;
        selectedView = null;
        gestureDetector = new GestureDetector(getContext(), new SpinGestureListener());
        quadrantTouched = new boolean[]{false, false, false, false, false};

        // Override default values if available
        if (attrs != null) {
            TypedArray styledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.SpinLayout);

            speed = styledAttributes.getInt(R.styleable.SpinLayout_speed, speed);
            radius = styledAttributes.getDimension(R.styleable.SpinLayout_radius, radius);
            isRotating = styledAttributes.getBoolean(R.styleable.SpinLayout_isRotating, isRotating);

            // The angle where the first menu item will be drawn
            angle = styledAttributes.getInt(R.styleable.SpinLayout_selectedPosition, (int) angle);
            for (InitialPosition pos : InitialPosition.values()) {
                if (pos.getAngle() == angle) {
                    initialPosition = pos;
                    break;
                }
            }

            styledAttributes.recycle();

            // Needed for the ViewGroup to be drawn
            setWillNotDraw(false);
        }
    }

    /**
     * Recalculate and set the angles on all children views
     */
    private void setChildAngles() {
        int left, top, childWidth, childHeight, childCount = getChildCount();
        float angleDelay = 360.0f / childCount;
        float halfAngle = angleDelay / 2;
        float localAngle = angle;

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            if (localAngle > 360) {
                localAngle -= 360;
            } else if (localAngle < 0) {
                localAngle += 360;
            }

            childWidth = child.getMeasuredWidth();
            childHeight = child.getMeasuredHeight();
            left = Math
                    .round((float) (((circleWidth / 2.0) - childWidth / 2.0) + radius
                            * Math.cos(Math.toRadians(localAngle))));
            top = Math
                    .round((float) (((circleHeight / 2.0) - childHeight / 2.0) + radius
                            * Math.sin(Math.toRadians(localAngle))));

            child.setTag(localAngle);

            float distance = Math.abs(localAngle - initialPosition.getAngle());
            boolean isFirstItem = distance <= halfAngle || distance >= (360 - halfAngle);
            if (isFirstItem && selectedView != child) {
                selectedView = child;
                if (listener != null && isRotating) {
                    listener.onItemSelected(child);
                }
            }

            child.layout(left, top, left + childWidth, top + childHeight);
            localAngle += angleDelay;
        }
    }

    /**
     * @param endDegree
     * @param duration
     */
    private void animateTo(float endDegree, long duration) {

        if (animator != null && animator.isRunning() || Math.abs(angle - endDegree) < 1) {
            return;
        }

        animator = ObjectAnimator.ofFloat(SpinLayout.this, "angle", angle, endDegree);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            private boolean wasCancelled = false;

            @Override
            public void onAnimationStart(Animator animation) {
                if (listener != null) {
                    View view = getSelectedItem();
                    listener.onRotationStart(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (wasCancelled) {
                    return;
                }

                if (listener != null) {
                    View view = getSelectedItem();
                    listener.onRotationFinished(view);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                wasCancelled = true;
            }
        });
        animator.start();
    }

    private void stopAnimation() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            animator = null;
        }
    }

    /**
     * Rotates the given view to the initial position
     *
     * @param view the view to be rotated
     */
    public void rotateViewToInitialPosition(View view) {
        if (isRotating) {
            float viewAngle = view.getTag() != null ? (Float) view.getTag() : 0;
            float destAngle = initialPosition.getAngle() - viewAngle;

            if (destAngle < 0) {
                destAngle += 360;
            }

            if (destAngle > 180) {
                destAngle = -1 * (360 - destAngle);
            }

            animateTo(angle + destAngle, 7500L / speed);
        }
    }

    private void rotateButtons(float degrees) {
        angle += degrees;
        setChildAngles();
    }

    private void updateAngle() {
        // Update the position of the views, so we know which is the selected
        setChildAngles();
        rotateViewToInitialPosition(selectedView);
    }

    /**
     * Returns the currently selected menu, more formally, the view closest to
     * the initial position
     *
     * @return the view which is currently the closest to the initial position
     */
    public View getSelectedItem() {
        if (selectedView == null) {
            selectedView = getChildAt(0);
        }
        return selectedView;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle % 360;
        setChildAngles();
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        if (speed <= 0) {
            throw new InvalidParameterException("Speed must be a positive integer number");
        }
        this.speed = speed;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        if (radius < 0) {
            throw new InvalidParameterException("Radius cannot be negative");
        }
        this.radius = radius;
        setChildAngles();
    }

    public boolean isRotating() {
        return isRotating;
    }

    public void setRotating(boolean isRotating) {
        this.isRotating = isRotating;
    }

    public InitialPosition getFirstChildPosition() {
        return initialPosition;
    }

    public void setInitialPosition(InitialPosition initialPosition) {
        this.initialPosition = initialPosition;
        if (selectedView != null) {
            if (isRotating) {
                rotateViewToInitialPosition(selectedView);
            } else {
                setAngle(initialPosition.getAngle());
            }
        }
    }

    public void setListener(EventListener listener) {
        this.listener = listener;
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        updateAngle();
    }

    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
        updateAngle();
    }

    @Override
    public void removeViews(int start, int count) {
        super.removeViews(start, count);
        updateAngle();
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        super.addView(child, index, params);
        updateAngle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Measure child views first
        maxChildWidth = 0;
        maxChildHeight = 0;

        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            measureChild(child, childWidthMeasureSpec, childHeightMeasureSpec);

            maxChildWidth = Math.max(maxChildWidth, child.getMeasuredWidth());
            maxChildHeight = Math.max(maxChildHeight, child.getMeasuredHeight());
        }

        // Then decide what size we want to be
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(widthSize, heightSize);
        } else {
            //Be whatever you want
            width = maxChildWidth * 3;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(heightSize, widthSize);
        } else {
            //Be whatever you want
            height = maxChildHeight * 3;
        }

        setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int layoutWidth = r - l;
        int layoutHeight = b - t;

        if (radius < 0) {
            radius = (layoutWidth <= layoutHeight)
                    ? layoutWidth / 3
                    : layoutHeight / 3;
        }

        circleHeight = getHeight();
        circleWidth = getWidth();
        setChildAngles();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            gestureDetector.onTouchEvent(event);
            if (isRotating) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // reset the touched quadrants
                        for (int i = 0; i < quadrantTouched.length; i++) {
                            quadrantTouched[i] = false;
                        }

                        stopAnimation();
                        touchStartAngle = getPositionAngle(event.getX(),
                                event.getY());
                        didMove = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        double currentAngle = getPositionAngle(event.getX(),
                                event.getY());
                        rotateButtons((float) (touchStartAngle - currentAngle));
                        touchStartAngle = currentAngle;
                        didMove = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (didMove) {
                            rotateViewToInitialPosition(selectedView);
                        }
                        break;
                    default:
                        break;
                }
            }

            // set the touched quadrant to true
            quadrantTouched[getPositionQuadrant(event.getX()
                    - (circleWidth / 2), circleHeight - event.getY()
                    - (circleHeight / 2))] = true;
            return true;
        }
        return false;
    }

    /**
     * @return The angle of the unit bg_circle with the image views center
     */
    private double getPositionAngle(double xTouch, double yTouch) {
        double x = xTouch - (circleWidth / 2d);
        double y = circleHeight - yTouch - (circleHeight / 2d);

        switch (getPositionQuadrant(x, y)) {
            case 1:
                return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 2:
            case 3:
                return 180 - (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
            case 4:
                return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            default:
                // ignore, does not happen
                return 0;
        }
    }

    /**
     * @return The quadrant of the position
     */
    private static int getPositionQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }

    public interface EventListener {

        void onItemClick(View view);

        void onItemSelected(View view);

        void onCenterClick();

        void onRotationFinished(View view);

        void onRotationStart(View view);
    }

    private class SpinGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!isRotating) {
                return false;
            }
            // get the quadrant of the start and the end of the fling
            int q1 = getPositionQuadrant(e1.getX() - (circleWidth / 2),
                    circleHeight - e1.getY() - (circleHeight / 2));
            int q2 = getPositionQuadrant(e2.getX() - (circleWidth / 2),
                    circleHeight - e2.getY() - (circleHeight / 2));

            if ((q1 == 2 && q2 == 2 && Math.abs(velocityX) < Math
                    .abs(velocityY))
                    || (q1 == 3 && q2 == 3)
                    || (q1 == 1 && q2 == 3)
                    || (q1 == 4 && q2 == 4 && Math.abs(velocityX) > Math
                    .abs(velocityY))
                    || ((q1 == 2 && q2 == 3) || (q1 == 3 && q2 == 2))
                    || ((q1 == 3 && q2 == 4) || (q1 == 4 && q2 == 3))
                    || (q1 == 2 && q2 == 4 && quadrantTouched[3])
                    || (q1 == 4 && q2 == 2 && quadrantTouched[3])) {
                // the inverted rotations
                animateTo(
                        getCenteredAngle(angle - (velocityX + velocityY) / 25),
                        25000L / speed);
            } else {
                // the normal rotation
                animateTo(
                        getCenteredAngle(angle + (velocityX + velocityY) / 25),
                        25000L / speed);
            }

            return true;
        }

        private float getCenteredAngle(float angle) {
            if (getChildCount() == 0) {
                // Prevent divide by zero
                return angle;
            }

            float angleDelay = 360 / getChildCount();
            float localAngle = angle % 360;

            if (localAngle < 0) {
                localAngle = 360 + localAngle;
            }

            for (float i = initialPosition.getAngle(); i < initialPosition.getAngle() + 360; i += angleDelay) {
                float locI = i % 360;
                float diff = localAngle - locI;
                if (Math.abs(diff) < angleDelay) {
                    angle -= diff;
                    break;
                }
            }

            return angle;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View tappedView = null;
            int tappedViewsPosition = pointToChildPosition(e.getX(), e.getY());
            if (tappedViewsPosition >= 0) {
                tappedView = getChildAt(tappedViewsPosition);
                tappedView.setPressed(true);
            } else {
                // Determine if it was a center click
                float centerX = circleWidth / 2F;
                float centerY = circleHeight / 2F;

                if (listener != null
                        && e.getX() < centerX + radius - (maxChildWidth / 2)
                        && e.getX() > centerX - radius + (maxChildWidth / 2)
                        && e.getY() < centerY + radius - (maxChildHeight / 2)
                        && e.getY() > centerY - radius + (maxChildHeight / 2)) {
                    listener.onCenterClick();
                    return true;
                }
            }

            if (tappedView != null) {
                if (selectedView == tappedView) {
                    if (listener != null) {
                        listener.onItemClick(tappedView);
                    }
                } else {
                    rotateViewToInitialPosition(tappedView);
                    if (!isRotating) {
                        if (listener != null) {
                            listener.onItemSelected(tappedView);
                            listener.onItemClick(tappedView);
                        }
                    }
                }
                return true;
            }
            return super.onSingleTapUp(e);
        }

        private int pointToChildPosition(float x, float y) {
            for (int i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                if (view.getLeft() < x && view.getRight() > x
                        & view.getTop() < y && view.getBottom() > y) {
                    return i;
                }
            }
            return -1;
        }
    }
}
