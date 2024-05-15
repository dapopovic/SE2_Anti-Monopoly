package at.aau.anti_mon.client.activities;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import at.aau.anti_mon.client.R;
import at.aau.anti_mon.client.events.DiceNumberReceivedEvent;

public class CustomViewGameField extends ConstraintLayout {
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private float scaleFactor = 1.0f;
    private float previousX;
    private float previousY;

    public CustomViewGameField(@NonNull Context context) {
        super(context);
        initView();
    }

    public CustomViewGameField(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        Log.d("Detection", "onTouch event was detected! custom View gamefield");

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                previousX = event.getX();
                previousY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getX() - previousX;
                float deltaY = event.getY() - previousY;
                scrollBy((int) -deltaX, (int) -deltaY);

                previousX = event.getX();
                previousY = event.getY();
                break;
            default:
                break;
        }
        return true;
    }

    public void initView() {
        inflate(getContext(), R.layout.custom_view_gamefield, this);
        scaleGestureDetector = new ScaleGestureDetector(this.getContext(), new ScaleListener());
        gestureDetector = new GestureDetector(this.getContext(), new GestureListener());
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 10.f));
            setScaleX(scaleFactor);
            setScaleY(scaleFactor);
            Log.d("Detection", "Scaling detected" + scaleFactor);
            return true;
        }

    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
}



