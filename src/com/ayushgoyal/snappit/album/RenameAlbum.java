package com.ayushgoyal.snappit.album;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.ayushgoyal.snappit.JSONParser;
import com.ayushgoyal.snappit.R;
import com.ayushgoyal.snappit.beans.AlbumBean;
import com.ayushgoyal.snappit.dialogs.AlertDialogFragment;
import com.ayushgoyal.snappit.util.Constants;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.util.Log;

public class RenameAlbum extends AsyncTask<String, Void, Integer> {

	@Override
	protected Integer doInBackground(String... albums) {
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		Log.i("ORIGINAL", albums[0]);
		Log.i("NEW", albums[1]);
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
//				AlbumsGridViewAdapter.selectedItem.setTitle(albums[1]);
				AlbumBean renamedAlbumBean = AlbumBean.getAlbumBeanByTitle(albums[0], Constants.ALBUM_LIST);
				renamedAlbumBean.setName(albums[1]);
				ArrayList<String> album11 = new ArrayList<String>();
				for(AlbumBean bean :Constants.ALBUM_LIST){
					album11.add(bean.getName());
				}
				Log.i("ALBUMS LIST GLOBAL2:", album11.toString());
				for(ImageItem item: Albums.albums){
					if(albums[0].equals(item.getTitle())){
						item.setTitle(albums[1]);
						break;
					}
				}
				ArrayList<String> albums1 = new ArrayList<String>();
				for(ImageItem item : Albums.albums){
					albums1.add(item.getTitle());
				}
				Log.i("ALBUMS LIST GLOBAL3:", albums1.toString());
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
	
	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		Albums.gridViewAdapter.notifyDataSetChanged();
	}

}
