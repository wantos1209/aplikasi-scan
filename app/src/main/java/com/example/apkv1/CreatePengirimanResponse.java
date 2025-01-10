package com.example.apkv1;

public class CreatePengirimanResponse {
    private String status;
    private String message;
    private Data data;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        private int id;
        private String nomor;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
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
    }
}
