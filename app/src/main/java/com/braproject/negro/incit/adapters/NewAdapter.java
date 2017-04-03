package com.braproject.negro.incit.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.braproject.negro.incit.R;
import com.braproject.negro.incit.models.incit.model.Incidencia;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by negro-PC on 31-Mar-17.
 */

public class NewAdapter extends BaseAdapter {

    private ArrayList<Incidencia> listadoIncidentes;
    private ArrayList<Incidencia> listadoIncidentesFiltrados;
    LayoutInflater lInflater;
    private Context context;

    private NewAdapter.DistanceFilter dFilter;


    public NewAdapter(Context context, ArrayList<Incidencia> listadoIncidentes) {
        lInflater = LayoutInflater.from(context);
        this.context = context;
        this.listadoIncidentes = listadoIncidentes;

    }


    private class ViewHolder {
        TextView tituloIncidente;
        TextView subtiuloIncidente;
        ImageView imagenIncidente;
    }

    @Override
    public int getCount() {
        return listadoIncidentes.size();
    }

    @Override
    public Object getItem(int position) {
        return listadoIncidentes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = lInflater.inflate(R.layout.incidentevista, null);
        Incidencia incidente = listadoIncidentes.get(position);

        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.argb(80, 174, 211, 250));
        }


        TextView tituloIncidente = (TextView) convertView.findViewById(R.id.titulo);
        TextView subtiuloIncidente = (TextView) convertView.findViewById(R.id.subtitulo);
        TextView distanciaIncidente = (TextView) convertView.findViewById(R.id.lbl_distancia);
        TextView eyesIncidente = (TextView) convertView.findViewById(R.id.lbl_eyes);
        ImageView imagenIncidente = (ImageView) convertView.findViewById(R.id.logo);

        // Log.i("Adaptador", "categoria :"+incidente.getCategoria());

        switch (incidente.getTipoIncidente()) {
            case "Colision":
                imagenIncidente.setImageResource(R.drawable.ic_colision);
                break;
            case "Colisión":
                imagenIncidente.setImageResource(R.drawable.ic_colision);
                break;
            case "Arreglos":
                imagenIncidente.setImageResource(R.drawable.ic_arreglos);
                break;
            case "Trafico":
                imagenIncidente.setImageResource(R.drawable.ic_trafico);
                break;
            case "Precaución":
                imagenIncidente.setImageResource(R.drawable.ic_peligro);
                break;
            case "Emergencia":
                imagenIncidente.setImageResource(R.drawable.ic_emergencia);
                convertView.setBackgroundColor(Color.argb(70, 254, 1, 1));
                //TODO: implementar metodo para mantere fijo de los primeros el incidente de emergencia!
                break;
            default:
                imagenIncidente.setImageResource(R.drawable.ic_incident);
                break;

        }



        tituloIncidente.setText(incidente.getTitulo());
        subtiuloIncidente.setText(incidente.getSubtitulo());
        eyesIncidente.setText("Dist");
//        Log.d("ADPTR-DST", incidente.getDistance());
        if (incidente.getDistance() != null) {
            distanciaIncidente.setText(String.format("%s km", truncateDistance(incidente.getDistance())));
        }


        return convertView;
    }



    public Filter getFilter(int i) {
        Filter f = null;
        switch (i) {
            case 0:
                if (dFilter == null) {
                    dFilter = new NewAdapter.DistanceFilter();                }
                f = dFilter;
                break;
        }
        return f;
    }


    //Filtro de distancia
    private class DistanceFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            double radio = Double.parseDouble(String.valueOf(charSequence));
            FilterResults result = new FilterResults();

            if (charSequence.toString().length() > 0) {
                ArrayList<Incidencia> filteredIncidents = new ArrayList<Incidencia>();

                for (int i = 0, l = listadoIncidentes.size(); i < l; i++) {
                    Incidencia incidente = listadoIncidentes.get(i);
                    Log.d("FILTRO-Dist", i + " " + incidente.getDistance());
                    if (radio >= Double.valueOf(incidente.getDistance())) {
                        filteredIncidents.add(incidente);
                        Log.d("FILTRO", i + "-" + incidente.getTitulo());
                    }
                    result.count = filteredIncidents.size();
                    result.values = filteredIncidents;

                }
            } else {
                synchronized (this) {
                    result.values = listadoIncidentes;
                    result.count = listadoIncidentes.size();
                }
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listadoIncidentesFiltrados = (ArrayList<Incidencia>) filterResults.values;
            notifyDataSetChanged();
            listadoIncidentes.clear();
            //TODO: si saco esos comentarios me queda la embarrada, analizar el if
            //if (listadoIncidentes.size() != 0) {
            for (int i = 0, l = listadoIncidentesFiltrados.size(); i < l; i++)
                listadoIncidentes.add(listadoIncidentesFiltrados.get(i));
            //}

            notifyDataSetInvalidated();

        }
    }


    public String truncateDistance(String distance) {
        float distanceNumber = Float.parseFloat(distance);
        //float distanceNumber = Float.parseFloat(distance);
        distanceNumber = distanceNumber / 1000;
        DecimalFormat df = new DecimalFormat("##.##");
        df.setRoundingMode(RoundingMode.DOWN);
        return String.valueOf(df.format(distanceNumber));
    }



}