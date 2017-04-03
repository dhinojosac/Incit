package com.braproject.negro.incit.api;

import com.braproject.negro.incit.models.incit.model.IncidentResponse;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by negro-PC on 31-Mar-17.
 */

public class IncitApiClient {
    private Retrofit retrofit;
    private final static String BASE_URL = "http://www.incit.rql.cl/";
    // Este objeto puedo construirlo en el cliente o recibirlo como parametro, en el caso de recibirlo
    // lo que haria seria injectarlo al construirlo
    // Si lo recibo como parametro el testin seria mas granular.


    public IncitApiClient() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public IncitService getBotisService() {
        return this.retrofit.create(IncitService.class);
    }
}
