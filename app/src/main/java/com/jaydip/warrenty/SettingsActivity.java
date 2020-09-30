package com.jaydip.warrenty;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

public class SettingsActivity extends AppCompatActivity {
    ImageView backButton;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        Toolbar toolbar = findViewById(R.id.toolbar);
        backButton = findViewById(R.id.back_button);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            setSupportActionBar(toolbar);
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        SwitchPreference switchPreference;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            switchPreference = getPreferenceManager().findPreference("notification_switch");
            switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean val = (boolean) newValue;
                    Toast.makeText(getContext(),newValue.toString(),Toast.LENGTH_SHORT).show();

                  if(val){
                      Utils.setAlarm(getContext());
                  }
                  else {
                      Utils.cancelAlarm(getContext());
                  }
                    return true;
                }
            });
        }
    }
}