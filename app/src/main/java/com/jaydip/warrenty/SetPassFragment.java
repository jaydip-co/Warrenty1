package com.jaydip.warrenty;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import android.widget.Toast;

import com.jaydip.warrenty.Models.CategoryModel;
import com.jaydip.warrenty.ViewModels.CategoryViewModel;
import com.jaydip.warrenty.prefsUtil.PrefUtil;
import com.jaydip.warrenty.prefsUtil.prefIds;

public class SetPassFragment extends Fragment {

    EditText pass,pincode,pincodeAgain;
    ImageView e1,e2,e3,e4,e1Again,e2Again,e3Again,e4Again;
    EditText passAgain;
    Drawable input;
    Drawable inputFocus;
    Button save;
    TextView error,samePass;
    CategoryViewModel categoryViewModel;
    String pin = "";
    StringBuilder builder;
    Drawable pin_entered,pin_back,pinback_focus,pinback_entered_focus,pin_error;
    boolean isWrongPin = false;




    public SetPassFragment() {

        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryViewModel = new CategoryViewModel(getActivity().getApplication());

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ///////////////////////////////////////////////////////////////
        View v = inflater.inflate(R.layout.fragment_set_pass,container,false);
        input = getResources().getDrawable(R.drawable.round_intput);
        inputFocus = getResources().getDrawable(R.drawable.round_input_focus);

        pass = v.findViewById(R.id.password);
        passAgain = v.findViewById(R.id.passwordagain);
        save = v.findViewById(R.id.save);
        error = v.findViewById(R.id.correction);
        samePass = v.findViewById(R.id.samepass);
        e1 = v.findViewById(R.id.pin1);
        e2 = v.findViewById(R.id.pin2);
        e3 = v.findViewById(R.id.pin3);
        e4 = v.findViewById(R.id.pin4);
        e1Again = v.findViewById(R.id.pin1again);
        e2Again = v.findViewById(R.id.pin2again);
        e3Again = v.findViewById(R.id.pin3again);
        e4Again = v.findViewById(R.id.pin4again);

        builder = new StringBuilder();
        pin_entered = getActivity().getDrawable(R.drawable.pincode_entered_back);
        pin_back = getActivity().getDrawable(R.drawable.pincode_back);
        pinback_focus = getActivity().getDrawable(R.drawable.pinback_focus);
        pinback_entered_focus = getActivity().getDrawable(R.drawable.pinback_entered_focus);
        pin_error = getActivity().getDrawable(R.drawable.pin_error);
        pincode = v.findViewById(R.id.pincode_edit);
        pincodeAgain = v.findViewById(R.id.pincode_edit_again);

//        e1.setImageDrawable(pinback_focus);



        ////////////////////////////////////////////////////////////////////////
        pincode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                createPin();

            }
        });
        pincodeAgain.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                createPinAgain();
            }
        });


        View.OnFocusChangeListener focuslisner = new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText view = (EditText) v;
                if(hasFocus){

                    view.setBackground(inputFocus);
                }
                else {

                    view.setBackground(input);

                }
            }
        };


       pass.setOnFocusChangeListener(focuslisner);
       passAgain.setOnFocusChangeListener(focuslisner);

       save.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
//              if(error.getVisibility() == View.GONE && samePass.getVisibility() == View.GONE &&
//                    pass.getText().toString().length() > 0 && passAgain.getText().toString().length() > 0){
//                  saveNewUser();
//              }
               String pinAgain = pincodeAgain.getText().toString();
               String pin = pincode.getText().toString();
               if(pin.equals(pinAgain)){
                   Log.e("jaydip",pincode.getText().toString());
                   PrefUtil.saveToPrivate(getActivity(), prefIds.TEMP_PIN,pincode.getText().toString());
                   FragmentManager manager = getActivity().getSupportFragmentManager();
                   FragmentTransaction transaction = manager.beginTransaction();
                   QuestionsFrag questionsFrag = new QuestionsFrag();
                   transaction.replace(R.id.Container,questionsFrag);
                   transaction.commit();
               }
               else {
                   setNotSamePin();
               }

           }
       });


       pass.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               error.setText("");
               error.setVisibility(View.GONE);
           }
       });


       pass.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
