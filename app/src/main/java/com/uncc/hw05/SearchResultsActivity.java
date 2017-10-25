package com.uncc.hw05;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    ArrayList<Music> musicList;
    ArrayList<Music> favMusicList;
    SharedPreferences sharedpreferences;
    public static final String MyFAVORITES = "MyFav" ;
    public static final String FAVORITES = "Music_Favorite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        musicList = (ArrayList<Music>) getIntent().getExtras().getSerializable("musicList");
        ListView listView = (ListView)findViewById(R.id.searchList);
        favMusicList = new ArrayList<Music>();
        sharedpreferences = getSharedPreferences(MyFAVORITES, Context.MODE_PRIVATE);
        String jsonFavorites = sharedpreferences.getString(FAVORITES, null);
        Gson gson = new Gson();
        favMusicList = gson.fromJson(jsonFavorites,new TypeToken<List<Music>>(){}.getType());

        if(favMusicList!=null && musicList!=null) {
            for (Music music : musicList) {
                for (Music favMusic : favMusicList) {
                    if (music.getName().equalsIgnoreCase(favMusic.getName())) {
                        if (music.getArtist().equalsIgnoreCase(favMusic.getArtist())) {
                            if (music.getUrl().equalsIgnoreCase(favMusic.getUrl())) {
                                music.setFlag(1);
                                favMusic.setFlag(1);
                                music.setStarImage(R.drawable.btn_star_big_on+"");
                                favMusic.setStarImage(R.drawable.btn_star_big_on+"");
                            }
                        }
                    }
                }
            }
        }
        MusicAdaptor arrayAdapter = new MusicAdaptor(this,R.layout.row_music_layout,musicList);
        arrayAdapter.setNotifyOnChange(true);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Music music = musicList.get(position);
                Intent i = new Intent(SearchResultsActivity.this, TrackDetailsActivity.class);
                i.putExtra("music", music);
                startActivity(i);
            }
        });

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
            Intent intent = new Intent(SearchResultsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_quit){
         finishAffinity();
        }

        return super.onOptionsItemSelected(item);
    }
    public void favoriteHandler(View v){
        LinearLayout vwParentRow = (LinearLayout)v.getParent();
        ImageView imageStar = (ImageView)vwParentRow.getChildAt(1);
        //fetch already set sharedpreferences
        favMusicList = new ArrayList<Music>();
        sharedpreferences = getSharedPreferences(MyFAVORITES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        if (sharedpreferences.contains(FAVORITES)) {
            String jsonFavorites = sharedpreferences.getString(FAVORITES, null);
            if (jsonFavorites != null && !"".equalsIgnoreCase(jsonFavorites)) {
                favMusicList = gson.fromJson(jsonFavorites, new TypeToken<List<Music>>() {
                }.getType());
            }
        }
        //add new sharedpreferences
        SharedPreferences.Editor editor = sharedpreferences.edit();
        Music music = musicList.get(Integer.parseInt(v.getTag().toString()));
        if(music.getFlag() == 0) {
            imageStar.setImageResource(R.drawable.btn_star_big_on);
            music.setStarImage(R.drawable.btn_star_big_on+"");
            music.setFlag(1);
            favMusicList.add(music);
            gson = new Gson();
            String jsonFavoritesNew = gson.toJson(favMusicList);
            editor.putString(FAVORITES, jsonFavoritesNew);
            editor.commit();
            Toast.makeText(SearchResultsActivity.this,"Track added to favorites", Toast.LENGTH_SHORT).show();
        }else if(music.getFlag() == 1){
            imageStar.setImageResource(R.drawable.btn_star_big_off);
            music.setStarImage(R.drawable.btn_star_big_off+"");
            for(int index=0;index<favMusicList.size();index++){
                Music favMusic = favMusicList.get(index);
                if(music.getArtist().equalsIgnoreCase(favMusic.getArtist())){
                    if(music.getUrl().equalsIgnoreCase(favMusic.getUrl())){
                        favMusicList.remove(index);
                    }
                }
            }
            gson = new Gson();
            String jsonFavoritesNew = gson.toJson(favMusicList);
            editor.putString(FAVORITES, jsonFavoritesNew);
            editor.commit();
            Toast.makeText(SearchResultsActivity.this,"Track removed from favorites", Toast.LENGTH_SHORT).show();
        }
    }
}
