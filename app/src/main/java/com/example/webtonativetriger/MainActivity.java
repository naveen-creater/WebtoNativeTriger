package com.example.webtonativetriger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private TextView textFromWeb;
    private EditText textToWeb;
    private Button sendToWebButtom;

    private String JAVASCRIPT_OBJ = "javascript_obj";
    private String BASE_URL = "file:///android_asset/webview.html";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JavaScriptInterface(this),JAVASCRIPT_OBJ);
        webView.setWebViewClient(new MyWebClient(this));
        webView.loadUrl(BASE_URL);


    }

    private void initView() {
        webView = findViewById(R.id.my_web_view);
        textFromWeb = findViewById(R.id.txt_from_web);
        textToWeb = findViewById(R.id.edit_text_to_web);
        sendToWebButtom = findViewById(R.id.btn_send_to_web);

        sendToWebButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.evaluateJavascript("javascript: " +
                        "updateFromAndroid(\"" + textToWeb.getText().toString() + "\")", null);
            }
        });
    }

    public   class JavaScriptInterface {
        Context context;
        public JavaScriptInterface(Context context){
            this.context = context;

        }

        @JavascriptInterface
        public void textFromWeb(String value){
            textFromWeb.setText(value);
        }

    }


    public class MyWebClient extends WebViewClient {

        private Context context;

        public MyWebClient(Context context) {
            this.context = context;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            System.out.println("page_started"+url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            System.out.println("divs onPageFinished"+url);

            if (url.equals(BASE_URL)) {
                injectJavaScriptFunction();
            }
        }



        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            description = "<br><b>Error Occurred : </b><br>" + description;
            view.loadDataWithBaseURL(null, description, "text/html", "UTF-8", null); }

    }

    private void injectJavaScriptFunction() {
        webView.loadUrl("javascript: " +
                "window.androidObj.textToAndroid = function(message) { " +
                JAVASCRIPT_OBJ + ".textFromWeb(message) }");
    }


    @Override
    protected void onDestroy() {
        webView.removeJavascriptInterface(JAVASCRIPT_OBJ);
        super.onDestroy();
    }
}