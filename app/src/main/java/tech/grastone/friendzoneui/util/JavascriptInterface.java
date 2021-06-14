package tech.grastone.friendzoneui.util;

import android.content.Context;
import android.widget.Toast;

import tech.grastone.friendzoneui.HomeActivity;

public class JavascriptInterface {
    private HomeActivity lHomeActivity = null;
    private Context context = null;

    public JavascriptInterface(HomeActivity lHomeActivity) {
        this.lHomeActivity = lHomeActivity;
        this.context = lHomeActivity;
    }

    @android.webkit.JavascriptInterface()
    public void test() {
        Toast.makeText(context, "Calling fro javascript", Toast.LENGTH_LONG).show();
    }

    @android.webkit.JavascriptInterface()
    public void sendSDP(String sdpData) {


        Toast.makeText(context, "Calling fro javascript" + sdpData, Toast.LENGTH_LONG).show();
    }


}
