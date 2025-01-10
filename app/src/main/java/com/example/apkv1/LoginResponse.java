package com.example.apkv1;

public class LoginResponse {

    private String message;
    private User user;
    private String token;

    // Getter dan Setter

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static class User {
        private int id;
        private String username;
        private String area_nama;
        private String subarea_nama;

        // Getter dan Setter

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getArea_nama() {
            return area_nama;
        }

        public void setArea_nama(String area_nama) {
            this.area_nama = area_nama;
        }

        public String getSubarea_nama() {
            return subarea_nama;
        }

        public void setSubarea_nama(String subarea_nama) {
            this.subarea_nama = subarea_nama;
        }
    }
}
