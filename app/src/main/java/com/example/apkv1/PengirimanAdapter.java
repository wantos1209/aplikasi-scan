package com.example.apkv1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import java.util.List;

public class PengirimanAdapter extends RecyclerView.Adapter<PengirimanAdapter.ViewHolder> {

    private List<PengirimanResponse.Pengiriman> pengirimanList;
    private Context context;

    // Constructor untuk adapter
    public PengirimanAdapter(List<PengirimanResponse.Pengiriman> pengirimanList, Context context) {
        this.pengirimanList = pengirimanList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pengiriman, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PengirimanResponse.Pengiriman pengiriman = pengirimanList.get(position);

        holder.nomorTextView.setText("Nomor: " + pengiriman.getNomor());
        holder.tanggalTextView.setText("Tanggal: " + pengiriman.getCreated_at());
        holder.totalBarangTextView.setText("Total Barang: " + pengiriman.getTotalbarang());
        holder.totalBarangMissTextView.setText("Barang Miss: " + pengiriman.getTotalbarang_miss());

        // Menangani klik tombol Detail
        holder.detailButton.setOnClickListener(v -> {
            if (pengiriman != null) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("pengiriman_id", pengiriman.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pengirimanList.size();
    }

    // Metode untuk menambahkan pengiriman baru di awal daftar
    public void addPengiriman(PengirimanResponse.Pengiriman pengiriman) {
        pengirimanList.add(0, pengiriman); // Menambahkan item baru ke posisi pertama
        notifyItemInserted(0); // Memberi tahu adapter bahwa item baru ditambahkan di posisi pertama
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomorTextView, tanggalTextView, totalBarangTextView, totalBarangMissTextView;
        Button detailButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomorTextView = itemView.findViewById(R.id.nomorTextView);
            tanggalTextView = itemView.findViewById(R.id.tanggalTextView);
            totalBarangTextView = itemView.findViewById(R.id.totalBarangTextView);
            totalBarangMissTextView = itemView.findViewById(R.id.totalBarangMissTextView);
            detailButton = itemView.findViewById(R.id.detailButton);
        }
    }
}
