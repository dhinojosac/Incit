package com.braproject.negro.incit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.location.LocationListener;



public class LoadingActivity extends AppCompatActivity{
    static final String TAG = "loadingActivity";
    String lat;
    String lng;

    //Tiempo de espera de la activity
    private final int SPLASH_DISPLAY_LENGTH = 2000;


    private String url = "http://loslagos.transporteinforma.cl/servicios/desarrolladores/logica/download_xml.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        GpsHelperClass ghc = new GpsHelperClass(getApplicationContext());

        //PRUEBA
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data      = intent.getData();
        if(action!=null) {
            Log.i("L_ACTION", action.toString());
        }
        if(data!=null) {
            Log.i("L_DATA", data.toString());
            getDataFromLink(String.valueOf(data));
        }

        if(      Intent.ACTION_VIEW.equals(action) ) {
            //Log.i("TESTED_ACTION", "Si entro al if");
            Intent goListintent = new Intent(LoadingActivity.this, MainActivity.class);
            goListintent.putExtra("GO_MAP",true);
            goListintent.putExtra("GO_MAP_LAT",lat);
            goListintent.putExtra("GO_MAP_LNG",lng);
            startActivity(goListintent);
            finish();
            if("data".equals(data.getScheme())) {
               //no implementado
            }
        }
        else if(true) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Create an Intent that will start the Menu-Activity.
                    //PRUEBA PARA SALTARSE LOADING
                    Intent goListintent = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(goListintent);
                    finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }

    }

    private void getDataFromLink(String link){
        lat= link.substring(36,46);
        lng = link.substring(47,57);
        String partString = link.substring(36, 58);
        Log.i("SUBSTRING_LAT", lat);
        Log.i("SUBSTRING_LNG", lng);
        Log.i("SUBSTRING", partString);

    }

    public class GpsHelperClass implements LocationListener {

        protected LocationManager locationManager;
        Context context;

        //at constructor, context is passed from FragmentOne.java and context will be used by GpsHelperClass throughout.
        public GpsHelperClass(Context context) {
            this.context = context;
        }

        public boolean isGpsReadyToUse() {
            //this.context.getSystemService is required if we want to use in separate class
            locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        @Override
        public void onLocationChanged(Location location) {

        }


    }


}
