package com.ayushgoyal.snappit.album;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.ayushgoyal.snappit.R;
import com.ayushgoyal.snappit.beans.AlbumBean;
import com.ayushgoyal.snappit.beans.UserBean;
import com.ayushgoyal.snappit.dialogs.AlertDialogFragment;
import com.ayushgoyal.snappit.user.profile.MyProfile;
import com.ayushgoyal.snappit.util.Constants;

public class Albums extends Activity {

	public static ArrayList<ImageItem> albums = new ArrayList<ImageItem>();
	GridView gridView;
	public static AlbumsGridViewAdapter gridViewAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("Checkpoint", "2");
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		UserBean user = (UserBean) intent.getSerializableExtra("user");
		setContentView(R.layout.display_albums);
		gridView = (GridView) findViewById(R.id.albums_grid);
		albums = getData();
		gridViewAdapter = new AlbumsGridViewAdapter(this,
				R.layout.album_grid_item_layout, albums);
		gridView.setAdapter(gridViewAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_albums, menu);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_add:
			DialogFragment newFragment = AlertDialogFragment.newInstance(
					"Add Album", R.layout.add_album_dialog);
			newFragment.show(getFragmentManager(), "dialog");
			break;
			
		case R.id.action_profile:
			Intent intent = new Intent(getApplicationContext(), MyProfile.class);
			startActivity(intent);
			break;
			
		case R.id.action_sort:
			Toast.makeText(getApplicationContext(), "SORT", Toast.LENGTH_SHORT).show();
			Collections.sort(albums);
			Albums.gridViewAdapter.notifyDataSetChanged();
			break;
		
		default:
			return super.onOptionsItemSelected(item);

		}
		return super.onOptionsItemSelected(item);

	}

	private ArrayList<ImageItem> getData() {
		ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
		// Bitmap Test1 = BitmapFactory.decodeResource(getResources(),
		// R.drawable.ic_launcher);
		for (AlbumBean album : Constants.ALBUM_LIST) {
			imageItems
					.add(new ImageItem(album.getAlbumCover(), album.getName()));
		}

		return imageItems;
	}

}
