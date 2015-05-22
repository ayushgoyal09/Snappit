package com.ayushgoyal.snappit.album;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.ayushgoyal.snappit.JSONParser;
import com.ayushgoyal.snappit.user.profile.SyncAccount;
import com.ayushgoyal.snappit.util.Constants;

public class GetAlbumImageNames  extends AsyncTask<String, Void, Void>{
	
	@Override
	protected Void doInBackground(String... params) {
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("username", Constants.currentUser.getUsername()));
		args.add(new BasicNameValuePair("album",params[0]));
		JSONObject json = new JSONParser().makeHttpRequest(
				Constants.URL_get_imagesList, "GET", args);
		
		int success = 1;
		if (success == 1) {
			
			JSONArray images = null;
			try {
				images = json.getJSONArray("images");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (int i = 0; i < images.length(); i++) {
				JSONObject device;
				try {
					device = images.getJSONObject(i);
					Constants.SYNC_CLIENT_IMAGELIST.add(device.getString("name"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
//				String name = UPLOADS_FOLDER+Constants.currentUser.getUsername()+"/"+Constants.CURRENT_ALBUM+"/"+device.getString("name");
//				Log.i("image",name);
//				if(!image_urls.contains(name)){
//				
//				image_urls.add(name);
//
//				Log.i("Imagelog: ", image_urls.get(i).toString());
				}
			}

		
		
	return null;
		
	}
}
/*
class DisplayImages extends AsyncTask<String, String, String> {

	@Override
	protected String doInBackground(String... params) {
		URL url;
		try {
			List<NameValuePair> args = new ArrayList<NameValuePair>();
			args.add(new BasicNameValuePair("username", Constants.currentUser.getUsername()));
			args.add(new BasicNameValuePair("album",Constants.CURRENT_ALBUM));
			JSONObject json = new JSONParser().makeHttpRequest(
					URL_get_imagesList, "GET", args);
			Log.i("Output", json.toString());
			image_urls.clear();
			int success = 1;
			if (success == 1) {
				images = json.getJSONArray(TAG_IMAGES);
				for (int i = 0; i < images.length(); i++) {
					JSONObject device = images.getJSONObject(i);
					String name = UPLOADS_FOLDER+Constants.currentUser.getUsername()+"/"+Constants.CURRENT_ALBUM+"/"+device.getString("name");
					Log.i("image",name);
					if(!image_urls.contains(name)){
					
					image_urls.add(name);

					Log.i("Imagelog: ", image_urls.get(i).toString());
					}
				}

			}
			allImages.clear();
			int i = 0;
			while (i < image_urls.size()) {

				url = new URL(image_urls.get(i));
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				testImage = BitmapFactory.decodeStream(input);
				allImages.add(Bitmap.createScaledBitmap(testImage, 250,
						250, true));
				i++;
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		ThumbnailAdapter thumbsAdapter = new ThumbnailAdapter(Snappit.this);
		thumbsAdapter.notifyDataSetChanged();
		thumbnails.setAdapter(thumbsAdapter);

	}
}*/