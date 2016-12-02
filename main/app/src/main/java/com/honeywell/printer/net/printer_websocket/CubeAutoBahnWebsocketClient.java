package com.honeywell.printer.net.printer_websocket;


import android.content.Context;
import android.util.Log;


import com.honeywell.printer.net.autobaln_websocket.WebSocket;
import com.honeywell.printer.net.autobaln_websocket.WebSocketConnection;
import com.honeywell.printer.net.autobaln_websocket.WebSocketException;
import com.honeywell.printer.net.autobaln_websocket.WebSocketOptions;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by H157925 on 16/4/30. 13:57
 * Email:Shodong.Sun@honeywell.com
 */
public class CubeAutoBahnWebsocketClient {
    private static final String TAG = "CubeAutoBahnWebsocketClient";
    private static WebSocketConnection connection = new WebSocketConnection();
    private static Context mContext;
    private Thread connectionThread = null;
    private static volatile boolean isProcessRun = false;
    private static volatile boolean isReconnectToServer = false;
    private int reconnectCount = -1;//重连次数 超过10就可以不连了


    /**
     * 回调接口
     */
    private WebSocket.ConnectionHandler handler = new WebSocket.ConnectionHandler() {
        @Override
        public void onOpen() {
            Log.e(TAG, "ssd****************onOpen");
//            LoginController.getInstance(mContext).checkCubeOnlineStatus();
        }


        @Override
        public void onClose(WebSocketCloseNotification code, String reason) {
            Log.e(TAG, "ssd****************onClose with code :" + code + " reason :" + reason);
            CubeAutoBahnWebsocketClient.solvedOnClose(CubeAutoBahnWebsocketClient.getContext(), code, reason);

//            stopConnection(reason);
//            Loger.print(TAG, "ssd websocket is restart connect", Thread.currentThread());
//            reconnectToserver();
        }

        @Override
        public void onTextMessage(String payload) {
            Log.e(TAG, "ssd****************onTextMessage message:" + payload);
            final String str = payload;
            //使用队列操作
//            ReceiverQueueManager.getInstance(mContext).addRecevierToQueue(str);
//
//            //使用线程操作
////            new Thread(new Runnable() {
////                @Override
////                public void run() {
////                    ResponderController.newInstance(mContext).dealWithWebSocketResponce(str);
////                }
////            }).start();
        }

        @Override
        public void onRawTextMessage(byte[] payload) {
            Log.e(TAG, "11111111-ssd****************onRawTextMessage message :" + (new String(payload)));
            String receiveString = new String(payload);
            if (receiveString != null && receiveString.length() > 0) {
                //使用队列操作
//                ReceiverQueueManager.getInstance(mContext).addRecevierToQueue(receiveString);
            }
        }

        @Override
        public void onBinaryMessage(byte[] payload) {
            Log.e(TAG, "ssd****************onBinaryMessage message :" + (new String(payload)));
        }
    };

    private CubeAutoBahnWebsocketClient(Context context) {
        this.mContext = context;
    }

    private static CubeAutoBahnWebsocketClient cubeAutoBahnWebsocketClient = null;

    public static CubeAutoBahnWebsocketClient getInstance(Context context) {
        if (cubeAutoBahnWebsocketClient == null) {
            cubeAutoBahnWebsocketClient = new CubeAutoBahnWebsocketClient(context);
        }
        return cubeAutoBahnWebsocketClient;
    }

    public static WebSocketConnection getConnection() {
        return connection;
    }

    public static Context getContext() {
        return mContext;
    }

