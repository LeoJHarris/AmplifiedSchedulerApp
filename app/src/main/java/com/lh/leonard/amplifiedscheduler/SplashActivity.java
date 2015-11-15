package com.lh.leonard.amplifiedscheduler;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import se.simbio.encryption.Encryption;

public class SplashActivity extends Activity {

    Encryption encryption;
    private Boolean saveLogin;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Encryption encryption = Encryption.getDefault("Key", "Salt", new byte[16]);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        Intent homeIntent = new Intent(SplashActivity.this, MainActivity.class);
        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {

            String password = encryption.decryptOrNull(loginPreferences.getString("password", ""));
            String username = loginPreferences.getString("username", "");

            homeIntent.putExtra("password", password);
            homeIntent.putExtra("email", username);
            homeIntent.putExtra("savelogin", true);
            startActivity(homeIntent);
        } else {
            homeIntent.putExtra("savelogin", false);
            startActivity(homeIntent);
        }
    }
}
