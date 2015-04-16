package com.ayushgoyal.snappit.album;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.ayushgoyal.snappit.JSONParser;
import com.ayushgoyal.snappit.util.Constants;

import android.os.AsyncTask;
import android.util.Log;

public class RenameAlbum extends AsyncTask<String, Void, Integer> {

	@Override
	protected Integer doInBackground(String... albums) {
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("username", Constants.currentUser.getUsername()));
		args.add(new BasicNameValuePair("album", albums[0]));
		args.add(new BasicNameValuePair("newAlbumName", albums[1]));
		Log.i("Rename Album parameters:", "Username:"+Constants.currentUser.getUsername()+" Album:"+albums[0]+" New Album Name:"+albums[1]);
		JSONObject json = new JSONParser().makeHttpRequest(Constants.RENAME_ALBUM_URL, "POST",args);
		// check your log for json response
		Log.d("Rename album response:", json.toString());
		int result = 0;
		try {
			result = json.getInt(Constants.TAG_SUCCESS);
			Log.d("result", "is : " + result);
			if (result == 1) {
				Log.d("Album rename Successful!", json.toString());
				return 1;
			} else if (result == 0) {
				Log.d("Album rename Failure", json.toString());
				return 0;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
