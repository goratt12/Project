package com.example.user.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.Arrays;

import communication.Protocol;


public class ReceivedMessage {
    static MessageHandler registerHandler;
    static MessageHandler deliveryConfirmHandler;

    public static class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //write message on screen
            byte[] message = intent.getExtras().getByteArray("message");
            String s;
            if(message != null){
                s = new String(message);
                int msgCode = message[0] & 0xFF;
                byte[] rest = Arrays.copyOfRange(message,4,message.length);
                switch (msgCode){
                    case Protocol.CONFIRM:
                        confirm(rest);
                        break;
                }
            }
            else {
                s = "got nothing from server";
            }
            Toast.makeText(context, s, Toast.LENGTH_LONG).show();

        }
    }

    public static void addRegisterListener(MessageHandler handler){
        registerHandler = handler;
    }
    public static void addDeliveryConfirmListener(MessageHandler handler){
        deliveryConfirmHandler = handler;
    }

    private static void confirm(byte[] message){
        int confirmType = ByteBuffer.wrap(Arrays.copyOfRange(message,0,4)).getInt();
        int status = ByteBuffer.wrap(Arrays.copyOfRange(message,4,8)).getInt();
        int id;
        Bundle data = new Bundle();
        if(confirmType == Protocol.REGISTER_CONFIRM){
            data.putBoolean("isConfirmed",status == 1);
            if (registerHandler!= null)
                registerHandler.handleMessage(data);
        }
        else if(confirmType == Protocol.DELIVERY_ASK_CONFIRM){
            id = ByteBuffer.wrap(Arrays.copyOfRange(message,8,12)).getInt();
            data.putBoolean("isConfirmed",status == 1);
            data.putInt("id",id);
            if (deliveryConfirmHandler!= null)
                deliveryConfirmHandler.handleMessage(data);
        }
    }

    public interface MessageHandler{
        public void handleMessage(Bundle data);
    }
}
