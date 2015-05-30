package com.example.cpy.draghelpertest;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by cpy on 15-5-23.
 *
 * @author cpy
 */

/**
 * MyDragView
 * <br/>
 * Usage:<br/>
 * 1. Add a view(only one) that you want to drag from the bottom in this widget in layout XML<br/>
 * 2. Add header through {@link #setHeader(int)} or {@link #setHeader(View)} which will be display
 * after calling {@link #show()}<br/>
 * 3. Set the middle height of the view through {@link #setMiddleDisHeight(int)}<br/>
 * 4. Call {@link #setOnPositionChangedListener(OnPositionChangedListener)}
 * and {@link #setOnStopLevelChangedListener(OnStopLevelChangedListener)} to customize the action of your view
 */
public class MyDragView extends LinearLayout {

    public static final int STOP_MIN_HEIGHT = 0;
    public static final int STOP_MID_HEIGHT = 1;
    public static final int STOP_MAX_HEIGHT = 2;

    private ViewDragHelper viewDragHelper;
    private View childView;
    private int childTop = -1;
    private int dragRangeMin = 100;
    private Button bt;
    private int middleDisHeight = 300;
    private boolean isClick = true;
    private View header;
    private boolean isSetMinDisHeight = false;
    private OnPositionChangedListener onPositionChangedListener = null;
    private boolean isFirstMove = false;
    private int dragRange = 0;
    private OnStopLevelChangedListener onStopLevelChangedListener = null;

    private boolean isTouched = false;

