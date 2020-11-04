package com.jaydip.warrenty;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jaydip.warrenty.prefsUtil.PrefUtil;
import com.jaydip.warrenty.prefsUtil.prefIds;


public class QuestionsFrag extends Fragment {
    TextView NameLabel,AnswerLabel;
    EditText Name,Answer;
    Animation labelUp,labelDown;
    Spinner questionSpinner;
    Button Continue;
    String RecoveryQuestion;




    public QuestionsFrag() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_questions, container, false);

        Name = v.findViewById(R.id.Name);
        NameLabel = v.findViewById(R.id.labelName);
        labelUp = AnimationUtils.loadAnimation(getContext(),R.anim.label_up);
        labelDown = AnimationUtils.loadAnimation(getContext(),R.anim.label_down);
        questionSpinner = v.findViewById(R.id.Questions);
        AnswerLabel = v.findViewById(R.id.labelAnswer);
        Answer = v.findViewById(R.id.Answer);
        Continue = v.findViewById(R.id.Continue);

        setListeners();
        return v;
    }
   void setListeners(){
       Name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
           @Override
           public void onFocusChange(View v, boolean hasFocus) {
               if(hasFocus){
                   if(Name.getText().toString().length() == 0){
                       NameLabel.startAnimation(labelUp);
                   }
               }
               else {
                   if(Name.getText().toString().length() == 0){
                       NameLabel.startAnimation(labelDown);
                   }
               }
           }
       });
       Answer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
           @Override
           public void onFocusChange(View v, boolean hasFocus) {
               if(hasFocus){
                   if(Answer.getText().toString().length() == 0){
                       AnswerLabel.startAnimation(labelUp);
                   }
               }
               else {
                   if(Answer.getText().toString().length() == 0){
                       AnswerLabel.startAnimation(labelDown);
                   }
               }
           }
       });
       String[] array = getResources().getStringArray(R.array.recoveryQuestions);

       ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item
       ,array);
       stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       questionSpinner.setAdapter(stringArrayAdapter);
       questionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               Log.e("jaydip",array[position]);
               RecoveryQuestion = array[position];
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });
       TextWatcher watcher = new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               Validate();

           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       };
       Answer.addTextChangedListener(watcher);
       Name.addTextChangedListener(watcher);

       Continue.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               PrefUtil.saveToPrivate(getActivity(), prefIds.RECOVERY_QUESTION,Answer.getText().toString());
               PrefUtil.saveToPrivate(getActivity(),prefIds.RECOVERY_QUESTION,RecoveryQuestion);
               PrefUtil.saveToPrivate(getActivity(),prefIds.USER_NAME,Name.getText().toString());
              BackUpFragment fragment = new BackUpFragment();
              getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Container,fragment).commit();
           }
       });

   }
   void Validate(){
       Continue.setEnabled(Name.getText().toString().length() > 0 && Answer.getText().toString().length() > 0);
   }
}