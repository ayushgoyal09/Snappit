package com.ayushgoyal.snappit.album;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.ayushgoyal.snappit.JSONParser;
import com.ayushgoyal.snappit.R;
import com.ayushgoyal.snappit.beans.AlbumBean;
import com.ayushgoyal.snappit.beans.UserBean;
import com.ayushgoyal.snappit.dialogs.AlertDialogFragment;
import com.ayushgoyal.snappit.user.profile.MyProfile;
import com.ayushgoyal.snappit.user.profile.MyProfile.SyncDown;
import com.ayushgoyal.snappit.user.profile.MyProfile.SyncDownImages;
import com.ayushgoyal.snappit.user.profile.MyProfile.SyncUp;
import com.ayushgoyal.snappit.user.profile.MyProfile.SyncUpImages;
import com.ayushgoyal.snappit.util.Constants;

public class Albums extends Activity {

	public static ArrayList<ImageItem> albums = new ArrayList<ImageItem>();
	GridView gridView;
	public static AlbumsGridViewAdapter gridViewAdapter;
	File mediaStorageDir = new File(
			Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
					+ "/Snappit/" + Constants.currentUser.getUsername() + "/");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("Checkpoint", "2");
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		UserBean user = (UserBean) intent.getSerializableExtra("user");
		setContentView(R.layout.display_albums);
		gridView = (GridView) findViewById(R.id.albums_grid);
		albums = getData();
		gridViewAdapter = new AlbumsGridViewAdapter(this,
				R.layout.album_grid_item_layout, albums);
		gridView.setAdapter(gridViewAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_albums, menu);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_add:
			DialogFragment newFragment = AlertDialogFragment.newInstance(
					"Add Album", R.layout.add_album_dialog);
			newFragment.show(getFragmentManager(), "dialog");
			break;

		case R.id.action_profile:
			Intent intent = new Intent(getApplicationContext(), MyProfile.class);
			startActivity(intent);
			break;

		case R.id.action_sort:			
			Collections.sort(albums);
			Albums.gridViewAdapter.notifyDataSetChanged();
			Toast.makeText(getApplicationContext(), "Albums sorted alphabetically", Toast.LENGTH_SHORT)
			.show();
			break;

		case R.id.action_sync_up:
			Log.i("SYNC UP", "started--------------->>");
			new SyncUp().execute();
			break;

		case R.id.action_sync_down:
			Log.i("SYNC DOWN", "started--------------->>");
			new SyncDown().execute();
			break;

