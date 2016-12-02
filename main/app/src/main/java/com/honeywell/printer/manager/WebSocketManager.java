//package com.honeywell.printer.manager;
//
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.drafts.Draft;
//import org.java_websocket.handshake.ServerHandshake;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//
///**
// * Created by zhujunyu on 2016/11/9.
// */
//
//public class WebSocketManager {
//    private WebSocketClient client;
//    private Draft selectDraft;
//
//    private void connect() {
//        try {
//            client = new WebSocketClient(new URI(""), selectDraft) {
//                @Override
//                public void onOpen(ServerHandshake handshakedata) {
//
//                }
//
//                @Override
//                public void onMessage(String message) {
//
//                }
//
//                @Override
//                public void onClose(int code, String reason, boolean remote) {
//
//                }
//
//                @Override
//                public void onError(Exception ex) {
//
//                }
//            };
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        client.connect();
//        client.send("");
//    }
//}
