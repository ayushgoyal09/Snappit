package com.ayushgoyal.snappit.album;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.ayushgoyal.snappit.JSONParser;
import com.ayushgoyal.snappit.Snappit;
import com.ayushgoyal.snappit.beans.AlbumBean;
import com.ayushgoyal.snappit.util.Constants;

import android.os.AsyncTask;
import android.util.Log;

public class DeleteImages extends AsyncTask<ArrayList<String>, Void, Integer>{

	@Override
	protected Integer doInBackground(ArrayList<String>... params) {
		for(String item: params[0]){
			Log.i("DELETE:", item);
		}
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("username", Constants.currentUser.getUsername()));
		args.add(new BasicNameValuePair("album", Constants.CURRENT_ALBUM));
		for(String imageName : params[0]){
			imageName = imageName.substring(imageName.lastIndexOf("/")+1);
			Log.i("NAME:", imageName);
			args.add(new BasicNameValuePair("images[]", imageName));
		}
		
		
		Log.i("Delete Image Request", args.toString());
		Log.i("Delete image url:", Constants.DELETE_IMAGE_URL);
		JSONObject json = new JSONParser().makeHttpRequest(Constants.DELETE_IMAGE_URL, "POST", args);
		Log.i("Delete Image JSON Request", json.toString());
		
		int result = 0;
		return null;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
	}
	
	

}
