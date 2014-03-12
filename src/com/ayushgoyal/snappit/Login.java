package com.ayushgoyal.snappit;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener {
	private Button loginButton;
	private EditText username, password;
	private String user, pass;
	private ProgressDialog pDialog;

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

	JSONParser jsonParser = new JSONParser();
	private static final String LOGIN_URL = "http://192.168.1.4/webservice/login.php";
	private static final String TAG_RESULT = "result";
	private static final String TAG_MESSAGE = "message";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		loginButton = (Button) findViewById(R.id.login_button);
		loginButton.setOnClickListener(this);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		user = username.getText().toString();
		pass = password.getText().toString();
		if (checkForCredentials()) {
			Toast.makeText(getApplicationContext(), "COOL", Toast.LENGTH_SHORT)
					.show();
			new AttemptLogin().execute();
		}
	}

	public boolean checkForCredentials() {
		Log.d("Credentials", "username :" + user + "\npassword :" + pass);
		if (user.equals("") && pass.equals("")) {
			Toast.makeText(getApplicationContext(),
					"Username and password cannot be left blank!",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (user.equals("")) {
			Toast.makeText(getApplicationContext(),
					"Please enter the username", Toast.LENGTH_SHORT).show();
			return false;
		} else if (pass.equals("")) {
			Toast.makeText(getApplicationContext(),
					"Please enter the password", Toast.LENGTH_SHORT).show();
			return false;
		} else
			return true;
	}

	class AttemptLogin extends AsyncTask<String, String, String> {

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
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			int result = 0;
			try {
				List<NameValuePair> args = new ArrayList<NameValuePair>();
				args.add(new BasicNameValuePair("username", user));
				args.add(new BasicNameValuePair("password", pass));
				Log.d("request!", "starting" + user + pass);
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
					return json.getString(TAG_MESSAGE);
				} else if (result == 0) {
					Log.d("Login Failure", json.toString());
					return json.getString(TAG_MESSAGE);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();
			if (file_url != null) {
				Toast.makeText(Login.this, file_url, Toast.LENGTH_LONG).show();
			}

		}

	}
}
