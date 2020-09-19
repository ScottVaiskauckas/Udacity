package com.sitstaycreate.android.musicalstructureapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * {@link SongAdapter} is an {@link ArrayAdapter} that can provide the layout for each list item
 * based on a data source, which is a list of {@link Song} objects.
 */
public class SongAdapter extends ArrayAdapter<Song> {

    /**
     * Create a new {@link SongAdapter} object.
     *
     * @param context is the current context (i.e. Activity) that the adapter is being created in.
     * @param songs is the list of {@link Song}s to be displayed.
     */
    public SongAdapter(Activity context, ArrayList<Song> songs){
        super(context,0, songs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item,parent,false);
        }
        // Get the {@link Song} object located at this position in the list
        Song currentSong = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID song_name_text_view.
        TextView songNameTextView = (TextView) listItemView.findViewById(R.id.song_name_text_view);
        // Get the song's name from the currentSong object and set this text on
        // the default TextView.
        songNameTextView.setText(currentSong.getSongName());

        // Find the TextView in the list_item.xml layout with the ID artist_name_text_view.
        TextView artistNameTextView = (TextView) listItemView.findViewById(R.id.artist_name_text_view);
        // Get the artist's name from the currentSong object and set this text on
        // the default TextView.
        artistNameTextView.setText(currentSong.getArtistName());

        // Find the ImageView in the list_item.xml layout with the ID album_art.
        ImageView albumArtImageView = (ImageView) listItemView.findViewById(R.id.album_art);
        // Get the album art from the currentSong object and set this image on
        // the default ImageView.
        albumArtImageView.setImageResource(currentSong.getImageResourceId());

        // Return the whole list item layout (containing 2 TextViews and an ImageView) so that it can be shown in
        // the ListView.
        return listItemView;
    }
}
