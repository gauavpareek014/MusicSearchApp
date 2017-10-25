package com.uncc.hw05;
/*
* Homework 05
* MainActivity.java
* Gaurav Pareek
* Darshak Mehta
* */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String MyFAVORITES = "MyFav" ;
    public static final String FAVORITES = "Music_Favorite";
    SharedPreferences sharedpreferences;
    List<Music> musicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final EditText searchText = (EditText)findViewById(R.id.serachMusicValue);
        Button searchButton = (Button)findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!"".equalsIgnoreCase(searchText.getText().toString()) && !"#".equalsIgnoreCase(searchText.getText().toString())) {
                    String url = "http://ws.audioscrobbler.com/2.0/?format=json&method=track.search&track=" + searchText.getText().toString().trim() + "&api_key=acd1b709edee231755765d61b8be1245&limit=20";
                    if(isConnected()) {
                        new GetMusicAsyncTask(new GetMusicAsyncTask.AsyncResponse() {
                            @Override
                            public void processFinish(ArrayList<Music> musicList) {
                                if (musicList != null && musicList.size() > 0) {
                                    Intent i = new Intent(MainActivity.this, SearchResultsActivity.class);
                                    i.putExtra("musicList", musicList);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(MainActivity.this, "No music found with searched track name", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).execute(url, "search");
                    }else{
                        Toast.makeText(MainActivity.this,"No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if("#".equalsIgnoreCase(searchText.getText().toString())){
                        Toast.makeText(MainActivity.this, "Track Name is not valid", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this, "Track Name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        sharedpreferences = getSharedPreferences(MyFAVORITES, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(FAVORITES)) {
            String jsonFavorites = sharedpreferences.getString(FAVORITES, null);
            Gson gson = new Gson();
            musicList = gson.fromJson(jsonFavorites,new TypeToken<List<Music>>(){}.getType());

            if(musicList.size()>20)
            musicList = musicList.subList(0,20);

            ListView listView = (ListView)findViewById(R.id.favListView);
            MusicAdaptor arrayAdapter = new MusicAdaptor(this,R.layout.row_music_layout,musicList);
            listView.setAdapter(arrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Music music = musicList.get(position);
                    Intent i = new Intent(MainActivity.this, TrackDetailsActivity.class);
                    i.putExtra("music", music);
                    startActivity(i);
                }
            });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            final Intent intent = this.getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            this.finish();
            this.overridePendingTransition(0, 0);
            this.startActivity(intent);
            this.overridePendingTransition(0, 0);
        }else if(id == R.id.action_quit){
            finishAffinity();
        }

        return super.onOptionsItemSelected(item);
    }

    public void favoriteHandler(View v){
        LinearLayout vwParentRow = (LinearLayout)v.getParent();
        ImageView imageStar = (ImageView)vwParentRow.getChildAt(1);
        Log.d("tag",v.getTag().toString());

        //fetch already set sharedpreferences
        sharedpreferences = getSharedPreferences(MyFAVORITES, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(FAVORITES)) {
            String jsonFavorites = sharedpreferences.getString(FAVORITES, null);
            Gson gson = new Gson();
            musicList = gson.fromJson(jsonFavorites, new TypeToken<List<Music>>() {
            }.getType());

            //add new sharedpreferences
            SharedPreferences.Editor editor = sharedpreferences.edit();
            musicList.remove(Integer.parseInt(v.getTag().toString()));
            gson = new Gson();
            String jsonFavoritesNew = gson.toJson(musicList);
            editor.putString(FAVORITES, jsonFavoritesNew);
            editor.commit();
            ListView listView = (ListView) findViewById(R.id.favListView);
            if (musicList.size() > 20)
                musicList = musicList.subList(0, 20);
            MusicAdaptor arrayAdapter = new MusicAdaptor(this, R.layout.row_music_layout, musicList);
            listView.setAdapter(arrayAdapter);
            Toast.makeText(MainActivity.this, "Track removed from favorites", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
