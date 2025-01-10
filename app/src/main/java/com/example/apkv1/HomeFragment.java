package com.example.apkv1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PengirimanAdapter adapter;
    private Button createButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        createButton = rootView.findViewById(R.id.createButton);
        createButton.setOnClickListener(v -> createPengiriman());

        fetchPengirimanData();

        return rootView;
    }

    private void fetchPengirimanData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyApp", getContext().MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://purple-crocodile-829455.hostingersite.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<PengirimanResponse> call = apiService.getPengiriman("Bearer " + token);
        call.enqueue(new Callback<PengirimanResponse>() {
            @Override
            public void onResponse(Call<PengirimanResponse> call, Response<PengirimanResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new PengirimanAdapter(response.body().getData());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Gagal memuat data: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PengirimanResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Request gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createPengiriman() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyApp", getContext().MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://purple-crocodile-829455.hostingersite.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<CreatePengirimanResponse> call = apiService.createPengiriman("Bearer " + token);
        call.enqueue(new Callback<CreatePengirimanResponse>() {
            @Override
            public void onResponse(Call<CreatePengirimanResponse> call, Response<CreatePengirimanResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Dapatkan data pengiriman baru dari respons
                    CreatePengirimanResponse.Data pengirimanBaru = response.body().getData();

                    // Tambahkan data pengiriman baru ke adapter
                    if (adapter != null) {
                        PengirimanResponse.Pengiriman pengiriman = new PengirimanResponse.Pengiriman();
                        pengiriman.setId(pengirimanBaru.getId());
                        pengiriman.setNomor(pengirimanBaru.getNomor());
                        pengiriman.setCreated_at(pengirimanBaru.getCreated_at());
                        pengiriman.setTotalbarang(0); // Default nilai untuk barang
                        pengiriman.setTotalbarang_miss(0); // Default nilai untuk barang miss
                        adapter.addPengiriman(pengiriman); // Tambahkan ke RecyclerView
                    }

                    // Navigasikan ke DetailActivity
                    Intent intent = new Intent(getContext(), DetailActivity.class);
                    intent.putExtra("pengiriman_id", pengirimanBaru.getId());
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Gagal membuat pengiriman: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreatePengirimanResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Request gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
