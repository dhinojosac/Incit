package com.braproject.negro.incit.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by:       Diego O. Hinojosa Córdova.
 * Date:            10-08-2015
 * Project_name:    Incit
 * Paquege name:    Incit
 */
public class Incidente implements Parcelable{

    private String titulo;
    private String subtitulo;
    private String latitud;
    private String longitud;
    private String categoria;
    private String transporteAfectado;
    private int imagen;
    private String distance;

    public Incidente() {
        this.titulo = null;
        this.subtitulo = null;
        this.latitud = null;
        this.longitud = null;
        this.categoria = null;
        this.transporteAfectado = null;
        this.imagen = -1;
        this.distance = null;
    }

    public Incidente(String titulo,
                     String subtitulo,
                     String latitud,
                     String longitud,
                     String categoria,
                     String transporteAfectado,
                     int imagen,
                     String distance) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.categoria = categoria;
        this.transporteAfectado = transporteAfectado;
        this.imagen = imagen;
        this.distance =distance;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTransporteAfectado() {
        return transporteAfectado;
    }

    public void setTransporteAfectado(String transporteAfectado) {
        this.transporteAfectado = transporteAfectado;
    }

    public int getImagen() { return imagen;    }

    public void setImagen(int imagen) {   this.imagen = imagen;    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return titulo + "\n" + subtitulo + "\n" + "Lat:"+ latitud + " Lng:" + longitud;
    }

    public String toShare() {
        return "ATENCIÓN:\n"+titulo + "\n" + subtitulo + "\n" +
        "maps.google.com/maps?&z=10&q="+latitud+"+"+longitud+"&ll="+latitud+"+"+longitud;
    }


// AGREGADO PARA PARCEL

    protected Incidente(Parcel in) {
        titulo = in.readString();
        subtitulo = in.readString();
        latitud = in.readString();
        longitud = in.readString();
        categoria= in.readString();
        transporteAfectado= in.readString();
        imagen= in.readInt();
        distance =in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(titulo);
        dest.writeString(subtitulo);
        dest.writeString(latitud);
        dest.writeString(longitud);
        dest.writeString(categoria);
        dest.writeString(transporteAfectado);
        dest.writeInt(imagen);
        dest.writeString(distance);
    }

    @SuppressWarnings("unused")
    public static final Creator<Incidente> CREATOR = new Creator<Incidente>() {
        @Override
        public Incidente createFromParcel(Parcel in) {
            return new Incidente(in);
        }

        @Override
        public Incidente[] newArray(int size) {
            return new Incidente[size];
        }
    };
}

