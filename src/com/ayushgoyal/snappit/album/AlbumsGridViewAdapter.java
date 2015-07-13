package com.ayushgoyal.snappit.album;

import java.util.ArrayList;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ayushgoyal.snappit.R;
import com.ayushgoyal.snappit.Snappit;
import com.ayushgoyal.snappit.beans.AlbumBean;
import com.ayushgoyal.snappit.dialogs.AlertDialogFragment;
import com.ayushgoyal.snappit.util.Constants;

public class AlbumsGridViewAdapter extends ArrayAdapter {
	private Context context;
	private int layoutResourceId;
	private ArrayList data = new ArrayList();
	public static ImageItem selectedItem;
	

	String test[];
	Albums albumActivity;
	ActionMode mActionMode;
	ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		// Called each time the action mode is shown. Always called after
		// onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}

		// Called when the user exits the action mode
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// TODO Auto-generated method stub
			mActionMode = null;
		}

		// Called when the action mode is created; startActionMode() was
		// called
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_action_albums, menu);
			return true;

		}

		// Called when the user selects a contextual menu item
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.action_delete:
				DialogFragment deleteAlbumFragment = AlertDialogFragment.newInstance("Delete Album "+selectedItem.getTitle(), R.layout.delete_album_dialog);
				deleteAlbumFragment.show(albumActivity.getFragmentManager(), "dialog");
				mode.finish();
				return true;
				
//			case R.id.action_edit:
//				Log.i("SELECTED ITEM:", selectedItem.getTitle());
//				Constants.RENAME_ITEM = selectedItem.getTitle();
//				Log.i("RENAME ITEM:", selectedItem.getTitle());
//				DialogFragment newFragment = AlertDialogFragment.newInstance("Rename Album", R.layout.rename_album_dialog);
//				newFragment.show(albumActivity.getFragmentManager(), "dialog");
////				break;
//				
////				new RenameAlbum().execute(selectedItem.getTitle(),"renamed");
////				ArrayList<String> names = new ArrayList<String>();
////				for(AlbumBean bean: Constants.ALBUM_LIST){
////					names.add(bean.getName());
////				}
////				Log.i("ALBUMS LIST GLOBAL:", names.toString());
//				mode.finish();
//				return true;

			default:
				return false;
			}
		}
	};

	/**
	 * @param context
	 * @param layoutResourceId
	 * @param data
	 */
	public AlbumsGridViewAdapter(Context context, int layoutResourceId,
			ArrayList data) {
		super(context, layoutResourceId, data);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.data = data;
		albumActivity = (Albums) context;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new ViewHolder();
			holder.albumImage = (ImageView) row.findViewById(R.id.album_image);
			holder.albumName = (TextView) row.findViewById(R.id.album_name);
			row.setTag(holder);

		} else {
			holder = (ViewHolder) row.getTag();
		}

		ImageItem item = (ImageItem) data.get(position);
		holder.albumName.setText(item.getTitle());
		holder.albumImage.setImageBitmap(item.getImage());

		selectedItem = item;

		row.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageItem item = (ImageItem) data.get(position);
				Toast.makeText(context, "You Selected the Album: " + item.getTitle(),
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(context, Snappit.class);
				intent.putExtra("position", position);
				intent.putExtra("album", item.getTitle());
				context.startActivity(intent);

			}
		});

		row.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				selectedItem = (ImageItem) data.get(position);					
				Log.i("SELECTED ITEM:", selectedItem.getTitle());
				albumActivity.startActionMode(mActionModeCallback);
				return true;
			}
		});

		return row;
	}

	static class ViewHolder {
		TextView albumName;
		ImageView albumImage;
	}

}
