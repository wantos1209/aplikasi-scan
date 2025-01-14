package com.example.apkv1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.core.content.ContextCompat; // Pastikan import ini ada

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {

    private List<DetailResponse.DataDetail> detailList;
    private int pengirimanId;
    private Context context;
    private int subareaId;

    public DetailAdapter(List<DetailResponse.DataDetail> detailList, int pengirimanId, Context context, int subareaId) {
        this.detailList = detailList;
        this.pengirimanId = pengirimanId;
        this.context = context;
        this.subareaId = subareaId;  // Set subarea_id dari data utama
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetailResponse.DataDetail detail = detailList.get(position);
        holder.noSttTextView.setText("No STT: " + detail.getNo_stt());
        holder.subareaTextView.setText("Subarea: " + detail.getSubarea_nama());

        // Set background berdasarkan perbandingan subarea_id
        if (detail.getSubarea_id() != subareaId) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.pink)); // Background pink jika tidak cocok
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent)); // Default jika cocok
        }

        // Tombol Remove dengan dialog konfirmasi
        holder.removeButton.setOnClickListener(v -> {
            // Log untuk melihat nilai detail.getId()
            Log.d("DetailActivity", "ID STT yang akan dihapus: " + detail.getId());

            new AlertDialog.Builder(context)
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah Anda yakin ingin menghapus nomor STT ini?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        // Hapus item dari list terlebih dahulu
                        int currentPosition = holder.getAdapterPosition(); // Pastikan posisi yang benar
                        deleteDetail(detail.getId(), currentPosition);
                    })
                    .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                    .show();
        });

    }

    private void deleteDetail(int detailId, int position) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://purple-crocodile-829455.hostingersite.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<ApiResponse> call = apiService.deletePengirimanDetail("Bearer " + token, pengirimanId, detailId);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if ("success".equalsIgnoreCase(apiResponse.getStatus())) {
                        Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        // Hapus dari daftar di posisi yang benar
                        detailList.remove(position); // Hapus item dari list berdasarkan posisi
                        notifyItemRemoved(position); // Notifikasi RecyclerView untuk menghapus item
                    } else {
                        Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Gagal menghapus detail: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(context, "Request gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return detailList.size();
    }

    // Menambahkan metode untuk mendapatkan daftar data detail
    public List<DetailResponse.DataDetail> getDetailList() {
        return detailList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView noSttTextView, subareaTextView;
        Button removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noSttTextView = itemView.findViewById(R.id.noSttTextView);
            subareaTextView = itemView.findViewById(R.id.subareaTextView);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}



