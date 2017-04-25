package com.example.user.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import communication.Message;
import communication.Protocol;
import communication.TcpClient;


public class RegisterActivity extends AppCompatActivity{

    //f = form
    EditText fName;
    EditText fPhoneNumber;
    EditText fPassword;
    EditText fEmail;
    int fClientType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Register");

        fName = (EditText)findViewById(R.id.ETname);
        fPhoneNumber = (EditText)findViewById(R.id.ETphoneNumber);
        fEmail = (EditText)findViewById(R.id.ETemail);
        fPassword = (EditText)findViewById(R.id.ETpassword);
        fClientType = R.id.ImgClientSelect;

        MyApplication.setTcpClient(TcpClient.getInstance());


        ((Button)findViewById(R.id.email_register_button)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Message msg = new Message(getSelectedClientType(), Protocol.REGISTER);
                msg.putStringExtra(fName.getText().toString());
                msg.putStringExtra(fPhoneNumber.getText().toString());
                msg.putStringExtra(fPassword.getText().toString());
                msg.putStringExtra(fEmail.getText().toString());

                byte[] messageBytes = msg.getByteArray();
                MyApplication.getTcpClient().send(messageBytes);

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putString("Name",fName.getText().toString());
                editor.putString("PhoneName",fPhoneNumber.getText().toString());
                editor.putString("Password",fPassword.getText().toString());
                editor.putString("Email",fEmail.getText().toString());
                editor.putString("ClientType",""+getSelectedClientType());
                editor.commit();

                Toast.makeText(RegisterActivity.this, ""+getSelectedClientType(), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                intent.putExtra("state","register");
                startActivity(intent);

            }
        });
        ((TextView)findViewById(R.id.TVloginBtn)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                intent.putExtra("state","login");
                startActivity(intent);
            }
        });
    }



    public char getSelectedClientType(){
        if(fClientType == R.id.ImgClientSelect){
            return Protocol.CLIENT;
        }
        //else
        return Protocol.DRIVER;
    }

    public void customRadioButtonReplace(View v){
        if(v.getId() == R.id.ImgClientSelect)
        {
            ((ImageView)findViewById(R.id.ImgClientSelect)).setImageDrawable(getResources().getDrawable(R.drawable.client_icon_selected));
            ((ImageView)findViewById(R.id.ImgDriverSelect)).setImageDrawable(getResources().getDrawable(R.drawable.driver_icon));
            fClientType = R.id.ImgClientSelect;
        }
        else {
            ((ImageView)findViewById(R.id.ImgClientSelect)).setImageDrawable(getResources().getDrawable(R.drawable.client_icon));
            ((ImageView)findViewById(R.id.ImgDriverSelect)).setImageDrawable(getResources().getDrawable(R.drawable.driver_icon_selcted));
            fClientType = R.id.ImgDriverSelect;
        }
    }


    public static class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //write message on screen
            byte[] message = intent.getExtras().getByteArray("message");
            String s;
            if(message != null){
                s = new String(message);
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            }
            else {
                s = "got nothing from server";
            }
            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
        }
    }
}

