package com.example.apkv1;

import java.util.List;

public class DetailResponse {
    private String status;
    private String message;
    private Data data;
    private List<DataDetail> data_detail;

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

    public static class Data {
        private int id;
        private int userapk_id;
        private String nomor;
        private String created_at;
        private String updated_at;
        private int totalbarang;
        private int totalbarang_miss;

        public int getId() {
            return id;
        }

        public int getUserapk_id() {
            return userapk_id;
        }

        public String getNomor() {
            return nomor;
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

    public static class DataDetail {
        private int id;
        private int pengiriman_id;
        private int subarea_id;
        private String no_stt;
        private String created_at;
        private String updated_at;
        private String subarea_nama;

        public int getId() {
            return id;
        }

        public int getPengiriman_id() {
            return pengiriman_id;
        }

        public int getSubarea_id() {
            return subarea_id;
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
