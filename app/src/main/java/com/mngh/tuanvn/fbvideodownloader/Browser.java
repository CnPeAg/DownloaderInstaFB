package com.mngh.tuanvn.fbvideodownloader;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.kobakei.ratethisapp.RateThisApp;

import java.io.File;
import java.util.Random;

public class Browser extends AppCompatActivity {
    private FacebookVideoUrlManager facebookVideoUrlManager;
    Button download;
    private WebView facebookPage;
    private DownloadManager downloadManager;
    private ProgressBar progressBar;
    private InterstitialAd mInterstitialAd;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        facebookVideoUrlManager = new FacebookVideoUrlManager();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.navigation_clear);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTextPrimary));
        setSupportActionBar(toolbar);

        url = getIntent().getStringExtra("link");

        facebookPage = findViewById(R.id.facebook_page);
        progressBar = findViewById(R.id.progressBar);
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        showFacebookOnBrowser();


        AdView adView;
        adView = new AdView(this, "2061820020517519_2061821763850678", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        RelativeLayout adContainer = findViewById(R.id.banner2);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();


        mInterstitialAd = new InterstitialAd(this, "2061820020517519_2061824290517092");
        mInterstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                mInterstitialAd.loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });

        mInterstitialAd.loadAd();

        RateThisApp.onCreate(this);
        RateThisApp.Config config1 = new RateThisApp.Config(0, 0);
        RateThisApp.init(config1);
    }

    private void downloadVideoInBackground() {
        if (facebookVideoUrlManager.getUrl().isEmpty()) {
            showDialog("Opps!!", "Please click on the download icon on top video you want to download and click download");
        } else {
            String url = facebookVideoUrlManager.getUrl();
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.allowScanningByMediaScanner();
            String fileName = URLUtil.guessFileName(url, "Video", MimeTypeMap.getFileExtensionFromUrl(url));
            String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FBVideoDownloader";
            File file = new File(fullPath);
            if (!file.exists()) {
                file.mkdir();
            }
            request.setDestinationInExternalPublicDir(fullPath, fileName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            downloadManager.enqueue(request);
            Toast.makeText(Browser.this, "Downloading Video...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.logout:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    CookieManager.getInstance().removeAllCookies(null);
                    CookieManager.getInstance().flush();
                    showFacebookOnBrowser();
                } else {
                    CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(Browser.this);
                    cookieSyncMngr.startSync();
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.removeAllCookie();
                    cookieManager.removeSessionCookie();
                    cookieSyncMngr.stopSync();
                    cookieSyncMngr.sync();
                }

                break;
            case R.id.recycle_view:
                showFacebookOnBrowser();
                Toast.makeText(Browser.this, "Refreshing...", Toast.LENGTH_SHORT).show();
                break;

            case R.id.help:
                startActivity(new Intent(Browser.this, HowToUseActivity.class));
                break;
            case R.id.share:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String sAux = "\nLet me recommend you this application\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.mngh.tuanvn.fbvideodownloader \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "share via:"));
                } catch(Exception e) {
                    //e.toString();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    void showDialog(String title, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Browser.this);
        builder.setMessage("" + message + " video");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                downloadVideoInBackground();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }


    private void showFacebookOnBrowser() {
        facebookPage.getSettings().setJavaScriptEnabled(true);
        facebookPage.getSettings().getLoadWithOverviewMode();
        facebookPage.getSettings().getUseWideViewPort();
        facebookPage.getSettings().getAllowContentAccess();
        facebookPage.getSettings().getAllowFileAccessFromFileURLs();
        facebookPage.getSettings().getAllowFileAccess();
        facebookPage.getSettings().getAllowUniversalAccessFromFileURLs();
        facebookPage.getSettings().getCacheMode();
        facebookPage.addJavascriptInterface(this, "mJava");
        facebookPage.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.INVISIBLE);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        facebookPage.loadUrl("javascript:" +
                                "var e=0;\n" +
                                "window.onscroll=function()\n" +
                                "{\n" +
                                "\tvar ij=document.querySelectorAll(\"video\");\n" +
                                "\t\tfor(var f=0;f<ij.length;f++)\n" +
                                "\t\t{\n" +
                                "\t\t\tif((ij[f].parentNode.querySelectorAll(\"img\")).length==0)\n" +
                                "\t\t\t{\n" +
                                "\t\t\t\tvar nextimageWidth=ij[f].nextSibling.style.width;\n" +
                                "\t\t\t\tvar nextImageHeight=ij[f].nextSibling.style.height;\n" +
                                "\t\t\t\tvar Nxtimgwd=parseInt(nextimageWidth, 10);\n" +
                                "\t\t\t\tvar Nxtimghght=parseInt(nextImageHeight, 10); \n" +
                                "\t\t\t\tvar DOM_img = document.createElement(\"img\");\n" +
                                "\t\t\t\t\tDOM_img.height=\"68\";\n" +
                                "\t\t\t\t\tDOM_img.width=\"68\";\n" +
                                "\t\t\t\t\tDOM_img.style.top=(Nxtimghght/2-20)+\"px\";\n" +
                                "\t\t\t\t\tDOM_img.style.left=(Nxtimgwd/2-20)+\"px\";\n" +
                                "\t\t\t\t\tDOM_img.style.position=\"absolute\";\n" +
                                "\t\t\t\t\tDOM_img.src = \"https://image.ibb.co/kobwsk/one.png\"; \n" +
                                "\t\t\t\t\tij[f].parentNode.appendChild(DOM_img);\n" +
                                "\t\t\t}\t\t\n" +
                                "\t\t\tij[f].remove();\n" +
                                "\t\t} \n" +
                                "\t\t\te++;\n" +
                                "};" +
                                "var a = document.querySelectorAll(\"a[href *= 'video_redirect']\");\n" +
                                "for (var i = 0; i < a.length; i++) {\n" +
                                "    var mainUrl = a[i].getAttribute(\"href\");\n" +
                                "  a[i].removeAttribute(\"href\");\n" +
                                "\tmainUrl=mainUrl.split(\"/video_redirect/?src=\")[1];\n" +
                                "\tmainUrl=mainUrl.split(\"&source\")[0];\n" +
                                "    var threeparent = a[i].parentNode.parentNode.parentNode;\n" +
                                "    threeparent.setAttribute(\"src\", mainUrl);\n" +
                                "    threeparent.onclick = function() {\n" +
                                "        var mainUrl1 = this.getAttribute(\"src\");\n" +
                                "         mJava.getData(mainUrl1);\n" +
                                "    };\n" +
                                "}" +
                                "var k = document.querySelectorAll(\"div[data-store]\");\n" +
                                "for (var j = 0; j < k.length; j++) {\n" +
                                "    var h = k[j].getAttribute(\"data-store\");\n" +
                                "    var g = JSON.parse(h);\nvar jp=k[j].getAttribute(\"data-sigil\");\n" +
                                "    if (g.type === \"video\") {\n" +
                                "if(jp==\"inlineVideo\")" +
                                "{" +
                                "   k[j].removeAttribute(\"data-sigil\");" +
                                "}\n" +
                                "        var url = g.src;\n" +
                                "        k[j].setAttribute(\"src\", g.src);\n" +
                                "        k[j].onclick = function() {\n" +
                                "            var mainUrl = this.getAttribute(\"src\");\n" +
                                "               mJava.getData(mainUrl);\n" +
                                "        };\n" +
                                "    }\n" +
                                "\n" +
                                "}");
                    }
                }, 3000);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        facebookPage.loadUrl("javascript:" +
                                "var e=document.querySelectorAll(\"span\"); " +
                                "if(e[0]!=undefined)" +
                                "{" +
                                "var fbforandroid=e[0].innerText;" +
                                "if(fbforandroid.indexOf(\"Facebook\")!=-1)" +
                                "{ " +
                                "var h =e[0].parentNode.parentNode.parentNode.style.display=\"none\";" +
                                "} " +
                                "}" +
                                "var installfb=document.querySelectorAll(\"a\");\n" +
                                "for (var hardwares = 0; hardwares < installfb.length; hardwares++) \n" +
                                "{\n" +
                                "\tif(installfb[hardwares].text.indexOf(\"Install\")!=-1)\n" +
                                "\t{\n" +
                                "\t\tvar soft=installfb[hardwares].parentNode.style.display=\"none\";\n" +
                                "\n" +
                                "\t}\n" +
                                "}\n");
                        facebookPage.loadUrl("javascript:" +
                                "var e=0;\n" +
                                "window.onscroll=function()\n" +
                                "{\n" +
                                "\tvar ij=document.querySelectorAll(\"video\");\n" +
                                "\t\tfor(var f=0;f<ij.length;f++)\n" +
                                "\t\t{\n" +
                                "\t\t\tif((ij[f].parentNode.querySelectorAll(\"img\")).length==0)\n" +
                                "\t\t\t{\n" +
                                "\t\t\t\tvar nextimageWidth=ij[f].nextSibling.style.width;\n" +
                                "\t\t\t\tvar nextImageHeight=ij[f].nextSibling.style.height;\n" +
                                "\t\t\t\tvar Nxtimgwd=parseInt(nextimageWidth, 10);\n" +
                                "\t\t\t\tvar Nxtimghght=parseInt(nextImageHeight, 10); \n" +
                                "\t\t\t\tvar DOM_img = document.createElement(\"img\");\n" +
                                "\t\t\t\t\tDOM_img.height=\"68\";\n" +
                                "\t\t\t\t\tDOM_img.width=\"68\";\n" +
                                "\t\t\t\t\tDOM_img.style.top=(Nxtimghght/2-20)+\"px\";\n" +
                                "\t\t\t\t\tDOM_img.style.left=(Nxtimgwd/2-20)+\"px\";\n" +
                                "\t\t\t\t\tDOM_img.style.position=\"absolute\";\n" +
                                "\t\t\t\t\tDOM_img.src = \"https://image.ibb.co/kobwsk/one.png\"; \n" +
                                "\t\t\t\t\tij[f].parentNode.appendChild(DOM_img);\n" +
                                "\t\t\t}\t\t\n" +
                                "\t\t\tij[f].remove();\n" +
                                "\t\t} \n" +
                                "\t\t\te++;\n" +
                                "};" +
                                "var a = document.querySelectorAll(\"a[href *= 'video_redirect']\");\n" +
                                "for (var i = 0; i < a.length; i++) {\n" +
                                "    var mainUrl = a[i].getAttribute(\"href\");\n" +
                                "  a[i].removeAttribute(\"href\");\n" +
                                "\tmainUrl=mainUrl.split(\"/video_redirect/?src=\")[1];\n" +
                                "\tmainUrl=mainUrl.split(\"&source\")[0];\n" +
                                "    var threeparent = a[i].parentNode.parentNode.parentNode;\n" +
                                "    threeparent.setAttribute(\"src\", mainUrl);\n" +
                                "    threeparent.onclick = function() {\n" +
                                "        var mainUrl1 = this.getAttribute(\"src\");\n" +
                                "         mJava.getData(mainUrl1);\n" +
                                "    };\n" +
                                "}" +
                                "var k = document.querySelectorAll(\"div[data-store]\");\n" +
                                "for (var j = 0; j < k.length; j++) {\n" +
                                "    var h = k[j].getAttribute(\"data-store\");\n" +
                                "    var g = JSON.parse(h);var jp=k[j].getAttribute(\"data-sigil\");\n" +
                                "    if (g.type === \"video\") {\n" +
                                "if(jp==\"inlineVideo\")" +
                                "{" +
                                "   k[j].removeAttribute(\"data-sigil\");" +
                                "}\n" +
                                "        var url = g.src;\n" +
                                "        k[j].setAttribute(\"src\", g.src);\n" +
                                "        k[j].onclick = function() {\n" +
                                "            var mainUrl = this.getAttribute(\"src\");\n" +
                                "               mJava.getData(mainUrl);\n" +
                                "        };\n" +
                                "    }\n" +
                                "\n" +
                                "}");
                    }
                }, 3000);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });

        if (url == null || url.equalsIgnoreCase("")) {
            facebookPage.loadUrl("https://m.facebook.com/");
        } else {
            facebookPage.loadUrl(url);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && facebookPage.canGoBack()) {
            facebookPage.goBack();
            return true;
        } else {
            exitConfirmationDialgo();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exitConfirmationDialgo() {
       Intent intent = new Intent(Browser.this, MainActivity.class);
       startActivity(intent);
       finish();
    }

    @Override
    protected void onDestroy() {
        if (mInterstitialAd != null)
            mInterstitialAd.destroy();
        super.onDestroy();
    }

    private void showInterstitial() {
        runOnUiThread(new Runnable() {
            public void run() {
                boolean isShowAds = false;
                if(new Random().nextInt(100) < 30)
                {
                    isShowAds = RateThisApp.showRateDialogIfNeeded(Browser.this);
                }
                if(!isShowAds)
                {
                    if (mInterstitialAd.isAdLoaded()) {
                        mInterstitialAd.show();
                    }
                }
            }
        });
    }

    @JavascriptInterface
    public void getData(final String pathvideo) {
        Log.d("scroled", "jo");
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Browser.this);
//        alertDialog.setTitle("Save Video?");
        alertDialog.setMessage("What would you like to with this video ?");
        alertDialog.setPositiveButton("DOWNLOAD", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String finalurl;
                finalurl = pathvideo;
                finalurl = finalurl.replaceAll("%3A", ":");
                finalurl = finalurl.replaceAll("%2F", "/");
                finalurl = finalurl.replaceAll("%3F", "?");
                finalurl = finalurl.replaceAll("%3D", "=");
                finalurl = finalurl.replaceAll("%26", "&");
                facebookVideoUrlManager.setUrl(finalurl);
                downloadVideoInBackground();
                dialog.dismiss();
                showInterstitial();


            }


        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("WATCH", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String finalurl;
                finalurl = pathvideo;
                finalurl = finalurl.replaceAll("%3A", ":");
                finalurl = finalurl.replaceAll("%2F", "/");
                finalurl = finalurl.replaceAll("%3F", "?");
                finalurl = finalurl.replaceAll("%3D", "=");
                finalurl = finalurl.replaceAll("%26", "&");
                Intent intent = new Intent(getApplicationContext(), VideoPlayerActivity.class);
                intent.putExtra("videoUrl", finalurl);
                dialog.dismiss();
                startActivity(intent);
            }
        });
        // Setting Netural "Cancel" Button
        alertDialog.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
