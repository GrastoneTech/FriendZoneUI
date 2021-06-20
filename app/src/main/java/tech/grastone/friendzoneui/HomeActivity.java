package tech.grastone.friendzoneui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Slide;
import androidx.transition.TransitionManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import tech.grastone.friendzoneui.util.JSInterface;
import tech.grastone.friendzoneui.util.MessageBean;
import tech.grastone.friendzoneui.util.RequestBody;
import tech.grastone.friendzoneui.util.Util;

public class HomeActivity extends AppCompatActivity {

    private FrameLayout loadingFrame, videoFrame, ownFaceFrame;
    private WebView videoWV;
    private TextView loadingMsgTW;
    private LottieAnimationView backToStartLAV;

    private OkHttpClient okHttpClient;
    public WebSocket webSocket;
    public String uuid = null;
    private Gson gson;
    private MessageBean currBean = null;
    private SwipeListener swipeListener;
    private TimerTextHelper timerTextHelper = null;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        uuid = sharedPreferences.getString("UUID", "");
        setContentView(R.layout.activity_home);
        loadingFrame = findViewById(R.id.loadingFrame);
        videoFrame = findViewById(R.id.videoFrame);
        ownFaceFrame = findViewById(R.id.ownFaceFrame);

        backToStartLAV = findViewById(R.id.backToStartLAV);
        videoWV = findViewById(R.id.videoWV);
        loadingMsgTW = findViewById(R.id.loadingMsgTW);
        loadingFrame.setVisibility(View.VISIBLE);
        videoFrame.setVisibility(View.GONE);
        ownFaceFrame.setVisibility(View.GONE);
        webSocket = null;

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.homeBannerAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        runOnUiThread(() -> {
            new Handler().postDelayed(() -> init(), 5000);

        });

        backToStart();

