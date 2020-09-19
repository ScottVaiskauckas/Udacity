package com.sitstaycreate.android.musicalstructureapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    int playingState = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);


        final NowPlaying playingSong = new NowPlaying();

        final TextView nowPlayingSong = (TextView) findViewById(R.id.now_playing_song_name_text_view);
        final TextView nowPlayingArtist = (TextView) findViewById(R.id.now_playing_artist_name_text_view);
        final ImageView nowPlayingAlbumArt = (ImageView) findViewById(R.id.now_playing_album_art);
        final ImageButton transportControl = (ImageButton) findViewById(R.id.transport_control);

        nowPlayingSong.setText(playingSong.getNowPlayingSongName());
        nowPlayingArtist.setText(playingSong.getNowPlayingArtistName());
        nowPlayingAlbumArt.setImageResource(playingSong.getNowPlayingImageResourceId());

        //Set the color of the top menu indicating which activity you are viewing
        int color = getResources().getColor(R.color.colorAccent);
        TextView libraryTextView = (TextView) findViewById(R.id.library_text_view);
        TextView queueTextView = (TextView) findViewById(R.id.queue_text_view);
        TextView songDetailsTextView = (TextView) findViewById(R.id.song_details_text_view);
        libraryTextView.setBackgroundColor(color);

        //On click listener for top menu for switching between the Library and Queue
        queueTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent queueIntent = new Intent(MainActivity.this, QueueActivity.class);
                startActivity(queueIntent);
            }
        });

        //On click listener for top menu for switching between the Library and Queue
        songDetailsTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent songDetailsIntent = new Intent(MainActivity.this, SongDetailsActivity.class);
                startActivity(songDetailsIntent);
            }
        });

        transportControl.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(playingState == 0){
                    transportControl.setImageResource(R.drawable.baseline_pause_circle_filled_black_24);
                    playingState = 1;
                }
                else if(playingState == 1){
                    transportControl.setImageResource(R.drawable.baseline_play_arrow_black_24);
                    playingState = 0;
                }

            }
        });

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // song_list.xml layout file.
        final ListView listView = (ListView) findViewById(R.id.list);

        // Create a list of songs
        ArrayList<Song> songs = new ArrayList<Song>();
        songs.add(new Song("Heatwave","Sbtrkt", R.drawable.sbtrkt));
        songs.add(new Song("Hold On","Sbtrkt", R.drawable.sbtrkt));
        songs.add(new Song("Wildfire","Sbtrkt", R.drawable.sbtrkt));
        songs.add(new Song("Sanctuary","Sbtrkt", R.drawable.sbtrkt));
        songs.add(new Song("Trials of the Past","Sbtrkt", R.drawable.sbtrkt));
        songs.add(new Song("Right Thing To Do","Sbtrkt", R.drawable.sbtrkt));
        songs.add(new Song("Something Goes Right","Sbtrkt", R.drawable.sbtrkt));
        songs.add(new Song("Pharaohs","Sbtrkt", R.drawable.sbtrkt));
        songs.add(new Song("Ready Set Loop","Sbtrkt", R.drawable.sbtrkt));
        songs.add(new Song("Never Never","Sbtrkt", R.drawable.sbtrkt));
        songs.add(new Song("Go Bang","Sbtrkt", R.drawable.sbtrkt));
        songs.add(new Song("Feel the Love","Kids See Ghosts", R.drawable.kids_see_ghosts));
        songs.add(new Song("Fire","Kids See Ghosts", R.drawable.kids_see_ghosts));
        songs.add(new Song("4th Dimension","Kids See Ghosts", R.drawable.kids_see_ghosts));
        songs.add(new Song("Freeee","Kids See Ghosts", R.drawable.kids_see_ghosts));
        songs.add(new Song("Reborn","Kids See Ghosts", R.drawable.kids_see_ghosts));
        songs.add(new Song("Kids See Ghosts","Kids See Ghosts", R.drawable.kids_see_ghosts));
        songs.add(new Song("Cudi Montage","Kids See Ghosts", R.drawable.kids_see_ghosts));

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
            public void onItemClick(final AdapterView<?> adapterView, View view, int position, long id){
                final String artistName = finalSongs.get(position).getArtistName();
                final String songName = finalSongs.get(position).getSongName();
                final int albumArt = finalSongs.get(position).getImageResourceId();
                final Intent songDetailsIntent = new Intent(view.getContext(), SongDetailsActivity.class);


                //Create a PopupMenu that will be displayed when a ListView item is clicked
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, listView);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                //Create OnMenuItemClickListener for the PopupMenu
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String selection = item.getTitle().toString();
                        switch (selection){
                            case "Play":
                                playingSong.setNowPlaying(artistName, songName, albumArt);
                                nowPlayingSong.setText(playingSong.getNowPlayingSongName());
                                nowPlayingArtist.setText(playingSong.getNowPlayingArtistName());
                                nowPlayingAlbumArt.setImageResource(playingSong.getNowPlayingImageResourceId());
                                transportControl.setImageResource(R.drawable.baseline_pause_circle_filled_black_24);
                                playingState = 1;
                                break;
                            case "Add to queue":
                                Queue queue = new Queue();
                                queue.setSongs(songName, artistName, albumArt);
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