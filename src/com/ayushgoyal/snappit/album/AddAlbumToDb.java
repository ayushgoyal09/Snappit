package com.ayushgoyal.snappit.album;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.ayushgoyal.snappit.JSONParser;
import com.ayushgoyal.snappit.beans.AlbumBean;
import com.ayushgoyal.snappit.util.Constants;

public class AddAlbumToDb extends AsyncTask<AlbumBean,String, Integer>{

	@Override
	protected Integer doInBackground(AlbumBean... albums) {

		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("username", Constants.currentUser.getUsername()));
		args.add(new BasicNameValuePair("albumName", albums[0].getName()));
	
		Log.i("Adding Album", "User: "+Constants.currentUser.getUsername()+" Album Name: "+albums[0].getName());

		Log.i("Add Album Request", args.toString());
		Log.i("Add album url:", Constants.ADD_ALBUM_URL);
		JSONObject json = new JSONParser().makeHttpRequest(Constants.ADD_ALBUM_URL, "POST", args);
		Log.i("Add Album JSON Request", json.toString());
		int result = 0;
		try {
			result = json.getInt(Constants.TAG_SUCCESS);
			Log.d("Add Album Response", ""+result);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

}
