package com.sitstaycreate.android.musicalstructureapp;

/**
 * {@link NowPlaying} represents an ArrayList of songs that has been added from the library activity.
 * It contains the name of the song title and the name of the artist who created the song, as well
 * as the album artwork.
 */
public class NowPlaying {

    /** The name of the song */
    private static String mSongName;

    /** The name of the artist */
    private static String mArtistName;

    /** The Id of the image resource */
    private static int mImageResourceId;

    /**
     * Create a new Song object.
     *  @param songName is the name of a song.
     *
     * @param artistName is the name of the artist.
     *@param imageResourceId is the id of the image resource.
     */

    public void setNowPlaying(String songName, String artistName, int imageResourceId) {
        mSongName = songName;
        mArtistName = artistName;
        mImageResourceId = imageResourceId;
    }

    /**
     * Get the song's name.
     */
    public String getNowPlayingSongName(){
        return mSongName;
    }

    /**
     * Get the artist's name.
     */
    public String getNowPlayingArtistName(){
        return mArtistName;
    }

    /**
     * Get the album art.
     */
    public int getNowPlayingImageResourceId(){
        return mImageResourceId;
    }

}