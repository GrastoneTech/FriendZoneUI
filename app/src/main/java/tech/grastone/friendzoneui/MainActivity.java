package tech.grastone.friendzoneui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {


    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private String serverURL;
    private TextView splashName;
    private LottieAnimationView animationView;
    private SharedPreferences sharedPreferences = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        splashName = findViewById(R.id.frienzoneTW);
        animationView = findViewById(R.id.animationView);

        splashName.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        basicReadWrite();

        // loadParams();
        //startup();

    }

    private void startup() {

        String uuid = sharedPreferences.getString("UUID", "");
        String serverHost = sharedPreferences.getString("serverHost", "");
        String serverName = sharedPreferences.getString("serverName", "");
        String serverBase = sharedPreferences.getString("serverBase", "");
        String serverPort = sharedPreferences.getString("serverPort", "");
        String serverProtocol = sharedPreferences.getString("serverProtocol", "");
        String wsserverProtocol = sharedPreferences.getString("wsserverProtocol", "");
        OkHttpClient client = new OkHttpClient();


        if (uuid.equals("")) {
            new Thread(() -> {
                //Request request = new Request.Builder().url("http://116.73.15.125:8080/LiveMatchingEngine/GetUniqueId").build();
//                Request request = new Request.Builder().url("http://" + Util.BASE_PATH + "/GetUniqueId").build();
                String url = serverProtocol + "://" + serverHost + ":" + serverPort + serverBase + "/GetUniqueId";
                System.out.println("URL==" + url);
                Request request = new Request.Builder().url(url).build();
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
            Intent startActivityIntent = new Intent(MainActivity.this, StartActivity.class);
            startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(startActivityIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }, 3400);
    }

    public void basicReadWrite() {
        // [START write_message]
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> dataMap = (HashMap<String, Object>) dataSnapshot.getValue();
                System.out.println("111111111111111111111111111111111111111111" + dataMap);

                String serverHost = (String) ((Map) dataMap.get("configuration")).get("server-host");
                String serverName = (String) ((Map) dataMap.get("configuration")).get("server-name");
                String serverBase = (String) ((Map) dataMap.get("configuration")).get("server-base");
                String serverPort = (String) ((Map) dataMap.get("configuration")).get("server-port") + "";
                String serverProtocol = (String) ((Map) dataMap.get("configuration")).get("server-protocol");
                String wsserverProtocol = (String) ((Map) dataMap.get("configuration")).get("wsserver-protocol");

                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                //{configuration={server-host=34.134.207.126, server-name=friendzone, server-base=/LiveMatchinEngine, wsserver-protocol=ws, server-port=80, server-protocol=http}}
                myEdit.putString("serverHost", serverHost);
                myEdit.putString("serverName", serverName);
                myEdit.putString("serverBase", serverBase);
                myEdit.putString("serverPort", serverPort);
                myEdit.putString("serverProtocol", serverProtocol);
                myEdit.putString("wsserverProtocol", wsserverProtocol);
                myEdit.commit();

                startup();

                // System.out.println("111111111111111111111111111111111111111111serverHost" + serverHost);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Something went wrong. Pease check internet connection", Toast.LENGTH_SHORT).show();
                });
            }
        });
        // [END read_message]
    }


}