package com.mngh.tuanvn.facebookvideodownloader;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mngh.tuanvn.facebookvideodownloader.Controllers.VideoFilesAdapters;
import com.mngh.tuanvn.facebookvideodownloader.Model.Get;
import com.mngh.tuanvn.facebookvideodownloader.Model.VideoModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private GetDataForAdapter dataForAdapter;
    private VideoFilesAdapters adapters;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Intent myIntent;
    private RequestQueue requestQueue;
    private Gson gson;
    private String delayAds;
    private String percentAds;
    private static final String ENDPOINT = "https://config-app-game-4.firebaseio.com/com_mngh_tuanvn_facebookvideodownloader.json";
    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Get get = null;
            try {
                get = gson.fromJson(response, Get.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (get != null) {
                delayAds = get.getDELAY();
                percentAds = get.getPercentAds();

                int int_delayAds = Integer.parseInt(delayAds);
                int int_percentAds = Integer.parseInt(percentAds);

                editor.putInt("delayAds", int_delayAds);
                editor.putInt("percentAds", int_percentAds);
                editor.commit();

                boolean check = pref.getBoolean("startedService", false);
                if (myIntent == null && !check) {
                    myIntent = new Intent(MainActivity.this, runningService.class);
                    startService(myIntent);
                    editor.putBoolean("startedService", true);
                }
            }
            Log.i("tuancon91", delayAds + ":" + percentAds);
        }
    };

    private void fetchGet() {
        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT, onPostsLoaded, onPostsError);
        requestQueue.add(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_display);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataForAdapter = new GetDataForAdapter(MainActivity.this);
        recyclerView = findViewById(R.id.recycler_view_for_video);

        Button browseFacebook = findViewById(R.id.login_fb);
        Button url_link = findViewById(R.id.url_video);
        browseFacebook.setOnClickListener(this);
        url_link.setOnClickListener(this);

        setRecyclerView();

        pref = getApplicationContext().getSharedPreferences("DataCountService", MODE_PRIVATE);
        editor = pref.edit();
        editor.putLong("timeInstall", System.currentTimeMillis());
        editor.apply();

        requestQueue = Volley.newRequestQueue(this);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();
        fetchGet();

        AdView adView;
        adView = new AdView(this, "1920315171602379_1920315438269019", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        RelativeLayout adContainer = findViewById(R.id.banner1);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();
    }

//    private ArrayList<VideoModel> getDataList() {
//        for (int i = 0; i < 20; i++) {
//            VideoModel model = new VideoModel();
//            model.setName("Video Downloaded From Facebook");
//            dataList.add(model);
//        }
//        return dataList;
//    }

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
//                   AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
//                   builder.setTitle("Permission Denied");
//                   builder.setMessage("Storage Permission is denied. Please exit and reopen the application if you want to
                    Log.e("Permission Denied", "True");
                }
                break;
        }
    }

    @Override
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.more_apps:
                Intent a = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=DINH+VIET+HUNG"));
                startActivity(a);
                break;
            case R.id.reload:
                recreate();
                break;

            case R.id.help2:
                startActivity(new Intent(MainActivity.this, PresentationActivity.class));
                break;
            case R.id.share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Try this one, it's really good app!";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share App");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share                 startActivity(Intent.createChooser(sharingIntent, \"Share via\"));\nvia"));
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
