package tech.grastone.friendzoneui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private static final int STORAGE_PERMISSION_CODE = 1;
    private final String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO};
    private FrameLayout startFrame, loadingFrame, videoFrame, ownFaceFrame;
    private Button startMatchingBtn;
    private WebView videoWV;
    private TextView loadingMsgTW;
    private LottieAnimationView backToStartLAV;

    private OkHttpClient okHttpClient;
    public WebSocket webSocket;
    public String uuid = null;
    private Gson gson;
    private MessageBean currBean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        uuid = sharedPreferences.getString("UUID", "");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        startFrame = findViewById(R.id.startFrame);
        loadingFrame = findViewById(R.id.loadingFrame);
        videoFrame = findViewById(R.id.videoFrame);
        ownFaceFrame = findViewById(R.id.ownFaceFrame);

        backToStartLAV = findViewById(R.id.backToStartLAV);
//        startMatchingBtn = findViewById(R.id.startMatchingBtn);
        videoWV = findViewById(R.id.videoWV);
        loadingMsgTW = findViewById(R.id.loadingMsgTW);

        startFrame.setVisibility(View.GONE);
        loadingFrame.setVisibility(View.GONE);
        videoFrame.setVisibility(View.GONE);
        ownFaceFrame.setVisibility(View.GONE);

        webSocket = null;

        //Initial Startup Screen
        startFrame.setVisibility(View.VISIBLE);


       /* startMatchingBtn.setOnClickListener(v -> {
            if (checkPermissions()) {
                init();
            } else {
                grantPermissions();
            }
        });*/

        startFrame.setOnClickListener(v -> {
            if (checkPermissions()) {
                init();
            } else {
                grantPermissions();
            }
        });

        backToStartLAV.setOnClickListener(v -> {

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

                            startFrame.setVisibility(View.VISIBLE);
                            loadingFrame.setVisibility(View.GONE);
                            videoFrame.setVisibility(View.GONE);
                            ownFaceFrame.setVisibility(View.GONE);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();


        });
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
            System.out.println("============================>" + str);
            webSocket.send(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startFrame.setVisibility(View.GONE);
        loadingFrame.setVisibility(View.VISIBLE);
    }

    private void initializeWS() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        okHttpClient = builder.build();
        //Request request = new Request.Builder().url("ws://116.73.15.125:8080/LiveMatchingEngine/messenger/"+uuid).build();
        Request request = new Request.Builder().url("ws://" + Util.BASE_PATH + "/messenger/" + uuid).build();
        webSocket = okHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                runOnUiThread(() -> {
                    Toast.makeText(HomeActivity.this, "Connection Closed" + reason, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                try {
                    messageHandler(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                runOnUiThread(() -> {
                    Toast.makeText(HomeActivity.this, "Connected to the server", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                runOnUiThread(() -> {
                    Toast.makeText(HomeActivity.this, "Closing session =" + reason, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                runOnUiThread(() -> {
                    Toast.makeText(HomeActivity.this, "Failed session =" + t.getMessage(), Toast.LENGTH_LONG).show();
                    t.printStackTrace();
                });
            }
        });
    }

    private void messageHandler(String text) {
        //Waiting for reponse
        System.out.println("Message recieved-------------------------" + text);
        MessageBean bean = gson.fromJson(text, MessageBean.class);
        if (bean.getMessageBody() != null) {
            switch (bean.getMessageBody().getMsgType()) {
                case "STRANGER_MATCHED":
                    runOnUiThread(() -> {
                        strangerMatched(bean);
                        Toast.makeText(HomeActivity.this, "You have matched with == " + bean.getMessageBody().getMatchedWith(), Toast.LENGTH_SHORT).show();
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
    }

    private void recieveSDP(MessageBean bean) {

        System.out.println("Recieved sdp --->" + bean.getMessageBody().getMsgText());
        callJavascriptFunction("javascript:recieveSDP('" + bean.getMessageBody().getMsgText() + "')");

    }

    private void strangerMatched(MessageBean bean) {
        currBean = bean;
        System.out.println("INSIDE STRANGER MATCHED");
        startFrame.setVisibility(View.GONE);
        loadingFrame.setVisibility(View.GONE);
        videoFrame.setVisibility(View.VISIBLE);
        //ownFaceFrame.setVisibility(View.VISIBLE);
        setupWebView();

        videoWV.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                callJavascriptFunction("javascript:init(" + bean.getMessageBody().isInitiator() + ")");
            }
        });
    }


    private void setupWebView() {
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
                        startFrame.setVisibility(View.GONE);
                        loadingFrame.setVisibility(View.GONE);
                        videoFrame.setVisibility(View.GONE);
                        ownFaceFrame.setVisibility(View.GONE);

                        loadingFrame.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(() -> {
                            startMatching();
                        }, 5000);

                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, "Android");


        String filePath = "file:android_asset/call.html";


        videoWV.loadUrl(filePath);

        //callJavascriptFunction("javascript:test('a','b')");

    }

    private void callJavascriptFunction(String scriptName) {
        System.out.println("---------------------------> Calling javascript  function");
        videoWV.post(() -> {
            videoWV.evaluateJavascript(scriptName, null);
        });
    }

    private void grantPermissions() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(HomeActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                System.out.println("Granting permission for " + permission);
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission needed")
                            .setMessage("This permission is needed because of this and that")
                            .setPositiveButton("ok", (dialog, which) -> ActivityCompat.requestPermissions(HomeActivity.this,
                                    new String[]{permission}, STORAGE_PERMISSION_CODE))
                            .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                            .create().show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{permission}, STORAGE_PERMISSION_CODE);
                }
            }

        }
    }

    private boolean checkPermissions() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(HomeActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
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
        super.onStop();
        finish();
    }


}