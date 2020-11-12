package com.jaydip.warrenty;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.jaydip.warrenty.Addapters.CategoryAddapter;
import com.jaydip.warrenty.Broadcast.NotificationRecieverer;
import com.jaydip.warrenty.Listeners.DeleteCategory;
import com.jaydip.warrenty.Models.CategoryModel;
import com.jaydip.warrenty.Service.DriveServiceHelper;
import com.jaydip.warrenty.ViewModels.CategoryViewModel;
import com.jaydip.warrenty.ViewModels.ItemViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeActivity extends AppCompatActivity implements DeleteCategory {
    CategoryAddapter Caddapter;
    List<CategoryModel> list;
    RecyclerView CategoryView;
    CategoryViewModel categoryViewModel;
    ItemViewModel itemViewModel;
    FloatingActionButton fab,fab1,fab2;
    Animation fab_rotate,fab_back,fab_open,fab_close;
    TextView fab_text2,fab_text3;
    boolean isopen;
    List<String> categories;
    ImageView background,setting;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_home);

//        SearchForFile();

        ///////////////////////////////////////////////////////////
        Caddapter = new CategoryAddapter(this);
        list = new ArrayList<>();
        CategoryView = findViewById(R.id.category);
        CategoryView.setLayoutManager(new GridLayoutManager(this,2));
        categoryViewModel = new CategoryViewModel(getApplication());
        itemViewModel = new ItemViewModel(getApplication());
        CategoryView.setAdapter(Caddapter);
        fab = findViewById(R.id.fab1);
        fab1 = findViewById(R.id.fab2);
        fab_rotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_on);
        fab_back = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_clos);
        fab2 = findViewById(R.id.fab3);
        fab_text2 = findViewById(R.id.fab_text2);
        fab_text3 = findViewById(R.id.fab_text3);
        isopen = false;
        background = findViewById(R.id.background);
        setting = findViewById(R.id.setting);


        //////////////////////////////////////////////////////////
        categoryViewModel.getListLiveData().observe(this, new Observer<List<CategoryModel>>() {
            @Override
            public void onChanged(List<CategoryModel> categoryModels) {
                Caddapter.setList(categoryModels);
            }
        });
        categoryViewModel.getAllcategory().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                categories = strings;
            }
        });

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFab();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isopen){

                        showFab();

                }
                else {

                    hideFab();
                }

            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(),AddItem.class);
                startActivity(intent);
                hideFab();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });
        Toast.makeText(this,"Long press to delete Category",Toast.LENGTH_LONG).show();


        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(i);
            }
        });

        /////////////////////////testing

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean val = preferences.getBoolean("notification_switch",false);
        Log.e("prefrence",val+"");


        FloatingActionButton floatingActionButton = findViewById(R.id.sendNotification);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryViewModel.addDefault();
                Log.e("jaydip","clicked");
                DriveServiceHelper helper = new DriveServiceHelper(getApplicationContext());
//                helper.deleteLastBackup();
                helper.printAllIDs();
//                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
//                if(account != null){
//                    GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(),
//                            Collections.singleton(DriveScopes.DRIVE_APPDATA));
//                    credential.setSelectedAccount(account.getAccount());
//                    Drive drive = new Drive.Builder(AndroidHttp.newCompatibleTransport(),new GsonFactory(),credential)
//                            .setApplicationName("Warranty")
//                            .build();
//                    DriveServiceHelper helper = new DriveServiceHelper(drive);
//                    helper.delete();
//                }
//                ComponentName name = new ComponentName(getApplicationContext(), NotificationRecieverer.class);
//
//                PackageManager manager = getPackageManager();
//                manager.setComponentEnabledSetting(name,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
//                Intent intent = new Intent(getApplicationContext(), NotificationRecieverer.class);
//                getApplicationContext().sendBroadcast(intent);

            }
        });
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(App.NOTIFICATI_ID,"Warrenty", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("for Warrenty");
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Log.e("jaydip","channel created");
        }
//        savetoZar();
        File file = new File(String.valueOf(getDatabasePath("WarrentyDatabase-wal")));
        Log.e("jaydip",file.getAbsolutePath());
    }


@RequiresApi(api = Build.VERSION_CODES.N)
void savetoZar(){
//    Log.e("jaydip","started");
//    File  file = new File(this.getFilesDir(),"/pdfs/pdf_23102020_1133.pdf");
//    File newfile = new File(this.getFilesDir(),"/zip");
//    Log.e("jaydip",this.getFilesDir()+"   filepath");
//    Log.e("jaydip",this.getDataDir()+"   Datapath");
//    File ddataFile = new File(this.getDataDir(),"/files/zipfile.zar");
//    File[] filesList = ddataFile.listFiles();
//    for (int i=0;i<filesList.length;i++){
//        Log.e("jaydip",filesList[i].getName());
//    }

//    if(!newfile.exists()){
//        newfile.mkdir();
//    }
//    if(file.exists()){
//        Log.e("jaydip","started");
//        PathProvider.compressToZar(file.getAbsolutePath(),newfile.getAbsolutePath()+"file.zip");
//    }
}
///////////////requesting permission


    ///// ad new category

    void addCategory(){
        EditText newCategory;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v  = inflater.inflate(R.layout.add_category,null,false);
        newCategory = v.findViewById(R.id.newCategory);
        builder.setView(v)
                .setCancelable(false)
                .setTitle("Add category")
                .setNegativeButton("cancel",null)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       String s = newCategory.getText().toString();
                       if(categories.contains(s) && s.length() > 0){
                           Toast.makeText(getApplicationContext(),"Category exist",Toast.LENGTH_SHORT).show();
                       }
                       else if(s.length() > 0){
                           CategoryModel model = new CategoryModel();
                           model.setTotalItem(0);
                           model.setCategoryName(s);
                           model.setIconName("default_catrgoty");
                           categoryViewModel.addCategory(model);
                           Toast.makeText(getApplicationContext(),"Category Added",Toast.LENGTH_SHORT).show();
                           hideFab();
                       }
                    }
                });
        builder.show();
    }

    /// showing fab buttons
    void showFab()  {

        background.setVisibility(View.VISIBLE);
        fab.startAnimation(fab_rotate);
        fab1.startAnimation(fab_open);
        fab1.setVisibility(View.VISIBLE);
        fab2.startAnimation(fab_open);
        fab2.setVisibility(View.VISIBLE);

        fab_text2.setVisibility(View.VISIBLE);
        fab_text3.setVisibility(View.VISIBLE);
        fab_text2.startAnimation(fab_open);
        fab_text3.startAnimation(fab_open);
        isopen = !isopen;

    }

    //hidding fab button
    void hideFab(){
        background.setVisibility(View.GONE);
        fab.startAnimation(fab_back);
        fab1.startAnimation(fab_close);
        fab1.setVisibility(View.GONE);
        fab2.startAnimation(fab_close);
        fab2.setVisibility(View.GONE);

        fab_text2.setVisibility(View.INVISIBLE);
        fab_text3.setVisibility(View.INVISIBLE);
        fab_text2.startAnimation(fab_close);
        fab_text3.startAnimation(fab_close);
        isopen = !isopen;

    }

    @Override
    public void onBackPressed() {
        if(isopen){
            hideFab();
        }
        else {
            Log.e("jaydip","backprssed");
            super.onBackPressed();
        }

    }

    @Override
    public void DeleteCCategory(CategoryModel category) {
        categoryViewModel.deleteCat(category);
        itemViewModel.deleteAll(category.getCategoryName());
    }
}