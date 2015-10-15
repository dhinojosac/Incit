package com.braproject.negro.incit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.braproject.negro.incit.adapters.IncidentesAdapter;
import com.braproject.negro.incit.util.Incidente;
import com.braproject.negro.incit.util.XMLPullParserHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener,
        com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    static final String TAG = "HomeActivity";
    ArrayList<Incidente> incidentes;
    ArrayList<Incidente> incidentes_all= null;
    ArrayList<Incidente> ownIncidentes2= null;

    IncidentesAdapter adaptador;



    Button botonMapa;
    Button botonRefresh;
    TextView txtCiudad;
    ListView lista;

    private double RADIO = 10000;

    int request_Code = 1;

    //Location
    private Location myPosition = new Location("Mi posicion"); //NEW
    private Location mLastLocation; //NEW
    protected GoogleApiClient googleApiClient;
    protected Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();
        googleApiClient.connect();

        getPosition();
        Log.i(TAG, myPosition.toString());

        setUpInterfaceLayout();

        //lee la lista de incidentes desde la actividad anterior
        parserXML();
        //readList();


        myPosition = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        incidentes_all = incidentes;
        adaptador = new IncidentesAdapter(MainActivity.this.getApplicationContext(),incidentes);
        lista.setAdapter(adaptador);

        setUpLisnterer();


        //setDistanceToList();
    }


    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient(){
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_settings:
                break;
            case R.id.action_share:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void readList(){
        incidentes =  getIntent().getParcelableArrayListExtra("LISTA");
       /* //debug
        for (int i=0; i<incidentes.size(); i++){
            Log.d("MapActivity", incidentes.get(i).getTitulo());
        }
       */
    }



    //Implementacion onClick de los botones
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.boton_mapa_header:

                /** StartActivityForResult**/
                Intent i = new Intent(MainActivity.this, MapsIncidentActivity.class);
                i.putParcelableArrayListExtra("LISTA", (ArrayList) incidentes);

                SharedPreferences prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
                double myRadio = Double.longBitsToDouble(prefs.getLong("mRadio", 10000));
                i.putExtra("RADIO_send", myRadio);

                startActivityForResult(i, request_Code);
                break;

            case R.id.boton_refresh:

                /**Implementar**/

        }


    }

    /** PARA RECIBIR EL RADIO DESDE MAPACTIVITY **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                double result=data.getDoubleExtra("RADIO", 1);
                Log.d("ForResult", String.valueOf(result));//Debug
                Toast.makeText(getBaseContext(), "Su radio de busqueda: " + (result / 1000) + "KM", Toast.LENGTH_SHORT).show();

                //preferencias de usaurio
                SharedPreferences prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("mRadio", Double.doubleToLongBits(result));
                editor.commit();

            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
                Toast.makeText(getBaseContext(),"No se pudo fijar un radio",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Implementacion ItemClick
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(getBaseContext(),"Presiono item: "+position,Toast.LENGTH_SHORT).show(); //DEBUG

        /*
        Incidente incidente = new Incidente((Parcel) parent.getItemAtPosition(position));
        Intent sendIntet = new Intent(HomeActivity.this, MapOnceIncidentActivity.class);
        sendIntet.putExtras(incidente);
        startActivity(sendIntet);
        */
    }

    //Implemantacion ItemLongClick SHARE OPTION
    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
       // Toast.makeText(getBaseContext(),"Presion Larga item: "+position,Toast.LENGTH_SHORT).show();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Alert!!");
        alert.setMessage("Are you sure to delete record");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do your work here
                dialog.dismiss();

            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alert.show();

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



    public float getDistance(Location mPosition, LatLng mIncident){
        Location location2 = new Location("Destino");
        location2.setLatitude(mIncident.latitude);
        location2.setLongitude(mIncident.longitude);

        return mPosition.distanceTo(location2);
    }

    @Override
    public void onLocationChanged(Location location) {
        myPosition.setLatitude(location.getLatitude());
        myPosition.setLongitude(location.getLongitude());
        Log.d(TAG, "Cambio de posicion: " + myPosition.getLatitude() + "," + myPosition.getLongitude());

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "GoogleApiclient Connected...");

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30000);
        LocationServices.FusedLocationApi.
                requestLocationUpdates(googleApiClient, mLocationRequest, this);

        myPosition =  LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        googleApiClient.connect();
    }

    public void onDisconnected(){
        Log.i(TAG, "Disconnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: " + connectionResult.getErrorCode());
    }



    public void getPosition(){
        myPosition.setLatitude(getIntent().getDoubleExtra("POSITION_LAT", 0));
        myPosition.setLongitude(getIntent().getDoubleExtra("POSITION_LNG", 0));
    }


    private void parserXML(){
        try {
            XMLPullParserHandler parser = new XMLPullParserHandler();
            incidentes = (ArrayList<Incidente>) parser.parse(getAssets().open("Incidentes.xml"));
            incidentes_all = incidentes;

            for (int i=0; i<incidentes.size(); i++){
                Log.d("LoadingScreen", incidentes.get(i).getTitulo()); //DEBUG
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpLisnterer(){
        //listener OnClick de botones
        botonMapa.setOnClickListener(this);
        botonRefresh.setOnClickListener(this);

        //listener para click y longClick de cada item
        lista.setOnItemClickListener(this);
        lista.setOnItemLongClickListener(this);

        //menu para cada item de la lista
        registerForContextMenu(lista);
    }

    private void setUpInterfaceLayout(){
        //obtener View desde layout
        lista = (ListView) findViewById(R.id.list);
        botonMapa = (Button) findViewById(R.id.boton_mapa_header);
        botonRefresh = (Button) findViewById(R.id.boton_refresh);
        txtCiudad = (TextView) findViewById(R.id.header_ciudad);

    }

    //TODO: esto se puede implementar en el servidor de internet, para que cada incidente tenga la distancia.
    private void setDistanceToList(){
        for(int i =0; i<incidentes_all.size(); i++){
            Incidente inc = incidentes_all.get(i);
            LatLng latLng = new LatLng(Double.valueOf(inc.getLatitud()),Double.valueOf(inc.getLongitud()));
            inc.setDistance(String.valueOf(distanceToMe(myPosition, latLng)));
            Log.i("Distancias: ", inc.getTitulo());
            Log.i("Distancias: ",String.valueOf(distanceToMe(myPosition, latLng)));
        }
    }
    private double distanceToMe(Location myPosition, LatLng latLng){
        double dist=0;
        Location incidente = new Location("incidente");
        incidente.setLatitude(latLng.latitude);
        incidente.setLongitude(latLng.longitude);
        dist = myPosition.distanceTo(incidente);
        return dist;
    }


}
