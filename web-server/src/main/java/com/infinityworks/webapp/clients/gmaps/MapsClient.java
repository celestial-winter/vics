package com.infinityworks.webapp.clients.gmaps;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MapsClient {
    /**
     * Gets the streets in the given ward
     *
     * @param wardCode the code of the ward to get the streets for
     * @return a collection of all streets in the given ward.
     */
    @GET("geocode/json")
    Call<Object> reverseLookupAddress(@Query("address") String wardCode, @Query("key") String apiKey);
}
