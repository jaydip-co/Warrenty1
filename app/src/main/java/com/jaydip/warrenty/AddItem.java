package com.jaydip.warrenty;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;
import com.jaydip.warrenty.Models.CategoryModel;
import com.jaydip.warrenty.Models.ItemModel;
import com.jaydip.warrenty.ViewModels.CategoryViewModel;
import com.jaydip.warrenty.ViewModels.ItemViewModel;
import com.jaydip.warrenty.prefsUtil.PrefUtil;
import com.jaydip.warrenty.prefsUtil.prefIds;
import com.shockwave.pdfium.PdfiumCore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;



public class AddItem extends AppCompatActivity implements PickiTCallbacks {
    EditText Iname,Imonth,Idetail;
    TextView Idate,Isave,Inamelabel,Imonthlabel,Idetaillabel,pdfName;
    Button AttachImage,AttachBill;
    Spinner Icategory;
    ItemViewModel itemViewModel;
    ImageView IImage,IBill,backbutton,dateSelect;
    private static int REQUEST_CODE_Image =100;
    private static int REQUEST_CODE_Bill =150;
    Bitmap IImageBitmap,IBillBitmap,scaledImage,scaledBill;
    ByteArrayOutputStream stream ;
    CategoryViewModel categoryViewModel;
    String currentCategory,pdfPath,currentBillUri,currentImageUri,currentpdfpath;
    int day,Month,Year;
    ItemModel model;
    public static int CODE_FOR_PDF = 205;
    PickiT picKit;

    boolean isbillPdf,isBillset;
    int STORAGE_REQUEST_CODE = 112;




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
        picKit = new PickiT(this,this,this);

        isBillset = false;
//        currentCategory = intent.getStringExtra(ItemActivity.KEY_FOR_CATEGORY);
//        if(currentCategory == null){
//            currentCategory = "Electronics";
//        }

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
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AddItem.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_REQUEST_CODE);
                }
                else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, REQUEST_CODE_Image);
                }
            }
        });

        AttachBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AddItem.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_REQUEST_CODE);
                }
                else {
                     Intent intent = new Intent();

                     intent.setType("image/*");
                     String[] mimetype = {"image/*", "application/pdf"};
                     intent.setAction(Intent.ACTION_GET_CONTENT);
                     intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetype);
                     startActivityForResult(intent, REQUEST_CODE_Bill);
                 }
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
    void requestPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AddItem.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == STORAGE_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(),"Permission granted",Toast.LENGTH_SHORT).show();
            }
        }
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
            model.setDurationMonth(Integer.parseInt(Imonth.getText().toString()));
            Calendar calendarNow= Calendar.getInstance();
            String updateDate = calendarNow.get(Calendar.DAY_OF_MONTH)+"/"+(calendarNow.get(Calendar.MONTH)+1)+"/"+calendarNow.get(Calendar.YEAR);
            model.setLastUpdateDate(updateDate);
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
//                FileOutputStream Fstream = new FileOutputStream(file);
                scaledImage = getSacalabel(IImageBitmap);
                Utils.copyPdf(getApplicationContext(),currentImageUri,file.getPath());
