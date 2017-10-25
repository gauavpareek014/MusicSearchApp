package com.uncc.hw05;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by gaurav on 10/8/2017.
 */

public class MusicAdaptor extends ArrayAdapter<Music> {
    List<Music> mData;
    Context mContext;
    int mResource;
    public MusicAdaptor(@NonNull Context context, @LayoutRes int resource, @NonNull List<Music> objects) {
        super(context, resource, objects);
        this.mData = objects;
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource,parent,false);
        }
        Music music = mData.get(position);
        TextView trackName = (TextView)convertView.findViewById(R.id.trackName);
        TextView artist = (TextView)convertView.findViewById(R.id.artist);
        ImageView artistImage = (ImageView)convertView.findViewById(R.id.artistImage);
        trackName.setText(music.getName());
        artist.setText(music.getArtist());
        ImageView starImage = (ImageView)convertView.findViewById(R.id.starImage);
        starImage.setTag(position);
        starImage.setImageResource(Integer.parseInt(music.getStarImage()));
        Picasso.with(mContext).load(music.getSmallImageURL()).into(artistImage);

        return convertView;
    }
}
