package com.example.apkv1;

import java.util.List;

public class SubareaResponse {

    private String status;
    private String message;
    private List<Subarea> data;

    public List<Subarea> getData() {
        return data;
    }

    public void setData(List<Subarea> data) {
        this.data = data;
    }

    public static class Subarea {
        private String id;
        private String subarea_nama;

        public Subarea(String id, String subarea_nama) {
            this.id = id;
            this.subarea_nama = subarea_nama;
        }

        public String getId() {
            return id;
        }

        public String getSubarea_nama() {
            return subarea_nama;
        }

        @Override
        public String toString() {
            return subarea_nama;  // Menampilkan subarea_nama di Spinner
        }
    }
}