    /**
     * 连接服务器主体
     *
     * @param
     */
    private void connectServer() {
        if (connection == null) {
            connection = new WebSocketConnection();
        }


//        Loger.print(TAG, "ssd websocket cookie : " + info.all_header_fields_cookie, Thread.currentThread());
        ArrayList<BasicNameValuePair> list = new ArrayList<>();
//        BasicNameValuePair pair01 = new BasicNameValuePair(CommonData.JSON_WEBSOCKET_USER_ORIGIN, NetConstant.URI_WEBSOCKET_ORIGIN);
//        BasicNameValuePair pair02 = new BasicNameValuePair(CommonData.JSON_WEBSOCKET_USER_COOKIE, info.all_header_fields_cookie);
//        list.add(pair01);
//        list.add(pair02);
        try {
            if (connection.isConnected()) {
//                Loger.print(TAG, "ssd WebSocket error has connected", Thread.currentThread());
//                getConfigInfo();
                return;
            }
            /**
             * 设置有效的最大负载 否则会报错 code 4
             */
//            WampOptions options = new WampOptions();
//            options.setReceiveTextMessagesRaw(false);
//            options.setMaxMessagePayloadSize(512 * 1024);
//            options.setMaxFramePayloadSize(512 * 1024);
//            options.setTcpNoDelay(true);
//            Loger.print(TAG, "ssd websocket url : " + NetConstant.URI_WEBSOCKET, Thread.currentThread());
            WebSocketOptions options = new WebSocketOptions();
            connection.connect("", null, handler, options, list);
//            connection.connect(NetConstant.URI_WEBSOCKET, new String[]{"wamp"}, handler, options, list);
            //启动命令发送线程
//            CommandQueueManager.getInstance(mContext).startRun();
//            if (!ReceiverQueueManager.getInstance(mContext).isRunFlag()) {
//                ReceiverQueueManager.getInstance(mContext).startRun();
//            }
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }


    /**
     * 通过EventBus发送WebSocket关闭原因
     *
     * @param context
     * @param code
     * @param reason
     */
    private static void solvedOnClose(Context context, WebSocket.ConnectionHandler.WebSocketCloseNotification code, String reason) {
        String errorStr = "";
        if (code == WebSocket.ConnectionHandler.WebSocketCloseNotification.NORMAL) {
            return;
        } else if (code == WebSocket.ConnectionHandler.WebSocketCloseNotification.CANNOT_CONNECT) {
            //无法链接
//            errorStr = context.getString();
        } else if (code == WebSocket.ConnectionHandler.WebSocketCloseNotification.CONNECTION_LOST) {
            //链接丢失
//            errorStr = context.getString(R.string.websocket_close_connect_lost);
        } else if (code == WebSocket.ConnectionHandler.WebSocketCloseNotification.PROTOCOL_ERROR) {
            //违反协议
//            errorStr = context.getString(R.string.websocket_close_protocol_error);
        } else if (code == WebSocket.ConnectionHandler.WebSocketCloseNotification.INTERNAL_ERROR) {
            //内部错误
//            errorStr = context.getString(R.string.websocket_close_internal_error);
        } else if (code == WebSocket.ConnectionHandler.WebSocketCloseNotification.SERVER_ERROR) {
            //服务器错误
//            errorStr = context.getString(R.string.websocket_close_server_error);
        } else if (code == WebSocket.ConnectionHandler.WebSocketCloseNotification.RECONNECT) {
            //重连
//            errorStr = context.getString(R.string.websocket_close_reconnect);
        } else {
            //未知错误
//            errorStr = context.getString(R.string.error_unknown);
        }
//        Loger.print(TAG, "ssd websocket断了，并发出了Event事件", Thread.currentThread());
        if (isProcessRun) {
//            LoginController.getInstance(context).logout(mContext, false);
            isProcessRun = false;
//            EventBus.getDefault().post(new CubeBasicEvent(CubeEvents.CubeBasicEventType.CONNECTING_LOST, false, mContext.getString(R.string.error_time_out) + " : " + errorStr));
        }
    }

    /**
     * 分线程启动WebSocket
     *
     * @param
     */
    public synchronized void startConnectServer() {

        isProcessRun = true;
        if (connection == null) {
            connection = new WebSocketConnection();
        }
        connectionThread = new Thread(connectionRunnable);
        connectionThread.start();
//        if (LoginController.getInstance(mContext).getLoginType() == LoginController.LOGIN_TYPE_CONNECT_WIFI) {
//            SocketController.newInstance(mContext).stopSocketConnect("Login webSocket");
//        }
    }

    /**
     * 关闭当前连接
     *
     * @param reason
     */
    public void stopConnection(String reason) {
        isProcessRun = false;
//        ReceiverQueueManager.getInstance(mContext).stopRun();
        if (connectionThread != null && connectionThread.isAlive()) {
            connectionThread.interrupt();
            connectionThread = null;
        }
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
        }
        connection = null;
    }

    /**
     * 重连服务器
     */
    private synchronized void reconnectToServer() {
        isReconnectToServer = true;//重连
        isProcessRun = true;
        connectionThread = new Thread(reConnectionRunnable);
        connectionThread.start();
    }

    private Runnable reConnectionRunnable = new Runnable() {
        @Override
        public void run() {
            while (isReconnectToServer) {
                if (isProcessRun) {
                    if (reconnectCount > 10) {
                        reconnectCount = -1;
                        isReconnectToServer = false;
                    } else {
                        reconnectCount++;
                        connectServer();
                    }
                }
            }
        }
    };

    private Runnable connectionRunnable = new Runnable() {
        @Override
        public void run() {
            if (isProcessRun) {
                if (!connection.isConnected()) {
                    connectServer();
                }
            }
        }
    };
}
