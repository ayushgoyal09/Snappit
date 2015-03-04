package com.ayushgoyal.snappit.album;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import com.ayushgoyal.snappit.JSONParser;
import com.ayushgoyal.snappit.R;
import com.ayushgoyal.snappit.beans.AlbumBean;
import com.ayushgoyal.snappit.beans.UserBean;
import com.ayushgoyal.snappit.util.Constants;

public class Albums extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		UserBean user = (UserBean) intent.getSerializableExtra("user");
		setContentView(R.layout.display_albums);
		GridView gridView = (GridView) findViewById(R.id.albums_grid);
		AlbumsGridViewAdapter gridViewAdapter = new AlbumsGridViewAdapter(this,
				R.layout.album_grid_item_layout, getData());
		
		gridView.setAdapter(gridViewAdapter);

		new GetAlbums().execute(user);
	}

	private ArrayList getData() {
		ArrayList imageItems = new ArrayList();
		Bitmap Test1 = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		Bitmap Test2 = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		imageItems.add(new ImageItem(Test1, "test1"));
		imageItems.add(new ImageItem(Test2, "test2"));
		imageItems.add(new ImageItem(Test2, "test2"));
		imageItems.add(new ImageItem(Test2, "test2"));
		imageItems.add(new ImageItem(Test2, "test2"));
		imageItems.add(new ImageItem(Test2, "test2"));
		imageItems.add(new ImageItem(Test2, "test2"));
		imageItems.add(new ImageItem(Test2, "test2"));
		imageItems.add(new ImageItem(Test2, "test2"));
		imageItems.add(new ImageItem(Test2, "test2"));
		imageItems.add(new ImageItem(Test2, "test2"));
		return imageItems;
	}

	class GetAlbums extends AsyncTask<UserBean, String, List<AlbumBean>> {
		ProgressDialog pDialog = new ProgressDialog(Albums.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setMessage("Getting your Albums...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected List<AlbumBean> doInBackground(UserBean... users) {
			List<NameValuePair> args = new ArrayList<NameValuePair>();

			args.add(new BasicNameValuePair("users_id", users[0].getUsername()));
			Log.i("ALBUM REQUEST FOR USER : ", users[0].getUsername());
			JSONObject json = new JSONParser().makeHttpRequest(
					Constants.GET_ALBUMS_URL, "POST", args);
			Log.i("ALBUMS", json.toString());
			return null;
		}

		@Override
		protected void onPostExecute(List<AlbumBean> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();

		}

	}

}