		default:
			return super.onOptionsItemSelected(item);

		}
		return super.onOptionsItemSelected(item);

	}

	private ArrayList<ImageItem> getData() {
		ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
		// Bitmap Test1 = BitmapFactory.decodeResource(getResources(),
		// R.drawable.ic_launcher);
		for (AlbumBean album : Constants.ALBUM_LIST) {
			imageItems
					.add(new ImageItem(album.getAlbumCover(), album.getName()));
		}

		return imageItems;
	}

	public class SyncUp extends AsyncTask<Void, Void, Integer> {
		ProgressDialog pDialog;
		ArrayList<String> combinedAlbums = new ArrayList<String>();

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Albums.this);
			pDialog.setMessage("Sync-Up Albums...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			Constants.SYNC_UP_CLIENT_IMAGELIST.clear();
			ArrayList<String> clientAlbums = new ArrayList<String>();

			Log.i("Media Storage:", mediaStorageDir.getAbsolutePath());
			// File file = new File("/path/to/directory");
			String[] directories = mediaStorageDir.list(new FilenameFilter() {
				@Override
				public boolean accept(File current, String name) {
					return new File(current, name).isDirectory();
				}
			});

			if (directories != null) {
				for (String album : directories) {
					clientAlbums.add(album);
				}
			}

			Log.i("Client albums:", clientAlbums.toString());

			Log.i("Current User:", Constants.currentUser.getUsername());
			ArrayList<String> serverAlbums = new ArrayList<String>();
			for (AlbumBean album : Constants.ALBUM_LIST) {
				serverAlbums.add(album.getName());
			}
			Log.i("Server Albums:", serverAlbums.toString());

			for (String album : serverAlbums) {
				combinedAlbums.add(album);
			}

			for (String album : clientAlbums) {
				if (!combinedAlbums.contains(album)) {
					combinedAlbums.add(album);
				}
			}
			Log.i("Combined Albums:", combinedAlbums.toString());

			ArrayList<String> serverMissingAlbums = new ArrayList<String>();

			for (String album : combinedAlbums) {
				if (!serverAlbums.contains(album)) {
					serverMissingAlbums.add(album);
				}
			}

			Log.i("Server Missing Albums:", serverMissingAlbums.toString());

			ArrayList<String> clientMissingAlbums = new ArrayList<String>();

			for (String album : combinedAlbums) {
				if (!clientAlbums.contains(album)) {
					clientMissingAlbums.add(album);
				}
			}

			Log.i("Client Missing Albums:", clientMissingAlbums.toString());

			// 1. Add missing albums to Server
			for (String album : serverMissingAlbums) {
				try {
					int j = new AddAlbumToDb().execute(new AlbumBean(album))
							.get(1000, TimeUnit.MILLISECONDS);
					Log.i("J", "" + j);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// For each album in combined albums
			// get clientImages and serverImages
			// create combined images.
			// get missingClientImages and missingServerImages
			// upload missing server images
			// download missing client images

			return 3;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			for (String album : combinedAlbums) {
				try {
					ArrayList<String> k = new SyncUpImages().execute(album)
							.get(3000, TimeUnit.MILLISECONDS);
					Log.i("K", k + "");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	public class SyncUpImages extends
			AsyncTask<String, Void, ArrayList<String>> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			Log.i("Syncing:", params[0]);

			ArrayList<String> clientImages = new ArrayList<String>();
			if (params[0] != null) {
				String[] listOfimgClient = new File(mediaStorageDir + "/"
						+ params[0]).list();
				if (listOfimgClient != null) {
					for (String img : listOfimgClient) {
						clientImages.add(img);
					}
				}
			}

			Log.i("Client Images: ", params[0] + ":" + clientImages.toString());

			ArrayList<String> serverImages = new ArrayList<String>();
			List<NameValuePair> args = new ArrayList<NameValuePair>();
			args.add(new BasicNameValuePair("username", Constants.currentUser
					.getUsername()));
			args.add(new BasicNameValuePair("album", params[0]));
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

			ArrayList<String> combinedImages = new ArrayList<String>();

			for (String image : serverImages) {
				combinedImages.add(image);
			}

			for (String images : clientImages) {
				if (!combinedImages.contains(images)) {
					combinedImages.add(images);
				}
			}
			Log.i("Combined Images:", combinedImages.toString());

			ArrayList<String> serverMissingImages = new ArrayList<String>();
			ArrayList<String> syncUpImagePaths = new ArrayList<String>();

			for (String image : combinedImages) {
				if (!serverImages.contains(image)) {
					serverMissingImages.add(image);
					// Constants.SYNC_UP_CLIENT_IMAGELIST.add(params[0] + "/"
					// + image);
					syncUpImagePaths.add(params[0] + "/" + image);

				}
			}

			Log.i("Server Missing Images:", serverMissingImages.toString());

			ArrayList<String> clientMissingImages = new ArrayList<String>();

			for (String image : combinedImages) {
				if (!clientImages.contains(image)) {
					clientMissingImages.add(image);

				}
			}

			Log.i("Client Missing Images:", clientMissingImages.toString());
			Log.i("Sync Up Images Path:", syncUpImagePaths.toString());

			Log.i("Images to be synced", syncUpImagePaths.size() + "");

			Log.i("Server Images:", params[0] + ": " + serverImages.toString());
			syncUpImagePaths.add(0, params[0]);
			return syncUpImagePaths;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			super.onPostExecute(result);
			String albumName = result.get(0);
			result.remove(0);
			if (result.size() != 0) {
				DialogFragment syncUpFrag = AlertDialogFragment.newInstance(
						"Upload " + result.size() + " images to album : "
								+ albumName + " ?", R.layout.sync_up_dialog,
						result);
				syncUpFrag.show(getFragmentManager(), "dialog");
			}
		}

	}

	public class SyncDown extends AsyncTask<Void, Void, Integer> {
		ProgressDialog pDialog;
		ArrayList<String> combinedAlbums = new ArrayList<String>();

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Albums.this);
			pDialog.setMessage("Sync-Up Albums...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Integer doInBackground(Void... params) {

			// Check if App directory exists, if not create one.
			File snappitDirectory = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
							+ "/Snappit/");
			if (!snappitDirectory.exists()) {
				snappitDirectory.mkdir();
				Log.i("Directory created:", " Snappit app directory");
			}

			// Check if user directory exists, if not create one.
			File userDir = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
							+ "/Snappit/" + Constants.currentUser.getUsername());
			if (!userDir.exists()) {
				userDir.mkdir();
			}

			ArrayList<String> clientAlbums = new ArrayList<String>();
			Log.i("Media Storage:", mediaStorageDir.getAbsolutePath());
			// File file = new File("/path/to/directory");
			String[] directories = mediaStorageDir.list(new FilenameFilter() {
				@Override
				public boolean accept(File current, String name) {
					return new File(current, name).isDirectory();
				}
			});

			if (directories != null) {
				for (String album : directories) {
					clientAlbums.add(album);
				}
			}

			Log.i("Client albums:", clientAlbums.toString());

			Log.i("Current User:", Constants.currentUser.getUsername());
			ArrayList<String> serverAlbums = new ArrayList<String>();
			for (AlbumBean album : Constants.ALBUM_LIST) {
				serverAlbums.add(album.getName());
			}
			Log.i("Server Albums:", serverAlbums.toString());

			for (String album : serverAlbums) {
				combinedAlbums.add(album);
			}

			for (String album : clientAlbums) {
				if (!combinedAlbums.contains(album)) {
					combinedAlbums.add(album);
				}
			}
			Log.i("Combined Albums:", combinedAlbums.toString());

			ArrayList<String> serverMissingAlbums = new ArrayList<String>();

			for (String album : combinedAlbums) {
				if (!serverAlbums.contains(album)) {
					serverMissingAlbums.add(album);
				}
			}

			Log.i("Server Missing Albums:", serverMissingAlbums.toString());

			ArrayList<String> clientMissingAlbums = new ArrayList<String>();

			for (String album : combinedAlbums) {
				if (!clientAlbums.contains(album)) {
					clientMissingAlbums.add(album);
				}
			}

			Log.i("Client Missing Albums:", clientMissingAlbums.toString());

			// 1. Add missing albums to client
			if (clientMissingAlbums != null) {
				for (String album : clientMissingAlbums) {
					new File(mediaStorageDir + "/" + album).mkdir();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			for (String album : combinedAlbums) {
				new SyncDownImages().execute(album);
			}

		}
	}

	public class SyncDownImages extends
			AsyncTask<String, Void, ArrayList<String>> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			Log.i("Syncing Down:", params[0]);

			ArrayList<String> clientImages = new ArrayList<String>();
			if (params[0] != null) {
				String[] listOfimgClient = new File(mediaStorageDir + "/"
						+ params[0]).list();
				if (listOfimgClient != null) {
					for (String img : listOfimgClient) {
						clientImages.add(img);
					}
				}
			}

			Log.i("Client Images: ", params[0] + ":" + clientImages.toString());

			ArrayList<String> serverImages = new ArrayList<String>();
			List<NameValuePair> args = new ArrayList<NameValuePair>();
			args.add(new BasicNameValuePair("username", Constants.currentUser
					.getUsername()));
			args.add(new BasicNameValuePair("album", params[0]));
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
			Log.i("Server Images:", serverImages.toString());

			ArrayList<String> combinedImages = new ArrayList<String>();

			for (String image : serverImages) {
				combinedImages.add(image);
			}

			for (String images : clientImages) {
				if (!combinedImages.contains(images)) {
					combinedImages.add(images);
				}
			}
			Log.i("Combined Images:", combinedImages.toString());

			ArrayList<String> clientMissingImages = new ArrayList<String>();
			ArrayList<String> syncUpImagePaths = new ArrayList<String>();

			for (String image : combinedImages) {
				if (!clientImages.contains(image)) {
					clientMissingImages.add(image);
					// Constants.SYNC_UP_CLIENT_IMAGELIST.add(params[0] + "/"
					// + image);
					syncUpImagePaths.add(params[0] + "/" + image);

				}
			}

			Log.i("Client Missing Images:", clientMissingImages.toString());
			Log.i("Sync DOWN Images Path:", syncUpImagePaths.toString());
			Log.i("Images to be synced", syncUpImagePaths.size() + "");
			Log.i("Client Images:", params[0] + ": " + clientImages.toString());
			syncUpImagePaths.add(0, params[0]);
			return syncUpImagePaths;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			super.onPostExecute(result);
			String albumName = result.get(0);
			result.remove(0);
			if (result.size() != 0) {
				DialogFragment syncDownFrag = AlertDialogFragment.newInstance(
						"Download " + result.size() + " images to album : "
								+ albumName + " ?", R.layout.sync_down_dialog,
						result);
				syncDownFrag.show(getFragmentManager(), "dialog");
			}
		}
	}

}
