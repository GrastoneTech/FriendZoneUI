package tech.grastone.friendzoneui.util;

public interface JSInterface {
    @android.webkit.JavascriptInterface()
    void sendSDP(String sdpData);

    @android.webkit.JavascriptInterface()
    void next();

    @android.webkit.JavascriptInterface()
    void onException();
}
