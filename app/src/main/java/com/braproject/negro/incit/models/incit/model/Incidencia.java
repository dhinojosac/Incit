
package com.braproject.negro.incit.models.incit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Incidencia {

    @SerializedName("id_evento")
    @Expose
    private String idEvento;
    @SerializedName("ciudad")
    @Expose
    private String ciudad;
    @SerializedName("sector")
    @Expose
    private String sector;
    @SerializedName("titulo")
    @Expose
    private String titulo;
    @SerializedName("subtitulo")
    @Expose
    private String subtitulo;
    @SerializedName("tipo_incidente")
    @Expose
    private String tipoIncidente;
    @SerializedName("categoria")
    @Expose
    private String categoria;
    @SerializedName("informacion")
    @Expose
    private String informacion;
    @SerializedName("latitud")
    @Expose
    private String latitud;
    @SerializedName("longitud")
    @Expose
    private String longitud;
    @SerializedName("fecha_inicio")
    @Expose
    private String fechaInicio;
    @SerializedName("hora_inicio")
    @Expose
    private String horaInicio;
    @SerializedName("fecha_fin")
    @Expose
    private String fechaFin;
    @SerializedName("hora_fin")
    @Expose
    private String horaFin;
    @SerializedName("fecha_act")
    @Expose
    private String fechaAct;
    @SerializedName("hora_act")
    @Expose
    private String horaAct;
    @SerializedName("autor")
    @Expose
    private String autor;
    @SerializedName("trans_afectado")
    @Expose
    private String transAfectado;
    private String distance;

    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
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

    public String getTipoIncidente() {
        return tipoIncidente;
    }

    public void setTipoIncidente(String tipoIncidente) {
        this.tipoIncidente = tipoIncidente;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getInformacion() {
        return informacion;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
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

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getFechaAct() {
        return fechaAct;
    }

    public void setFechaAct(String fechaAct) {
        this.fechaAct = fechaAct;
    }

    public String getHoraAct() {
        return horaAct;
    }

    public void setHoraAct(String horaAct) {
        this.horaAct = horaAct;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getTransAfectado() {
        return transAfectado;
    }

    public void setTransAfectado(String transAfectado) {
        this.transAfectado = transAfectado;
    }

    @Override
    public String toString() {
        return titulo + "\n" + subtitulo + "\n" + "Lat:"+ latitud + " Lng:" + longitud;
    }

    public String toShare() {
        return "ATENCIÓN:\n"+titulo + "\n" + subtitulo + "\n" +
                "maps.google.com/maps?&z=10&q="+latitud+"+"+longitud+"&ll="+latitud+"+"+longitud;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
