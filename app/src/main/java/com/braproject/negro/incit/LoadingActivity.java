package com.braproject.negro.incit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.braproject.negro.incit.util.Incidente;
import com.braproject.negro.incit.util.XMLPullParserHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LoadingActivity extends AppCompatActivity{
    static final String TAG = "loadingActivity";


    //Tiempo de espera de la activity
    private final int SPLASH_DISPLAY_LENGTH = 2000;


    private String url = "http://loslagos.transporteinforma.cl/servicios/desarrolladores/logica/download_xml.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

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

        //Descargar archivo
        /*
        try {
            XMLPullParserHandler parser = new XMLPullParserHandler();
            incidentes = parser.parse(getAssets().open("Incidentes.xml"));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //PRUEBA PARA SALTARSE LOADING
                        Intent goMapintent = new Intent(LoadingActivity.this, MainActivity.class);
                        goMapintent.putParcelableArrayListExtra("LISTA", (ArrayList) incidentes);
                        startActivity(goMapintent);
                    }
                }, SPLASH_DISPLAY_LENGTH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */



    }



}
