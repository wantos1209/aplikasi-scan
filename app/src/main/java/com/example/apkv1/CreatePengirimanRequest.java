package com.example.apkv1;

public class CreatePengirimanRequest {

    private String subarea_id;

    public CreatePengirimanRequest(String subarea_id) {
        this.subarea_id = subarea_id;
    }

    public String getSubarea_id() {
        return subarea_id;
    }

    public void setSubarea_id(String subarea_id) {
        this.subarea_id = subarea_id;
    }
}
