package com.example.apkv1;

import java.util.List;

public class PengirimanResponse {

    private String status;
    private String message;
    private List<Pengiriman> data;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<Pengiriman> getData() {
        return data;
    }

    public static class Pengiriman {
        private int id;
        private String nomor;
        private String created_at;
        private int totalbarang;
        private int totalbarang_miss;

        // Getter dan Setter
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNomor() {
            return nomor;
        }

        public void setNomor(String nomor) {
            this.nomor = nomor;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public int getTotalbarang() {
            return totalbarang;
        }

        public void setTotalbarang(int totalbarang) {
            this.totalbarang = totalbarang;
        }

        public int getTotalbarang_miss() {
            return totalbarang_miss;
        }

        public void setTotalbarang_miss(int totalbarang_miss) {
            this.totalbarang_miss = totalbarang_miss;
        }
    }
}
