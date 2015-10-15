package com.braproject.negro.incit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.braproject.negro.incit.R;
import com.braproject.negro.incit.util.Incidente;

import java.util.ArrayList;

/**
 * Created by negro on 13-08-2015.
 */

public class IncidentesAdapter extends BaseAdapter {

    private ArrayList<Incidente> listadoIncidentes;
    LayoutInflater lInflater;
    private Context context;


    public IncidentesAdapter(Context context, ArrayList<Incidente> listadoIncidentes) {
        lInflater = LayoutInflater.from(context);
        this.context = context;
        this.listadoIncidentes = listadoIncidentes;

    }

    private class ViewHolder{
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

        convertView = lInflater.inflate(R.layout.incidentevista,null);
        Incidente incidente = listadoIncidentes.get(position);

        TextView tituloIncidente = (TextView) convertView.findViewById(R.id.titulo);
        TextView subtiuloIncidente = (TextView) convertView.findViewById(R.id.subtitulo);
        ImageView imagenIncidente = (ImageView) convertView.findViewById(R.id.logo);

        //imagenIncidente.setImageResource(incidente.getImagen());
        tituloIncidente.setText(incidente.getTitulo());
        subtiuloIncidente.setText(incidente.getSubtitulo());

        return convertView;
    }




}
