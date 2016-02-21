package com.example.avirudhtheraja.spotifymusicdownload;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class DownloaderActivity extends AppCompatActivity {
    private ProgressDialog pd;
    private long downloadID;
    private WebView wv;
    DownloadManager dm;
    private String downloadCompleteIntentName = DownloadManager.ACTION_DOWNLOAD_COMPLETE;
    private IntentFilter downloadCompleteIntentFilter = new IntentFilter(downloadCompleteIntentName);
    private BroadcastReceiver downloadCompleteReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);
        this.registerReceiver(downloadCompleteReceiver,downloadCompleteIntentFilter);
        pd = new ProgressDialog(this,ProgressDialog.THEME_HOLO_DARK);
        pd.setMessage("Please wait, fetching your file");
        pd.show();
        String url = getIntent().getExtras().get("url").toString();
        /*Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);*/

        Log.d("Avi","Webview url is "+url);
        wv = (WebView)findViewById(R.id.webView);
        wv.getSettings().setJavaScriptEnabled(true);
        //wv.setWebChromeClient(new WebChromeClient());
        wv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        wv.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Name of your downloadble file goes here, example: Mathematics II ");
                dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                downloadID = dm.enqueue(request);
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); //This is important!
                intent.addCategory(Intent.CATEGORY_OPENABLE); //CATEGORY.OPENABLE
                intent.setType("*/*");//any application,any extension
                pd.hide();
                onBackPressed();
                downloadCompleteReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // TO BE FILLED
                        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L);
                        if (id != downloadID) {
                            Log.v("Avi", "Ingnoring unrelated download " + id);
                            return;
                        }
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(id);
                        Cursor cursor = dm.query(query);

                        // it shouldn't be empty, but just in case
                        if (!cursor.moveToFirst()) {
                            Log.e("Avi", "Empty row");
                            return;
                        }
                        int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL != cursor.getInt(statusIndex)) {
                            Log.w("Avi", "Download Failed");
                            return;
                        }
                        int uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                        String downloadedPackageUriString = cursor.getString(uriIndex);
                        try {
                            File f = new File(new URI(downloadedPackageUriString).getPath());
                            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(".mp3");
                            Intent i = new Intent();
                            i.setAction(android.content.Intent.ACTION_VIEW);
                            i.setDataAndType(Uri.fromFile(f), mime);
                            startActivityForResult(i,10);
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                };
            }
        });
        wv.setVisibility(View.INVISIBLE);
        wv.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //this.unregisterReceiver(downloadCompleteReceiver);
    }
}
