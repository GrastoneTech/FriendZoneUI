package tech.grastone.friendzoneui;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

public class DetectSwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int MIN_SWIPE_DISTANCE_X = 100;
    private static final int MIN_SWIPE_DISTANCE_Y = 100;

    private static final int MAX_SWIPE_DISTANCE_X = 1000;
    private static final int MAX_SWIPE_DISTANCE_Y = 1000;

    private HomeActivity homeActivity = null;

    public DetectSwipeGestureListener(HomeActivity pHomeActivity) {
        this.homeActivity = pHomeActivity;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float deltaX = e1.getX() - e2.getX();
        float deltaY = e1.getY() - e2.getY();

        float deltaXAbs = Math.abs(deltaX);
        float deltaYAbs = Math.abs(deltaY);

        if (deltaYAbs >= MIN_SWIPE_DISTANCE_Y && deltaYAbs <= MAX_SWIPE_DISTANCE_X) {

            if (deltaY > 0) {


                Toast.makeText(homeActivity, "SWIPE UP ", Toast.LENGTH_LONG).show();
            }
        }
        return true;
    }
}