        super.onCreate(savedInstanceState);
    }

    private void backToStart() {
        backToStartLAV.setOnClickListener(v -> {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you really want to stop matching?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                webSocket.close(1000, "GOOD BYE");
                                webSocket = null;

                                if (videoWV != null) {
                                    videoWV.clearCache(true);
                                    videoWV.clearHistory();
                                    videoWV.onPause();
                                    videoWV.removeAllViews();
                                    videoWV.destroyDrawingCache();
                                    videoWV.pauseTimers();
                                }

//                            startFrame.setVisibility(View.VISIBLE);
                                loadingFrame.setVisibility(View.GONE);
                                mAdView.setVisibility(View.GONE);
                                videoFrame.setVisibility(View.GONE);
                                ownFaceFrame.setVisibility(View.GONE);

                                startActivity(new Intent(HomeActivity.this, StartActivity.class));
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                finish();
                                finishAndRemoveTask();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } catch (Exception e) {
                onException();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("Example", "onResume");
    }

    private void init() {
        try {
            initializeWS();
            startMatching();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startMatching() {
        //Start Matching
        try {
            RequestBody requestBody = new RequestBody();
            requestBody.setId(Integer.parseInt(uuid));
            requestBody.setGender((byte) 2);
            requestBody.setIntrestedGender((byte) 2);
            requestBody.setMsgType("START_MATCHING");
            requestBody.setMatchingPresense(true);
            requestBody.setKeywords(new String[]{});
            requestBody.setMsgText("START_MATCHING");
            requestBody.setServiceId(1);
            requestBody.setServiceType("START_MATCHING");
            MessageBean bean = new MessageBean("" + uuid, "SYSTEM", requestBody);

            try {
                String str = new Gson().toJson(bean);
//                System.out.println("============================>" + str);
                webSocket.send(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadingFrame.setVisibility(View.VISIBLE);
            mAdView.setVisibility(View.VISIBLE);
        } catch (Exception ex) {
            onException();
            ex.printStackTrace();
        }
    }

    private void initializeWS() {
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            okHttpClient = builder.build();
            //Request request = new Request.Builder().url("ws://116.73.15.125:8080/LiveMatchingEngine/messenger/"+uuid).build();
            Request request = new Request.Builder().url("ws://" + Util.BASE_PATH + "/messenger/" + uuid).build();
            webSocket = okHttpClient.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                    runOnUiThread(() -> {
//                        try {
//                            Toast.makeText(HomeActivity.this, "Connection Closed" + reason, Toast.LENGTH_SHORT).show();
//                        }catch (Exception e){
                        onException("Connection Closed");
//                        }
                    });
                }

                @Override
                public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                    try {
                        messageHandler(text);
                    } catch (Exception e) {
                        e.printStackTrace();
                        onException();
                    }
                }

                @Override
                public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
//                    runOnUiThread(() -> {
//                        Toast.makeText(HomeActivity.this, "Connected to the server", Toast.LENGTH_SHORT).show();
//                    });
                }

                @Override
                public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
//                    runOnUiThread(() -> {
//                        Toast.makeText(HomeActivity.this, "Closing session =" + reason, Toast.LENGTH_SHORT).show();
//                    });
                }

                @Override
                public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
//                    runOnUiThread(() -> {
//                        Toast.makeText(HomeActivity.this, "Failed session =" + t.getMessage(), Toast.LENGTH_LONG).show();
//                        t.printStackTrace();
//                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            onException();
        }
    }

    private void messageHandler(String text) {
        //Waiting for reponse
        //System.out.println("Message recieved-------------------------" + text);
        try {
            MessageBean bean = gson.fromJson(text, MessageBean.class);
            if (bean.getMessageBody() != null) {
                switch (bean.getMessageBody().getMsgType()) {
                    case "STRANGER_MATCHED":
                        runOnUiThread(() -> {
                            strangerMatched(bean);
                            //Toast.makeText(HomeActivity.this, "You have matched with == " + bean.getMessageBody().getMatchedWith(), Toast.LENGTH_SHORT).show();
                        });
                        break;
                    case "SHARING_SDP":
                        runOnUiThread(() -> {
                            recieveSDP(bean);
                            //Toast.makeText(HomeActivity.this, "You have matched with == " + bean.getMessageBody().getMatchedWith(), Toast.LENGTH_SHORT).show();
                        });
                        break;
                    default:
                        System.out.println("Unable to recognised text==" + text);
                }
            }
        } catch (Exception e) {
            onException();
        }
    }

    private void recieveSDP(MessageBean bean) {
        try {
            //System.out.println("Recieved sdp --->" + bean.getMessageBody().getMsgText());
            callJavascriptFunction("javascript:recieveSDP('" + bean.getMessageBody().getMsgText() + "')");
        } catch (Exception e) {
            e.printStackTrace();
            onException();

        }


    }


    private void strangerMatched(MessageBean bean) {
        try {
            currBean = bean;
            // System.out.println("INSIDE STRANGER MATCHED");
//        startFrame.setVisibility(View.GONE);
            loadingFrame.setVisibility(View.GONE);
            mAdView.setVisibility(View.GONE);
            TransitionManager.beginDelayedTransition(videoFrame, new Slide(Gravity.RIGHT));
            videoFrame.setVisibility(View.VISIBLE);

            //ownFaceFrame.setVisibility(View.VISIBLE);
            setupWebView();


            videoWV.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    callJavascriptFunction("javascript:init(" + bean.getMessageBody().isInitiator() + ")");
                }
            });
            findViewById(R.id.swipeupView).setVisibility(View.GONE);
            findViewById(R.id.swipeUpToSkipTW).setVisibility(View.GONE);

            new Handler().postDelayed(() -> {
                swipeListener = new SwipeListener(videoWV);
                findViewById(R.id.swipeupView).setVisibility(View.VISIBLE);
                findViewById(R.id.swipeUpToSkipTW).setVisibility(View.VISIBLE);
            }, 5000);

            timerTextHelper = new TimerTextHelper(findViewById(R.id.videoCallTimer));
            timerTextHelper.start();
        } catch (Exception e) {
            e.printStackTrace();
            onException();
        }


    }


    private void setupWebView() {
        System.out.println("Setup WV calling  TAG");
        videoWV.getSettings().setJavaScriptEnabled(true);
        videoWV.getSettings().setMediaPlaybackRequiresUserGesture(false);
        videoWV.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
            }


        });

        videoWV.addJavascriptInterface(new JSInterface() {
            @Override
            @android.webkit.JavascriptInterface()
            public void sendSDP(String sdpData) {
                RequestBody sdpBody = new RequestBody();
                sdpBody.setMsgType("SHARING_SDP");
                sdpBody.setMsgText(sdpData);
                sdpBody.setServiceId(1);
                sdpBody.setServiceType("SHARING_SDP");
                MessageBean bean = new MessageBean("" + uuid, "" + currBean.getMessageBody().getMatchedWith(), sdpBody);

                try {
                    String str = new Gson().toJson(bean);
                    System.out.println("Sending ============================>" + str);
                    webSocket.send(str);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            @android.webkit.JavascriptInterface()
            public void next() {
                try {
                    runOnUiThread(() -> {
                        Toast.makeText(HomeActivity.this, "Loading next value", Toast.LENGTH_SHORT).show();
//                        startFrame.setVisibility(View.GONE);
                        loadingFrame.setVisibility(View.GONE);
                        videoFrame.setVisibility(View.GONE);
                        ownFaceFrame.setVisibility(View.GONE);
                        TransitionManager.beginDelayedTransition(loadingFrame, new Slide(Gravity.BOTTOM));
                        loadingFrame.setVisibility(View.VISIBLE);
                        mAdView.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(() -> {
                            startMatching();
                        }, 5000);

                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            @android.webkit.JavascriptInterface()
            public void onException() {
                onException();
            }
        }, "Android");
        String filePath = "file:android_asset/call.html?rand=" + new Random().nextInt();
        videoWV.loadUrl(filePath);
        //callJavascriptFunction("javascript:test('a','b')");

    }

    private void callJavascriptFunction(String scriptName) {
        System.out.println("---------------------------> Calling javascript  function");
        videoWV.post(() -> {
            videoWV.evaluateJavascript(scriptName, null);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoWV.clearCache(true);
        videoWV.clearHistory();
        videoWV.onPause();
        videoWV.removeAllViews();
        videoWV.destroyDrawingCache();
        videoWV.pauseTimers();
        videoWV = null;
//        finish();
    }

    private void onException() {
        Toast.makeText(HomeActivity.this, "Something went wrong with our end. Please try again later", Toast.LENGTH_LONG).show();
    }

    private void onException(String msg) {
        Toast.makeText(HomeActivity.this, "" + msg, Toast.LENGTH_LONG).show();
    }

    public class SwipeListener implements View.OnTouchListener {
        GestureDetector gestureDetector;


        SwipeListener(View view) {
            System.out.println("Swipe Is INTIALIZEEED11111111111111111111111111");
            int threshold = 300;
            int velocityThreshold = 300;


            GestureDetector.SimpleOnGestureListener listener =
                    new GestureDetector.SimpleOnGestureListener() {

                        @Override
                        public boolean onDown(MotionEvent e) {

                            return true;
                        }

                        @Override
                        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                            float xDiff = e2.getX() - e1.getX();
                            float yDiff = e2.getY() - e1.getY();

                            try {
                                if (Math.abs(xDiff) > Math.abs(yDiff)) {
                                    if (Math.abs(xDiff) > threshold && Math.abs(velocityX) > velocityThreshold) {
                                        if (xDiff > 0) {
                                            Toast.makeText(HomeActivity.this, "R", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(HomeActivity.this, "L", Toast.LENGTH_LONG).show();
                                        }

                                        return true;

                                    }

                                } else {
                                    if (Math.abs(yDiff) > threshold && Math.abs(velocityY) > velocityThreshold) {
                                        if (yDiff > 0) {
                                            Toast.makeText(HomeActivity.this, "D", Toast.LENGTH_LONG).show();
                                        } else {
                                            callJavascriptFunction("close()");
                                            runOnUiThread(() -> {
                                                Toast.makeText(HomeActivity.this, "Loading next value", Toast.LENGTH_SHORT).show();
//                        startFrame.setVisibility(View.GONE);
                                                loadingFrame.setVisibility(View.GONE);
                                                videoFrame.setVisibility(View.GONE);
                                                ownFaceFrame.setVisibility(View.GONE);
                                                TransitionManager.beginDelayedTransition(loadingFrame, new Slide(Gravity.BOTTOM));
                                                loadingFrame.setVisibility(View.VISIBLE);
                                                mAdView.setVisibility(View.VISIBLE);
                                                videoWV.setOnTouchListener(null);
                                                timerTextHelper.stop();

                                                timerTextHelper = null;

                                                new Handler().postDelayed(() -> {
                                                    startMatching();
                                                }, 5000);

                                            });

                                            Toast.makeText(HomeActivity.this, "U", Toast.LENGTH_LONG).show();
                                        }

                                        return true;

                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    };


            gestureDetector = new GestureDetector(listener);
            view.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }
    }


}