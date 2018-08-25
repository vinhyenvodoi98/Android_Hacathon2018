package com.example.hanh.ava_android.remote;

import com.example.hanh.ava_android.model.AnswerInsrtuct;
import com.example.hanh.ava_android.model.Map;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MapService {

    @POST("api/local")
    Call<Map> createMap(@Header("Content-Type") String contentType,
                        @Body java.util.Map local);

    @GET("/api/local")
    Call<AnswerInsrtuct> getWaypoint(@Query("start_location_lat") Double startLocationlat,
                                         @Query("start_location_lng") Double startLocationlng,
                                         @Query("end_location_lat") Double endLocationlat,
                                         @Query("end_location_lng") Double endLocationlng);


}
