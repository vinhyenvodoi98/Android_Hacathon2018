package com.example.hanh.ava_android.remote;

import com.example.hanh.ava_android.model.Map;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface MapService {

    @POST("api/local")
    Call<Map> createMap(@Header("Content-Type") String contentType,
                        @Body java.util.Map local);


}
