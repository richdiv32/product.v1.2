package com.ng.campusbuddy.education.reader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

//import com.github.barteksc.pdfviewer.PDFView;
import com.ng.campusbuddy.R;

public class PDFActivity extends AppCompatActivity {

//    PDFView pdfView;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        this will prevent screenshot in an activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window w = getWindow();
            //removes status bar with background
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

//        pdfView = findViewById(R.id.pdfViewer);
//
//        pdfView.fromAsset("business.pdf").load();

//        webView = findViewById(R.id.webview);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.l

    }


}
