package com.ayushgoyal.snappit.image;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.ayushgoyal.snappit.AndroidMultiPartEntity;
import com.ayushgoyal.snappit.AndroidMultiPartEntity.ProgressListener;
import com.ayushgoyal.snappit.util.Constants;



public class UploadSyncUpImages extends AsyncTask<String, String, String> {

	private static final String URL = "http://www.ayushgoyal09.com/webservice/fileUpload2.php";
	ProgressDialog pDialog;
	Activity activity;
	
	public UploadSyncUpImages(Activity activity){	
		this.activity = activity;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		pDialog = new ProgressDialog(activity);
		pDialog.setMessage("Upload Image in progress...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.show();
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(String... arg0) {
		
		
		Log.i("album:", arg0[0]);
		Log.i("File path:", arg0[1]);
		
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
				
		File sourceFile = new File(arg0[1]);
		Log.i("SOURCE FILE PATH:", sourceFile.toString());
		
		// Adding file data to http body
		entity.addPart("image", new FileBody(sourceFile));
		
		// Extra parameters if you want to pass to server
		entity.addPart("username", new StringBody(Constants.currentUser.getUsername()));
		entity.addPart("album", new StringBody(arg0[0]));
		
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
		Log.i("SERVER RESPONSE:", "DONEEEEEEEEEEEE");
		// dismiss the dialog once done
		pDialog.dismiss();
		Toast toast = Toast.makeText(activity,
				"Image synced up Successfully!", Toast.LENGTH_SHORT);
		toast.show();
//		new DisplayImages().execute();
//		runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				new ThumbnailAdapter(getApplicationContext()).notifyDataSetChanged();
//				
//			}
//		});
		

	}

}