package com.jaydip.warrenty;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.jaydip.warrenty.Listeners.OnReceieCategory;
import com.jaydip.warrenty.Models.CategoryModel;
import com.jaydip.warrenty.Models.ItemModel;
import com.jaydip.warrenty.ViewModels.CategoryViewModel;
import com.jaydip.warrenty.ViewModels.ItemViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.jaydip.warrenty.PathProvider.getOutputMediaFile;


public class AddItem extends AppCompatActivity  {
    EditText Iname,Imonth,Idetail;
    TextView Idate,Isave,Inamelabel,Imonthlabel,Idetaillabel;
    Button AttachImage,AttachBill;
    Spinner Icategory;
    ItemViewModel itemViewModel;
    ImageView IImage,IBill,backbutton,dateSelect;
    private static int REQUEST_CODE_Image =100;
    private static int REQUEST_CODE_Bill =150;
    Bitmap IImageBitmap,IBillBitmap,scaledImage,scaledBill;
    ByteArrayOutputStream stream ;
    CategoryViewModel categoryViewModel;
    String currentCategory;
    int day,Month,Year,eMonth,eYear;
    ItemModel model;
    CategoryModel currentCategoryModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        ////////////////////////////////////////
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Iname = findViewById(R.id.IName);
        Inamelabel = findViewById(R.id.labelName);
        Imonth = findViewById(R.id.IMonth);
        Imonthlabel = findViewById(R.id.IMonthlabel);
        Idetail = findViewById(R.id.IDetail);
        Idetaillabel = findViewById(R.id.Idetaillabel);
        Idate = findViewById(R.id.Idate);
        dateSelect = findViewById(R.id.dateSelect);
        AttachBill = findViewById(R.id.AttachBill);
        AttachImage = findViewById(R.id.AttachImage);
        Icategory = findViewById(R.id.Icategory);
        Isave = findViewById(R.id.ISave);
        IImage = findViewById(R.id.IImage);
        IBill = findViewById(R.id.IBill);
        backbutton = findViewById(R.id.back_button);
        itemViewModel = new ItemViewModel(getApplication());
        categoryViewModel = new CategoryViewModel(getApplication());
        Intent intent = getIntent();
        Animation labelUp,labelDown;

        /////////////////////////////////////////////////////////

        labelUp = AnimationUtils.loadAnimation(this,R.anim.label_up);
        labelDown = AnimationUtils.loadAnimation(this,R.anim.label_down);

