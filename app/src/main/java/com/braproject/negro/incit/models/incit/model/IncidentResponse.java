
package com.braproject.negro.incit.models.incit.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IncidentResponse {

    @SerializedName("Lista de incidencias")
    @Expose
    private List<Incidencia> incidencias = null;

    public List<Incidencia> getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(List<Incidencia> incidencias) {
        this.incidencias = incidencias;
    }


}
