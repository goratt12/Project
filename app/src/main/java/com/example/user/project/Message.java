package com.example.user.project;

import java.nio.ByteBuffer;

public class Message {

    ByteBuffer msg;


    public Message(char userType, int msgCode, int length) {
        msg = ByteBuffer.allocate(length);
        msg.put((byte)userType);
        msg.putInt(msgCode);
    }

    public Message(char userType, int msgCode) {
        this(userType, msgCode, 1024);
    }

    public void putStringExtra(String extra){
        msg.putInt(extra.length());
        for (int i = 0; i < extra.length(); i++){
            msg.put((byte)extra.charAt(i));
        }
    }

    public void putIntExtra(int extra){
        msg.putInt(Protocol.INT_SIZE); //length
        msg.putInt(extra);
    }

    public byte[] getByteArray(){
        int len = msg.position();

        ByteBuffer trimed = ByteBuffer.allocate(len);
        for (int i = 0; i < len; i++){
            trimed.put(msg.get(i));
        }

        return trimed.array();
    }
}
