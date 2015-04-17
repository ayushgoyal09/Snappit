package com.ayushgoyal.snappit.user.profile;

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

public class DeleteUserAccount extends AsyncTask<Void, Void, Integer> {

	@Override
	protected Integer doInBackground(Void... params) {
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("username", Constants.currentUser.getUsername()));
		Log.i("Deleting user account", "User: "+Constants.currentUser.getUsername());
		Log.i("Delete Account Request", args.toString());
		Log.i("Delete account url:", Constants.DELETE_ACCOUNT_URL);
		JSONObject json = new JSONParser().makeHttpRequest(Constants.DELETE_ACCOUNT_URL, "POST", args);
		Log.i("Delete account JSON Request", json.toString());
		
		int result = 0;
		try {
			result = json.getInt(Constants.TAG_SUCCESS);
			Log.d("Delete account response:", ""+result);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	
	}

}