//               checkPass(s);
               if(passAgain.getText().toString().length() > 0){
                   chckSame(passAgain.getText().toString());
               }
           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });

       passAgain.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               chckSame(s);
           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });
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
        pincode.addTextChangedListener(watcher);
//       e1.addTextChangedListener(watcher);
//        e2.addTextChangedListener(watcher);
//        e3.addTextChangedListener(watcher);
//        e4.addTextChangedListener(watcher);

        TextWatcher watcherAgain = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                createPinAgain();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        pincodeAgain.addTextChangedListener(watcherAgain);
        return v;
    }
    void createPinAgain(){
     Log.e("jaydip pin again ", pincodeAgain.getText().toString());
     int len = pincodeAgain.getText().toString().length();
        if(len == 4 && pincode.getText().length() == 4){
            save.setEnabled(true);
        }
        else {
            save.setEnabled(false);
        }
     switch (len){
         case 0:
             if(isWrongPin){
                 e1Again.setImageDrawable(pin_error);
                 e2Again.setImageDrawable(pin_error);
                 e3Again.setImageDrawable(pin_error);
                 e4Again.setImageDrawable(pin_error);
                 break;
             }
             e1Again.setImageDrawable(pinback_focus);
             e2Again.setImageDrawable(pin_back);
             e3Again.setImageDrawable(pin_back);
             e4Again.setImageDrawable(pin_back);
             break;
         case 1:
             isWrongPin = false;
//                e1.setText("");
//                e1.setBackground(getActivity().getDrawable(R.drawable.pincode_entered_back));
//                e2.requestFocus();
             e1Again.setImageDrawable(pin_entered);
             e2Again.setImageDrawable(pinback_focus);
             e3Again.setImageDrawable(pin_back);
             e4Again.setImageDrawable(pin_back);
             break;
         case 2:

//                e2.setBackground(getActivity().getDrawable(R.drawable.pincode_entered_back));
//                e3.requestFocus();
//                e2.setText("");
             e1Again.setImageDrawable(pin_entered);
             e2Again.setImageDrawable(pin_entered);
             e3Again.setImageDrawable(pinback_focus);
             e4Again.setImageDrawable(pin_back);
             break;
         case 3:
//                e3.setText("");
//                e3.setBackground(getActivity().getDrawable(R.drawable.pincode_entered_back));
//                e4.requestFocus();
             e1Again.setImageDrawable(pin_entered);
             e2Again.setImageDrawable(pin_entered);
             e3Again.setImageDrawable(pin_entered);
             e4Again.setImageDrawable(pinback_focus);
             break;
         case 4:
             e1Again.setImageDrawable(pin_entered);
             e2Again.setImageDrawable(pin_entered);
             e3Again.setImageDrawable(pin_entered);
             e4Again.setImageDrawable(pinback_entered_focus);
             break;

     }
    }
    void setNotSamePin(){
        isWrongPin = true;
        e1Again.setImageDrawable(pin_error);
        e2Again.setImageDrawable(pin_error);
        e3Again.setImageDrawable(pin_error);
        e4Again.setImageDrawable(pin_error);
        pincodeAgain.setText("");

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void createPin(){
//        Log.e("jaydip",s);
//        builder.append(s);
        Log.e("jaydip",pincode.getText().toString());


        int len = pincode.getText().toString().length();
        if(len == 4 && pincodeAgain.getText().length() == 4){
            save.setEnabled(true);
        }
        else {
            save.setEnabled(false);
        }
        switch (len){
            case 0:
                e1.setImageDrawable(pinback_focus);
                e2.setImageDrawable(pin_back);
                e3.setImageDrawable(pin_back);
                e4.setImageDrawable(pin_back);
                break;
            case 1:
//                e1.setText("");
//                e1.setBackground(getActivity().getDrawable(R.drawable.pincode_entered_back));
//                e2.requestFocus();
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

    public void chckSame(CharSequence s){
        String re = s.toString();
        if(re.length() > 0){
            if(!re.equals(pass.getText().toString())){
                samePass.setVisibility(View.VISIBLE);
            }
            else{
                samePass.setVisibility(View.GONE);
            }
        }
        else{
            samePass.setVisibility(View.GONE);
        }

    }




}