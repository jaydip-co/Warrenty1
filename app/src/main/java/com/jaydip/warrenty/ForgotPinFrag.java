package com.jaydip.warrenty;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jaydip.warrenty.prefsUtil.PrefUtil;
import com.jaydip.warrenty.prefsUtil.prefIds;


public class ForgotPinFrag extends Fragment {
    TextView QuestionView,AnswerLabel;
    EditText Answer;
    Button Continue;
    String recoveryAns;
    Drawable WrongAns,RightAnswer;
    Animation labelUp,labelDown;





    public ForgotPinFrag() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        labelUp = AnimationUtils.loadAnimation(getContext(),R.anim.label_up);
        labelDown = AnimationUtils.loadAnimation(getContext(),R.anim.label_down);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_forgot_pin, container, false);
        QuestionView = view.findViewById(R.id.QuestionView);
        Answer = view.findViewById(R.id.Answer);
        Continue = view.findViewById(R.id.Continue);
        /////////////setting question

        QuestionView.setText(PrefUtil.getPrefField(getContext(), prefIds.RECOVERY_QUESTION));
        recoveryAns = PrefUtil.getPrefField(getContext(),prefIds.RECOVERY_ANSWER);
        setListener();
        WrongAns = getActivity().getDrawable(R.drawable.text_field_red_outline);
        RightAnswer = getActivity().getDrawable(R.drawable.category_border);
        AnswerLabel = view.findViewById(R.id.labelAnswer);
        return view;
    }
    void setListener(){
        Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });
        Answer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(Answer.getText().toString().length() == 0){
                    if(hasFocus){
                     AnswerLabel.startAnimation(labelUp);
                    }
                    else {
                        AnswerLabel.startAnimation(labelDown);
                    }
                }
            }
        });
        Answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() == 0 ){
                    Continue.setEnabled(false);
                }
                else {
                    Answer.setBackground(RightAnswer);
                    Continue.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    void checkAnswer(){
        String currentPin = Answer.getText().toString();
        Log.e("jaydip",recoveryAns+"  "+currentPin);
        ResetPinFrag resetPinFrag = new ResetPinFrag();


//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.Container,resetPinFrag).commit();
        if(recoveryAns.equals(currentPin)){
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.Container,resetPinFrag).commit();
        }
        else {
            setWrongAns();
        }
    }
    void setWrongAns(){
        Answer.setBackground(WrongAns);
        Answer.setText("");

    }

}