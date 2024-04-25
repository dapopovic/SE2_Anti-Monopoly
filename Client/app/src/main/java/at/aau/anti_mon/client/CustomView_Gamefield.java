package at.aau.anti_mon.client;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class CustomView_Gamefield extends ConstraintLayout {
        private ScaleGestureDetector scaleGestureDetector;
    private float FACTOR = 1.0f;

    public CustomView_Gamefield(@NonNull Context context) {
        super(context);
        initView();
    }

    public CustomView_Gamefield(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        Log.d("Detection", "onTouch event was detected! custom View gamefield");
        return true;
    }

    public void initView() {
        inflate(getContext(), R.layout.custom_view_gamefield, this);
        scaleGestureDetector = new ScaleGestureDetector(this.getContext(), new ScaleListener());
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            FACTOR *= detector.getScaleFactor();
            FACTOR = Math.max(0.1f, Math.min(FACTOR, 10.f));
            setScaleX(FACTOR);
            setScaleY(FACTOR);
            Log.d("Detection", "Scaling detected" + FACTOR);
            return true;
        }

    }


}



