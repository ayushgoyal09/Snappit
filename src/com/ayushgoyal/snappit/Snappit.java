package com.ayushgoyal.snappit;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Snappit extends Activity implements OnClickListener {

	private static final int MEDIA_TYPE_IMAGE = 1;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static String mCurrentPhotoPath; // File path to the last image captured
	private Uri fileUri;
	private Button takePictureButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);
		takePictureButton = (Button) findViewById(R.id.camera_button);
		takePictureButton.setOnClickListener(this);

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

}
