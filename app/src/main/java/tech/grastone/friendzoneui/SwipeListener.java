//package tech.grastone.friendzoneui;
//
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.Toast;
//
//public class SwipeListener implements View.OnTouchListener {
//    GestureDetector gestureDetector;
//
//
//    SwipeListener(View view){
//        int threshold=100;
//        int velocityThreshold =100;
//
//
//        GestureDetector.SimpleOnGestureListener listener =
//                new GestureDetector.SimpleOnGestureListener(){
//
//                    @Override
//                    public boolean onDown(MotionEvent e) {
//
//                        return  true;
//                    }
//
//                    @Override
//                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                        float xDiff = e2.getX()- e1.getX();
//                        float yDiff = e2.getY() - e1.getY();
//
//                        try {
//                            if(Math.abs(xDiff)>Math.abs(yDiff)){
//                                if(Math.abs(xDiff)>threshold && Math.abs(velocityX) > velocityThreshold){
//
//                                }
//
//                            }
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                };
//    }
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        return false;
//    }
//}
