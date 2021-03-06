package com.example.avirudhtheraja.spotifymusicdownload;

import android.app.DownloadManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadYoutubeId {
    private static String song = null;
    private static String youtubeURL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=";
    private final static String YOUTUBE_KEY="YOUTUBE_KEY";
    private static StringBuilder sb = new StringBuilder();

    public static void downloadSong(String songName, String artistName){
        String append = artistName+" "+songName;
        song = replaceSpaces(append);
        Log.d("Avi","Song is "+song);
        String url = youtubeURL+song+"&key="+YOUTUBE_KEY;
        Log.d("Avi",url);
        new AsyncTask().execute(url);
    }

    private static String replaceSpaces(String s){
        char[] arr = s.toCharArray();
        for(int i = 0; i < arr.length; i++)
            if(arr[i] == ' ')
                arr[i] = '+';
        return new String(arr);
    }

    private static class AsyncTask extends android.os.AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            String s = params[0];
            Log.d("Avi","Url passed in is "+s);
            String result = null;
            try{
                URL url = new URL(s);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                InputStream in = new BufferedInputStream(con.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    sb.append(inputLine);
                }
                result = sb.toString();
                reader.close();
                con.disconnect();
                in.close();
                JSONObject object = new JSONObject(result);
                result = null;
                int num = object.getJSONObject("pageInfo").getInt("resultsPerPage");
                for(int i = 0; i < num; i++) {
                    JSONObject item = object.getJSONArray("items").getJSONObject(i);
                    if(item.getJSONObject("id").getString("kind").equals("youtube#video")) {
                        result = item.getJSONObject("id").getString("videoId");
                        break;
                    }

                }

            }
            catch (Exception e){e.printStackTrace();}
            if(result != null)
                Log.d("Avi",result);
            else
                Log.d("Avi","Failed to get youtube id");
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            sb.setLength(0);
            if(s==null) {
                Log.d("Avi","No video");
                return;
            }
            Log.d("Avi","Final result is "+s);
            DownloadManager dm = TrackFragment.getManager();
            DownloadSong.downloadSong(s,dm);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

}
