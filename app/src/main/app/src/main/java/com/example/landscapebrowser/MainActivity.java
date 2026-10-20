package com.example.landscapebrowser;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.view.WindowManager;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.net.Uri;

public class MainActivity extends Activity {
    WebView web;
    EditText address;
    ProgressBar progress;

    int dp(int n) {
        return (int)(n * getResources().getDisplayMetrics().density + 0.5f);
    }

    Button btn(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(Color.rgb(42,54,77)));
        return b;
    }

    void fullScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        fullScreen();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(16,21,34));

        LinearLayout bar = new LinearLayout(this);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(4),dp(4),dp(4),dp(4));

        Button back = btn("‹");
        Button home = btn("⌂");
        Button reload = btn("↻");
        address = new EditText(this);
        address.setSingleLine(true);
        address.setTextColor(Color.WHITE);
        address.setHintTextColor(Color.LTGRAY);
        address.setHint("Search Google or enter website");
        address.setTextSize(14);
        address.setImeOptions(EditorInfo.IME_ACTION_GO);

        Button go = btn("GO");
        bar.addView(back,new LinearLayout.LayoutParams(dp(42),dp(44)));
        bar.addView(home,new LinearLayout.LayoutParams(dp(42),dp(44)));
        bar.addView(address,new LinearLayout.LayoutParams(0,dp(44),1));
        bar.addView(reload,new LinearLayout.LayoutParams(dp(42),dp(44)));
        bar.addView(go,new LinearLayout.LayoutParams(dp(48),dp(44)));

        progress = new ProgressBar(this,null,
            android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100);

        web = new WebView(this);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView v,String url) {
                address.setText(url);
            }
        });
        web.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView v,int p) {
                progress.setProgress(p);
                progress.setVisibility(p >= 100 ? View.GONE : View.VISIBLE);
            }
        });

        Runnable load = () -> {
            String s = address.getText().toString().trim();
            if (s.isEmpty()) return;
            if (s.matches("(?i)^https?://.*")) web.loadUrl(s);
            else if (s.contains(".") && !s.contains(" "))
                web.loadUrl("https://" + s);
            else web.loadUrl("https://www.google.com/search?q=" +
                Uri.encode(s));
        };

        back.setOnClickListener(v -> {
            if (web.canGoBack()) web.goBack();
        });
        home.setOnClickListener(v -> web.loadUrl("https://www.google.com"));
        reload.setOnClickListener(v -> web.reload());
        go.setOnClickListener(v -> load.run());
        address.setOnEditorActionListener((v,id,event) -> {
            if (id == EditorInfo.IME_ACTION_GO ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                load.run();
                return true;
            }
            return false;
        });

        root.addView(bar);
        root.addView(progress,new LinearLayout.LayoutParams(-1,dp(2)));
        root.addView(web,new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
        web.loadUrl("https://www.google.com");
    }

    @Override public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        if (focus) fullScreen();
    }

    @Override public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }
              }
