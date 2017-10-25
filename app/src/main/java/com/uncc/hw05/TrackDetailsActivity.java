package com.uncc.hw05;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TrackDetailsActivity extends AppCompatActivity {
    ArrayList<Music> similarMusicList;
    ListView similarListView;
    ArrayList<Music> favMusicList;
    SharedPreferences sharedpreferences;
    public static final String MyFAVORITES = "MyFav" ;
    public static final String FAVORITES = "Music_Favorite";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Music music = (Music) getIntent().getExtras().getSerializable("music");

        final ImageView trackImage = (ImageView) findViewById(R.id.trackImage);
        TextView trackName = (TextView) findViewById(R.id.trackName);
        TextView artist = (TextView) findViewById(R.id.artist);
        TextView url = (TextView) findViewById(R.id.url);

        trackName.setText("Name:  " + music.getName());
        trackName.setTextColor(getResources().getColor(R.color.colorBlack));
        artist.setText("Artist:  " + music.getArtist());
        artist.setTextColor(getResources().getColor(R.color.colorBlack));
        url.setText("URL:  " + music.getUrl());
        url.setTextColor(getResources().getColor(R.color.colorBlack));
        final String finalMusicURL = music.getUrl();
        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(finalMusicURL));
                startActivity(intent);
            }
        });
        if (isConnected()) {
            Picasso.with(this).load(music.getSmallImageURL()).into(trackImage);
        } else {
            Toast.makeText(TrackDetailsActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        similarListView = (ListView) findViewById(R.id.similarTrackListView);
        favMusicList = new ArrayList<Music>();
        String similarTrackURL = "http://ws.audioscrobbler.com/2.0/?format=json&method=track.getsimilar&artist=" + music.getArtist() + "&track=" + music.getName() + "&api_key=acd1b709edee231755765d61b8be1245&limit=10";
        if(isConnected()) {
        new GetMusicAsyncTask(new GetMusicAsyncTask.AsyncResponse() {
            @Override
            public void processFinish(ArrayList<Music> musicList) {
                similarMusicList = musicList;

                sharedpreferences = getSharedPreferences(MyFAVORITES, Context.MODE_PRIVATE);
                if (sharedpreferences.contains(FAVORITES)) {
                    String jsonFavorites = sharedpreferences.getString(FAVORITES, null);
                    Gson gson = new Gson();
                    favMusicList = gson.fromJson(jsonFavorites, new TypeToken<List<Music>>() {
                    }.getType());
                }
                if (favMusicList != null && musicList != null) {
                    for (Music music : musicList) {
                        for (Music favMusic : favMusicList) {
                            if (music.getName().equalsIgnoreCase(favMusic.getName())) {
                                if (music.getArtist().equalsIgnoreCase(favMusic.getArtist())) {
                                    if (music.getUrl().equalsIgnoreCase(favMusic.getUrl())) {
                                        music.setFlag(1);
                                        favMusic.setFlag(1);
                                        music.setStarImage(R.drawable.btn_star_big_on + "");
                                        favMusic.setStarImage(R.drawable.btn_star_big_on + "");
                                    }
                                }
                            }
                        }
                    }
                }
                if (musicList != null && musicList.size() > 0) {
                    MusicAdaptor arrayAdapter = new MusicAdaptor(TrackDetailsActivity.this, R.layout.row_music_layout, musicList);
                    similarListView.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(TrackDetailsActivity.this, "No similar Track found", Toast.LENGTH_SHORT).show();
                }


            }
        }).execute(similarTrackURL, "similar");
    }
        similarListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Music music = similarMusicList.get(position);
                Intent i = new Intent(TrackDetailsActivity.this, TrackDetailsActivity.class);
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
            Intent intent = new Intent(TrackDetailsActivity.this, MainActivity.class);
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
        Music music = similarMusicList.get(Integer.parseInt(v.getTag().toString()));
        if(music.getFlag() == 0) {
            imageStar.setImageResource(R.drawable.btn_star_big_on);
            music.setStarImage(R.drawable.btn_star_big_on+"");
            music.setFlag(1);
            favMusicList.add(music);
            gson = new Gson();
            String jsonFavoritesNew = gson.toJson(favMusicList);
            editor.putString(FAVORITES, jsonFavoritesNew);
            editor.commit();
            Toast.makeText(TrackDetailsActivity.this,"Track added to favorites", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(TrackDetailsActivity.this,"Track removed from favorites", Toast.LENGTH_SHORT).show();
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
