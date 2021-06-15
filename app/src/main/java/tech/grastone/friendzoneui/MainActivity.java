package tech.grastone.friendzoneui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tech.grastone.friendzoneui.util.Util;


public class MainActivity extends AppCompatActivity {


    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private String serverURL;
    private TextView splashName;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        splashName = findViewById(R.id.frienzoneTW);
        animationView = findViewById(R.id.animationView);

        splashName.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));


        // loadParams();
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String uuid = sharedPreferences.getString("UUID", "");
        OkHttpClient client = new OkHttpClient();


        if (uuid.equals("")) {
            new Thread(() -> {
                //Request request = new Request.Builder().url("http://116.73.15.125:8080/LiveMatchingEngine/GetUniqueId").build();
                Request request = new Request.Builder().url("http://" + Util.BASE_PATH + "/GetUniqueId").build();
                String uniqueId = "";
                try (Response response = client.newCall(request).execute()) {
                    uniqueId = response.body().string();
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("UUID", uniqueId);
                    myEdit.commit();
                    System.out.println("--------------------->uniqueId == " + uniqueId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            System.out.println("--------------------->uuid already exist == " + uuid);

        }

        new Handler().postDelayed(() -> {
            Intent startActivityIntent = new Intent(MainActivity.this, HomeActivity.class);
            startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(startActivityIntent);
            finish();
        }, 3400);

    }


}