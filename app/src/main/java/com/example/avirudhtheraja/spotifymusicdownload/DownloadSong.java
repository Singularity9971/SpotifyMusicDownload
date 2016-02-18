package com.example.avirudhtheraja.spotifymusicdownload;

import android.app.DownloadManager;
import android.net.Uri;
import android.util.Log;

public class DownloadSong {

    private static String url = "http://www.youtubeinmp3.com/fetch/?video=http://www.youtube.com/watch?v=";
    private static DownloadManager dm = null;

    public static void downloadSong(String youtubeId, DownloadManager manager){
        String passInUrl = url+youtubeId+"&autostart=1";
        dm = manager;
        new AsyncTask().execute(passInUrl);
    }

    private static class AsyncTask extends android.os.AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {
            String s = params[0];
            Log.d("Avi","Url passed in is "+s);
            try{
                Uri uri = Uri.parse(s);
                DownloadManager.Request req = new DownloadManager.Request(uri);
                dm.enqueue(req);
            }
            catch (Exception e){e.printStackTrace();}
            return null;
        }

    }

}
