package com.example.avirudhtheraja.spotifymusicdownload;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;

import org.jsoup.nodes.Document;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadSong {

    private static String url = "http://www.youtubeinmp3.com/fetch/?video=https://www.youtube.com/watch?v=";

    public static void downloadSong(String youtubeId, DownloadManager manager){
        String passInUrl = url+youtubeId+"&autostart=1";
        MainActivity.startActivity(passInUrl);
    }

}
