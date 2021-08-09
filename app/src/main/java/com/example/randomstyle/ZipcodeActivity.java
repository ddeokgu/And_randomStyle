package com.example.randomstyle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ZipcodeActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zipcode);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        webView = (WebView) findViewById(R.id.webView);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(), "android");

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:getPostCode()");
            }
        });

        webView.loadUrl(Common.SERVER_URL + "/randomStyle/member/address.do");
    }

    class AndroidBridge {
        @JavascriptInterface
        public void putAddress(String zipcode, String address) {
            Intent intent = new Intent();
            intent.putExtra("zipcode" , zipcode);
            intent.putExtra("address", address);
            setResult(100, intent);
            finish();
        }
    }
}