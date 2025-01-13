package com.example.apkv1;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Untuk menulis ke Excel
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.content.Context;
import androidx.core.app.NotificationCompat;

public class DetailActivity extends AppCompatActivity {

    private TextView nomorTextView, createdAtTextView, totalBarangTextView, totalBarangMissTextView;
    private RecyclerView recyclerView;
    private DetailAdapter adapter;
    private Button backButton, manualButton, downloadButton;
    private FloatingActionButton fab;

    // Scanner launcher
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    submitData(result.getContents());
                } else {
                    Toast.makeText(this, "Scan dibatalkan", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        nomorTextView = findViewById(R.id.nomorTextView);
        createdAtTextView = findViewById(R.id.createdAtTextView);
        totalBarangTextView = findViewById(R.id.totalBarangTextView);
        totalBarangMissTextView = findViewById(R.id.totalBarangMissTextView);
        recyclerView = findViewById(R.id.recyclerView);

        // Back button
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Tombol Download
        downloadButton = findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(v -> exportToExcel());

        // Manual button
        manualButton = findViewById(R.id.manualButton);
        manualButton.setOnClickListener(v -> showManualInputDialog());

        // Scan button
        fab = findViewById(R.id.feb);
        fab.setOnClickListener(v -> startScanner());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int pengirimanId = getIntent().getIntExtra("pengiriman_id", -1);
        if (pengirimanId != -1) {
            fetchDetail(pengirimanId);
        } else {
            Toast.makeText(this, "Invalid Pengiriman ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchDetail(int pengirimanId) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://purple-crocodile-829455.hostingersite.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<DetailResponse> call = apiService.getPengirimanDetail("Bearer " + token, pengirimanId);
        call.enqueue(new Callback<DetailResponse>() {
            @Override
            public void onResponse(Call<DetailResponse> call, Response<DetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DetailResponse.Data data = response.body().getData();

                    nomorTextView.setText("Nomor: " + data.getNomor());
                    createdAtTextView.setText("Tanggal: " + data.getCreated_at());
                    totalBarangTextView.setText("Total Barang: " + data.getTotalbarang());
                    totalBarangMissTextView.setText("Total Miss: " + data.getTotalbarang_miss());

                    int subareaId = data.getSubarea_id(); // Ambil subarea_id dari data utama

                    // Mengurutkan data detail sebelum dikirim ke adapter
                    List<DetailResponse.DataDetail> sortedDetailList = sortDetailList(response.body().getDataDetail(), subareaId);

                    // Pass subarea_id dan sorted list ke adapter
                    adapter = new DetailAdapter(sortedDetailList, pengirimanId, DetailActivity.this, subareaId);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(DetailActivity.this, "Gagal memuat detail", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DetailResponse> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Request gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fungsi untuk mengurutkan data_detail
    private List<DetailResponse.DataDetail> sortDetailList(List<DetailResponse.DataDetail> dataList, int subareaId) {
        // Membagi menjadi dua list: yang cocok dan tidak cocok
        List<DetailResponse.DataDetail> unmatched = new ArrayList<>();
        List<DetailResponse.DataDetail> matched = new ArrayList<>();

        for (DetailResponse.DataDetail detail : dataList) {
            if (detail.getSubarea_id() != subareaId) {
                unmatched.add(detail); // Tidak cocok, taruh di atas
            } else {
                matched.add(detail); // Cocok, taruh di bawah
            }
        }

        // Gabungkan unmatched di depan matched
        unmatched.addAll(matched);
        return unmatched; // Kembalikan hasil yang sudah diurutkan
    }

    private void startScanner() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
        options.setPrompt("Arahkan kamera ke barcode/QR code");
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(false);
        barcodeLauncher.launch(options);
    }

    private void showManualInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_manual_input, null);
        builder.setView(dialogView);

        EditText inputField = dialogView.findViewById(R.id.inputNoStt);
        Button submitButton = dialogView.findViewById(R.id.submitButton);

        AlertDialog dialog = builder.create();

        submitButton.setOnClickListener(v -> {
            String input = inputField.getText().toString().trim();
            if (!input.isEmpty()) {
                submitData(input);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Harap masukkan no_stt", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void submitData(String noStt) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        int pengirimanId = getIntent().getIntExtra("pengiriman_id", -1);

        if (pengirimanId == -1) {
            Toast.makeText(this, "Pengiriman ID tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://purple-crocodile-829455.hostingersite.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Ensure the method matches this request
        Call<DestinasiResponse> call = apiService.getDestination("Bearer " + token, noStt, new DestinasiRequest(pengirimanId));
        call.enqueue(new Callback<DestinasiResponse>() {
            @Override
            public void onResponse(Call<DestinasiResponse> call, Response<DestinasiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DestinasiResponse destinasiResponse = response.body();
                    if (destinasiResponse.isExist()) {
                        Toast.makeText(DetailActivity.this, "stt berhasil diinput", Toast.LENGTH_SHORT).show();
                        fetchDetail(pengirimanId); // Refresh detail
                    } else {
                        Toast.makeText(DetailActivity.this, "no_stt sudah diinput atau tidak valid", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetailActivity.this, "Gagal memproses data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DestinasiResponse> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Request gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fungsi exportToExcel
    private void exportToExcel() {
        if (adapter == null || adapter.getItemCount() == 0) {
            Toast.makeText(this, "Tidak ada data untuk diekspor!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Simpan file di folder Download
            File downloadsDir = new File(getExternalFilesDir(null), "Download");
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs(); // Buat folder jika belum ada
            }

            File file = new File(downloadsDir, "DetailPengiriman.xls");
            WritableWorkbook workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("Detail Pengiriman", 0);

            // Header
            sheet.addCell(new Label(0, 0, "No STT"));
            sheet.addCell(new Label(1, 0, "Subarea"));

            // Data
            List<DetailResponse.DataDetail> detailList = adapter.getDetailList();
            for (int i = 0; i < detailList.size(); i++) {
                DetailResponse.DataDetail detail = detailList.get(i);
                sheet.addCell(new Label(0, i + 1, detail.getNo_stt()));
                sheet.addCell(new Label(1, i + 1, detail.getSubarea_nama()));
            }

            workbook.write();
            workbook.close();

            // Berikan notifikasi setelah file berhasil disimpan
            sendNotification(file.getPath());
            Toast.makeText(this, "File berhasil diunduh: " + file.getPath(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Gagal mengekspor data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void sendNotification(String filePath) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "download_channel";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Download Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.stat_sys_download_done) // Ikon notifikasi
                .setContentTitle("Download Selesai")
                .setContentText("File disimpan di: " + filePath)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }
}
