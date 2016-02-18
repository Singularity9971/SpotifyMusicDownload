package com.example.avirudhtheraja.spotifymusicdownload;

import java.util.ArrayList;

public class Playlist implements Comparable {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Playlist(String playlistId, String name) {
        this.playlistId = playlistId;
        songs = new ArrayList<>();
        this.name = name;

    }

    private ArrayList<Track> songs;
    private String playlistId;
    private String name;

    public ArrayList<Track> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Track> songs) {
        this.songs = songs;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    @Override
    public int compareTo(Object another) {
        Playlist other = (Playlist)another;
        String otherId = other.playlistId;
        if(otherId.equals(playlistId))
            return 0;
        int minLength = Math.min(playlistId.length(),otherId.length());
        int i = 0;
        while(i < minLength){
            int first = (int)playlistId.charAt(i);
            int second = (int)otherId.charAt(i);
            if(first < second)
                return -1;
            if(first > second)
                return 1;
            i++;
        }
        if(playlistId.length() < otherId.length())
            return -1;
        return 1;
    }
}
