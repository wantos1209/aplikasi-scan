package com.example.apkv1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mengecek status login di SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Jika sudah login, tampilkan BottomNavigationView
            setContentView(R.layout.activity_main_bottom_navigation);
            bottomNavigationView = findViewById(R.id.bottom_navigation);

            // Menampilkan HomeFragment secara default
            loadFragment(new HomeFragment());

            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                if (item.getItemId() == R.id.nav_home) {
                    loadFragment(new HomeFragment());
                    return true;
                } else if (item.getItemId() == R.id.nav_profile) {
                    loadFragment(new ProfileFragment());
                    return true;
                }
                return false;
            });
        } else {
            // Jika belum login, tampilkan form login
            setContentView(R.layout.activity_main);

            usernameEditText = findViewById(R.id.usernameEditText);
            passwordEditText = findViewById(R.id.passwordEditText);
            loginButton = findViewById(R.id.loginButton);

            loginButton.setOnClickListener(v -> {
                // Ambil data username dan password
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Panggil API untuk login
                login(username, password);
            });
        }
    }

    // Method untuk mengganti fragment
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFrame, fragment);
        transaction.commit();
    }

    // Method untuk login ke API
    private void login(String username, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://purple-crocodile-829455.hostingersite.com/")  // Gantilah dengan URL API yang sesuai
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        LoginRequest loginRequest = new LoginRequest(username, password);
        Call<LoginResponse> call = apiService.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    String token = response.body().getToken();
                    String area = response.body().getUser().getArea_nama();
                    String subarea = response.body().getUser().getSubarea_nama();

                    // Simpan token, username, area, subarea, dan status login ke SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", token);
                    editor.putString("username", username);
                    editor.putString("area", area);  // Simpan area
                    editor.putString("subarea", subarea);  // Simpan subarea
                    editor.putBoolean("isLoggedIn", true);  // Tandai sebagai sudah login
                    editor.apply();

                    // Setelah login berhasil, pindah ke Bottom Navigation
                    setContentView(R.layout.activity_main_bottom_navigation);
                    bottomNavigationView = findViewById(R.id.bottom_navigation);

                    // Menampilkan HomeFragment secara default
                    loadFragment(new HomeFragment());

                    bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                        if (item.getItemId() == R.id.nav_home) {
                            loadFragment(new HomeFragment());
                            return true;
                        } else if (item.getItemId() == R.id.nav_profile) {
                            loadFragment(new ProfileFragment());
                            return true;
                        }
                        return false;
                    });
                } else {
                    // Tampilkan pesan error jika login gagal
                    Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Menampilkan pesan error jika request gagal
                Toast.makeText(MainActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}