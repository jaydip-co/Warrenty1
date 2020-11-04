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
import com.jaydip.warrenty.Models.ItemModel;
import com.jaydip.warrenty.ViewModels.CategoryViewModel;
import com.jaydip.warrenty.ViewModels.ItemViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

public class EditItem extends AppCompatActivity implements PickiTCallbacks {

    EditText Iname,Imonth,Idetail,labelTest;
    TextView Idate,Isave,Inamelabel,Idatelebel,Imonthlabel,Idetaillabel,pdfName;
    Button AttachImage,AttachBill,AttachPdf;
    Spinner Icategory;
    ImageView IImage,IBill,backButton,dateSelect;
    ItemModel item;
    CategoryViewModel categoryViewModel;
    ItemViewModel itemViewModel;
    String currentCategory,lastDate,lastCategory,currentImageUri,currentBillUri,currentpdfpath;
    Toolbar toolbar;
    byte[] ItemImage,BillImage;
    Bitmap ItemBitmap,BillBitmap;
    private static int REQUEST_CODE_Image =100;
    private static int REQUEST_CODE_Bill =150;
    ByteArrayOutputStream stream;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    int lastvalidity;
    Animation labelUp,labelDown;
    boolean isbillchanged,isImageChanged,ispdfchanged,isBillPdf;
    PickiT picKit;
    int STORAGE_REQUEST_CODE = 112;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        ///////////////////////////////////////////////////////////////
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        backButton = findViewById(R.id.back_button);
        Iname = findViewById(R.id.IName);
        Inamelabel = findViewById(R.id.labelName);
        Imonth = findViewById(R.id.IMonth);
        Imonthlabel = findViewById(R.id.IMonthlabel);
        Idetail = findViewById(R.id.IDetail);
        Idetaillabel = findViewById(R.id.Idetaillabel);
        Idate = findViewById(R.id.Idate);
        dateSelect = findViewById(R.id.dateSelect);
        Idatelebel = findViewById(R.id.IDatelabel);
        AttachBill = findViewById(R.id.AttachBill);
        AttachImage = findViewById(R.id.AttachImage);
        Icategory = findViewById(R.id.Icategory);
        Isave = findViewById(R.id.Isave);
        IImage = findViewById(R.id.IImage);
        IBill = findViewById(R.id.IBill);


        itemViewModel = new ItemViewModel(getApplication());
        categoryViewModel = new CategoryViewModel(getApplication());



        Intent intent = getIntent();
        item = (ItemModel) intent.getSerializableExtra("item");
        currentCategory = item.getCategory();
        lastCategory = currentCategory;
        isbillchanged = false;
        isbillchanged = false;
        ispdfchanged = false;
        isBillPdf = item.isBillPdf();

        picKit = new PickiT(this,this,this);
        /////////////////////////////////////////////////////////////////



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





        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




        initValues();

        categoryViewModel.getAllcategory().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,strings);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Icategory.setAdapter(adapter);
                String c = item.getCategory();
                int a = strings.indexOf(c);
                Icategory.setSelection(a);
            }
        });
        Icategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        AttachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(EditItem.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_REQUEST_CODE);
                }
                else {
                     Intent itemIntent = new Intent();
                     itemIntent.setAction(Intent.ACTION_GET_CONTENT);
                     itemIntent.setType("image/*");
                     startActivityForResult(itemIntent, REQUEST_CODE_Image);
                 }
            }
        });
        AttachBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(EditItem.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_REQUEST_CODE);
                }
                else {
                     Intent itemIntent = new Intent();
                     itemIntent.setAction(Intent.ACTION_GET_CONTENT);
                     itemIntent.setType("image/*");
                     String[] mimetype = {"image/*", "application/pdf"};
                     itemIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetype);
                     startActivityForResult(itemIntent, REQUEST_CODE_Bill);
                 }


            }
        });
        dateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });
        Isave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
            if(validateInfo(true)){
                Log.e("jaydip","saved");
                saveItem();
            }
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

    //for fetching image from storage true for item image and false for bill image



