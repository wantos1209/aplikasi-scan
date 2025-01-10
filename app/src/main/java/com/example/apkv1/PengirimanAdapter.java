package com.example.apkv1;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PengirimanAdapter extends RecyclerView.Adapter<PengirimanAdapter.ViewHolder> {

    private List<PengirimanResponse.Pengiriman> pengirimanList;

    public PengirimanAdapter(List<PengirimanResponse.Pengiriman> pengirimanList) {
        this.pengirimanList = pengirimanList;
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

        // Tombol Detail
        holder.detailButton.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra("pengiriman_id", pengiriman.getId());
            holder.itemView.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return pengirimanList.size();
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

    public void addPengiriman(PengirimanResponse.Pengiriman pengiriman) {
        pengirimanList.add(0, pengiriman); // Tambahkan ke posisi teratas
        notifyItemInserted(0); // Beritahu adapter bahwa item baru ditambahkan
    }
}
