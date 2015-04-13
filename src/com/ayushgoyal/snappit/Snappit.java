package com.ayushgoyal.snappit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.ayushgoyal.snappit.AndroidMultiPartEntity.ProgressListener;
import com.ayushgoyal.snappit.adapters.ThumbnailAdapter;
import com.ayushgoyal.snappit.image.ImageSlidePagerActivity;
import com.ayushgoyal.snappit.util.Constants;

public class Snappit extends Activity implements OnClickListener {

	private ProgressDialog pDialog;
//	private static final String URL = "http://www.ayushgoyal09.com/webservice/upload_image2.php";
	private static final String URL = "http://www.ayushgoyal09.com/webservice/fileUpload1.php";
	private static final String URL_get_imagesList = "http://www.ayushgoyal09.com/webservice/get_all_images1.php";
	private static final String UPLOADS_FOLDER = "http://www.ayushgoyal09.com/webservice/uploadss/";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_IMAGES = "images";
	JSONArray images;
	private static ArrayList<String> image_urls = new ArrayList<String>();
	private static final int MEDIA_TYPE_IMAGE = 1;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static String mCurrentPhotoPath; // File path to the last image
												// captured
	private Uri fileUri;
	GridView thumbnails;
	private Button takePictureButton;
	public static Bitmap testImage;
	public static ArrayList<Bitmap> allImages = new ArrayList<Bitmap>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String album = intent.getStringExtra("album");
		Log.i("Album:", album);
		Constants.CURRENT_ALBUM = album;
		Log.i("User:", Constants.currentUser.getUsername());
		setContentView(R.layout.home_screen);
		Log.i("size of images", "" + image_urls.size());
		thumbnails = (GridView) findViewById(R.id.image_grid);
		takePictureButton = (Button) findViewById(R.id.camera_button);
		takePictureButton.setOnClickListener(this);
		new DisplayImages().execute();
		thumbnails.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				Intent intent = new Intent(getApplicationContext(), FullScreenImage.class);
				Intent intent = new Intent(getApplicationContext(), ImageSlidePagerActivity.class);
				intent.putExtra("position", position);
				startActivity(intent);
				
			}
		});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.camera_button:
			dispatchTakePictureIntent();

			break;

		default:
			break;
		}
	}

	private void dispatchTakePictureIntent() {
		try {
			Intent takePictureIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file
																// to save the
																// image
			Log.i("FILEURI", "" + fileUri);
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set
																			// the
																			// image
																			// file
																			// name
			startActivityForResult(takePictureIntent,
					CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
				if (resultCode == RESULT_OK) {
					// Save Image To Gallery
					Intent mediaScanIntent = new Intent(
							Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
					File f = new File(mCurrentPhotoPath);
					Uri contentUri = Uri.fromFile(f);
					mediaScanIntent.setData(contentUri);
					this.sendBroadcast(mediaScanIntent);
					new UploadImage().execute();

					// Image captured and saved to fileUri specified in the
					// Intent
					Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT)
							.show();
					// Toast.makeText(this, "Image saved to:\n" +
					// data.getData(), Toast.LENGTH_LONG).show();
				} else if (resultCode == RESULT_CANCELED) {
					// User cancelled the image capture
					Toast.makeText(this, "Image caputure cancelled by user",
							Toast.LENGTH_SHORT).show();
				} else {
					// Image capture failed, advise user
					Toast.makeText(this, "Image caputure failed",
							Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
			Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Snappit/" + Constants.currentUser.getUsername() + "/" + Constants.CURRENT_ALBUM);
		Log.i("Media Storage:", mediaStorageDir.getAbsolutePath());
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("Snappit", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
			mCurrentPhotoPath = mediaFile.getAbsolutePath();

		} else {
			return null;
		}

		return mediaFile;
	}

	/**
	 * Here we store the file url as it will be null after returning from camera
	 * app
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// save file url in bundle as it will be null on scren orientation
		// changes
		outState.putParcelable("file_uri", fileUri);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// get the file url
		fileUri = savedInstanceState.getParcelable("file_uri");
	}

	class UploadImage extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Snappit.this);
			pDialog.setMessage("Uploading Image...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@SuppressWarnings("deprecation")
		@Override
		protected String doInBackground(String... arg0) {
			
			String responseString = null;
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(URL);
			try{
			AndroidMultiPartEntity entity = new AndroidMultiPartEntity(new ProgressListener() {
				
				@Override
				public void transferred(long num) {
					// TODO Auto-generated method stub
					
				}
			});
					
			File sourceFile = new File(mCurrentPhotoPath);
			Log.i("SOURCE FILE PATH:", sourceFile.toString());
			
			// Adding file data to http body
			entity.addPart("image", new FileBody(sourceFile));
			
			// Extra parameters if you want to pass to server
			entity.addPart("username", new StringBody(Constants.currentUser.getUsername()));
			entity.addPart("album", new StringBody(Constants.CURRENT_ALBUM));
			
			long totalSize = entity.getContentLength();
			httppost.setEntity(entity);

			// Making server call
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity r_entity = response.getEntity();

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				// Server response
				responseString = EntityUtils.toString(r_entity);
			} else {
				responseString = "Error occurred! Http Status Code: "
						+ statusCode;
			}
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return responseString;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String result) {
			Log.i("SERVER RESPONSE:", result);
			// dismiss the dialog once done
			pDialog.dismiss();
			Toast toast = Toast.makeText(getApplicationContext(),
					"Image added Successfully!", Toast.LENGTH_SHORT);
			toast.show();
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					new ThumbnailAdapter(getApplicationContext()).notifyDataSetChanged();
					
				}
			});

		}

	}

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
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			thumbnails.setAdapter(new ThumbnailAdapter(Snappit.this));
			

		}
	}
}