//set initial values

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initValues(){
        isBillPdf = item.isBillPdf();
        Iname.setText(item.getIname());
        Inamelabel.startAnimation(labelUp);
        Inamelabel.setBackgroundColor(getResources().getColor(R.color.label));

        Idate.setText(item.getPurchaseDate());
        lastDate = item.getPurchaseDate();
        Idetail.setText(item.getDetail());
        if(item.getDetail().length() > 0){
            Idetaillabel.startAnimation(labelUp);
            Idetaillabel.setBackgroundColor(getResources().getColor(R.color.label));
        }

        lastvalidity = item.getDurationMonth();
        Imonth.setText(item.getDurationMonth()+"");
        Imonthlabel.startAnimation(labelUp);
        Imonthlabel.setBackgroundColor(getResources().getColor(R.color.label));
        ItemImage = item.getItemImage();
        if(ItemImage != null){

            ItemBitmap = BitmapFactory.decodeByteArray(ItemImage,0, ItemImage.length);
            IImage.setImageBitmap(ItemBitmap);
            currentImageUri = item.getItemImageUri();
//            IImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getApplicationContext(),ImageActivity.class);
//                    intent.putExtra("image",item.getItemImageUri());
//                    startActivity(intent);
//                }
//            });
        }
        currentBillUri = item.getBillUri();
        if(currentBillUri != null) {
            if (isBillPdf) {
                IBill.setImageDrawable(getDrawable(R.drawable.pdf));
            } else {
                BillBitmap = BitmapFactory.decodeByteArray(item.getBillImage(), 0, item.getBillImage().length);
                IBill.setImageBitmap(BillBitmap);
            }
        }

        setIntent();
    }


    //for save update
    void saveItem(){
        try {
            item.setIname(Iname.getText().toString());
            item.setCategory(currentCategory);
            item.setPurchaseDate(Idate.getText().toString());
            item.setDurationMonth(Integer.parseInt(Imonth.getText().toString()));
            item.setDetail(Idetail.getText().toString());
            Calendar calendar = Calendar.getInstance();
            String updateDate = calendar.get(Calendar.DAY_OF_MONTH)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR);
            item.setLastUpdateDate(updateDate);
            if (ItemBitmap != null && isImageChanged) {
                File file;
                if(item.getItemImageUri() != null){
                     file = new File(item.getItemImageUri());
                }
                else {
                    file = PathProvider.getOutputMediaFile(getApplicationContext(),true);
                    item.setItemImageUri(file.getPath());
                }
                stream = new ByteArrayOutputStream();
                Bitmap scaledImage = getSacalabel(ItemBitmap);

                FileOutputStream fstream = new FileOutputStream(file);
                scaledImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                ItemBitmap.compress(Bitmap.CompressFormat.JPEG,100,fstream);
                fstream.close();
                ItemImage = stream.toByteArray();
                item.setItemImage(ItemImage);
            }
            if(isbillchanged){
                if(item.getBillUri() == null){
                    if(isBillPdf){

                        File  newpdf = PathProvider.getnewPdf(getApplicationContext());
                        Utils.copyPdf(getApplicationContext(),currentBillUri,newpdf.getPath());
                        item.setBillUri(newpdf.getPath());
                    }
                    else {
                        setBillImage();
                        File newimg = PathProvider.getOutputMediaFile(getApplicationContext(),false);
                        Utils.copyPdf(getApplicationContext(),currentBillUri,newimg.getPath());
                        item.setBillUri(newimg.getPath());

                    }

                }
                else {
                    if (item.isBillPdf() == isBillPdf) {
                        if (!isBillPdf) {
                            setBillImage();
                        }
                        Utils.copyPdf(getApplicationContext(), currentBillUri, item.getBillUri());
                    } else {
                        if (isBillPdf) {
                            File file = new File(item.getBillUri());
                            file.delete();
                            file = PathProvider.getnewPdf(getApplicationContext());
                            Utils.copyPdf(getApplicationContext(), currentBillUri, file.getPath());

                            item.setBillUri(file.getPath());
                            item.setBillImage(null);
                        } else {
                            File file = new File(item.getBillUri());
                            file.delete();
                            file = PathProvider.getOutputMediaFile(getApplicationContext(), false);
                            Utils.copyPdf(getApplicationContext(), currentBillUri, file.getPath());
                            setBillImage();
                            item.setBillUri(file.getPath());
                        }
                    }
                }
                item.setBillPdf(isBillPdf);
            }
//            if (BillBitmap != null && isbillchanged) {
//                File file;
//                if(item.getBillImageUri() != null){
//                    file = new File(item.getBillImageUri());
//                }
//                else {
//                    file = PathProvider.getOutputMediaFile(getApplicationContext(),false);
//                    item.setBillImageUri(file.getPath());
//                }
//                stream = new ByteArrayOutputStream();
//                Bitmap scaledBill = getSacalabel(BillBitmap);
//                FileOutputStream fstream = new FileOutputStream(file);
//                BillBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fstream);
//                scaledBill.compress(Bitmap.CompressFormat.JPEG,100,stream);
//                fstream.close();
//                BillImage = stream.toByteArray();
//                item.setBillImage(BillImage);
//            }
            String newDate = Idate.getText().toString();
            if (!newDate.equals(lastDate) || lastvalidity != Integer.parseInt(Imonth.getText().toString())) {
                Log.e("jaydip", "defferent");
                String newExpire = calculateExpireDate(newDate, Integer.parseInt(Imonth.getText().toString()));
                item.setExpireDate(newExpire);
            }
//            if(ispdfchanged){
//                String toStore;
//                if(item.getPdfPath() != null){
//                    toStore = item.getPdfPath();
//                }
//                else {
//                    File newpdf = PathProvider.getnewPdf(getApplicationContext());
//                    toStore = newpdf.getPath();
//                    item.setPdfPath(toStore);
//                }
//                Utils.copyPdf(getApplicationContext(),currentpdfpath,toStore);
//                item.setPdfName(pdfName.getText().toString());
//            }
            itemViewModel.update(item);
            if (!currentCategory.equals(lastCategory)) {
                categoryViewModel.decrementItem(lastCategory);
                categoryViewModel.incrementItem(currentCategory);
            }
            finish();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    void setBillImage(){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Bitmap scaled = getSacalabel(BillBitmap);
        scaled.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        item.setBillImage(outputStream.toByteArray());
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    Bitmap getSacalabel(Bitmap bitmap){
        Bitmap res = null;
        int ra = (int)(bitmap.getHeight() * (512.0 / bitmap.getWidth()));
        res = Bitmap.createScaledBitmap(bitmap,512,ra,true);
        return  res;
    }




    //for change purchase date
    void setDate(){
        try {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Idate.setText(dayOfMonth+"/"+(month+1)+"/"+year);
            }
        };
        Date date = format.parse(Idate.getText().toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,listener,
              calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    ///for checking info
    public boolean validateInfo(boolean val){
        boolean result = true;

        if(Imonth.getText().toString().length() == 0){
            Imonth.setBackground(getDrawable(R.drawable.required_outline));
           if(val){ Imonth.requestFocus();}
            result = false;
        }
        else {
            Imonth.setBackground(getDrawable(R.drawable.category_border));
        }
        if(Iname.getText().toString().length() == 0 ){
            Iname.setBackground(getDrawable(R.drawable.required_outline));
            if(val){ Iname.requestFocus();}
            result = false;
        }
        else {
            Iname.setBackground(getDrawable(R.drawable.category_border));
        }
        return result;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("jaydip","reached at result");
        if(resultCode == RESULT_OK && data.getData() != null){
            if(requestCode == REQUEST_CODE_Image){
                Uri path = data.getData();
                try {
                    isImageChanged = true;
                    ItemBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                    IImage.setImageBitmap(ItemBitmap);
                    currentImageUri = Utils.storeToTemp(getApplicationContext(),ItemBitmap,true);
                    setIntent();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode == REQUEST_CODE_Bill){
                Uri path = data.getData();
                try {
                    isbillchanged = true;
//                    BillBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
//                    IBill.setImageBitmap(BillBitmap);
//                    currentBillUri = Utils.storeToTemp(getApplicationContext(),BillBitmap,false);
//                    setIntent();
                    ContentResolver resolver = getContentResolver();
                    MimeTypeMap map = MimeTypeMap.getSingleton();
                    String type = map.getExtensionFromMimeType(resolver.getType(path));
                    setBill(path,type);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode == AddItem.CODE_FOR_PDF){
                Uri path = data.getData();
                picKit.getPath(path,Build.VERSION.SDK_INT);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == STORAGE_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(),"Permission granted",Toast.LENGTH_SHORT).show();
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void setBill(Uri path, String type){
        try {
            if (type.equals("jpeg") || type.equals("jpg") || type.equals("png")) {
                isBillPdf = false;
                BillBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                currentBillUri = Utils.storeToTemp(getApplicationContext(),BillBitmap,false);
                IBill.setImageBitmap(BillBitmap);
                setIntent();

            } else if (type.equals("pdf")) {
                isBillPdf = true;
                IBill.setImageDrawable(getDrawable(R.drawable.pdf));
                picKit.getPath(path,Build.VERSION.SDK_INT);

            }
        }
        catch (Exception e){
            Log.e("jaydip",e.toString());
        }

    }

    //////setting intent to pdf
    public void setIntent(){
        if(ItemBitmap != null){

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
        if(currentBillUri != null) {
            AttachBill.setText("Change Bill");
            if (isBillPdf) {
                IBill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), pdfViewActivity.class);
                        intent.putExtra(pdfViewActivity.PDF_URI, currentBillUri);
                        startActivity(intent);
                    }
                });
            } else {
                IBill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                        intent.putExtra(ImageActivity.KEY_FOR_IMAGE_URI, currentBillUri);
                        startActivity(intent);
                    }
                });
            }
        }

    }


    //calculate new expire date
    public String calculateExpireDate(String newDate,int expmonth){
            String expiredate = "";
        try {

            Date date = format.parse(newDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int Month = calendar.get(Calendar.MONTH);
            int Year = calendar.get(Calendar.YEAR);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            Month = Month + expmonth;
            int yearToadd = (int) Month / 12;
            Year = Year + yearToadd;
            Month = Month % 12;
            expiredate = day + "/" + (Month + 1) + "/" + Year;
            Log.e("expire", expiredate);

        }
        catch (Exception e){
            e.printStackTrace();
        }
            return expiredate;
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
        currentBillUri = path;
//        File file = new File(path);
//        pdfName.setText(file.getName());

        setIntent();

    }
}