        Iname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && Iname.getText().toString().length() == 0){
                    Inamelabel.startAnimation(labelUp);
                    Inamelabel.setBackgroundColor(getResources().getColor(R.color.label));
                }
                else {
                    if(!hasFocus && Iname.getText().toString().length() == 0){
                        Inamelabel.startAnimation(labelDown);
                        Inamelabel.setBackground(getDrawable(R.drawable.label_back));
                    }
                }
            }
        });
        Imonth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && Imonth.getText().toString().length() == 0){
                    Imonthlabel.startAnimation(labelUp);
                    Imonthlabel.setBackgroundColor(getResources().getColor(R.color.label));
                }
                else {
                    if(!hasFocus && Imonth.getText().toString().length() == 0){
                        Imonthlabel.startAnimation(labelDown);
                        Imonthlabel.setBackground(getDrawable(R.drawable.label_back));
                    }
                }
            }
        });
        Idetail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && Idetail.getText().toString().length() == 0){
                    Idetaillabel.startAnimation(labelUp);
                    Idetaillabel.setBackgroundColor(getResources().getColor(R.color.label));
                }
                else {
                    if(!hasFocus && Idetail.getText().toString().length() == 0){
                        Idetaillabel.startAnimation(labelDown);
                        Idetaillabel.setBackground(getDrawable(R.drawable.label_back));
                    }
                }
            }
        });

        setInitVlues();
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        categoryViewModel.getAllcategory().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplication(),android.R.layout.simple_spinner_item,strings);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                Icategory.setAdapter(adapter);
                currentCategory = intent.getStringExtra(ItemActivity.KEY_FOR_CATEGORY);
                if(currentCategory != null){
                    if(currentCategory.length() > 0 ){
                        int index = strings.indexOf(currentCategory);
                        Icategory.setSelection(index);
                        Icategory.setEnabled(false);
                    }
                }



            }
        });


        Icategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCategory = parent.getItemAtPosition(position).toString();
                Log.e("spinner",currentCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AttachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQUEST_CODE_Image);
            }
        });

        AttachBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQUEST_CODE_Bill);
            }
        });
        Isave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(validateInfo(true)){
                    saveItem();
                }
            }
        });

        dateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               selectDate();
            }
        });




        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInfo(false);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        Iname.addTextChangedListener(watcher);
        Imonth.addTextChangedListener(watcher);


    }

    //for setting default values
    public void setInitVlues(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Idate.setText(day+"/"+(month+1)+"/"+year);

    }


    //for selecting date
    public void selectDate(){
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Idate.setText(dayOfMonth+"/"+(month+1)+"/"+year);
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,listener,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)//////////////////////////////////////////////////////////////////////

    ///////val for checking if save button is pressed or not
    public boolean validateInfo(boolean val){
        boolean result = true;

        if(Imonth.getText().toString().length() == 0){
            Imonth.setBackground(getDrawable(R.drawable.required_outline));
            if(val){Imonth.requestFocus();}
            result = false;
        }
        else {
            Imonth.setBackground(getDrawable(R.drawable.category_border));
        }
        if(Iname.getText().toString().length() == 0 ){
            Iname.setBackground(getDrawable(R.drawable.required_outline));
            if(val){Iname.requestFocus();}
            result = false;
        }
        else {
            Iname.setBackground(getDrawable(R.drawable.category_border));
        }
        return result;

    }


    //for saving data in database
    public void saveItem(){
        try {
            categoryViewModel.incrementItem(currentCategory);
            model = new ItemModel();
            model.setIname(Iname.getText().toString());
            model.setCategory(currentCategory);
            model.setPurchaseDate(Idate.getText().toString());
            model.setDetail(Idetail.getText().toString());
            model.setDurationMonth(12);
            String date = Idate.getText().toString();
            SimpleDateFormat formate = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date purchaseDate = formate.parse(date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(purchaseDate);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                Month = calendar.get(Calendar.MONTH);
                Year = calendar.get(Calendar.YEAR);
//            Log.e("jaydip","day "+calendar.get(Calendar.MONTH));

            } catch (ParseException e) {
                e.printStackTrace();
            }
            calculateExpireDate();
            if (IImageBitmap != null) {
                stream = new ByteArrayOutputStream();
                File file = PathProvider.getOutputMediaFile(this,true);
                FileOutputStream Fstream = new FileOutputStream(file);
                scaledImage = getSacalabel(IImageBitmap);
                IImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, Fstream);
                Fstream.close();
                scaledImage.compress(Bitmap.CompressFormat.JPEG,100,stream);
                byte[] image = stream.toByteArray();
                model.setItemImage(image);
                model.setItemImageUri(file.getPath());
            }
            if (IBillBitmap != null) {
                stream = new ByteArrayOutputStream();
                File file = PathProvider.getOutputMediaFile(this,false);
                FileOutputStream fstream = new FileOutputStream(file);
                scaledBill = getSacalabel(IBillBitmap);
                IBillBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fstream);
                fstream.close();
                scaledBill.compress(Bitmap.CompressFormat.JPEG,100,stream);
                byte[] image = stream.toByteArray();
                model.setBillImage(image);
                model.setBillImageUri(file.getPath());
            }
            itemViewModel.addItem(model);

            Log.e("jaydip", "added");
            Toast.makeText(this, Iname.getText().toString() + " added ", Toast.LENGTH_SHORT).show();
            finish();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    Bitmap getSacalabel(Bitmap bitmap){
        Bitmap res = null;
        int ra = (int)(bitmap.getHeight() * (512.0 / bitmap.getWidth()));
        res = Bitmap.createScaledBitmap(bitmap,512,ra,true);
        return  res;
    }


    public void calculateExpireDate(){
        int WarrentInMonth = Integer.parseInt(Imonth.getText().toString());
        Month = Month + WarrentInMonth;
        int yearToadd = (int)Month / 12;
        Year = Year + yearToadd;
        Month = Month % 12;
        String expireDate = day+"/"+(Month+1)+"/"+Year;
        model.setExpireDate(expireDate);
        Log.e("expire",day+"/"+Month+"/"+Year);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_Image && resultCode == RESULT_OK && data.getData() != null){
            Uri filepath = data.getData();
            try {
                IImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
//                int ratio =(int) (IImageBitmap.getHeight() *(512.0 / IImageBitmap.getWidth()));
//                Bitmap scaled = Bitmap.createScaledBitmap(IImageBitmap,512,ratio,true);
//
//                Log.e("jaydip",scaled.getByteCount()+"     "+IImageBitmap.getByteCount()+"    ");
//
//                File pic = getOutputMediaFile();
//                FileOutputStream stream = new FileOutputStream(pic);
//                String s = pic.getPath();
//                IImageBitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
//                Log.e("jaydip",Environment.getDataDirectory().getAbsolutePath());
//                stream.close();
                IImage.setImageBitmap(IImageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if(requestCode == REQUEST_CODE_Bill && resultCode == RESULT_OK && data.getData() != null){
            Uri filepath = data.getData();
            try {
                IBillBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                IBill.setImageBitmap(IBillBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }





}