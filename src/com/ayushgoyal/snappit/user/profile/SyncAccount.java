package com.ayushgoyal.snappit.user.profile;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.ayushgoyal.snappit.JSONParser;
import com.ayushgoyal.snappit.album.AddAlbumToDb;
import com.ayushgoyal.snappit.album.GetAlbumImageNames;
import com.ayushgoyal.snappit.beans.AlbumBean;
import com.ayushgoyal.snappit.util.Constants;

public class SyncAccount extends AsyncTask<Void, Void, Void>{

	@Override
	protected Void doInBackground(Void... params) {
		ArrayList<String> clientAlbums = new ArrayList<String>();
		File mediaStorageDir = new File(
		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Snappit/" + Constants.currentUser.getUsername() + "/");
		Log.i("Media Storage:", mediaStorageDir.getAbsolutePath());
//		File file = new File("/path/to/directory");
		String[] directories = mediaStorageDir.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return new File(current, name).isDirectory();
		  }
		});
		for(String album: directories){
			clientAlbums.add(album);
		}
		Log.i("Client albums:",clientAlbums.toString());
		
		
		
		Log.i("Current User:", Constants.currentUser.getUsername());
		ArrayList<String> serverAlbums = new ArrayList<String>();
		for(AlbumBean album: Constants.ALBUM_LIST){
			serverAlbums.add(album.getName());
		}
		Log.i("Server Albums:", serverAlbums.toString());
		
		ArrayList<String> combinedAlbums = new ArrayList<String>();
		
		for(String album: serverAlbums){
			combinedAlbums.add(album);
		}
		
		
		for(String album: clientAlbums){
			if(!combinedAlbums.contains(album)){
				combinedAlbums.add(album);
			}
		}
		Log.i("Combined Albums:", combinedAlbums.toString());
		
		ArrayList<String> serverMissingAlbums = new ArrayList<String>();
		
		for(String album: combinedAlbums){
			if(!serverAlbums.contains(album)){
				serverMissingAlbums.add(album);
			}
		}
		
		
		Log.i("Server Missing Albums:", serverMissingAlbums.toString());
		
		ArrayList<String> clientMissingAlbums = new ArrayList<String>();
		
		for(String album: combinedAlbums){
			if(!clientAlbums.contains(album)){
				clientMissingAlbums.add(album);
			}
		}
		
		
		Log.i("Client Missing Albums:", clientMissingAlbums.toString());
		
		//1. Add missing albums to Server
		for(String album: serverMissingAlbums){
			new AddAlbumToDb().execute(new AlbumBean(album));
		}
		
		//2. Add missing albums to client
		for(String album: clientMissingAlbums){
			new File(mediaStorageDir+"/"+album).mkdir();
		}

		//For each album in combined albums
		// get clientImages and serverImages
		// create combined images.
		//	get missingClientImages and missingServerImages
		// upload missing server images
		// download missing client images
		
		for(String album: combinedAlbums){
			
			ArrayList<String> clientImages = new ArrayList<String>();
			String[] listOfimgClient = new File(mediaStorageDir+"/"+album).list();
			for(String img: listOfimgClient){
				clientImages.add(img);
			}
			Log.i("Client Images: ", album+":"+clientImages.toString());
			
			
			ArrayList<String> serverImages = new ArrayList<String>();
			List<NameValuePair> args = new ArrayList<NameValuePair>();
			args.add(new BasicNameValuePair("username", Constants.currentUser.getUsername()));
			args.add(new BasicNameValuePair("album",album));
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
						serverImages.add(device.getString("name"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					}
				}

			Log.i("Server Images:", album+": "+serverImages.toString());
			
			ArrayList<String> combinedImages= new ArrayList<String>();
			
			for(String image: serverImages){
				combinedImages.add(image);
			}
			
			
			for(String images: clientImages){
				if(!combinedImages.contains(images)){
					combinedImages.add(images);
				}
			}
			Log.i("Combined Images:", combinedImages.toString());
		
			ArrayList<String> serverMissingImages = new ArrayList<String>();
			
			for(String image: combinedImages){
				if(!serverImages.contains(image)){
					serverMissingImages.add(image);
				}
			}
			
			
			Log.i("Server Missing Images:", serverMissingImages.toString());
			
			ArrayList<String> clientMissingImages = new ArrayList<String>();
			
			for(String image: combinedImages){
				if(!clientImages.contains(image)){
					clientMissingImages.add(image);
				}
			}
			
			
			Log.i("Client Missing Images:", clientMissingImages.toString());
			
				
		}
		
		
		
		
		return null;
	}
	
}
