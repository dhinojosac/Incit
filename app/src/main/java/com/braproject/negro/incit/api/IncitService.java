package com.braproject.negro.incit.api;

import com.braproject.negro.incit.models.incit.model.IncidentResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by negro-PC on 31-Mar-17.
 */

public interface IncitService {
    @GET("JSON.php")
    Call<IncidentResponse> getIncidents();
}
