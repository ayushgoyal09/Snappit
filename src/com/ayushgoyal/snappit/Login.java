package com.ayushgoyal.snappit;

import java.util.ArrayList;
import java.util.List;

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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ayushgoyal.snappit.album.Albums;
import com.ayushgoyal.snappit.beans.AlbumBean;
import com.ayushgoyal.snappit.beans.UserBean;
import com.ayushgoyal.snappit.dialogs.AlertDialogFragment;
import com.ayushgoyal.snappit.util.ConnectionDetector;
import com.ayushgoyal.snappit.util.Constants;
import com.ayushgoyal.snappit.util.ResourceUtil;

public class Login extends Activity implements OnClickListener {
	private Button loginButton;
	private EditText username, password;
	private UserBean user = new UserBean();
	private ProgressDialog pDialog;
	private TextView signUp;
	private static final String LOGIN_URL = "http://www.ayushgoyal09.com/webservice/login.php";
	private static final String TAG_RESULT = "result";
	JSONParser jsonParser = new JSONParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			DialogFragment newFragment = AlertDialogFragment
					.newInstance("Internet Connection Error",
							R.layout.internet_error_dialog);
			newFragment.show(getFragmentManager(), "dialog");
		}

		loginButton = (Button) findViewById(R.id.login_button);
		loginButton.setOnClickListener(this);
		signUp = (TextView) findViewById(R.id.sign_up);
		signUp.setOnClickListener(this);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.login_button:
			user.setUsername(username.getText().toString());
			user.setPassword(password.getText().toString());
			if (checkForCredentials()) {
				new AttemptLogin().execute();
			}
			break;

		case R.id.sign_up:
			Intent intent = new Intent(this, Signup.class);
			startActivity(intent);
			break;
		}

	}

	public boolean checkForCredentials() {
		Log.d("Credentials", "username :" + user.getUsername() + "\npassword :"
				+ user.getPassword());
		// Log.d("IP Address", myIP);
		if (user.getUsername().equals("") && user.getPassword().equals("")) {
			Toast.makeText(getApplicationContext(),
					"Username and password cannot be left blank!",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (user.getUsername().equals("")) {
			Toast.makeText(getApplicationContext(),
					"Please enter the username", Toast.LENGTH_SHORT).show();
			return false;
		} else if (user.getPassword().equals("")) {
			Toast.makeText(getApplicationContext(),
					"Please enter the password", Toast.LENGTH_SHORT).show();
			return false;
		} else
			return true;
	}

	class AttemptLogin extends AsyncTask<String, String, Integer> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Attempting login...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub

			int result = 0;
			try {
				List<NameValuePair> args = new ArrayList<NameValuePair>();
				args.add(new BasicNameValuePair("username", user.getUsername()));
				args.add(new BasicNameValuePair("password", user.getPassword()));
				Log.d("request!",
						"starting" + user.getUsername() + user.getPassword());
				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",
						args);
				// check your log for json response
				Log.d("Login attempt", json.toString());
				// json success tag

				result = json.getInt(TAG_RESULT);
				Log.d("result", "is : " + result);
				if (result == 1) {
					Log.d("Login Successful!", json.toString());
					return 1;
				} else if (result == 0) {
					Log.d("Login Failure", json.toString());
					return 0;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return 0;
		}

		protected void onPostExecute(Integer res) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();
			switch (res) {
			case 0:
				Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_LONG)
						.show();
				break;

			case 1:

				Toast.makeText(Login.this, "Welcome " + user.getUsername(),
						Toast.LENGTH_LONG).show();
				new GetAlbums().execute(user);
				Constants.currentUser = user;
				Log.i("Current User", Constants.currentUser.getUsername());
				// Intent intent = new Intent(getApplicationContext(),
				// Snappit.class);
				// startActivity(intent);
				break;
			}

		}

	}

	class GetAlbums extends AsyncTask<UserBean, String, Void> {
		ProgressDialog pDialog = new ProgressDialog(Login.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setMessage("Getting your Albums...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(UserBean... users) {
			List<NameValuePair> args = new ArrayList<NameValuePair>();
			List<AlbumBean> albumBeans = new ArrayList<AlbumBean>();
			args.add(new BasicNameValuePair("users_id", users[0].getUsername()));
			Log.i("ALBUM REQUEST FOR USER : ", users[0].getUsername());
			JSONObject json = new JSONParser().makeHttpRequest(
					Constants.GET_ALBUMS_URL, "POST", args);
			Log.i("ALBUMS", json.toString());
			int success = 0;
			try {
				success = json.getInt(Constants.TAG_SUCCESS);
				if (success == 1) {
					JSONArray albumsArray = json.getJSONArray("albums");
					for (int i = 0; i < albumsArray.length(); i++) {
						JSONObject album = albumsArray.getJSONObject(i);
						Constants.ALBUM_LIST.add(new AlbumBean(album
								.getString("id"), album.getString("name"),
								ResourceUtil.getBitmapFromURL(album
										.getString("cover"))));
						// Constants.ALBUM_LIST.add(new
						// AlbumBean(album.getString("id"),
						// album.getString("name")));
					}

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("Checkpoint", "1");
			Log.i("Success", "" + success);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			pDialog.dismiss();
			Intent intent = new Intent(getApplicationContext(), Albums.class);
			intent.putExtra("user", user);
			startActivity(intent);

		}

	}
}
