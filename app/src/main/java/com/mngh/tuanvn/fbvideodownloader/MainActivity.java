package com.mngh.tuanvn.fbvideodownloader;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mngh.tuanvn.fbvideodownloader.Controllers.VideoFilesAdapters;
import com.mngh.tuanvn.fbvideodownloader.Model.AdsConfig;
import com.mngh.tuanvn.fbvideodownloader.Model.Get;
import com.mngh.tuanvn.fbvideodownloader.service.MyService;
import com.mngh.tuanvn.fbvideodownloader.utils.AppConstants;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private GetDataForAdapter dataForAdapter;
    private VideoFilesAdapters adapters;
    Button loginOrCheck;
    EditText editText;
    DrawerLayout androidDrawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_display);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        androidDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_design_support_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                androidDrawerLayout, R.string.app_name, R.string.app_name);
        androidDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        dataForAdapter = new GetDataForAdapter(MainActivity.this);
        recyclerView = findViewById(R.id.recycler_view_for_video);
        editText = (EditText) findViewById(R.id.edittext);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                String text = editText.getText().toString();
//                if (text.equalsIgnoreCase("")) {
//                    loginOrCheck.setText(R.string.login_fb);
//                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = editText.getText().toString();
                if (text.equalsIgnoreCase("")) {
                    loginOrCheck.setText(R.string.login_fb);
                } else
                    loginOrCheck.setText(R.string.check_link);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editText.getText().toString();
                if (text.equalsIgnoreCase("")) {
                    loginOrCheck.setText(R.string.login_fb);
                } else
                    loginOrCheck.setText(R.string.check_link);
            }
        });
        loginOrCheck = findViewById(R.id.login_fb);
        loginOrCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = loginOrCheck.getText().toString();
                if (text.equalsIgnoreCase("Login Facebook")) {
                    Intent intent = new Intent(MainActivity.this, Browser.class);
                    startActivity(intent);
                } else if (text.equalsIgnoreCase("Check Link")) {
                    String url = editText.getText().toString();

                    if (!url.equalsIgnoreCase("") && Patterns.WEB_URL.matcher(url).matches()) {
                        Intent intent = new Intent(MainActivity.this, Browser.class);
                        intent.putExtra("link", url);
                        startActivity(intent);
                    } else
                        Toast.makeText(MainActivity.this, "Your link is invalid!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setRecyclerView();


        AdView adView;
        adView = new AdView(this, "2061820020517519_2061821763850678", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        RelativeLayout adContainer = findViewById(R.id.banner1);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();
//        getAppConfig();
    }

    private void callFacebook() {
        String apppackage = "com.facebook.katana";
        try {
            Intent i = getPackageManager().getLaunchIntentForPackage(apppackage);
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "You have not installed Instagram", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.moreApp:
                Intent a = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=DINH+VIET+HUNG"));
                startActivity(a);
                androidDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.shareApp:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "FB Downloader");
                    String sAux = "\nLet me recommend you this application\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.mngh.tuanvn.fbvideodownloader \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "Share via"));
                } catch (Exception e) {
                    //e.toString();
                }
                androidDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.howTo:
                startActivity(new Intent(MainActivity.this, HowToUseActivity.class));
                androidDrawerLayout.closeDrawer(GravityCompat.START);
                return true;

        }
        androidDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setRecyclerView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                recyclerViewPart();
            }
        } else {
            recyclerViewPart();
        }

    }

    public void recyclerViewPart() {
        adapters = new VideoFilesAdapters(MainActivity.this, dataForAdapter.getVideoData());
        recyclerView.setAdapter(adapters);

        GridLayoutManager manager = new GridLayoutManager(MainActivity.this, 2);
        recyclerView.setLayoutManager(manager);
        registerForContextMenu(recyclerView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setRecyclerView();
                } else {
                    Log.e("Permission Denied", "True");
                }
                break;
        }
    }

  /*  @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_fb:
                Intent intent = new Intent(MainActivity.this, Browser.class);
                startActivity(intent);
                break;
            case R.id.url_video:
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final EditText edittext = new EditText(MainActivity.this);
                builder.setMessage("Paste your public facebook video's link here !");
                builder.setView(edittext);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String url = edittext.getText().toString();

                        if (!url.equalsIgnoreCase("") && Patterns.WEB_URL.matcher(url).matches()) {
                            Intent intent = new Intent(MainActivity.this, Browser.class);
                            intent.putExtra("link", url);
                            startActivity(intent);
                            dialogInterface.dismiss();
                        } else
                            Toast.makeText(MainActivity.this, "Your link is invalid!", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });
                builder.setCancelable(false);
                builder.create();
                builder.show();
                break;
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                androidDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.removeAds:

                return true;
            case R.id.goToFB:
                callFacebook();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAppConfig() {
        mPrefs = getSharedPreferences("adsserver", 0);
        String uuid;
        if (mPrefs.contains("uuid")) {
            uuid = mPrefs.getString("uuid", UUID.randomUUID().toString());
        } else {
            uuid = UUID.randomUUID().toString();
            mPrefs.edit().putString("uuid", "fbnew" + uuid).apply();
        }

        OkHttpClient client = new OkHttpClient();
        Request okRequest = new Request.Builder()
                .url(AppConstants.URL_CLIENT_CONFIG + "?id_game=" + getPackageName())
                .build();

        client.newCall(okRequest).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                Gson gson = new GsonBuilder().create();//"{\"delayAds\":24,\"delayService\":24,\"idFullService\":\"/21617015150/734252/21734366950\",\"intervalService\":10,\"percentAds\":50}";//
                AdsConfig adsConfig = gson.fromJson(response.body().string(), AdsConfig.class);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putInt("intervalService", adsConfig.intervalService);
                editor.putString("idFullService", adsConfig.idFullService);
                editor.putInt("delayService", adsConfig.delayService);
                editor.putInt("delay_report", adsConfig.delay_report);
                editor.putString("idFullFbService", adsConfig.idFullFbService);

                if (!mPrefs.contains("delay_retention")) {
                    if (new Random().nextInt(100) < adsConfig.retention) {
                        editor.putInt("delay_retention", adsConfig.delay_retention).apply();
                    } else {
                        editor.putInt("delay_retention", -1);
                    }
                }

                editor.commit();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent myIntent = new Intent(MainActivity.this, MyService.class);
                        startService(myIntent);
                    }
                });
            }
        });
    }
}
