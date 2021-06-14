package tech.grastone.friendzoneui.util;

public interface JSInterface {
    @android.webkit.JavascriptInterface()
    void sendSDP(String sdpData);
}
