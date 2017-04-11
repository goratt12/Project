package com.example.user.project;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class AskDeliveryActivity extends AppCompatActivity {
    int PICK_PICKER_REQUEST = 1;
    int DROP_PICKER_REQUEST = 1;

    private String pickUpLocation;
    private String dropLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_delivery);

        if(MyApplication.getClientType() != Protocol.CLIENT){
            //TODO only client can access this activity
        }

        Button buttonPickUp = (Button)findViewById(R.id.btnPickupLocation);
        Button buttonDrop = (Button)findViewById(R.id.btnDropLocation);

        Button buttonConfirm = (Button)findViewById(R.id.btnAskDelivery);

        final PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        buttonPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivityForResult(builder.build(AskDeliveryActivity.this), PICK_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        buttonDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivityForResult(builder.build(AskDeliveryActivity.this), DROP_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                };
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validate()){
                    //validate input
                    Toast.makeText(AskDeliveryActivity.this, "the data is not full", Toast.LENGTH_LONG).show();
                    return;
                }

                Message msg = new Message(Protocol.CLIENT, Protocol.ASK_DELIVERY);
                //location format = 31°47′0″N 35°13′0″E as String
                msg.putStringExtra(getPickUpLocation()); //pick
                msg.putStringExtra(getDropLocation()); //drop

                String volume = ((EditText)findViewById(R.id.etVolume)).getText().toString();
                msg.putIntExtra(Integer.parseInt(volume)); //volume in cm X cm

                String weight = ((EditText)findViewById(R.id.etWeight)).getText().toString();
                msg.putIntExtra(Integer.parseInt(weight)); //weight in grams

                MyApplication.getTcpClient().send(msg.getByteArray());
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                this.pickUpLocation = convert(place.getLatLng().latitude, place.getLatLng().longitude);

                ((TextView)findViewById(R.id.tvPickupLocation)).setText(this.pickUpLocation);

                String toastMsg = String.format("location: %s", place.getLatLng());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == DROP_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                this.dropLocation = convert(place.getLatLng().latitude, place.getLatLng().longitude);

                ((TextView)findViewById(R.id.tvDropLocation)).setText(this.dropLocation);

                String toastMsg = String.format("location: %s", place.getLatLng());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    private String convert(double latitude, double longitude) {
        StringBuilder builder = new StringBuilder();

        //lat
        String latitudeDegrees = Location.convert(Math.abs(latitude), Location.FORMAT_SECONDS);
        String[] latitudeSplit = latitudeDegrees.split(":");
        builder.append(latitudeSplit[0]);
        builder.append("°");
        builder.append(latitudeSplit[1]);
        builder.append("'");
        builder.append(latitudeSplit[2]);
        builder.append("\"");

        if (latitude < 0) {
            builder.append("S");
        } else {
            builder.append("N");
        }
        //lat

        builder.append(" ");

        //lon
        String longitudeDegrees = Location.convert(Math.abs(longitude), Location.FORMAT_SECONDS);
        String[] longitudeSplit = longitudeDegrees.split(":");
        builder.append(longitudeSplit[0]);
        builder.append("°");
        builder.append(longitudeSplit[1]);
        builder.append("'");
        builder.append(longitudeSplit[2]);
        builder.append("\"");

        if (longitude < 0) {
            builder.append("W");
        } else {
            builder.append("E");
        }
        //lon

        return builder.toString();
    }

    private boolean validate(){
        if(dropLocation == null)
            return false;
        if(pickUpLocation == null)
            return false;

        String volume = ((EditText)findViewById(R.id.etVolume)).getText().toString();
        if(volume.equals(""))
            return false;

        String weight = ((EditText)findViewById(R.id.etWeight)).getText().toString();
        if(weight.equals(""))
            return false;

        //else
        return true;
    }
}