package com.ayushgoyal.snappit;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.ayushgoyal.snappit.album.Albums;
import com.ayushgoyal.snappit.beans.UserBean;
import com.ayushgoyal.snappit.util.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Signup extends Activity implements OnClickListener {

	private static final String URL = "http://www.ayushgoyal09.com/webservice/add_user.php";
	private EditText username, password, confirmPassword, email;
	String user = "", pass = "", confirmPass = "", emailAddress = "";
	Button signUpButton;
	private static final String TAG_RESULT = "success";
	JSONParser jsonParser = new JSONParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		confirmPassword = (EditText) findViewById(R.id.confirm_password);
		email = (EditText) findViewById(R.id.email);
		signUpButton = (Button) findViewById(R.id.signup_button);
		signUpButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.signup_button:
			if (checkForInputs()) {
				new AddUser().execute();
			}

			break;

		default:
			break;
		}
	}

	private boolean checkForInputs() {
		user = username.getText().toString();
		pass = password.getText().toString();
		emailAddress = email.getText().toString();
		confirmPass = confirmPassword.getText().toString();
		if (user.equals("") || pass.equals("") || confirmPass.equals("")
				|| emailAddress.equals("")) {
			Toast.makeText(getApplicationContext(),
					"All the fields are required", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (!pass.equals(confirmPass)) {
			Toast.makeText(getApplicationContext(),
					"The two passwords do not match", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		return true;
	}

	class AddUser extends AsyncTask<String, String, Integer> {

		@Override
		protected Integer doInBackground(String... arg0) {
			// TODO Auto-generated method stub

			List<NameValuePair> args = new ArrayList<NameValuePair>();

			args.add(new BasicNameValuePair("username", user));
			args.add(new BasicNameValuePair("password", pass));
			args.add(new BasicNameValuePair("email", emailAddress));

			Log.i("SIGN UP", "REQUEST SENT!!!!!!!!!!!");

			Log.d("REQUEST", args.toString());
			JSONObject json = jsonParser.makeHttpRequest(URL, "POST", args);
			int result = 0;
			try {
				result = json.getInt(TAG_RESULT);
				Log.d("result", "is : " + result);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return result;

		}

		protected void onPostExecute(Integer res) {

			switch (res) {
			case 0:
				Toast.makeText(getApplicationContext(),
						"User cannot be added!", Toast.LENGTH_SHORT).show();

				break;

			case 1:
				Intent intent = new Intent(getApplicationContext(),Albums.class);
				UserBean newUser = new UserBean(user, pass, emailAddress);
				Constants.currentUser = newUser;
				intent.putExtra("user", newUser);
				startActivity(intent);

				Toast.makeText(getApplicationContext(),
						"Welcome to Snappit " + user, Toast.LENGTH_SHORT)
						.show();

				break;
			}
		}
	}

}
