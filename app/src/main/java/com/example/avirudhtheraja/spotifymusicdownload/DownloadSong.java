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

    private static String url = "http://www.youtubeinmp3.com/download/?video=https://www.youtube.com/watch?v=";

    public static void downloadSong(String youtubeId, DownloadManager manager){
        String passInUrl = url+youtubeId+"&autostart=1";
        MainActivity.startActivity(passInUrl);

    }

    /*private static class AsyncTask extends android.os.AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {
            String s = params[0];
            Log.d("Avi","Url passed in is "+s);
            try{
                /*
                Uri uri = Uri.parse(s);
                DownloadManager.Request req = new DownloadManager.Request(uri);
                req.setMimeType(".mp3");
                dm.enqueue(req);*/
                /*
                StringBuilder sb = new StringBuilder();
                URL url = new URL(s);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                InputStream in = new BufferedInputStream(con.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    sb.append(inputLine);
                }
                //Log.d("Avi",sb.toString());

                reader.close();
                con.disconnect();
                in.close();

            }
            catch (Exception e){e.printStackTrace();}
            return null;
        }

    }*/

}
