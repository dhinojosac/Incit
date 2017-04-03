package com.braproject.negro.incit;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.braproject.negro.incit.models.Incidente;
import com.braproject.negro.incit.util.XMLPullParserHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;

public class MapsIncidentActivity extends FragmentActivity implements View.OnClickListener{
    static final String TAG = "MapActivity";
    // Tomar Views del layout
    Button BtnBackToList ;
    TextView txtRadio ;
    Button BtnLista2;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng myPosition;
    private LatLng mIncident;


    //private LatLng myPosition =  new LatLng(-41.461132, -72.955155);//ubicada en puerto montt //DEBUG
    ArrayList<Incidente> incidentes;

    //private static final double RADIO = 10*1000; //radio en  metros
    private  double RADIO;

    //Para desacativar LocationChangeListener
    boolean StatusChangeListener = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_incident);

        // Inicializa la posicion
        Location location = new Location("MiPosicion");
        myPosition = new LatLng(location.getLatitude(), location.getLongitude());


        RADIO = getIntent().getDoubleExtra("RADIO_send", 300); // iNICIALIZA RADIO 300 M
        Log.i("Mapa ", "recibe" +RADIO);

        double lat = getIntent().getDoubleExtra("iLAT", -1); // recibe lat de incidente compartido
        double lng = getIntent().getDoubleExtra("iLNG", -1); // recibe lngde incidente compartido
        mIncident =new LatLng(lat,lng);

        // Extrae Views desde layout
        BtnLista2 = (Button) findViewById(R.id.boton_lista_header2);
        txtRadio = (TextView)findViewById(R.id.txt_radio_view);


        txtRadio.setText(String.valueOf(RADIO / 1000) + " KM"); //inicializa la muestra del radio
        BtnLista2.setOnClickListener(this);

        readList(); // lee los incidentes pasados desde loadingActivity DEBUG (incidente compartido)
        if(lat==-1 && lng==-1) {
            setUpMapIfNeeded();
        } else {

            //TODO: mejorar la implementacion para que se dirija el focus al incidente.
            showIncident();
            Log.i("SHAREDMAP","Abierto por incidente compartido");
        }



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
    public void onBackPressed() {
        backToList();
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
        setTypeOfMap();

        for (int i=0; i<incidentes.size(); i++){
            titulo=incidentes.get(i).getTitulo();
            subtitulo=incidentes.get(i).getSubtitulo();
            lat=Double.parseDouble(incidentes.get(i).getLatitud());
            lng =Double.parseDouble(incidentes.get(i).getLongitud());



            //testo de filtro radio
            if(ifDistanceLessThan(myPosition, new LatLng(lat, lng))) {
                String categoria = incidentes.get(i).getCategoria();

                switch (categoria){
                    case "Colision":
                        mMap.addMarker(new MarkerOptions().
                                        position(new LatLng(lat, lng)).
                                        title(titulo)
                                        .snippet(subtitulo)
                                        .draggable(false)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_colision))
                        );
                        break;
                    case "Arreglos":
                        mMap.addMarker(new MarkerOptions().
                                        position(new LatLng(lat, lng)).
                                        title(titulo)
                                        .snippet(subtitulo)
                                        .draggable(false)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_arreglo))
                        );
                        break;
                    case "Trafico":
                        mMap.addMarker(new MarkerOptions().
                                        position(new LatLng(lat, lng)).
                                        title(titulo)
                                        .snippet(subtitulo)
                                        .draggable(false)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_trafico))
                        );
                        break;
                    case "Precaucion":
                        mMap.addMarker(new MarkerOptions().
                                        position(new LatLng(lat, lng)).
                                        title(titulo)
                                        .snippet(subtitulo)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_precaucion))
                        );
                        break;
                    case "EMERGENCIA":
                        mMap.addMarker(new MarkerOptions().
                                        position(new LatLng(lat, lng)).
                                        title(titulo)
                                        .snippet(subtitulo)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_emergencia))
                        );
                        break;
                    default:
                        mMap.addMarker(new MarkerOptions().
                                        position(new LatLng(lat, lng)).
                                        title(titulo)
                                        .snippet(subtitulo)
                                        .draggable(false));
                        break;

                }
               // mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(titulo).snippet(subtitulo).draggable(false));
            }

        }

    }

    private void showIncident(){
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            addCircleFilter();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                //mMap.setOnMyLocationChangeListener(myLocationChangeListener); //captura mi posicion

               // mMap.setMyLocationEnabled(true); //muestra mi posicion
                setUpMap();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mIncident,17));
            }
        }
    }

    //Desactivar LocationChangeListener

    //Listener que reacciona cuando cambia la posicion
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {

            if(!StatusChangeListener) {
                myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                if (mMap != null) {
                    //la  camara sigue nuestra posicion
                   // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, (getZoomLevel())));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, (getZoomLevel())), 1, null);
                   // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, (getZoomLevel())));
                    mMap.clear();
                    setUpMap();


                    StatusChangeListener = true; //Nuevo para desactivar Change

                }
            }

        }


    };

    //Lee la lista de incidentes que llega en el Intent
    private void readList(){
        parserXML();
        //incidentes =  getIntent().getParcelableArrayListExtra("LISTA");
        for (int i=0; i<incidentes.size(); i++){
            //Log.d("MapActivity", incidentes.get(i).getTitulo());
        }
    }



    //Controla la accion de los botones existentes en el layout
    @Override
    public void onClick(View v) {
        switch (v.getId()){

        case R.id.boton_lista_header2:
                backToList();
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
                .fillColor(Color.argb(50, 255, 0, 0))
                .strokeWidth(6));
    }

    //** controla el nivel de zoom segun el radio de busqueda **//
    public int getZoomLevel() {
        double scale = RADIO / 500;
        return (int) (16 - Math.log(scale) / Math.log(2));
    }

    public void setTypeOfMap(){
        //SELECCION DE TIPO DE MAPA
        //  mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //  mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        //  mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    private void parserXML(){
        try {
            XMLPullParserHandler parser = new XMLPullParserHandler();
            incidentes = (ArrayList<Incidente>) parser.parse(getAssets().open("Incidentes.xml"));
            //incidentes_all = incidentes;

            for (int i=0; i<incidentes.size(); i++){
                //Log.d("LoadingScreen", incidentes.get(i).getTitulo()); //DEBUG
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void backToList(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("RADIO",RADIO);
        setResult(RESULT_OK,returnIntent);
        finish();
    }



}
