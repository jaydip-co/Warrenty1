package com.jaydip.warrenty;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.CancellationSignal;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaydip.warrenty.prefsUtil.PrefUtil;
import com.jaydip.warrenty.prefsUtil.prefIds;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class HomeFragment extends Fragment {

    Button loginButton;
    EditText pincode;
    String pass;
    Activity main;
    TextView description,ForgotPin;
    ImageView e1,e2,e3,e4;
    Drawable pin_entered,pin_back,pinback_focus,pinback_entered_focus,pin_error;
    boolean isWrongOcured = false;

    public HomeFragment(String pass) {
        this.pass = pass;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //////////////////////////////////////////////////////
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        loginButton = v.findViewById(R.id.Login);
        main = getActivity();
        String userName= PrefUtil.getPrefField(getContext(), prefIds.USER_NAME);
        String textDes = "hey, "+userName;
        description = v.findViewById(R.id.descriptionHome);
        description.setText(textDes);
        e1 = v.findViewById(R.id.pin1);
        e2 = v.findViewById(R.id.pin2);
        e3 = v.findViewById(R.id.pin3);
        e4 = v.findViewById(R.id.pin4);
        pin_entered = getActivity().getDrawable(R.drawable.pincode_entered_back);
        pin_back = getActivity().getDrawable(R.drawable.pincode_back);
        pinback_focus = getActivity().getDrawable(R.drawable.pinback_focus);
        pinback_entered_focus = getActivity().getDrawable(R.drawable.pinback_entered_focus);
        pincode = v.findViewById(R.id.pincode_edit);
        pin_error = getActivity().getDrawable(R.drawable.pin_error);
        ForgotPin = v.findViewById(R.id.ForgotPass);


        //////////////////////////////////////////////////
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                createPin();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        pincode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                createPin();
            }
        });
        pincode.addTextChangedListener(watcher);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current = pincode.getText().toString();
                Log.e("password", current);

                if(pass.equals(current)){

                    Log.e("password", " success");
                    Intent intent = new Intent(getActivity().getApplicationContext(),HomeActivity.class);
                    getActivity().startActivity(intent);
                }
                else {
                    seterror();
                    Log.e("password", " unsuccess");
                    isWrongOcured = true;
                }
            }
        });
        ForgotPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPinFrag forgotPin = new ForgotPinFrag();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.Container,forgotPin)
                        .commit();


            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            checkFinger();
        }
        return v;
    }
    void createPin(){
//        Log.e("jaydip",s);
//        builder.append(s);
        Log.e("jaydip",pincode.getText().toString());


        int len = pincode.getText().toString().length();
        if(len == 4){
            loginButton.setEnabled(true);
        }
        else {
            loginButton.setEnabled(false);
        }
        switch (len){
            case 0:
                if(isWrongOcured){
                    e1.setImageDrawable(pin_error);
                    e2.setImageDrawable(pin_error);
                    e3.setImageDrawable(pin_error);
                    e4.setImageDrawable(pin_error);
                    break;
                }
                e1.setImageDrawable(pinback_focus);
                e2.setImageDrawable(pin_back);
                e3.setImageDrawable(pin_back);
                e4.setImageDrawable(pin_back);
                break;
            case 1:
//                e1.setText("");
//                e1.setBackground(getActivity().getDrawable(R.drawable.pincode_entered_back));
//                e2.requestFocus();
                isWrongOcured = false;
                e1.setImageDrawable(pin_entered);
                e2.setImageDrawable(pinback_focus);
                e3.setImageDrawable(pin_back);
                e4.setImageDrawable(pin_back);
                break;
            case 2:

//                e2.setBackground(getActivity().getDrawable(R.drawable.pincode_entered_back));
//                e3.requestFocus();
//                e2.setText("");
                e1.setImageDrawable(pin_entered);
                e2.setImageDrawable(pin_entered);
                e3.setImageDrawable(pinback_focus);
                e4.setImageDrawable(pin_back);
                break;
            case 3:
//                e3.setText("");
//                e3.setBackground(getActivity().getDrawable(R.drawable.pincode_entered_back));
//                e4.requestFocus();
                e1.setImageDrawable(pin_entered);
                e2.setImageDrawable(pin_entered);
                e3.setImageDrawable(pin_entered);
                e4.setImageDrawable(pinback_focus);
                break;
            case 4:
                e1.setImageDrawable(pin_entered);
                e2.setImageDrawable(pin_entered);
                e3.setImageDrawable(pin_entered);
                e4.setImageDrawable(pinback_entered_focus);
                break;
        }

    }
    void seterror(){
        isWrongOcured = true;
        pincode.setText("");
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    void checkFinger(){
        Executor executor = Executors.newSingleThreadExecutor();

        BiometricPrompt prompt = new BiometricPrompt.Builder(getContext())
                .setTitle("Fingerprint Authentication")
                .setDescription("Use fingerprint Authentication to unlock")
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