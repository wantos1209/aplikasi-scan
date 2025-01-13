package com.example.apkv1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ProgressBar;

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

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PengirimanAdapter adapter;
    private Button createButton;
    private Button refreshButton;
    private Spinner subareaSpinner;
    private ProgressBar loadingProgressBar;  // Declare ProgressBar

    private List<SubareaResponse.Subarea> subareaList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        createButton = rootView.findViewById(R.id.createButton);
        createButton.setOnClickListener(v -> createPengiriman());

        refreshButton = rootView.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> refreshPengirimanData()); // Mengaktifkan tombol refresh

        subareaSpinner = rootView.findViewById(R.id.subareaSpinner);
        loadingProgressBar = rootView.findViewById(R.id.loadingProgressBar); // Initialize ProgressBar

        // Memanggil API untuk mengambil daftar subarea
        fetchSubareas();

        return rootView;
    }

    private void fetchSubareas() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyApp", getContext().MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://purple-crocodile-829455.hostingersite.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Mengambil daftar subarea menggunakan API
        Call<SubareaResponse> call = apiService.getSubareas("Bearer " + token);
        call.enqueue(new Callback<SubareaResponse>() {
            @Override
            public void onResponse(Call<SubareaResponse> call, Response<SubareaResponse> response) {
                // Hide loading indicator after response
                loadingProgressBar.setVisibility(View.GONE); // Hide loading progress bar
                if (response.isSuccessful() && response.body() != null) {
                    SubareaResponse subareaResponse = response.body();
                    subareaList = subareaResponse.getData();

                    // Menambahkan opsi "Pilih Area Kecamatan" di paling atas
                    subareaList.add(0, new SubareaResponse.Subarea("", "Pilih Area Kecamatan"));

                    // Membuat adapter untuk Spinner
                    if (isAdded()) {
                        ArrayAdapter<SubareaResponse.Subarea> adapter = new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_spinner_item, subareaList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        subareaSpinner.setAdapter(adapter);
                    }

                    // Listener item spinner
                    subareaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            // Mengambil subarea yang dipilih
                            SubareaResponse.Subarea selectedSubarea = subareaList.get(position);
                            fetchPengiriman(selectedSubarea.getId());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            recyclerView.setVisibility(View.GONE); // Sembunyikan RecyclerView jika tidak ada subarea yang dipilih
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Gagal memuat subarea", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SubareaResponse> call, Throwable t) {
                loadingProgressBar.setVisibility(View.GONE); // Hide loading progress bar
                Toast.makeText(getContext(), "Request gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method untuk mengambil data pengiriman berdasarkan subarea yang dipilih
    private void fetchPengiriman(String subareaId) {
        if (subareaId.isEmpty()) {
            recyclerView.setVisibility(View.GONE); // Jangan tampilkan RecyclerView jika subarea tidak dipilih
            return;
        }

        // Show loading indicator
        loadingProgressBar.setVisibility(View.VISIBLE);  // Show loading progress bar
        recyclerView.setVisibility(View.GONE);  // Sembunyikan RecyclerView sementara

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyApp", getContext().MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://purple-crocodile-829455.hostingersite.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Mengambil data pengiriman berdasarkan subarea yang dipilih
        Call<PengirimanResponse> call = apiService.getPengiriman("Bearer " + token, subareaId);
        call.enqueue(new Callback<PengirimanResponse>() {
            @Override
            public void onResponse(Call<PengirimanResponse> call, Response<PengirimanResponse> response) {
                // Hide loading indicator after response
                loadingProgressBar.setVisibility(View.GONE);  // Hide loading progress bar
                if (response.isSuccessful() && response.body() != null) {
                    PengirimanResponse pengirimanResponse = response.body();
                    if (pengirimanResponse.getData().isEmpty()) {
                        recyclerView.setVisibility(View.GONE); // Tidak ada data pengiriman ditemukan
                    } else {
                        adapter = new PengirimanAdapter(pengirimanResponse.getData(), getContext());
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE); // Tampilkan RecyclerView jika data ada
                    }
                } else {
                    Toast.makeText(getContext(), "Gagal memuat pengiriman", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PengirimanResponse> call, Throwable t) {
                loadingProgressBar.setVisibility(View.GONE);  // Hide loading progress bar
                Toast.makeText(getContext(), "Request gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method untuk refresh data pengiriman
    private void refreshPengirimanData() {
        // Ambil subarea_id dari Spinner yang dipilih
        SubareaResponse.Subarea selectedSubarea = (SubareaResponse.Subarea) subareaSpinner.getSelectedItem();
        if (selectedSubarea != null) {
            fetchPengiriman(selectedSubarea.getId()); // Lakukan refresh dengan subarea yang dipilih
        }
    }

    // Method untuk membuat pengiriman baru
    private void createPengiriman() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyApp", getContext().MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        // Ambil subarea_id dari Spinner yang dipilih
        SubareaResponse.Subarea selectedSubarea = (SubareaResponse.Subarea) subareaSpinner.getSelectedItem();
        String subareaId = selectedSubarea != null ? selectedSubarea.getId() : "";

        if (subareaId.isEmpty()) {
            Toast.makeText(getContext(), "Harap pilih area", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://purple-crocodile-829455.hostingersite.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Membuat body untuk request create pengiriman
        CreatePengirimanRequest request = new CreatePengirimanRequest(subareaId);

        // Mengirimkan request create pengiriman dengan body
        Call<CreatePengirimanResponse> call = apiService.createPengiriman("Bearer " + token, request);
        call.enqueue(new Callback<CreatePengirimanResponse>() {
            @Override
            public void onResponse(Call<CreatePengirimanResponse> call, Response<CreatePengirimanResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CreatePengirimanResponse.Data pengirimanBaru = response.body().getData();

                    // Menambahkan pengiriman baru ke adapter
                    if (adapter != null) {
                        PengirimanResponse.Pengiriman pengiriman = new PengirimanResponse.Pengiriman();
                        pengiriman.setId(pengirimanBaru.getId());
                        pengiriman.setNomor(pengirimanBaru.getNomor());
                        pengiriman.setCreated_at(pengirimanBaru.getCreated_at());
                        pengiriman.setTotalbarang(0); // Default nilai untuk barang
                        pengiriman.setTotalbarang_miss(0); // Default nilai untuk barang miss

                        // Menambahkan pengiriman baru di awal daftar
                        adapter.addPengiriman(pengiriman);
                        recyclerView.scrollToPosition(0); // Scroll ke item pertama
                    }

                    // Navigasi ke DetailActivity
                    Intent intent = new Intent(getContext(), DetailActivity.class);
                    intent.putExtra("pengiriman_id", pengirimanBaru.getId());
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Gagal membuat pengiriman", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreatePengirimanResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Request gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

