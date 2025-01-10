package com.example.apkv1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView usernameTextView, areaTextView, subareaTextView;
    private Button logoutButton;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameTextView = rootView.findViewById(R.id.usernameTextView);
        areaTextView = rootView.findViewById(R.id.areaTextView);
        subareaTextView = rootView.findViewById(R.id.subareaTextView);
        logoutButton = rootView.findViewById(R.id.logoutButton);

        // Ambil data pengguna dari SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyApp", getContext().MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Unknown User");
        String area = sharedPreferences.getString("area", "Unknown Area");
        String subarea = sharedPreferences.getString("subarea", "Unknown Subarea");

        // Set data pengguna di TextViews
        usernameTextView.setText("Username: " + username);
        areaTextView.setText("Area: " + area);
        subareaTextView.setText("Subarea: " + subarea);

        // Menangani klik tombol Logout
        logoutButton.setOnClickListener(v -> {
            // Hapus token dan status login dari SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("token");
            editor.remove("isLoggedIn");
            editor.remove("username");
            editor.remove("area");
            editor.remove("subarea");
            editor.apply();

            // Arahkan ke MainActivity (halaman login) setelah logout
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);  // Menghapus semua aktivitas sebelumnya
            startActivity(intent);

            // Menutup aktivitas ProfileActivity
            getActivity().finish();  // Menutup ProfileActivity atau aktivitas yang sedang berjalan
        });

        return rootView;
    }
}