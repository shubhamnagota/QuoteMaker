package com.thestubbornthinker.quotemaker;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import android.os.Environment;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Button button,buttonC,buttonT,buttonS,buttonF;
    AutoResizeEditText ft;
    int themeColor,textcolor;
    Typeface[] typesFaces={Typeface.DEFAULT_BOLD,Typeface.MONOSPACE,Typeface.SANS_SERIF,Typeface.SERIF,Typeface.DEFAULT};
    int[] typeStyle={Typeface.NORMAL,Typeface.BOLD,Typeface.ITALIC};
    private InterstitialAd mInterstitialAd;
    int font=0;
    int style=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        AdRequest request = new AdRequest.Builder()
                .addTestDevice("33BE2250B43518CCDA7DE426D04EE232")
                .build();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(request);

        button = (Button) findViewById(R.id.themeButton);
        buttonT=(Button)findViewById(R.id.textButton);
        buttonF=(Button)findViewById(R.id.fontButton);
        buttonS=(Button)findViewById(R.id.styleButton);
        setTitle(null);
        Random rnd = new Random();
        themeColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        button.getRootView().setBackgroundColor(themeColor);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(themeColor);
            if (getSupportActionBar().isShowing()) getSupportActionBar().hide();

            buttonC = (Button) findViewById(R.id.shareButton);
            ft = (AutoResizeEditText) findViewById(R.id.textView);
            ft.setEnabled(true);
            ft.setFocusableInTouchMode(true);
            ft.setFocusable(true);
            ft.setEnableSizeCache(false);
            ft.setMovementMethod(null);
            // can be added after layout inflation; it doesn't have to be fixed
            // value
            ft.setMaxHeight(330);
            final View root = button.getRootView();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ft.clearFocus();
                    Random rnd = new Random();
                    themeColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    root.setBackgroundColor(themeColor);
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.setStatusBarColor(themeColor);
                    }
                }
            });

            buttonT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Random rnd = new Random();
                    textcolor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    if(textcolor==themeColor) textcolor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    ft.setTextColor(textcolor);
                }
            });

            buttonF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ft.setTypeface(typesFaces[font]);
                    font++;
                    if(font>=5) font=0;
                }
            });

            buttonS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ft.setTypeface(ft.getTypeface(), typeStyle[style]);
                    style++;
                    if(style>=3) style=0;
                }
            });
            buttonC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ft.clearFocus();
                    button.setVisibility(View.INVISIBLE);
                    buttonC.setVisibility(View.INVISIBLE);
                    root.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(root.getDrawingCache());
                    root.setDrawingCacheEnabled(false);
                    final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
                    File dir = new File(dirPath);
                    if (!dir.exists())
                        dir.mkdirs();
                    File file = new File(dirPath, "screenshot.png");
                    try {
                        FileOutputStream fOut = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                        fOut.flush();
                        fOut.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Uri uri = Uri.fromFile(file);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("image/*");

                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    try {
                        startActivity(Intent.createChooser(intent, "Share Screenshot"));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(MainActivity.this, "No App Available", Toast.LENGTH_SHORT).show();
                    }
                    button.setVisibility(View.VISIBLE);
                    buttonC.setVisibility(View.VISIBLE);
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                    }
                }
            });
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        setupUI(findViewById(R.id.main), ft);

    }

    public void setupUI(View view, final AutoResizeEditText aText) {

        // if the view is not instance of AutoResizeEditText
        // i.e. if the user taps outside of the box
        if (!(view instanceof AutoResizeEditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();

                    Log.d("TXTS",
                            "Text Size = "
                                    + aText.getTextSize());
                    if (aText.getTextSize() < 50f) {
                        // you can define your minSize, in this case is 50f
                        // trim all the new lines and set the text as it was
                        // before
                        aText.setText(aText.getText().toString().replaceAll("(?m)^[ \t]*\r?\n", ""));
                    }

                    return false;
                }
            });
        }

        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, aText);
            }
        }
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (this.getCurrentFocus() != null
                && this.getCurrentFocus().getWindowToken() != null)
            inputMethodManager.hideSoftInputFromWindow(this
                    .getCurrentFocus().getWindowToken(), 0);
    }
}
