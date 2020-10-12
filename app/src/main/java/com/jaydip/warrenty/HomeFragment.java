package com.jaydip.warrenty;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.CancellationSignal;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class HomeFragment extends Fragment {

    Button loginButton;
    EditText loginpassField;
    String pass;
    Activity main;

    public HomeFragment(String pass) {
        this.pass = pass;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //////////////////////////////////////////////////////
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        loginButton = v.findViewById(R.id.Login);
        loginpassField = v.findViewById(R.id.LoginPass);
        main = getActivity();

        //////////////////////////////////////////////////
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current = loginpassField.getText().toString();
//                Log.e("password", pass);

                if(pass.equals(current)){

                    Log.e("password", " success");
                    Intent intent = new Intent(getActivity().getApplicationContext(),HomeActivity.class);
                    getActivity().startActivity(intent);
                }
                else {
                    Log.e("password", " unsuccess");
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            checkFinger();
        }
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    void checkFinger(){
        Executor executor = Executors.newSingleThreadExecutor();

        BiometricPrompt prompt = new BiometricPrompt.Builder(getContext())
                .setTitle("Fingerprint Authentication")
                .setDescription("Use fingerprint Authentication to unlock application")
                .setNegativeButton("cancel", executor, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("biomatric","canceled");
                    }
                })
                .build();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

        }
        prompt.authenticate(new CancellationSignal(), executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.e("biomatric","success");
                Intent intent = new Intent(getActivity().getApplicationContext(),HomeActivity.class);
                getActivity().startActivity(intent);
//                main.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
            }
        });

    }

}