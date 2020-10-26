package com.jaydip.warrenty;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.services.drive.DriveScopes;
import com.jaydip.warrenty.Workers.uploadZip;
import com.jaydip.warrenty.Workers.zipConverter;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class SettingsActivity extends AppCompatActivity {
    ImageView backButton;
    public static String key_time_hour = "time_hour";
    public static String key_time_Minute = "time_minute";
    public static String key_gmail ="key_for_current_signIn_gmail";


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
        int  SIGN_IN_CODE = 205;
        SwitchPreference switchPreference,syncSwitch;
        Preference timePref;
        SharedPreferences.Editor editor;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            switchPreference = getPreferenceManager().findPreference("notification_switch");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            syncSwitch = getPreferenceManager().findPreference("Sync_switch");
            syncSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean val = (boolean) newValue;
                    if(val){

                        setSyncProsess();
                    }
                    else {

                        return true;
                    }
                    return false;
                }
            });
             editor = preferences.edit();
            int hour = preferences.getInt(key_time_hour,8);
            int minute = preferences.getInt(key_time_Minute,30);
            String summery = preferences.getString(key_gmail,"enable sync");
            syncSwitch.setSummary(summery);

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
        void createPdf(){

            OneTimeWorkRequest
                    request = new OneTimeWorkRequest.Builder(zipConverter.class)
                    .build();
            OneTimeWorkRequest request1 = new OneTimeWorkRequest.Builder(uploadZip.class).build();
            WorkManager.getInstance(getContext()).beginWith(request).then(request1).enqueue();

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
        void setSyncProsess(){
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
            if(account == null){
                GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .requestEmail()
                        .build();
                GoogleSignInClient client = GoogleSignIn.getClient(getContext(),signInOptions);
                startActivityForResult(client.getSignInIntent(),SIGN_IN_CODE);
            }
            else
            {
                createPdf();
                syncSwitch.setChecked(true);
            }


        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(resultCode == RESULT_OK && requestCode == SIGN_IN_CODE) {
                GoogleSignIn.getSignedInAccountFromIntent(data)
                        .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                            @Override
                            public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                                Log.e("SettingActivity",googleSignInAccount.getEmail());
                                syncSwitch.setSummary(googleSignInAccount.getEmail());
                                syncSwitch.setChecked(true);
                                editor.putString(key_gmail,googleSignInAccount.getEmail());
                                createPdf();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("SettingActivity",e.toString());

                            }
                        });
            }
        }
    }
}