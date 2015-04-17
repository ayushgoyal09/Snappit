package com.ayushgoyal.snappit.user.profile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.ayushgoyal.snappit.JSONParser;
import com.ayushgoyal.snappit.R;
import com.ayushgoyal.snappit.album.DeleteAlbum;
import com.ayushgoyal.snappit.dialogs.AlertDialogFragment;
import com.ayushgoyal.snappit.util.Constants;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MyProfile extends Activity implements OnClickListener{
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
		currentPassword  = (EditText) findViewById(R.id.current_password);
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
			
			Toast.makeText(getApplicationContext(),
					"Change passwd", Toast.LENGTH_SHORT).show();
				if(checkPasswordFields()){
					new ModifyPassword().execute(newPasswordText,currentPasswordText);
				}
				
			break;
			
		case R.id.delete_account:
			DialogFragment newFragment = AlertDialogFragment.newInstance(
					"DELETE ACCOUNT?", R.layout.delete_account);
			newFragment.show(getFragmentManager(), "dialog");	
			
			break;

		default:
			break;
		}
		
	}

	private boolean checkPasswordFields() {
		Log.i("Password field Inputs", currentPasswordText+newPasswordText+confirmNewPasswordText);
		if("".equals(currentPasswordText) || "".equals(newPasswordText) || "".equals(confirmNewPasswordText)){
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

	class ModifyPassword extends AsyncTask<String, Void, Integer>{
		
		@Override
		protected Integer doInBackground(String... params) {
			
			List<NameValuePair> args = new ArrayList<NameValuePair>();
			args.add(new BasicNameValuePair("username", Constants.currentUser.getUsername()));
			args.add(new BasicNameValuePair("newPassword", params[0]));
			args.add(new BasicNameValuePair("currentPassword", params[1]));
			
			Log.i("Requesting change of password", "User: "+Constants.currentUser.getUsername()+" New Password: "+params[0]+"Current :"+params[1]);
			Log.i("Password Change Request", args.toString());
			Log.i("Password change url:", Constants.CHANGE_PASSWORD_URL);
			JSONObject json = new JSONParser().makeHttpRequest(Constants.CHANGE_PASSWORD_URL, "POST", args);
			Log.i("Modify Password JSON Request", json.toString());
			
			int result = 0;
			try {
				result = json.getInt(Constants.TAG_SUCCESS);
				Log.d("Modify Password Response", ""+result);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return result;
			
		}
		
	}

}
