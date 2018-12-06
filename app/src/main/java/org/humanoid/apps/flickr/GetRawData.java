package org.humanoid.apps.flickr;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

enum DownloadStatus {IDLE, PROCESSING, NOT_INITIALIZED, FAILED_OR_EMPTY, OK}

class GetRawData extends AsyncTask<String,Void,String> {
    private static final String TAG = "GetRawData";

    private DownloadStatus mDownloadStatus;
    private final OnDownloadComplete mCallBack;

    interface OnDownloadComplete{
        void onDownloadComplete(String data, DownloadStatus status);
    }

    public GetRawData(OnDownloadComplete callBack){
        this.mDownloadStatus = DownloadStatus.IDLE;
        mCallBack = callBack;
    }

    void runInSameThread(String s){
        Log.d(TAG, "runInSameThread: starts");
        if(mCallBack != null){
            mCallBack.onDownloadComplete(doInBackground(s),mDownloadStatus);
        }
        Log.d(TAG, "runInSameThread: ends");
    }

    @Override
    protected void onPostExecute(String s) {
//        Log.d(TAG, "onPostExecute: parmeter : " + s);
        if(mCallBack != null){
            mCallBack.onDownloadComplete(s,mDownloadStatus);
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        if(strings == null){
            mDownloadStatus = DownloadStatus.NOT_INITIALIZED;
        }

        try{
            mDownloadStatus = DownloadStatus.PROCESSING;
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            Log.d(TAG, "doInBackground: Response code : " + responseCode);

            StringBuilder result = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while(null != (line = reader.readLine())){
                result.append(line + "\n");

            }

            mDownloadStatus = DownloadStatus.OK;

            return result.toString();

        }catch (MalformedURLException e){
            Log.e(TAG, "doInBackground: Malformed URL" + e.getMessage());
        }catch (IOException e){
            Log.e(TAG, "doInBackground: IO Exception : " + e.getMessage());
        }catch (SecurityException e){
            Log.e(TAG, "doInBackground: Security Exception, Needs Permission?" + e.getMessage());
        }finally {
            if(connection != null){
                connection.disconnect();
            }
            if(reader != null){
                try {
                    reader.close();
                }catch (IOException e){
                    Log.e(TAG, "doInBackground: Error Closing Stream " + e.getMessage());
                }
            }
        }
        mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }
}
