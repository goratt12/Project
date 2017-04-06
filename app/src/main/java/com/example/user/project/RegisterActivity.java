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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;


public class RegisterActivity extends AppCompatActivity{

    int PLACE_PICKER_REQUEST = 1;
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
                MyApplication.getTcpClient().send(msg.getByteArray());

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);


                sharedPref.edit().putString("Name",fName.getText().toString());
                sharedPref.edit().putString("PhoneName",fPhoneNumber.getText().toString());
                sharedPref.edit().putString("Password",fPassword.getText().toString());
                sharedPref.edit().putString("Email",fEmail.getText().toString());
                sharedPref.edit().putString("ClientType",""+getSelectedClientType());
                sharedPref.edit().commit();

                Intent intent = new Intent(RegisterActivity.this, MapsActivity.class);
                intent.putExtra("state","register");
                //startActivity(intent);

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(RegisterActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
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

