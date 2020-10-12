package com.jaydip.warrenty;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import java.sql.Time;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {
    ImageView backButton;
    public static String key_time_hour = "time_hour";
    public static String key_time_Minute = "time_minute";


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
        Preference timePref;
        SharedPreferences.Editor editor;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            switchPreference = getPreferenceManager().findPreference("notification_switch");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
             editor = preferences.edit();
            int hour = preferences.getInt(key_time_hour,8);
            int minute = preferences.getInt(key_time_Minute,30);

            switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean val = (boolean) newValue;
                    Toast.makeText(getContext(),newValue.toString(),Toast.LENGTH_SHORT).show();

                  if(val){
                      timePref.setEnabled(true);
                      Utils.setAlarm(getContext());
                  }
                  else {
                      timePref.setEnabled(false);
                      Utils.cancelAlarm(getContext());
                  }
                    return true;
                }
            });
            timePref = getPreferenceManager().findPreference("time");
            timePref.setTitle(hour+":"+minute);
            timePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showTimepiker();

                    return true;
                }
            });
        }
        void showTimepiker(){
            Calendar calendar = Calendar.getInstance();
           TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
               @Override
               public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                   timePref.setTitle(hourOfDay+":"+minute);
                   editor.putInt(key_time_hour,hourOfDay);
                   editor.putInt(key_time_Minute,minute);
                   editor.commit();
                   Utils.cancelAlarm(getContext());
                   Utils.setAlarm(getContext());


               }
           },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);
            dialog.show();
        }
    }
}