    public MyDragView(Context context, AttributeSet attrs) {
        super(context, attrs);
        viewDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("test", "onClick");

            }
        });
        setBackgroundColor(0x00000000);
        setOrientation(LinearLayout.VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        childView = getChildAt(0);
    }

    class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(View view, int i) {
            boolean result = view == childView || childView.findViewById(view.getId()) != null;
            Log.v("test", "tryCaptureView:" + result);
            return result;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - dragRangeMin;
            final int newTop = Math.min(Math.max(top, topBound), bottomBound);
            Log.v("test", "newTop:" + newTop);
            return newTop;
        }


        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            childTop = top;
            requestLayout();

            if(onPositionChangedListener != null) {
                onPositionChangedListener.onPositionChanged(top);
            }
            if(onStopLevelChangedListener != null) {
                Log.v("MyDragView", "state:" + viewDragHelper.getViewDragState() + "top:" + top);
                if(!isTouched) {
                    Log.v("MyDragView", "enter idle");
                    if(top == getHeight() - dragRangeMin) {
                        onStopLevelChangedListener.onStopLevelChanged(STOP_MIN_HEIGHT);
                    }
                    else if(top == getHeight() - middleDisHeight) {
                        onStopLevelChangedListener.onStopLevelChanged(STOP_MID_HEIGHT);
                    }
                    else if(top == getHeight()) {
                        onStopLevelChangedListener.onStopLevelChanged(STOP_MAX_HEIGHT);
                    }

                }
            }
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return dragRange;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            Log.v("test", "onReleased " + xvel + "," + yvel);
            if (yvel < 0 || (yvel == 0 && childTop <= (2*getHeight() - middleDisHeight - dragRangeMin) / 2)) {
                int top = 0;
                if(childTop < getHeight() - middleDisHeight)
                    top = getPaddingTop();
                else
                    top = getHeight() - middleDisHeight;
                boolean scva = viewDragHelper.smoothSlideViewTo(releasedChild, releasedChild.getLeft(), top);
                Log.v("test", "settle to top " + top + " " + scva);
                invalidate();
            }
            else if(yvel > 0 || (yvel == 0 && childTop > (getHeight() - middleDisHeight) / 2)) {
                int top = getHeight() - dragRangeMin;
                boolean scva = viewDragHelper.smoothSlideViewTo(releasedChild, releasedChild.getLeft(), top);
                Log.v("test", "settle to top " + top + " " + scva);
                invalidate();
            }

        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            Log.v("test", "state:" + state);
        }
    }

    @Override
    public void computeScroll() {
        Log.v("test", "computeScroll");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean cs = viewDragHelper.continueSettling(true);
        Log.v("test", "continueSettleing " + cs);
        if(cs) {
            cs = viewDragHelper.continueSettling(true);
            Log.v("test", "continueSettleing " + cs);
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        dragRange = getHeight();
        if(childTop == -1)
            childTop = getHeight();
        Log.v("test", "childTop:" + childTop);
        int width = childView.getMeasuredWidth();
        int height = childView.getMeasuredHeight();
        childView.layout(0, childTop, width, childTop + height);
        Log.v("MyDragView", "onLayout header.height" + header.getHeight());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        Log.v("test", "onInterceptTouch:" + action);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            viewDragHelper.cancel();
            return false;
        }
        boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
        Log.v("test", "onInterceptTouch:" + result);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        Log.v("test", "onTouchEVent:" + action);
        if(action == MotionEvent.ACTION_DOWN) {
            isClick = true;
            isTouched = true;
        }
        else if(action == MotionEvent.ACTION_UP) {
            isTouched = false;
            if(isClick) {
                Log.v("test", "onClick");
                if(header != null && viewDragHelper.getViewDragState() != ViewDragHelper.STATE_SETTLING) {
                    if(isViewHit(header, (int)event.getX(), (int)event.getY())) {
                        if(childTop == getHeight() - dragRangeMin) {
                            viewDragHelper.smoothSlideViewTo(childView, childView.getLeft(), getHeight() - middleDisHeight);
                            invalidate();
                        }
                        else if(childTop == getHeight() - middleDisHeight) {
                            viewDragHelper.smoothSlideViewTo(childView, childView.getLeft(), getHeight() - dragRangeMin);
                            invalidate();
                        }
                    }

                }
            }
            isFirstMove = true;
        }
        else if(action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_CANCEL) {
            isTouched = true;
            isClick = false;
            if(action == MotionEvent.ACTION_MOVE) {
                if(isFirstMove) {
                    event.setAction(MotionEvent.ACTION_DOWN);
                    isFirstMove = false;
                }
            }
            else {
                isFirstMove = true;
            }
        }
        try {
            viewDragHelper.processTouchEvent(event);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return isViewHit(childView, (int)event.getX(), (int)event.getY());
    }

    /**
     * detect whether a view is hitted
     * @param view view to be detected
     * @param x touched x value on screen
     * @param y touched y value on screen
     * @return
     */
    private boolean isViewHit(View view, int x, int y) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0] && screenX < viewLocation[0] + view.getWidth() &&
                screenY >= viewLocation[1] && screenY < viewLocation[1] + view.getHeight();
    }

    /**
     * show the drag view
     */
    public void show() {
        //On the condition that after doing something change the header's height
        // and immediately invoke show(), the header's height cannot be updated in time.
        //To avoid this condition, we post the show action to the main thread's queue
        post(new Runnable() {
            @Override
            public void run() {
                if (isSetMinDisHeight || header == null)
                    viewDragHelper.smoothSlideViewTo(childView, 0, getHeight() - dragRangeMin);
                else {
                    dragRangeMin = header.getHeight();
                    Log.v("MyDragView", "show() header.height:" + dragRangeMin + ", DragView.height:" + getHeight());
                    viewDragHelper.smoothSlideViewTo(childView, 0, getHeight() - dragRangeMin);
                }
                invalidate();
            }
        });

    }

    /**
     * hide the drag view
     */
    public void hide() {
        viewDragHelper.smoothSlideViewTo(childView, 0, getHeight());
        invalidate();
    }

    /**
     * Set the minimun displayed height of the drag view
     * @param h minimun displayed height
     */
    public void setMinDisHeight(int h) {
        dragRangeMin = h;
        isSetMinDisHeight = true;
    }

    /**
     * The header view must be the child of this MyDragView
     * @param id
     */
    public void setHeader(int id) {
        header = findViewById(id);
        if(header == null)
            throw new IllegalArgumentException("Child view of the id doesn't exist");
    }

    /**
     * Set the header of the drag view. <br/>
     * After this is setted, minimun displayed height will be set to the height of the header.
     * So, {@link #setMinDisHeight(int)} doesn't work
     * Note: The header view must be the child of this MyDragView
     * @param header
     */
    public void setHeader(View header) {
        header = this.header;
    }

    /**
     * set the middle displayed height of the drag view
     * @param h middle displayed height
     */
    public void setMiddleDisHeight(int h) {
        middleDisHeight = h;
    }

    /**
     * Ingerface definition for callback to be invoked when the drag view's position is changed
     */
    public interface OnPositionChangedListener {
        /**
         * Called when drag view's position is changed.
         * @param top top value of drag view
         */
        public void onPositionChanged(int top);
    }

    /**
     * register a callback to be invoked when the drag view's position is changed
     * @param l
     */
    public void setOnPositionChangedListener(OnPositionChangedListener l) {
        onPositionChangedListener = l;
    }

    /**
     * Interface definition for a callback to be invoked when the drag view is stop on a specific
     * height level(max {@link #STOP_MAX_HEIGHT}, middle {@link #STOP_MIN_HEIGHT}, min {@link #STOP_MID_HEIGHT})
     * without finger touched on the screen. <br/>
     * Note: stop means the DragView stop at a position at the moment without dragging.
     * And the height level of the parameter may be the same as the one in the previous invoked.
     */
    public interface  OnStopLevelChangedListener {
        /**
         * Called when the drag view is stop on a specific
         * height level
         *
         * @param heightLevel height level(max {@link #STOP_MAX_HEIGHT}, middle {@link #STOP_MIN_HEIGHT},
         *                    min {@link #STOP_MID_HEIGHT})
         */
        public void onStopLevelChanged(int heightLevel);
    }

    /**
     * Register a callback to be invoked when the drag view is stop on a specific height level(
     * max {@link #STOP_MAX_HEIGHT}, middle {@link #STOP_MIN_HEIGHT}, min {@link #STOP_MID_HEIGHT})
     * without finger touched on the screen.
     * @param l The callback that will run
     */
    public void setOnStopLevelChangedListener(OnStopLevelChangedListener l) {
        onStopLevelChangedListener = l;
    }
}
