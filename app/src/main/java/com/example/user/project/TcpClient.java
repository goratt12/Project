package com.example.user.project;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TcpClient {

    private static TcpClient instance;
    private TcpHandler myTcpHandler;
    private static final int SERVER_PORT = 7001;
    private static final String SERVER_ADDRESS = "192.168.43.173";
    private Context context;

    public static TcpClient getInstance() {
        if(instance == null) {
            instance = new TcpClient();
        }
        return instance;
    }

    private TcpClient(){
        this.context = MyApplication.getContext();
        Intent intent = new Intent(context, TcpHandler.class);
        if(!isServiceRunning(TcpHandler.class.getName())) {
            context.startService(intent);
        }
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                myTcpHandler = ((TcpHandler.MyBinder) iBinder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {}
        }, Context.BIND_AUTO_CREATE);
    }

    public void send(byte[] data){
        myTcpHandler.send(data);
    }

    public void close() {
        context.stopService(new Intent(context, TcpHandler.class));
        instance = null;
    }

    public static boolean isServiceRunning(String serviceClassName){
        final ActivityManager activityManager = (ActivityManager)MyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
    }


    public static class TcpHandler extends Service{
        private Socket socket;
        private volatile boolean isConnected = false;
        private volatile boolean isPending = false;
        private volatile Queue<byte[]> pendingMessages = new LinkedList<>();


        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            connectToServer();
            return Service.START_NOT_STICKY;
        }

        private void connectToServer(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        isConnected = false;
                        while (!isConnected) {
                            socket = new Socket(InetAddress.getByName(SERVER_ADDRESS), SERVER_PORT);
                            if (socket.isConnected())
                                isConnected = true;
                        }
                        listenToServer(socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }

        public void send(final byte[] data){
            if(isConnected){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OutputStream out = socket.getOutputStream();
                            out.write(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            else{
                pendingMessages.add(data);
                sendFromPending();
            }
        }

        private void sendFromPending(){
            if(isPending)
                return;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        isPending = true;
                        while(!isConnected) {
                            Thread.sleep(1000);
                        }
                        while(!pendingMessages.isEmpty()){
                            send(pendingMessages.remove());
                        }

                        isPending = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        private void listenToServer(Socket socket){
            try {
                InputStream in = socket.getInputStream();
                byte[] data = new byte[1024];
                while (true) {
                    int status = in.read(data);
                    if(status == -1 ){//disconnected
                        connectToServer();
                        return;
                    }
                    Intent intent = new Intent();
                    intent.setAction("com.project.MESSAGE_FROM_SERVER");
                    intent.putExtra("message",data);
                    sendBroadcast(intent);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        @Override
        public IBinder onBind(Intent intent) {
            return new MyBinder();
        }

        public class MyBinder extends Binder {
            TcpHandler getService() {
                return TcpHandler.this;
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }
    }

}
