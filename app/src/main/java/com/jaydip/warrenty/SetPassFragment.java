package com.jaydip.warrenty;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.jaydip.warrenty.Models.CategoryModel;
import com.jaydip.warrenty.ViewModels.CategoryViewModel;

public class SetPassFragment extends Fragment {

    EditText pass;
    EditText passAgain;
    Drawable input;
    Drawable inputFocus;
    Button save;
    TextView error,samePass;
    CategoryViewModel categoryViewModel;




    public SetPassFragment() {

        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryViewModel = new CategoryViewModel(getActivity().getApplication());

    }

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


        ////////////////////////////////////////////////////////////////////////


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
              if(error.getVisibility() == View.GONE && samePass.getVisibility() == View.GONE &&
                    pass.getText().toString().length() > 0 && passAgain.getText().toString().length() > 0){
                  saveNewUser();
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
               checkPass(s);
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
        return v;
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

    public void checkPass(CharSequence s){
        String re = s.toString();
        Conditions con = PasswordValidator.validate(re);
        Log.e("jaydip", con.toString());
        switch(con){
            case ALPHABET:
                error.setText(getString(R.string.forAlphabet));
                error.setVisibility(View.VISIBLE);
                break;
            case MAXLENGTH:
                error.setText(getString(R.string.forMax));
                error.setVisibility(View.VISIBLE);
                break;
            case MINLENGTH:
                error.setText(getString(R.string.forMin));
                error.setVisibility(View.VISIBLE);
                break;
            default:
                error.setVisibility(View.GONE);
                break;
        }
    }

    public void saveNewUser(){
        String first = pass.getText().toString();
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.pass_key),first);
        editor.commit();
        CategoryModel category1 = new CategoryModel();
        category1.setCategoryName("Electronics");
        category1.setTotalItem(0);
        category1.setIconName("electronics1");
        CategoryModel category2 = new CategoryModel();
        category2.setCategoryName("Sports");
        category2.setTotalItem(0);
        category2.setIconName("sports");
        CategoryModel category3 = new CategoryModel();
        category3.setCategoryName("Kitchen");
        category3.setTotalItem(0);
        category3.setIconName("kitchen");
        categoryViewModel.addCategory(category1);
        categoryViewModel.addCategory(category2);
        categoryViewModel.addCategory(category3);
        category3 = new CategoryModel();
        category3.setCategoryName("Fashion");
        category3.setTotalItem(0);
        category3.setIconName("tshirt");
        categoryViewModel.addCategory(category3);
        category3 = new CategoryModel();
        category3.setCategoryName("Vehicles");
        category3.setTotalItem(0);
        category3.setIconName("automotive");
        categoryViewModel.addCategory(category3);
        category3 = new CategoryModel();
        category3.setCategoryName("Mobile");
        category3.setTotalItem(0);
        category3.setIconName("mobile");
        categoryViewModel.addCategory(category3);
        Toast.makeText(getActivity().getApplication().getApplicationContext(),"added",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(),HomeActivity.class);
        getActivity().startActivity(intent);

    }

}