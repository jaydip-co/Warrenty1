package com.jaydip.warrenty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.jaydip.warrenty.Service.DriveServiceHelper;
import com.jaydip.warrenty.ViewModels.CategoryViewModel;
import com.jaydip.warrenty.Workers.downloadWorker;
import com.jaydip.warrenty.prefsUtil.PrefUtil;
import com.jaydip.warrenty.prefsUtil.prefIds;

import java.io.IOException;
import java.util.Collections;
import java.util.PropertyResourceBundle;

import static android.app.Activity.RESULT_OK;


public class BackUpFragment extends Fragment {
    public  int codeForSignin = 110;
    Button  checkButton;
    GoogleSignInAccount Gaccount;
    DriveServiceHelper helper;
    TextView status,skipButton;
    ProgressBar progressBar,progressBarCercular;

    public static String Key_For_Status_String = "status";
    public static String Key_For_Status_process = "process";
    CategoryViewModel categoryViewModel;


    public BackUpFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(receiver,new IntentFilter(getString(R.string.intent_filter_for_process)));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View item = inflater.inflate(R.layout.fragment_back_up,container,false);
        checkButton = item.findViewById(R.id.check_button);
        status = item.findViewById(R.id.status);
        progressBar = item.findViewById(R.id.progressBar);
        skipButton = item.findViewById(R.id.skip_button);
        progressBarCercular = item.findViewById(R.id.progress_circular);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForAccount();
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lastId = PrefUtil.getPrefField(getContext(),prefIds.LAST_BACKUP_ID);
                if(!lastId.equals(PrefUtil.Default_Value)){
                    PrefUtil.saveToPrivate(getContext(),prefIds.DOWNLOAD_PENDING,"yes");
                }
                ContinueToHome();
                Intent i = new Intent(getActivity(),HomeActivity.class);
                getActivity().startActivity(i);
            }
        });
        checkForAccount();
        categoryViewModel = new CategoryViewModel(getActivity().getApplication());
        return item;

    }

    void checkForAccount(){
         Gaccount = GoogleSignIn.getLastSignedInAccount(getContext());
        if(Gaccount == null){
            GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                    .build();
            GoogleSignInClient client = GoogleSignIn.getClient(getContext(),options);
            startActivityForResult(client.getSignInIntent(),codeForSignin);
        }
        else {
            checkForUpDate();
        }
    }
    void changeAccount(){
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                .build();
        GoogleSignInClient client = GoogleSignIn.getClient(getContext(),options);
        client.revokeAccess().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("sign Out","success");
                startActivityForResult(client.getSignInIntent(),codeForSignin);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("error in sign Out",e.toString());
            }
        });



    }
    void checkForUpDate(){
        status.setVisibility(View.GONE);
        progressBarCercular.setVisibility(View.VISIBLE);
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getContext()
                , Collections.singleton(DriveScopes.DRIVE_APPDATA));
        credential.setSelectedAccount(Gaccount.getAccount());
        Drive drive = new Drive(AndroidHttp.newCompatibleTransport(),new GsonFactory(),credential);
        Drive mDrive = new Drive.Builder(AndroidHttp.newCompatibleTransport(),new GsonFactory(),credential)
                .setApplicationName("Warranty")
                .build();

         helper = new DriveServiceHelper(getContext());
        helper.getDetail().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e("jaydip  iiiiiii",s);
                if(s.equals("nothing")){
                    status.setVisibility(View.VISIBLE);
                    setCheckWithDifferentAccount();
                    progressBarCercular.setVisibility(View.GONE);
                }
                else {
                    PrefUtil.saveToPrivate(getActivity(), prefIds.LAST_BACKUP_ID,s);
                    status.setVisibility(View.VISIBLE);
                    setRestore();
                    progressBarCercular.setVisibility(View.GONE);
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("erroe" ,e.toString());
                    }
                });


    }
    void setCheckWithDifferentAccount(){
        status.setText("No Backup Found");
        checkButton.setText("Try Other Account");
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAccount();
            }
        });
    }
    void setRestore(){
        checkButton.setText("Restore Data");
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestoreData();
            }
        });
    }
    void RestoreData(){
        skipButton.setVisibility(View.GONE);
        checkButton.setEnabled(false);

        progressBar.setVisibility(View.VISIBLE);
        WorkRequest request = new OneTimeWorkRequest.Builder(downloadWorker.class).build();
        WorkManager workManager = WorkManager.getInstance(getContext());
        workManager.enqueue(request);
    }
    void setContinue() {
        checkButton.setText("Continue");
        checkButton.setEnabled(true);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContinueToHome();
                progressBarCercular.setVisibility(View.VISIBLE);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(getActivity(), HomeActivity.class);
                getActivity().startActivity(i);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("jaydip",resultCode+"");
        if(resultCode == RESULT_OK && requestCode == codeForSignin){
            GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                        @Override
                        public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                       Log.e("jaydip",googleSignInAccount.getEmail());
                       Gaccount = googleSignInAccount;
                       PrefUtil.saveToPrivate(getActivity(),prefIds.LOGED_IN_ACOUNT,googleSignInAccount.getEmail());
                       checkForUpDate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("erroe" ,e.toString());

                        }
                    });

        }
    }
    void ContinueToHome(){

        PrefUtil.setFinalPin(getContext());
        categoryViewModel.addDefault();

    }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getStringExtra(Key_For_Status_String);
            status.setText(s);
            int prog = intent.getIntExtra(Key_For_Status_process,0);
            progressBar.setProgress(intent.getIntExtra(Key_For_Status_process,0));
            if(prog == 100){
                setContinue();
            }
        }
    };
}