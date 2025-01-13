package com.example.apkv1;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface ApiService {

    // API login
    @Headers("Authorization: Bearer mSFuY1ze5o9qT6oIAjiwYiphpmqQJ0QipjGoGJViFHellSkP2ToZgYMwR7bb7Tbf")
    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // API Pengiriman
    // API Pengiriman
    @POST("api/pengiriman/{subarea_id}")
    Call<PengirimanResponse> getPengiriman(@Header("Authorization") String token, @Path("subarea_id") String subareaId);

    @POST("api/createpengiriman")
    Call<CreatePengirimanResponse> createPengiriman(
            @Header("Authorization") String token,
            @Body CreatePengirimanRequest request
    );
    @POST("api/pengirimandetail/{pengiriman_id}")
    Call<DetailResponse> getPengirimanDetail(
            @Header("Authorization") String token,
            @retrofit2.http.Path("pengiriman_id") int pengirimanId
    );

    @POST("api/getdestinasi/{kode}")
    Call<DestinasiResponse> getDestinasi(
            @Header("Authorization") String token,
            @retrofit2.http.Path("kode") String kode,
            @Body DestinasiRequest body
    );

    @POST("api/getdestinasi/{no_stt}")
    Call<DestinasiResponse> getDestination(
            @Header("Authorization") String token,
            @retrofit2.http.Path("no_stt") String noStt,
            @Body DestinasiRequest request
    );

    @DELETE("api/pengiriman/{pengiriman_id}/details/{pengirimandetail_id}")
    Call<ApiResponse> deletePengirimanDetail(
            @Header("Authorization") String token,
            @Path("pengiriman_id") int pengirimanId,
            @Path("pengirimandetail_id") int pengirimanDetailId
    );

    @POST("api/listsubarea")
    Call<SubareaResponse> getSubareas(@Header("Authorization") String token);
}