package com.ayushgoyal.snappit.dialogs;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ayushgoyal.snappit.R;
import com.ayushgoyal.snappit.album.Albums;
import com.ayushgoyal.snappit.album.ImageItem;
import com.ayushgoyal.snappit.util.Constants;

public class AlertDialogFragment extends DialogFragment{
	
	public static AlertDialogFragment newInstance(String title,int layout){
		AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("layout", layout);
        frag.setArguments(args);
        return frag;
        
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = getArguments().getString("title");
		final int layout = getArguments().getInt("layout");
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(layout, null);
		
		return new AlertDialog.Builder(getActivity())
				.setView(view)
				.setIcon(R.drawable.ic_add_album)
				.setTitle(title)
				.setPositiveButton(R.string.alert_dialog_ok,new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onClickPositiveButton();
						
					}

					private void onClickPositiveButton() {
						switch (layout) {
						case R.layout.add_album_dialog:
							//Create album icon with default image cover and user entered name.
							EditText editText = (EditText) view.findViewById(R.id.new_album_name);
							String albumName = editText.getText().toString();
							Albums.albums.add(new ImageItem(BitmapFactory.decodeResource(getResources(), R.drawable.ic_add_album), albumName));
							Albums.gridViewAdapter.notifyDataSetChanged();
							
							//Create an empty directory with album name
							File albumLocalDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/"+Constants.APP_NAME+"/"+albumName);
							Log.i("ALBUM PATH: ", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/"+Constants.APP_NAME+"/"+albumName);
							if(!albumLocalDirectory.exists()){
								albumLocalDirectory.mkdir();
							}
							
							//Save new album information to database.
							
							
							
							break;

						default:
							break;
						}
						
					}
				})
				.setNegativeButton(R.string.alert_dialog_cancel, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onClickNegativeButton();
						
					}

					private void onClickNegativeButton() {
						switch (layout) {
						case R.layout.add_album_dialog:
							Toast.makeText(getActivity(), "CANCEL", Toast.LENGTH_SHORT).show();;
							break;

						default:
							break;
						}
					}
				})
				.create();
	}

}
