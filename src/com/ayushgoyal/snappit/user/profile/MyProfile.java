package com.ayushgoyal.snappit.user.profile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ayushgoyal.snappit.JSONParser;
import com.ayushgoyal.snappit.R;
import com.ayushgoyal.snappit.album.AddAlbumToDb;
import com.ayushgoyal.snappit.beans.AlbumBean;
import com.ayushgoyal.snappit.dialogs.AlertDialogFragment;
import com.ayushgoyal.snappit.util.Constants;

public class MyProfile extends Activity implements OnClickListener {
	String currentPasswordText;
	String newPasswordText;
	String confirmNewPasswordText;
	EditText currentPassword;
	EditText newPassword;
	EditText confirmNewPassword;
	static boolean deleteAccount;

	public static boolean isDeleteAccount() {
		return deleteAccount;
	}

	public static void setDeleteAccount(boolean deleteAccount) {
		MyProfile.deleteAccount = deleteAccount;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_profile);
		Button changePassword = (Button) findViewById(R.id.change_password);
		changePassword.setOnClickListener(this);
		Button deleteAccount = (Button) findViewById(R.id.delete_account);
		deleteAccount.setOnClickListener(this);
		// Button syncAccount = (Button) findViewById(R.id.sync);
		// syncAccount.setOnClickListener(this);

		Button syncUp = (Button) findViewById(R.id.sync_up);
		syncUp.setOnClickListener(this);

		Button syncDown = (Button) findViewById(R.id.sync_down);
		syncDown.setOnClickListener(this);

		currentPassword = (EditText) findViewById(R.id.current_password);
		newPassword = (EditText) findViewById(R.id.new_password);
		confirmNewPassword = (EditText) findViewById(R.id.re_enter_new_password);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.change_password:
			currentPasswordText = currentPassword.getText().toString();
			newPasswordText = newPassword.getText().toString();
			confirmNewPasswordText = confirmNewPassword.getText().toString();

			Toast.makeText(getApplicationContext(), "Change passwd",
					Toast.LENGTH_SHORT).show();
			if (checkPasswordFields()) {
				new ModifyPassword().execute(newPasswordText,
						currentPasswordText);
			}

			break;

		case R.id.delete_account:
			DialogFragment newFragment = AlertDialogFragment.newInstance(
					"DELETE ACCOUNT?", R.layout.delete_account);
			newFragment.show(getFragmentManager(), "dialog");

			break;

		// case R.id.sync:
		// new SyncAccount().execute();

		case R.id.sync_up:
			new SyncUp().execute();
			
		case R.id.sync_down:
			new DownloadImage().execute();
			

