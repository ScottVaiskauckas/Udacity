package com.sitstaycreate.android.musicalstructureapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SongDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_details);

        NowPlaying playingSong = new NowPlaying();

        //Set the color of the top menu indicating which activity you are viewing
        int color = getResources().getColor(R.color.colorAccent);
        TextView libraryTextView = (TextView) findViewById(R.id.library_text_view);
        TextView queueTextView = (TextView) findViewById(R.id.queue_text_view);
        TextView songDetailsTextView = (TextView) findViewById(R.id.song_details_text_view);
        songDetailsTextView.setBackgroundColor(color);

        //On click listener for top menu for switching between the Library and Queue
        libraryTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent libraryIntent = new Intent(SongDetailsActivity.this, MainActivity.class);
                startActivity(libraryIntent);
            }
        });

        //On click listener for top menu for switching between the Library and Queue
        queueTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent queueIntent = new Intent(SongDetailsActivity.this, QueueActivity.class);
                startActivity(queueIntent);
            }
        });

        //Get explicit intent with extras for artist name, song name, and album art
        // and set TextViews and ImageView
        TextView songNameTextView = findViewById(R.id.display_song_name);
        TextView artistNameTextView = findViewById(R.id.display_artist_name);
        ImageView albumArtImageView = findViewById(R.id.artwork);
        songNameTextView.setText(playingSong.getNowPlayingSongName());
        artistNameTextView.setText(playingSong.getNowPlayingArtistName());
        albumArtImageView.setImageResource(playingSong.getNowPlayingImageResourceId());
    }
}
