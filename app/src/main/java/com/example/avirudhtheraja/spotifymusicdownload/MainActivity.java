package com.example.avirudhtheraja.spotifymusicdownload;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TreeSet;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.SavedTrack;
import kaaes.spotify.webapi.android.models.UserPrivate;
import kaaes.spotify.webapi.android.models.UserPublic;
import retrofit.Callback;
import retrofit.RetrofitError;

public class MainActivity extends AppCompatActivity implements ConnectionStateCallback, TrackFragment.OnListFragmentInteractionListener {

    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "id";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "spotify-music-download-app-login://calback";
    private static final int REQUEST_CODE = 1337;
    static String AuthenticationString = null;
    private static String userId = null;
    private TreeSet<Playlist> playlistSet = new TreeSet<>();
    private static final String savedSongsPlaylistId = "///////"; //so that it comes up as first element in the set based on ascii value comparison
    private ListView playlistListView;

    //Fix how to show the track fragment, current implementation sucks. Get more tracks from each playlist. Add item click listener to recycler view.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!checkForStoredData()) {
            Log.d("Avi","Getting data from spotify");
            getData();
        }
        else {
            Log.d("Avi","Getting data from storage");
            updateUI();
        }
    }

    private void getData(){
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"playlist-read-private", "user-library-read"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                AuthenticationString = response.getAccessToken();
                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(AuthenticationString);
                final SpotifyService spotify = api.getService();
                spotify.getMe(new Callback<UserPrivate>() {
                    @Override
                    public void success(UserPrivate userPrivate, retrofit.client.Response response) {
                        userId = userPrivate.id;
                        spotify.getPlaylists(userId, new Callback<Pager<PlaylistSimple>>() {
                            @Override
                            public void success(Pager<PlaylistSimple> playlistSimplePager, retrofit.client.Response response) {
                                for (PlaylistSimple playlist : playlistSimplePager.items) {
                                    String playlistId = playlist.id;
                                    final Playlist list = new Playlist(playlistId, playlist.name);
                                    playlistSet.add(list);
                                    userId = playlist.owner.id;
                                    spotify.getPlaylistTracks(userId, playlistId, new Callback<Pager<PlaylistTrack>>() {
                                        @Override
                                        public void success(Pager<PlaylistTrack> playlistTrackPager, retrofit.client.Response response) {
                                            for (PlaylistTrack track : playlistTrackPager.items) {
                                                list.getSongs().add(new Track(track.track.name, track.track.artists.get(0).name));
                                                Log.d("Avi", track.track.name);
                                            }
                                            spotify.getMySavedTracks(new Callback<Pager<SavedTrack>>() {
                                                @Override
                                                public void success(Pager<SavedTrack> savedTrackPager, retrofit.client.Response response) {
                                                    Playlist savedSongs = new Playlist(savedSongsPlaylistId, "Saved Tracks");
                                                    playlistSet.add(savedSongs);
                                                    for (SavedTrack track : savedTrackPager.items) {
                                                        Track song = new Track(track.track.name, track.track.artists.get(0).name);
                                                        savedSongs.getSongs().add(song);
                                                    }
                                                    storeData();
                                                    updateUI();
                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    Log.d("Avi", "Couldn't get saved tracks");
                                                }
                                            });

                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            Log.d("Avi", "Couldn't get the playlist tracks");
                                        }
                                    });
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.d("Avi", "Couldn't get playlists");
                            }
                        });
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("Avi", "Couldn't get user id");
                    }
                });
            }

        }
    }

    private void updateUI() {
        playlistListView = (ListView) findViewById(R.id.listView);
        playlistListView.setAdapter(new ListViewAdapter());
        playlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Track> songs = ((Playlist) parent.getItemAtPosition(position)).getSongs();
                Fragment f = TrackFragment.newInstance(1, songs);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.frame_container, f);
                transaction.commit();
                playlistListView.setVisibility(View.GONE);
            }
        });
    }

    private void storeData()
    {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        Gson gson = new Gson();
        int i = 0;
        for(Playlist list : playlistSet)
        {
            String json = gson.toJson(list);
            edit.putString("Playlist"+i,json);
            i++;
        }
        edit.apply();
    }

    public static void startActivity(String s){
        Intent i = new Intent(ApplicationController.getTheApplicationContext(),DownloaderActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("url",s);
        ApplicationController.getTheApplicationContext().startActivity(i);
    }

    private boolean checkForStoredData(){
        Gson gson = new Gson();
        int i = 0;
        for(;;){
            String json = getPreferences(MODE_PRIVATE).getString("Playlist"+i,null);
            if(json == null)
                break;
            Playlist list = gson.fromJson(json,Playlist.class);
            playlistSet.add(list);
            i++;
        }
        return i != 0;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getFragments().size() != 0) {
            if (playlistListView.getVisibility() == View.GONE) {
                playlistListView.setVisibility(View.VISIBLE);
                Fragment frag = getSupportFragmentManager().findFragmentById(R.id.frame_container);
                getSupportFragmentManager().beginTransaction().remove(frag).commit();
            }
        }
        else
            onPause();
    }

    @Override
    public void onListFragmentInteraction(Track item) {

    }

    private class ListViewAdapter extends BaseAdapter {
        private ArrayList<Playlist> list;

        public ListViewAdapter() {
            super();
            list = new ArrayList<>(playlistSet);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.playlist, null);
            }
            TextView playlistName = (TextView) convertView.findViewById(R.id.playlistTextview);
            String text = ((Playlist) getItem(position)).getName();
            playlistName.setText(text);
            return convertView;
        }
    }


    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }
}