		default:
			break;
		}

	}
	
	class DownloadImage extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {
			String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
			OutputStream fOut = null;
			File file = new File(path, "FitnessGirl.jpg"); // the File to save to
			try {
				fOut = new FileOutputStream(file);
				String myurl = "http://www.ayushgoyal09.com/webservice/uploadss/f/cloak/IMG_20150521_234035.jpg";
				URL url = new URL(myurl);
//				Bitmap pictureBitmap = getImageBitmap(myurl); // obtaining the Bitmap
				Bitmap pictureBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
				pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
				fOut.flush();
				fOut.close(); // do not forget to close the stream

				MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		
	}
	
	
	private void syncDown() throws IOException {
		String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
		OutputStream fOut = null;
		File file = new File(path, "FitnessGirl.jpg"); // the File to save to
		fOut = new FileOutputStream(file);
		String myurl = "http://www.ayushgoyal09.com/webservice/uploadss/f/cloak/IMG_20150521_234035.jpg";
		URL url = new URL(myurl);
//		Bitmap pictureBitmap = getImageBitmap(myurl); // obtaining the Bitmap
		Bitmap pictureBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
		pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
		fOut.flush();
		fOut.close(); // do not forget to close the stream

		MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
		
	}
	
	 public static void saveToFile(String filename,Bitmap bmp) {
	      try {
	          FileOutputStream out = new FileOutputStream(filename);
	          bmp.compress(CompressFormat.JPEG, 100, out);
	          out.flush();
	          out.close();
	      } catch(Exception e) {}
	  }

	public class SyncUp extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			Constants.SYNC_UP_CLIENT_IMAGELIST.clear();
			ArrayList<String> clientAlbums = new ArrayList<String>();
			ArrayList<String> syncUpImagePaths = new ArrayList<String>();
			File mediaStorageDir = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
							+ "/Snappit/"
							+ Constants.currentUser.getUsername()
							+ "/");
			Log.i("Media Storage:", mediaStorageDir.getAbsolutePath());
			// File file = new File("/path/to/directory");
			String[] directories = mediaStorageDir.list(new FilenameFilter() {
				@Override
				public boolean accept(File current, String name) {
					return new File(current, name).isDirectory();
				}
			});
			for (String album : directories) {
				clientAlbums.add(album);
			}
			Log.i("Client albums:", clientAlbums.toString());

			Log.i("Current User:", Constants.currentUser.getUsername());
			ArrayList<String> serverAlbums = new ArrayList<String>();
			for (AlbumBean album : Constants.ALBUM_LIST) {
				serverAlbums.add(album.getName());
			}
			Log.i("Server Albums:", serverAlbums.toString());

			ArrayList<String> combinedAlbums = new ArrayList<String>();

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
				new AddAlbumToDb().execute(new AlbumBean(album));
			}

			// For each album in combined albums
			// get clientImages and serverImages
			// create combined images.
			// get missingClientImages and missingServerImages
			// upload missing server images
			// download missing client images

			for (String album : combinedAlbums) {

				ArrayList<String> clientImages = new ArrayList<String>();
				String[] listOfimgClient = new File(mediaStorageDir + "/" + album)
						.list();
				for (String img : listOfimgClient) {
					clientImages.add(img);
				}
				Log.i("Client Images: ", album + ":" + clientImages.toString());

				ArrayList<String> serverImages = new ArrayList<String>();
				List<NameValuePair> args = new ArrayList<NameValuePair>();
				args.add(new BasicNameValuePair("username", Constants.currentUser
						.getUsername()));
				args.add(new BasicNameValuePair("album", album));
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

				Log.i("Server Images:", album + ": " + serverImages.toString());

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
				

				for (String image : combinedImages) {
					if (!serverImages.contains(image)) {
						serverMissingImages.add(image);
						Constants.SYNC_UP_CLIENT_IMAGELIST.add(album+"/"+image);
						syncUpImagePaths.add(album+"/"+image);
						
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
				
				Log.i("Images to be synced", syncUpImagePaths.size()+"");
				
				
						
				
				
				

			}

			return syncUpImagePaths.size();
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			DialogFragment syncUpFrag = AlertDialogFragment.newInstance(
					"Upload " + Constants.SYNC_UP_CLIENT_IMAGELIST.size()
							+ " files?", R.layout.sync_up_dialog);
			syncUpFrag.show(getFragmentManager(), "dialog");
			
			
		}

	}
	
	

	private boolean checkPasswordFields() {
		Log.i("Password field Inputs", currentPasswordText + newPasswordText
				+ confirmNewPasswordText);
		if ("".equals(currentPasswordText) || "".equals(newPasswordText)
				|| "".equals(confirmNewPasswordText)) {
			Toast.makeText(getApplicationContext(),
					"All the fields are required", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (!newPasswordText.equals(confirmNewPasswordText)) {
			Toast.makeText(getApplicationContext(),
					"The two passwords do not match", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}

	class ModifyPassword extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... params) {

			List<NameValuePair> args = new ArrayList<NameValuePair>();
			args.add(new BasicNameValuePair("username", Constants.currentUser
					.getUsername()));
			args.add(new BasicNameValuePair("newPassword", params[0]));
			args.add(new BasicNameValuePair("currentPassword", params[1]));

			Log.i("Requesting change of password", "User: "
					+ Constants.currentUser.getUsername() + " New Password: "
					+ params[0] + "Current :" + params[1]);
			Log.i("Password Change Request", args.toString());
			Log.i("Password change url:", Constants.CHANGE_PASSWORD_URL);
			JSONObject json = new JSONParser().makeHttpRequest(
					Constants.CHANGE_PASSWORD_URL, "POST", args);
			Log.i("Modify Password JSON Request", json.toString());

			int result = 0;
			try {
				result = json.getInt(Constants.TAG_SUCCESS);
				Log.d("Modify Password Response", "" + result);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return result;

		}

	}

}
