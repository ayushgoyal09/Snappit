package com.ayushgoyal.snappit.album;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.ayushgoyal.snappit.JSONParser;
import com.ayushgoyal.snappit.beans.AlbumBean;
import com.ayushgoyal.snappit.util.Constants;

import android.os.AsyncTask;
import android.util.Log;

public class DeleteAlbum extends AsyncTask<String, Void, Integer>{

	@Override
	protected Integer doInBackground(String... albums) {
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("username", Constants.currentUser.getUsername()));
		args.add(new BasicNameValuePair("album", albums[0]));
		
		Log.i("Deleting Album", "User: "+Constants.currentUser.getUsername()+" Album Name: "+albums[0]);
		Log.i("Delete Album Request", args.toString());
		Log.i("Delete album url:", Constants.DELETE_ALBUM_URL);
		JSONObject json = new JSONParser().makeHttpRequest(Constants.DELETE_ALBUM_URL, "POST", args);
		Log.i("Delete Album JSON Request", json.toString());
		
		int result = 0;
		try {
			result = json.getInt(Constants.TAG_SUCCESS);			
			Log.d("Delete Album Response", ""+result);
			if(result==1){
				for(AlbumBean album: Constants.ALBUM_LIST){
					if(albums[0].equals(album.getName())){
						Constants.ALBUM_LIST.remove(album);
						break;
					}
				}
				for(ImageItem item: Albums.albums){
					if(albums[0].equals(item.getTitle())){
						Albums.albums.remove(item);
						break;
					}
				}
				Log.i("ALBUMS:", Constants.ALBUM_LIST.toString());
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}
	
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		Albums.gridViewAdapter.notifyDataSetChanged();
	}

}
