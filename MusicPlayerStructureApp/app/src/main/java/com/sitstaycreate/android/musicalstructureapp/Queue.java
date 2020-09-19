package com.sitstaycreate.android.musicalstructureapp;

import java.util.ArrayList;
/**
 * {@link Queue} represents an ArrayList of songs that has been added from the library activity.
 * It contains the name of the song title and the name of the artist who created the song, as well
 * as the album artwork.
 */
public class Queue {

    /**
     * Static ArrayList stores songs added to Queue
     */
    private static ArrayList<Song> songQueue = new ArrayList<Song>();

    /**
     * Get the Queue object.
     */
    public ArrayList<Song> getSongs() {

        return songQueue;

    }

    /**
     * Add a song to the Queue
     *
     *  @param songName is the name of a song.
     *
     * @param artistName is the name of the artist.
     *
     * @param albumArt is the id of the image resource.
     */

    public void setSongs(String songName, String artistName, int albumArt) {
        songQueue.add(new Song(songName, artistName, albumArt));
    }

    /**
     * Remove a song from the Queue
     */

    public void removeSong(int position) {
        songQueue.remove(position);
    }

}
