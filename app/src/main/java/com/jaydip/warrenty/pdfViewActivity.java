package com.jaydip.warrenty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class pdfViewActivity extends AppCompatActivity {
    PDFView pdfView;
    public static String PDF_URI = "pdfUri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);
        pdfView = findViewById(R.id.pdfView);
        Intent intent = getIntent();
        String uriString  = intent.getStringExtra(PDF_URI);
        Log.e("jaydip",uriString);
        Uri uri = Uri.parse(uriString);
        File file = new File(uriString);
        pdfView.fromFile(file).load();
    }
}