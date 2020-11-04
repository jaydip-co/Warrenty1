package com.jaydip.warrenty;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.jaydip.warrenty.prefsUtil.PrefUtil;
import com.jaydip.warrenty.prefsUtil.prefIds;


public class ResetPinFrag extends Fragment {
    ImageView e1,e2,e3,e4;
    EditText pincode;
    Drawable pin_entered,pin_back,pinback_focus,pinback_entered_focus;
    Button ResetPin;




    public ResetPinFrag() {

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reset_pin, container, false);
        e1 = v.findViewById(R.id.pin1);
        e2 = v.findViewById(R.id.pin2);
        e3 = v.findViewById(R.id.pin3);
        e4 = v.findViewById(R.id.pin4);
        pincode = v.findViewById(R.id.pincode_edit);

        pin_entered = getActivity().getDrawable(R.drawable.pincode_entered_back);
        pin_back = getActivity().getDrawable(R.drawable.pincode_back);
        pinback_focus = getActivity().getDrawable(R.drawable.pinback_focus);
        pinback_entered_focus = getActivity().getDrawable(R.drawable.pinback_entered_focus);
        ResetPin = v.findViewById(R.id.ResetPin);
        setListeners();
        return v;
    }
    void setListeners(){
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
        pincode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                createPin();

            }
        });
        ResetPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefUtil.saveToPrivate(getContext(), prefIds.FINAL_PIN,pincode.getText().toString());
                Intent i = new Intent(getActivity(),HomeActivity.class);
                getActivity().startActivity(i);
            }
        });
    }
    void createPin(){
//        Log.e("jaydip",s);
//        builder.append(s);
        Log.e("jaydip",pincode.getText().toString());


        int len = pincode.getText().toString().length();
        if(len == 4){
            ResetPin.setEnabled(true);
        }
        else {
            ResetPin.setEnabled(false);
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
}