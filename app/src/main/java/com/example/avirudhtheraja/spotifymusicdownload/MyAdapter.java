package com.example.avirudhtheraja.spotifymusicdownload;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.avirudhtheraja.spotifymusicdownload.R;
import com.example.avirudhtheraja.spotifymusicdownload.Track;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.TrackViewHolder>{
    private ArrayList<Track> tracks;
    public MyAdapter(ArrayList<Track> songs) {
        tracks = new ArrayList<>(songs);
        mListener = new MyOnClickListener();
    }
    MyOnClickListener mListener;

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_track, parent, false);
        v.setOnClickListener(mListener);
        TrackViewHolder tvh = new TrackViewHolder(v);
        return tvh;
    }

    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        holder.songName.setText(tracks.get(position).getName());
        holder.artistName.setText(tracks.get(position).getArtist());
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder{

        TextView songName;
        TextView artistName;
        public TrackViewHolder(View itemView) {
            super(itemView);
            songName = (TextView)itemView.findViewById(R.id.songnameTextview);
            artistName = (TextView)itemView.findViewById(R.id.artistNameTextview);
        }
    }

}
