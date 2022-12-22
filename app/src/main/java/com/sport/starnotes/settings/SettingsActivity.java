package com.sport.starnotes.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sport.starnotes.Account;
import com.sport.starnotes.BuildConfig;
import com.sport.starnotes.MainActivity;
import com.sport.starnotes.R;
import com.sport.starnotes.WatchActivity;
import com.sport.starnotes.settings.backup.BackupActivity;

public class SettingsActivity extends AppCompatActivity {
    TextView textViewVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Account.theme().equals("normal")) {
            if (Account.theme().equals("purple")) {
                setTheme(R.style.Theme_Purple);
            } else if (Account.theme().equals("blue")) {
                setTheme(R.style.Theme_Blue);
            } else if (Account.theme().equals("amoled")) {
                setTheme(R.style.Theme_Amoled);
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        textViewVersion = findViewById(R.id.textViewVersion);
        textViewVersion.setText(versionName+" Build "+versionCode);
    }

    public void openBackup(View view) {
        Intent i = new Intent(SettingsActivity.this, BackupActivity.class);
        startActivity(i);
    }

    public void openTheme(View view) {
        Intent i = new Intent(SettingsActivity.this, ThemeActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void openWatchSync(View view) {
        Intent i = new Intent(this, WatchActivity.class);
        startActivity(i);
    }
}