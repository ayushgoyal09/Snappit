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

public class MoveImages extends AsyncTask<ArrayList<String>, Void, Integer>{

	@Override
	protected Integer doInBackground(ArrayList<String>... params) {
		
		Log.i("Selected Album: ", params[0].get(0));
		for(int i=1;i<params[0].size();i++){
			Log.i("MOVE: ", params[0].get(i));
		}
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("username", Constants.currentUser.getUsername()));
		args.add(new BasicNameValuePair("currentAlbum", Constants.CURRENT_ALBUM));
		args.add(new BasicNameValuePair("newAlbum", params[0].get(0)));
		for(int i=1;i<params.length;i++){
			args.add(new BasicNameValuePair("images[]", params[0].get(i)));
		}
		
		Log.i("Move Image Request", args.toString());
		Log.i("Move image url:", Constants.MOVE_IMAGE_URL);
		JSONObject json = new JSONParser().makeHttpRequest(Constants.MOVE_IMAGE_URL, "POST", args);
		String res = "";
		try {
			res = json.getString("message");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("ERROR", res);
//		Log.i("Move Image JSON Request", json.toString());
		return null;
	}

}
