package com.jaydip.warrenty;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class HomeFragment extends Fragment {

    Button loginButton;
    EditText loginpassField;
    String pass;

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

        //////////////////////////////////////////////////
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current = loginpassField.getText().toString();
//                Log.e("password", pass);
                Intent intent = new Intent(getActivity().getApplicationContext(),HomeActivity.class);
                getActivity().startActivity(intent);
                if(pass.equals(current)){

                    Log.e("password", " success");
                }
                else {
                    Log.e("password", " unsuccess");
                }
            }
        });
        return v;
    }

}