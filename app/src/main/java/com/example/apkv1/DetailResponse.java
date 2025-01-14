package com.example.apkv1;

import java.util.List;

public class DetailResponse {

    private String status;
    private String message;
    private Data data;  // Data utama pengiriman
    private List<DataDetail> data_detail;  // Daftar detail pengiriman

    // Getter dan Setter untuk status, message, data, dan data_detail
    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public List<DataDetail> getDataDetail() {
        return data_detail;
    }

    // Kelas Data yang berisi data utama pengiriman
    public static class Data {
        private int id;
        private int userapk_id;
        private String nomor;
        private String areaname;
        private String created_at;
        private String updated_at;
        private int totalbarang;
        private int totalbarang_miss;
        private int subarea_id;  // subarea_id dari data utama pengiriman

        // Getter untuk subarea_id
        public int getSubarea_id() {
            return subarea_id;
        }

        // Getter lainnya
        public int getId() {
            return id;
        }

        public int getUserapk_id() {
            return userapk_id;
        }

        public String getNomor() {
            return nomor;
        }

        public String getArea() {
            return areaname;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public int getTotalbarang() {
            return totalbarang;
        }

        public int getTotalbarang_miss() {
            return totalbarang_miss;
        }
    }

    // Kelas DataDetail yang berisi detail pengiriman
    public static class DataDetail {
        private int id;
        private int pengiriman_id;
        private int subarea_id;  // subarea_id dari detail pengiriman
        private String no_stt;
        private String created_at;
        private String updated_at;
        private String subarea_nama;

        // Getter untuk subarea_id
        public int getSubarea_id() {
            return subarea_id;
        }

        // Getter lainnya
        public int getId() {
            return id;
        }

        public int getPengiriman_id() {
            return pengiriman_id;
        }

        public String getNo_stt() {
            return no_stt;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public String getSubarea_nama() {
            return subarea_nama;
        }
    }
}