//                IImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, Fstream);
//                Fstream.close();
                scaledImage.compress(Bitmap.CompressFormat.JPEG,100,stream);
                byte[] image = stream.toByteArray();
                model.setItemImage(image);
                model.setItemImageUri(file.getPath());
            }
            if(isBillset){
                if(isbillPdf){
                    File newpdf = PathProvider.getnewPdf(getApplicationContext());
                    Utils.copyPdf(getApplicationContext(),currentBillUri,newpdf.getPath());
                    model.setBillUri(newpdf.getPath());

                }
                else {
                    File newimg = PathProvider.getOutputMediaFile(getApplicationContext(),false);
                    Utils.copyPdf(getApplicationContext(),currentBillUri,newimg.getPath());
                    Bitmap scaled = getSacalabel(IBillBitmap);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    scaled.compress(Bitmap.CompressFormat.JPEG,100,stream);
                    model.setBillImage(stream.toByteArray());
                    model.setBillUri(newimg.getPath());
                }
                model.setBillPdf(isbillPdf);
            }
         itemViewModel.addItem(model);
            PrefUtil.saveToPrivate(getApplicationContext(), prefIds.Daily_update_Check,"yes");
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_Image && resultCode == RESULT_OK && data.getData() != null){
            Uri filepath = data.getData();
            try {
                IImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                currentImageUri = Utils.storeToTemp(getApplicationContext(),IImageBitmap,true);
                setIntent();

                IImage.setImageBitmap(IImageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if(requestCode == REQUEST_CODE_Bill && resultCode == RESULT_OK && data.getData() != null){
            Uri filepath = data.getData();
            try {
                isBillset = true;
                ContentResolver resolver = getContentResolver();
                MimeTypeMap map = MimeTypeMap.getSingleton();
                String type = map.getExtensionFromMimeType(resolver.getType(filepath));
                Log.e("bill",type);
                setBill(filepath,type);
//
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if(requestCode == CODE_FOR_PDF && resultCode == RESULT_OK && data.getData() != null){
            Uri path = data.getData();
            picKit.getPath(path,Build.VERSION.SDK_INT);




        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void setBill(Uri uri, String type){
        try {
            if (type.equals("jpg") || type.equals("jpeg") || type.equals("png")) {
                currentpdfpath = null;
                IBillBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                currentBillUri = Utils.storeToTemp(getApplicationContext(), IBillBitmap, false);
                isbillPdf = false;
                setIntent();
                IBill.setImageBitmap(IBillBitmap);
            }
            else if(type.equals("pdf")){
                IBillBitmap = null;
                isbillPdf = true;
                picKit.getPath(uri,Build.VERSION.SDK_INT);
                IBill.setImageDrawable(getDrawable(R.drawable.pdf));
            }
//            setIntent();
        }
        catch (Exception e){
            Log.e("bill",e.toString());
        }

    }
    public void removeIntent(){
    }
    public void setIntent(){

        if(IImageBitmap != null){

            AttachImage.setText("Change Image of Item");
            IImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),ImageActivity.class);
                    intent.putExtra(ImageActivity.KEY_FOR_IMAGE_URI,currentImageUri);
                    startActivity(intent);

                }
            });
        }
        if(currentBillUri != null){
            AttachBill.setText("change Bill");
            if(isbillPdf){
                IBill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), pdfViewActivity.class);
                        intent.putExtra(pdfViewActivity.PDF_URI, currentBillUri);
                        startActivity(intent);
                    }
                });

            }
            else {
                IBill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),ImageActivity.class);
                        intent.putExtra(ImageActivity.KEY_FOR_IMAGE_URI,currentBillUri);
                        startActivity(intent);
                    }
                });

            }
        }


    }



    @Override
    public void PickiTonUriReturned() {

    }

    @Override
    public void PickiTonStartListener() {

    }

    @Override
    public void PickiTonProgressUpdate(int progress) {

    }

    @Override
    public void PickiTonCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason) {
        Log.e("pdf",path);
        currentBillUri = path;
//        Bitmap imageForBill = getPdfThump(path);
//        IBill.setImageBitmap(imageForBill);

        File file = new File(path);
        Log.e("pdf",file.getName());
//
//        pdfName.setText(file.getName());
        setIntent();
    }

    Bitmap getPdfThump(String uriString){
        Uri uri = Uri.parse(uriString);
        Bitmap bmp = null;
        int pageNumber = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(this);
        try {
            //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
//            ParcelFileDescriptor fd = getContentResolver().openFileDescriptor(uri, "r");
//            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
//            pdfiumCore.openPage(pdfDocument, pageNumber);
//            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
//            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
//            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
//            pdfiumCore.closeDocument(pdfDocument); // important!
            InputStream stream = new FileInputStream(uriString);
            byte[] buff = new byte[1024];
            int length = stream.read(buff);
            bmp = BitmapFactory.decodeByteArray(buff,0,buff.length);

        } catch(Exception e) {
            Log.e("tag",e.toString());
        }
        return  bmp;
    }
}