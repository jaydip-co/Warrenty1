package com.jaydip.warrenty;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.jaydip.warrenty.Service.DriveServiceHelper;
import com.jaydip.warrenty.Workers.downloadWorker;
import com.jaydip.warrenty.Workers.uploadZip;
import com.jaydip.warrenty.Workers.zipConverter;
import com.jaydip.warrenty.prefsUtil.PrefUtil;
import com.jaydip.warrenty.prefsUtil.prefIds;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroupAdapter;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import static com.jaydip.warrenty.BackUpFragment.Key_For_Status_String;
import static com.jaydip.warrenty.BackUpFragment.Key_For_Status_process;

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
       public static   int  SIGN_IN_CODE = 205;
        SwitchPreference switchPreference,syncSwitch,dailyUpdate;
        Preference timePref,update,restore,DaysPref;
        SharedPreferences.Editor editor;
        String id = "";

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getActivity().registerReceiver(receiver,new IntentFilter(getString(R.string.intent_filter_for_process)));
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            getActivity().unregisterReceiver(receiver);
        }


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            switchPreference = getPreferenceManager().findPreference("notification_switch");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            syncSwitch = getPreferenceManager().findPreference("Sync_switch");
            update = getPreferenceManager().findPreference("sync");
//            update.setEnabled(false);
            update.setLayoutResource(R.layout.update_button);
            restore = getPreferenceScreen().findPreference("restore");
            restore.setLayoutResource(R.layout.restore_button);
            DaysPref = getPreferenceScreen().findPreference("Days");
            dailyUpdate = getPreferenceScreen().findPreference("Daily_update");

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
            int hourT = PrefUtil.getPrefFieldInt(getContext(),prefIds.ALARM_TIME_HOUR);
            int minuteT = PrefUtil.getPrefFieldInt(getContext(),prefIds.ALARM_TIME_MINUTE);
            int hour = hourT == 0 ? 8 :hourT;
            int minute = minuteT ==0 ? 30 : minuteT;
//            String gmail = PrefUtil.getPrefField(getContext(),prefIds.LOGED_IN_ACOUNT);
//            if(!gmail.equals(PrefUtil.Default_Value)){
//                syncSwitch.setSummary(gmail);
//            }


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
            setInitialValues();
//            String InitialUpdate = PrefUtil.getPrefField(getContext(),prefIds.INITIAL_UPDATE_DONE);
//            if(InitialUpdate.equals(PrefUtil.Default_Value)){
//               syncSwitch.setChecked(false);
//            }

//            update.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//                    Log.e("jaydip",id);
//                    WorkManager manager = WorkManager.getInstance(getContext());
//                    WorkRequest request = new OneTimeWorkRequest.Builder(downloadWorker.class)
//                            .build();
//                    manager.enqueue(request);
//                    return false;
//                }
//            });
//            setup();


//            Log.e("jaydip","restore text "+t.getText().toString());



        }

        void setInitialValues(){
            String download_Pending = PrefUtil.getPrefField(getContext(),prefIds.DOWNLOAD_PENDING);
            if(!download_Pending.equals(PrefUtil.Default_Value)){
                restore.setVisible(true);
                update.setVisible(false);
            }
            String gmail = PrefUtil.getPrefField(getContext(),prefIds.LOGED_IN_ACOUNT);
            if(!gmail.equals(PrefUtil.Default_Value)){
                syncSwitch.setSummary(gmail);

            }
            else {
                syncSwitch.setChecked(false);
            }
            String ischange = PrefUtil.getPrefField(getContext(),prefIds.Daily_update_Check);
            if(ischange.equals(PrefUtil.Default_Value)){
               update.setEnabled(false);
            }
            update.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    createPdf();

                    return true;
                }
            });
            restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    downloadBackup();
                    return false;
                }
            });
            int dayT = PrefUtil.getPrefFieldInt(getContext(),prefIds.ALARM_DAY);
            int day = dayT == 0 ? 10 : dayT;
            DaysPref.setTitle(String.valueOf(day));
            DaysPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    NumberPicker numberPicker = new  NumberPicker(getContext());
                    numberPicker.setMinValue(7);
                    numberPicker.setMaxValue(15);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                            .setTitle("Choose Day to be notified")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.e("jaydip day value",numberPicker.getValue()+"");
                                    PrefUtil.saveToPrivateInt(getContext(),prefIds.ALARM_DAY,numberPicker.getValue());
                                    DaysPref.setTitle(numberPicker.getValue()+"");
                                }
                            })
                            .setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.setView(numberPicker);
                    builder.show();

                    return false;
                }
            });
            dailyUpdate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if((boolean) newValue){
                        Utils.setDailyUpdate(getContext());
                    }
                    else {
                        Utils.desableDailUpdate(getContext());
                    }

                    return true;
                }
            });



        }


        void setRestore(){

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
            if(account != null){
                String intialUpdate = PrefUtil.getPrefField(getContext(), prefIds.INITIAL_UPDATE_DONE);
                if(intialUpdate.equals(PrefUtil.Default_Value)){
                    DriveServiceHelper helper = new DriveServiceHelper(getContext());
                    helper.getDetail().addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            if(!s.equals("nothing")){
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                                        .setTitle("Update Found With Account")
                                        .setMessage("Click Download to start download")
                                        .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                               downloadBackup();

                                            }
                                        })
                                        .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                PrefUtil.saveToPrivate(getContext(),prefIds.DOWNLOAD_PENDING,"yes");
                                                restore.setVisible(true);
                                            }
                                        }).setCancelable(false);
                                builder.show();
                                PrefUtil.saveToPrivate(getActivity(),prefIds.LAST_BACKUP_ID,s);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
//                    restore.setVisible(true);
                    update.setVisible(false);
                }
                else {
                    update.setVisible(true);
                }
            }

        }
        void downloadBackup(){
            WorkRequest workRequest = new OneTimeWorkRequest.Builder(downloadWorker.class)
                    .build();
            WorkManager.getInstance(getContext()).enqueue(workRequest);
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
                   PrefUtil.saveToPrivateInt(getContext(),prefIds.ALARM_TIME_HOUR,hourOfDay);
//                   editor.putInt(key_time_hour,hourOfDay);
//                   editor.putInt(key_time_Minute,minute);
//                   editor.commit();
                   PrefUtil.saveToPrivateInt(getContext(),prefIds.ALARM_TIME_MINUTE,minute);
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
                        .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                        .requestEmail()
                        .build();
                GoogleSignInClient client = GoogleSignIn.getClient(getContext(),signInOptions);
                startActivityForResult(client.getSignInIntent(),SIGN_IN_CODE);
            }
            else
            {
//                setRestore();
//                createPdf();
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
                               PrefUtil.saveToPrivate(getContext(),prefIds.LOGED_IN_ACOUNT,googleSignInAccount.getEmail());
//                                createPdf();
                                setRestore();

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
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                int prog = intent.getIntExtra(Key_For_Status_process,0);
                if(prog == 0){
                    Toast.makeText(getContext(),"download started..",Toast.LENGTH_LONG).show();
                }
                if(prog == 100){
                    Toast.makeText(getContext(),"download Completed",Toast.LENGTH_LONG).show();
                    PrefUtil.saveToPrivate(getActivity(),prefIds.DOWNLOAD_PENDING,PrefUtil.Default_Value);
                    restore.setVisible(false);
                    update.setVisible(true);
                    System.exit(0);
                }
            }
        };
    }
}