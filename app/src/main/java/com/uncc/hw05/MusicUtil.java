package com.uncc.hw05;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class MusicUtil {
    static public class MusicJSONParser {
        static ArrayList<Music> parseTracks(String in) throws JSONException {
            ArrayList<Music> musicList = new ArrayList<Music>();
            JSONObject root = new JSONObject(in);
            JSONObject resultsJSONObject = root.getJSONObject("results");
            JSONObject trackJSONObject = resultsJSONObject.getJSONObject("trackmatches");
            JSONArray trackJSONOArray = trackJSONObject.getJSONArray("track");


            for(int i=0; i<trackJSONOArray.length(); i++) {
                JSONObject musicJSONObject = trackJSONOArray.getJSONObject(i);
                Music music = new Music();
                music.setStarImage(R.drawable.btn_star_big_off+"");
                if(musicJSONObject.has("name"))
                music.setName(musicJSONObject.getString("name"));
                if(musicJSONObject.has("artist"))
                    music.setArtist(musicJSONObject.getString("artist"));
                if(musicJSONObject.has("url"))
                    music.setUrl(musicJSONObject.getString("url"));

                if(musicJSONObject.has("image")) {
                    JSONArray imageArray = musicJSONObject.getJSONArray("image");
                    JSONObject smallImageJSONObject = imageArray.getJSONObject(0);
                    if(smallImageJSONObject.has("#text")) {
                     music.setSmallImageURL(smallImageJSONObject.getString("#text"));
                    }
                    JSONObject largeImageJSONObject = imageArray.getJSONObject(2);
                    if(largeImageJSONObject.has("#text")) {
                        music.setLargeImageURL(largeImageJSONObject.getString("#text"));
                    }
                }

                musicList.add(music);
            }
            return musicList;
        }

        static ArrayList<Music> parseSimilarTracks(String in) throws JSONException {
            ArrayList<Music> musicList = new ArrayList<Music>();
            JSONObject root = new JSONObject(in);
            JSONObject resultsJSONObject = root.getJSONObject("similartracks");
            JSONArray trackJSONOArray = resultsJSONObject.getJSONArray("track");


            for(int i=0; i<trackJSONOArray.length(); i++) {
                JSONObject musicJSONObject = trackJSONOArray.getJSONObject(i);
                Music music = new Music();
                music.setStarImage(R.drawable.btn_star_big_off+"");
                if(musicJSONObject.has("name"))
                    music.setName(musicJSONObject.getString("name"));
                if(musicJSONObject.has("artist")) {
                    JSONObject artistObject = musicJSONObject.getJSONObject("artist");
                    if(artistObject.has("name"))
                    music.setArtist(artistObject.getString("name"));
                }
                if(musicJSONObject.has("url"))
                    music.setUrl(musicJSONObject.getString("url"));

                if(musicJSONObject.has("image")) {
                    JSONArray imageArray = musicJSONObject.getJSONArray("image");
                    JSONObject smallImageJSONObject = imageArray.getJSONObject(0);
                    if(smallImageJSONObject.has("#text")) {
                        music.setSmallImageURL(smallImageJSONObject.getString("#text"));
                    }
                    JSONObject largeImageJSONObject = imageArray.getJSONObject(2);
                    if(largeImageJSONObject.has("#text")) {
                        music.setLargeImageURL(largeImageJSONObject.getString("#text"));
                    }
                }
                musicList.add(music);
            }
            return musicList;
        }
    }
}
