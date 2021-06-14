package tech.grastone.friendzoneui.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketInit {

    private static WebSocketInit lWebSocketInit;
    private WebSocket webSocket;


    private WebSocketInit() {
        // initializeSocketConnection();
    }

    public static WebSocketInit getInstance() {

        if (lWebSocketInit == null) {
            synchronized (WebSocketInit.class) {
                lWebSocketInit = new WebSocketInit();
            }
        }

        return lWebSocketInit;
    }


    public WebSocket getWebSocket() {
        return webSocket;
    }

    public WebSocketInit initializeSocketConnection(String pServerPath) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(pServerPath).build();
        webSocket = client.newWebSocket(request, new WebSocketInit.SocketListener());
        return lWebSocketInit;
    }

    private class SocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {

            System.out.println(response.body().toString());

        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            System.out.println("------------------------>" + text);
        }

    }

}
