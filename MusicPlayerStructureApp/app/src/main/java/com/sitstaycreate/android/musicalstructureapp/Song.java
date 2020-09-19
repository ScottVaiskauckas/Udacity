package com.sitstaycreate.android.musicalstructureapp;

import android.content.Context;
/**
 * {@link Song} represents a song that is available in the library activity.
 * It contains the name of the song title and the name of the artist who created the song, as well
 * as the album artwork.
 */
public class Song{

    /** The name of the song */
    private String mSongName;

    /** The name of the artist */
    private String mArtistName;

    /** The Id of the image resource */
    private int mImageResourceId;

    /**
     * Create a new Song object.
     *
     * @param songName is the name of a song.
     *
     * @param artistName is the name of the artist.
     *
     * @param imageResourceId is the id of the image resource.
     */

    public Song(String songName, String artistName, int imageResourceId){
        mSongName = songName;
        mArtistName = artistName;
        mImageResourceId = imageResourceId;
    }

    /**
     * Get the song's name.
     */
    public String getSongName(){
        return mSongName;
    }

    /**
     * Get the artist's name.
     */
    public String getArtistName(){
        return mArtistName;
    }

    /**
     * Get the album art.
     */
    public int getImageResourceId(){
        return mImageResourceId;
    }

}
