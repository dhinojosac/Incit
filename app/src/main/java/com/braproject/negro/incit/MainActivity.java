package com.braproject.negro.incit;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.braproject.negro.incit.Events.EventIncidentsReady;
import com.braproject.negro.incit.Events.EventPositionNow;
import com.braproject.negro.incit.adapters.IncidentesAdapter;
import com.braproject.negro.incit.adapters.NewAdapter;
import com.braproject.negro.incit.api.IncitApiClient;
import com.braproject.negro.incit.api.IncitService;
import com.braproject.negro.incit.models.Incidente;
import com.braproject.negro.incit.models.incit.model.Incidencia;
import com.braproject.negro.incit.models.incit.model.IncidentResponse;
import com.braproject.negro.incit.util.XMLPullParserHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,
        com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    static final String TAG = "MAINActivity";

    //Variables importantes
    private boolean isIncidents = false;
    private boolean isLocation = false;

    //Lista de incidentes
    ArrayList<Incidencia> incidentes;    //nuevos desde api
    ArrayList<Incidencia> lista_incidencias;    //nuevos desde api

    //intent
    private IncitService service; //get incidents

    //IncidentesAdapter adaptador que acomoda los incidentes
    NewAdapter adaptador; //adapter

    //interfaz
    Button botonMapa;
    ImageButton botonSetting;
    TextView txtCiudad;
    TextView txtClima;

    private double RADIO = 10000;    //Radio inicial en Km.

    int request_Code = 1;            //

    //Location
    private Location myPosition = new Location("Mi posicion"); //NEW
    private Location oldPosition = new Location("Antigua posicion");
    protected GoogleApiClient googleApiClient;

    private LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();
        googleApiClient.connect();

        //Prepara la interfaz
        setUpInterfaceLayout();

        //obtener los incidentes desde la api
        getIncidents();

        //revisa si viene desde un incidente compartido
        // por alguna red social, para luego pasar directo al mapa
        getIntentFromShared();

        //inicializa el radio pro primera vez
        checkInitiate();


        //lee la lista de incidentes desde XML
        //TODO: Esta funcion debe implementarse como consulta al servicio REST
        //parserXML();
        //parserXML2();

        //Refresca la lista
        refreshList();
        Log.d(TAG, "REFRESH - ON-CREATE");

        //Prepara los listener para
        setUpListener();

    }

    // SUBSCRIBER DE EVENTOS
    public void onEvent(EventPositionNow event) {
        Log.i("EventPosition", event.getPosicion().toString());
        isLocation = true;

        addDistanceToIncidentList(lista_incidencias);

        //TODO: Agregar condicional para actualización de la lista

        //Refresca la lista despues de agregar la distancia
        //refreshList();
        //Log.d(TAG, "REFRESH- onEvent");

        //Filtrar incidentes
        //SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        //double myRadio = Double.longBitsToDouble(prefs.getLong("mRadio", 1000000));
        //Log.d("ONEVENT-Ref", String.valueOf(myRadio));
        //adaptador.getFilter(0).filter(String.valueOf(myRadio));
        //adaptador.notifyDataSetChanged();


    }

    // SUBSCRIBER de evento de obtener incidentes
    public void onEvent(EventIncidentsReady isReady) {
        Log.i("EventIncidentReady","Se obtuvieron los incidentes");
        Log.i("isReady.getState:",String.valueOf(isReady.getState()));
        if(isReady.getState()){
            isIncidents = true;
            if(isLocation){
                addDistanceToIncidentList(lista_incidencias);
                setCity();
            }
            refreshList();
        }else {
            isIncidents = false;
            disableUI();
        }

     }

    /*
    //antigupo
    //Metodo para agregar distancia a Incidentes a Lista de incidentes
    public void addDistanceToIncidentList(ArrayList<Incidente> list) {
        for (int i = 0; i < list.size(); i++) {
            LatLng incident = new LatLng(Double.valueOf(list.get(i).getLatitud())
                    , Double.valueOf(list.get(i).getLongitud()));
            list.get(i).setDistance(String.valueOf(DistanceToIncident(myPosition, incident)));
        }
    }

    public float DistanceToIncident(Location MyPosition, LatLng MIncident) {
        Location location1 = myPosition;
        Location location2 = new Location("Destino");
        location2.setLatitude(MIncident.latitude);
        location2.setLongitude(MIncident.longitude);
        return location1.distanceTo(location2);
    }
    */

    //Metodo para agregar distancia a Incidentes a Lista de incidentes
    //nuevo
    public void addDistanceToIncidentList(ArrayList<Incidencia> list) {
        if(isIncidents) {
            for (int i = 0; i < list.size(); i++) {
                LatLng incident = new LatLng(Double.valueOf(list.get(i).getLatitud())
                        , Double.valueOf(list.get(i).getLongitud()));
                list.get(i).setDistance(String.valueOf(DistanceToIncident(myPosition, incident)));
            }
            Log.d("addDistance", "added distance to incident");
        }else{
            Log.d("addDistance", "distance not added yet");
        }
    }

    public float DistanceToIncident(Location MyPosition, LatLng MIncident) {
        Location location1 = myPosition;
        Location location2 = new Location("Destino");
        location2.setLatitude(MIncident.latitude);
        location2.setLongitude(MIncident.longitude);
        return location1.distanceTo(location2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                break;
            case R.id.action_share:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //Implementacion onClick de los botones
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.boton_mapa_header1:
                sendRadio();
                break;

            case R.id.btn_action_setting:
                // Toast.makeText(this, "Presiono Setting", Toast.LENGTH_SHORT).show();
                PopupMenu settingBtnPopup = new PopupMenu(this, v);
                settingBtnPopup.getMenuInflater().inflate(R.menu.menu_main, settingBtnPopup.getMenu());
                settingBtnPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    //MENU FILA
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_settings:
                                Toast.makeText(MainActivity.this, "Setting item", Toast.LENGTH_LONG).show(); //DEBUG
                                break;
                        }
                        return true;
                    }
                    //FIN MENU FILA

                });
                settingBtnPopup.show();
                break;
        }
    }

    /**
     * PARA RECIBIR EL RADIO DESDE MAPACTIVITY
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                double result = data.getDoubleExtra("RADIO", 1000);
                Log.d("ForResult", String.valueOf(result));//Debug

                Toast.makeText(getBaseContext(), "Su radio de busqueda: " + (result / 1000) + "KM", Toast.LENGTH_SHORT).show();

                //preferencias de usaurio
                SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("mRadio", Double.doubleToLongBits(result));
                editor.apply();
                //editor.commit();


                refreshList();

                Log.d(TAG, "REFRESH - ON-result");

                //Filtrar incidentes
                double myRadio = Double.longBitsToDouble(prefs.getLong("mRadio", 10000));
                adaptador.getFilter(0).filter(String.valueOf(myRadio));
                adaptador.notifyDataSetChanged();



            }

            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
                Toast.makeText(getBaseContext(), "No se pudo fijar un radio", Toast.LENGTH_SHORT).show();


            }
        }
    }

    //Implementacion ItemClick
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO: implementar on click de cada incidente
        //Incidente inci = incidentes.get(position + 1);
        Incidencia inci = incidentes.get(position);
        showInfoItemDialog(inci);
    }

    //Implemantacion ItemLongClick SHARE OPTION
    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
        //Muestra pop up para compartir incidente en redes sociales
        PopupMenu popup = new PopupMenu(MainActivity.this, view);
        popup.getMenuInflater().inflate(R.menu.menu_incidente, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            //MENU FILA
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_share:

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, incidentes.get(position).toShare());
                        sendIntent.setType("text/plain");
                        //startActivity(Intent.createChooser(sendIntent,);
                        startActivity(Intent.createChooser(sendIntent, "Compartir incidente usando:"));
                        //  Toast.makeText(HomeActivity.this, "compartir item", Toast.LENGTH_LONG).show(); //DEBUG
                        break;
                }
                return true;
            }
            //FIN MENU FILA

        });
        popup.show();
        return true;
    }


    @Override
    public void onLocationChanged(Location location) {
        if(myPosition!=null) oldPosition = myPosition;
        myPosition.setLatitude(location.getLatitude());
        myPosition.setLongitude(location.getLongitude());
        Log.d(TAG, "Cambio de posicion: " + myPosition.getLatitude() + "," + myPosition.getLongitude());
        Log.d(TAG, "Posicion anterior: " + oldPosition.getLatitude() + "," + oldPosition.getLongitude());

        if (myPosition != null) {
            //Avisa que obtuvo la posicion
            EventBus.getDefault().post(new EventPositionNow(myPosition));
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "GoogleApiclient Connected...");

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(50 * 1000) //40 segundos
               // .setSmallestDisplacement((float) 1)
               ; //minimo desplazamiento

        LocationServices.FusedLocationApi.
                requestLocationUpdates(googleApiClient, mLocationRequest, this);
        myPosition = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        googleApiClient.connect();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: " + connectionResult.getErrorCode());
    }


    private void setUpListener() {
        //listener OnClick de botones
        botonMapa.setOnClickListener(this);
        botonSetting.setOnClickListener(this);
    }

    private void setUpInterfaceLayout() {
        botonMapa = (Button) findViewById(R.id.boton_mapa_header1);
        botonSetting = (ImageButton) findViewById(R.id.btn_action_setting);
        txtCiudad = (TextView) findViewById(R.id.header_ciudad);
        txtClima = (TextView) findViewById(R.id.lbl_temp_header);
    }

    private void refreshList() {
        Log.i("POPULATE", "se esta reiniciando lista");
        if (isIncidents) {
            ListView lista = (ListView) findViewById(R.id.list);

            incidentes = new ArrayList<>(lista_incidencias);
            //ArrayList<Incidencia> incidentesTest = new ArrayList<>(lista_incidencias);     // nuevos


            //for (int i = 0; i < incidentesTest.size(); i++) {
            //    Log.i("REFRESH_LIST_T", i + " " + incidentesTest.get(i).getTitulo());
            //}

            for (int i = 0; i < incidentes.size(); i++) {
                Log.i("REFRESH_LIST_T", i + " " + incidentes.get(i).getTitulo());
            }

            if (incidentes.size()!=0) {
                //adaptador = new IncidentesAdapter(this, incidentesTest);
                //adaptador = new NewAdapter(this, incidentesTest);
                adaptador = new NewAdapter(this, incidentes);
                lista.setAdapter(adaptador);

                //avisa que se actualizo la lista
                adaptador.notifyDataSetChanged();

                //listener para click y longClick de cada item
                lista.setOnItemClickListener(this);
                lista.setOnItemLongClickListener(this);

                //menu para cada item de la lista
                registerForContextMenu(lista);
            }else {
                showDialogNoIncidents();
            }
        }

    }

    private void sendRadio() {
        //TODO:Arreglar paso de incidentes al mapa
        Toast.makeText(getApplicationContext(),"Este boton esta deshabilitado temporalmente, hasta solucionar algunos problemas",
                Toast.LENGTH_LONG).show();
        /*
        Intent i = new Intent(MainActivity.this, MapsIncidentActivity.class);
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        double myRadio = Double.longBitsToDouble(prefs.getLong("mRadio", 10000));
        Log.i("PressMAPBTN", "Envia un " + myRadio);
        i.putExtra("RADIO_send", myRadio);
        startActivityForResult(i, request_Code);
        */
    }

    private void sendIncidentRadio(Double lat, Double lng) {
        //TODO:Arreglar paso de incidentes al mapa
        Toast.makeText(getApplicationContext(),"Este boton esta deshabilitado temporalmente, hasta solucionar algunos problemas",
                Toast.LENGTH_LONG).show();
        /*
        Intent IncidIntetS = new Intent(MainActivity.this, MapsIncidentActivity.class);
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        double myRadio = Double.longBitsToDouble(prefs.getLong("mRadio", 10000));
        IncidIntetS.putExtra("RADIO_send", myRadio);
        IncidIntetS.putExtra("iLAT", lat);
        IncidIntetS.putExtra("iLNG", lng);
        startActivityForResult(IncidIntetS, request_Code);
        */
    }

    private void checkInitiate() {
        Log.i("CHECK-INIT", "Validando si es la primara vez que se abre la aplicacion");

        SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Log.i("CHECK-INIT", String.valueOf(isNew()));
        if (isNew()) {
            Log.i("CHECK1", String.valueOf(prefs.getBoolean("InitApp", false)));
            Log.i("CHECK1", "La aplicación se instalo por primera vez");
            editor.putLong("mRadio", Double.doubleToLongBits(5000));
            editor.apply();
            Log.i("Radio?", String.valueOf(prefs.getLong("mRadio", 1)));
            editor.putBoolean("InitApp", true);
            editor.apply();
        }
        Log.i("CHECK2", String.valueOf(prefs.getBoolean("InitApp", false)));

    }

    private boolean isNew() {
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        return !prefs.getBoolean("InitApp", false);
    }

    private void getIntentFromShared() {
        if (getIntent().getBooleanExtra("GO_MAP", false)) {
            Log.d("GOMAP", String.valueOf(getIntent().getBooleanExtra("GO_MAP", false)));
            Double lat = Double.valueOf(getIntent().getStringExtra("GO_MAP_LAT"));
            Double lng = Double.valueOf(getIntent().getStringExtra("GO_MAP_LNG"));
            sendIncidentRadio(lat, lng);
        }
    }

    private void showInfoItemDialog(final Incidencia inci) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle(inci.getCategoria() + ":");
        builder.setMessage(inci.getTitulo() + "\n" + inci.getSubtitulo());
        // builder.setPositiveButton("OK", null);
        builder.setNegativeButton("COMPARTIR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, inci.toShare());
                sendIntent.setType("text/plain");
                //startActivity(Intent.createChooser(sendIntent,);
                startActivity(Intent.createChooser(sendIntent, "Compartir incidente usando:"));
            }
        });

        builder.show();
    }

    private void showDialogNoIncidents() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle("No hay incidentes cerca de tu posición");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    //TODO: generar metodo para agregar los filtros multiples
    public void filtrarLista(int num) {


    }

    public void getIncidents() {
        service = new IncitApiClient().getBotisService();
        Call<IncidentResponse> call = service.getIncidents();

        Callback<IncidentResponse> callback = new Callback<IncidentResponse>() {
            EventIncidentsReady state = new EventIncidentsReady(); // POST state from request
            @Override
            public void onResponse(Call<IncidentResponse> call, Response<IncidentResponse> response) {
                if(response.isSuccessful()){
                    IncidentResponse incidentResponse = response.body();
                    Log.d("TEST-success", incidentResponse.getIncidencias().get(0).getTitulo());
                    lista_incidencias = (ArrayList<Incidencia>) incidentResponse.getIncidencias();
                    // lanzar evento por event bus para visar que se obtuvieron los incidentes

                    state.setStateSuccess();
                    EventBus.getDefault().post(state);


                } else {
                    Log.d("TEST-Error", "no se pudieron obtener los incidentes");
                    state.setStateFail();
                    EventBus.getDefault().post(state);

                }
            }

            @Override
            public void onFailure(Call<IncidentResponse> call, Throwable t) {

            }
        };
        call.enqueue(callback);


    }

    void disableUI(){
        botonMapa.setEnabled(false);

    }

    void enableUI(){
        botonMapa.setEnabled(false);

    }

    void setCity(){
        if(isLocation){
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

            try {
                String address = "";
                List<Address> addressList = geocoder.getFromLocation(myPosition.getLatitude(),myPosition.getLongitude(),1);

                if (addressList!=null && addressList.size()>0){

                    if(addressList.get(0).getLocality() != null){
                        txtCiudad.setText(addressList.get(0).getLocality() );
                    }

                }



            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }



}
