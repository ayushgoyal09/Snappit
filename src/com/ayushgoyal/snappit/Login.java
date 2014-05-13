package com.ayushgoyal.snappit;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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

public class Login extends Activity implements OnClickListener {
	private Button loginButton;
	private EditText username, password;
	private String user, pass;
	private ProgressDialog pDialog;
	private TextView signUp;
//	private static final String LOGIN_URL = "http://192.168.1.4/webservice/login.php";
	private static final String LOGIN_URL = "http://www.ayushgoyal09.com/webservice/login.php";
	private static final String TAG_RESULT = "result";
	JSONParser jsonParser = new JSONParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
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
			user = username.getText().toString();
			pass = password.getText().toString();
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
		Log.d("Credentials", "username :" + user + "\npassword :" + pass);
		// Log.d("IP Address", myIP);
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
					return 1;
				} else if (result == 0) {
					Log.d("Login Failure", json.toString());
					return 0;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
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
				Intent intent = new Intent(getApplicationContext(),
						Snappit.class);
				startActivity(intent);
				Toast.makeText(Login.this, "Welcome "+user, Toast.LENGTH_LONG).show();
				break;
			}

		}

	}
}
