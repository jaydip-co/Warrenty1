package com.jaydip.warrenty;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

public class MainActivity extends FragmentActivity {
    FrameLayout container;
    EditText loginPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //////////////////////////////////////////////////////////
        container = findViewById(R.id.Container);
        SetPassFragment pass = new SetPassFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        loginPass = findViewById(R.id.LoginPass);
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        final String pasString = sharedPreferences.getString(getString(R.string.pass_key),"nothing");
        HomeFragment home= new HomeFragment(pasString);
       Log.e("password",pasString);
       ////////////////////////////////////////////////////////////////
        //cheching if password is exsists or not
        if(pasString.equals("nothing")){
            transaction.add(R.id.Container,pass);
            transaction.commit();
        }
        else {
            transaction.add(R.id.Container,home);
            transaction.commit();
        }



        }


}
