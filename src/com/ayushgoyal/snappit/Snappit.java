package com.ayushgoyal.snappit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

import org.apache.http.NameValuePair;
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

import com.ayushgoyal.snappit.adapters.ThumbnailAdapter;
import com.ayushgoyal.snappit.image.FullScreenImage;
import com.ayushgoyal.snappit.image.ImageSlidePagerActivity;

public class Snappit extends Activity implements OnClickListener {

	private ProgressDialog pDialog;
	private static final String URL = "http://www.ayushgoyal09.com/webservice/upload_image.php";
	private static final String URL_get_imagesList = "http://www.ayushgoyal09.com/webservice/get_all_images.php";
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
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"Snappit");
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

		@Override
		protected String doInBackground(String... arg0) {
			HttpURLConnection connection = null;
			DataOutputStream outputStream = null;
			DataInputStream inputStream = null;
			String lineEnd = "\r\n";
			String twoHyphens = "--";
			String boundary = "*****";
			int bytesRead, bytesAvailable, bufferSize;
			byte[] buffer;
			int maxBufferSize = 1 * 1024 * 1024;

			try {
				FileInputStream fileInputStream = new FileInputStream(
						mCurrentPhotoPath);
				URL url = new URL(URL);
				connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);
				// Set HTTP method to POST.
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Connection", "Keep-Alive");
				connection.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				outputStream = new DataOutputStream(
						connection.getOutputStream());
				outputStream.writeBytes(twoHyphens + boundary + lineEnd);
				outputStream
						.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
								+ mCurrentPhotoPath + "\"" + lineEnd);
				outputStream.writeBytes(lineEnd);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];
				// Read file
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				while (bytesRead > 0) {
					outputStream.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}

				outputStream.writeBytes(lineEnd);
				outputStream.writeBytes(twoHyphens + boundary + twoHyphens
						+ lineEnd);

				// Responses from the server (code and message)
				int serverResponseCode = connection.getResponseCode();
				String serverResponseMessage = connection.getResponseMessage();
				Log.i("Response iMage", serverResponseMessage + "  "
						+ serverResponseCode);

				fileInputStream.close();
				outputStream.flush();
				outputStream.close();

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

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
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
				JSONObject json = new JSONParser().makeHttpRequest(
						URL_get_imagesList, "GET", args);
				Log.i("Output", json.toString());

				int success = 1;
				if (success == 1) {
					images = json.getJSONArray(TAG_IMAGES);
					for (int i = 0; i < images.length(); i++) {
						JSONObject device = images.getJSONObject(i);
						String name = UPLOADS_FOLDER+device.getString("name");
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
