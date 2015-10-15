package com.braproject.negro.incit;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.braproject.negro.incit.R;
import com.braproject.negro.incit.util.Incidente;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsIncidentActivity extends FragmentActivity implements View.OnClickListener{
    // Tomar Views del layout
    Button BtnBackToList ;
    TextView txtRadio ;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng myPosition;

    //private LatLng myPosition =  new LatLng(-41.461132, -72.955155);//ubicada en puerto montt //DEBUG
    ArrayList<Incidente> incidentes;

    //private static final double RADIO = 10*1000; //radio en  metros
    private  double RADIO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_incident);

        // Inicializa la posicion
        Location location = new Location("MiPosicion");
        myPosition = new LatLng(location.getLatitude(), location.getLongitude());


        RADIO = getIntent().getDoubleExtra("RADIO_send", 300); // iNICIALIZA RADIO 300 M

        // Extrae Views desde layout
        BtnBackToList = (Button) findViewById(R.id.btn_volver_map); // vuelve a la activity anterior
        txtRadio = (TextView)findViewById(R.id.txt_radio_view);


        txtRadio.setText(String.valueOf(RADIO / 1000) + " KM"); //inicializa la muestra del radio

        readList(); // lee los incidentes pasados desde loadingActivity DEBUG

        setUpMapIfNeeded();

        BtnBackToList.setOnClickListener(this);
        BtnBackToList.setOnClickListener(this);


        //SeekBar
        final SeekBar radioControl = (SeekBar) findViewById(R.id.seekBar);
        radioControl.setProgress((int) RADIO);

        radioControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                RADIO = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMap.clear();
                setUpMap();

                //Calculo del focus segun el radio MEJORAR
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, (getZoomLevel())));
                txtRadio.setText(String.valueOf((RADIO/1000) )+" KM");
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            addCircleFilter();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setOnMyLocationChangeListener(myLocationChangeListener); //captura mi posicion
                mMap.setMyLocationEnabled(true); //muestra mi posicion
                setUpMap();
            }
        }
    }


    private void setUpMap() {
        String titulo;
        String subtitulo;
        Double lat;
        Double lng;

        //mostrar circulo de filtro
        addCircleFilter();


        for (int i=0; i<incidentes.size(); i++){
            titulo=incidentes.get(i).getTitulo();
            subtitulo=incidentes.get(i).getSubtitulo();
            lat=Double.parseDouble(incidentes.get(i).getLatitud());
            lng =Double.parseDouble(incidentes.get(i).getLongitud());

            setTypeOfMap();

            //testo de filtro radio
            if(ifDistanceLessThan(myPosition, new LatLng(lat, lng))) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(titulo).snippet(subtitulo).draggable(false));
            }

        }

    }


    //Listener que reacciona cuando cambia la posicion
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            myPosition = new LatLng(location.getLatitude(), location.getLongitude());

            if (mMap != null) {
                //la  camara sigue nuestra posicion
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, (getZoomLevel())));

                mMap.clear();
                setUpMap();

            }

        }


    };

    //Lee la lista de incidentes que llega en el Intent
    private void readList(){
        incidentes =  getIntent().getParcelableArrayListExtra("LISTA");
        for (int i=0; i<incidentes.size(); i++){
            Log.d("MapActivity", incidentes.get(i).getTitulo());
        }
    }



    //Controla la accion de los botones existentes en el layout
    @Override
    public void onClick(View v) {
        switch (v.getId()){

        case R.id.btn_volver_map:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("RADIO",RADIO);
                setResult(RESULT_OK,returnIntent);
                finish();
                break;
        }

    }

    //Compara las distancias entre mi posicion y el incidente es menor o igual al RADIO
    public boolean ifDistanceLessThan(LatLng MmyPosition, LatLng MIncident){
        Location location1 = new Location("Origen");
        Location location2 = new Location("Destino");
        location1.setLatitude(MmyPosition.latitude);
        location1.setLongitude(MmyPosition.longitude);
        location2.setLatitude(MIncident.latitude);
        location2.setLongitude(MIncident.longitude);

        return location1.distanceTo(location2) <= RADIO;
    }

    //Agrega un circulo que puestra el filtro de RADIO
    public void addCircleFilter(){

        mMap.addCircle(new CircleOptions()
                .center(myPosition)
                .radius(RADIO)
                .strokeColor(Color.RED)
                .fillColor(Color.argb(50,255,0,0))
                .strokeWidth(6));
    }

    //** controla el nivel de zoom segun el radio de busqueda **//
    public int getZoomLevel() {
        double scale = RADIO / 500;
        int zoomLevel =(int) (16 - Math.log(scale) / Math.log(2));

        return zoomLevel;
    }

    public void setTypeOfMap(){
        //SELECCION DE TIPO DE MAPA
        //  mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //  mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        //  mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }




}
