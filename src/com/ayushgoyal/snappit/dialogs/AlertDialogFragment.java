package com.ayushgoyal.snappit.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.ayushgoyal.snappit.R;

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
		
		return new AlertDialog.Builder(getActivity())
				.setView(inflater.inflate(layout, null))
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
							Toast.makeText(getActivity(), "OK", Toast.LENGTH_SHORT).show();;
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
