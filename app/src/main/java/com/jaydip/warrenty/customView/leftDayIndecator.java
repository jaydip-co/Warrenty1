package com.jaydip.warrenty.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.jaydip.warrenty.R;

public class leftDayIndecator extends View {
    public leftDayIndecator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.leftDayIndecator,0,0);
        try {
            float val = a.getFloat(R.styleable.leftDayIndecator_value,0);
            Log.e("jaydip",val+"");
        }
        catch (Exception e){
         e.printStackTrace();
        }
        finally {
            a.recycle();
        }
    }
}
