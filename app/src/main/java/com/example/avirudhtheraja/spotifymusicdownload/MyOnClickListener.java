package com.example.avirudhtheraja.spotifymusicdownload;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MyOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        String name = ((TextView)v.findViewById(R.id.songnameTextview)).getText().toString();
        String artist = ((TextView)v.findViewById(R.id.artistNameTextview)).getText().toString();
        Log.d("Avi","Song is "+name+" artist is "+artist);
        DownloadYoutubeId.downloadSong(name,artist);
    }
}
