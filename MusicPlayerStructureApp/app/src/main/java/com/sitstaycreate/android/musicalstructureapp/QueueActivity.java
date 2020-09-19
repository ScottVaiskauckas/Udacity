package com.sitstaycreate.android.musicalstructureapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

public class QueueActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);

        final NowPlaying playingSong = new NowPlaying();

        final TextView nowPlayingSong = (TextView) findViewById(R.id.now_playing_song_name_text_view);
        final TextView nowPlayingArtist = (TextView) findViewById(R.id.now_playing_artist_name_text_view);
        final ImageView nowPlayingAlbumArt = (ImageView) findViewById(R.id.now_playing_album_art);

        nowPlayingSong.setText(playingSong.getNowPlayingSongName());
        nowPlayingArtist.setText(playingSong.getNowPlayingArtistName());
        nowPlayingAlbumArt.setImageResource(playingSong.getNowPlayingImageResourceId());

        //Set the color of the top menu indicating which activity you are viewing
        int color = getResources().getColor(R.color.colorAccent);
        TextView libraryTextView = (TextView) findViewById(R.id.library_text_view);
        TextView queueTextView = (TextView) findViewById(R.id.queue_text_view);
        TextView songDetailsTextView = (TextView) findViewById(R.id.song_details_text_view);
        queueTextView.setBackgroundColor(color);

        //On click listener for top menu for switching between the Library and Queue
        libraryTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent libraryIntent = new Intent(QueueActivity.this, MainActivity.class);
                startActivity(libraryIntent);
            }
        });

        //On click listener for top menu for switching between the Library and Queue
        songDetailsTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent songDetailsIntent = new Intent(QueueActivity.this, SongDetailsActivity.class);
                startActivity(songDetailsIntent);
            }
        });

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // song_list.xml layout file.
        final ListView listView = (ListView) findViewById(R.id.list);

        // Create an instance of the Queue class
        Queue queue = new Queue();

        // Create a list of songs stored in the Queue class
        ArrayList<Song> songs = queue.getSongs();

        // Create an {@link SongAdapter}, whose data source is a list of {@link Song}s. The
        // adapter knows how to create list items for each item in the list.
        final SongAdapter adapter = new SongAdapter(this, songs);

        final ArrayList<Song> finalSongs = songs;

        // Make the {@link ListView} use the {@link SongAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Song} in the list.
        listView.setAdapter(adapter);

        //Create OnItemClickListener for the items in the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> nowPlaying, View view, final int position, long id){
                final String artistName = finalSongs.get(position).getArtistName();
                final String songName = finalSongs.get(position).getSongName();
                final int albumArt = finalSongs.get(position).getImageResourceId();
                //Create a PopupMenu that will be displayed when a ListView item is clicked
                PopupMenu popupMenu = new PopupMenu(QueueActivity.this, listView);
                popupMenu.getMenuInflater().inflate(R.menu.queue_popup_menu, popupMenu.getMenu());
                //Create OnMenuItemClickListener for the PopupMenu
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String selection = item.getTitle().toString();
                        Queue queue = new Queue();
                        switch (selection){
                            case "Play":
                                playingSong.setNowPlaying(artistName, songName, albumArt);
                                nowPlayingSong.setText(playingSong.getNowPlayingSongName());
                                nowPlayingArtist.setText(playingSong.getNowPlayingArtistName());
                                nowPlayingAlbumArt.setImageResource(playingSong.getNowPlayingImageResourceId());
                                break;

                            case "Remove from queue":
                                queue.removeSong(position);
                                adapter.notifyDataSetChanged();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }
}