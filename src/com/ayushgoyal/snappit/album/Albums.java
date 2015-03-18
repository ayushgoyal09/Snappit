package com.ayushgoyal.snappit.album;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.ayushgoyal.snappit.R;
import com.ayushgoyal.snappit.beans.AlbumBean;
import com.ayushgoyal.snappit.beans.UserBean;
import com.ayushgoyal.snappit.util.Constants;

public class Albums extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("Checkpoint", "2");
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		UserBean user = (UserBean) intent.getSerializableExtra("user");
		setContentView(R.layout.display_albums);
		GridView gridView = (GridView) findViewById(R.id.albums_grid);
		AlbumsGridViewAdapter gridViewAdapter = new AlbumsGridViewAdapter(this,
				R.layout.album_grid_item_layout, getData());
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
			Toast.makeText(getApplicationContext(), "Add an album", Toast.LENGTH_SHORT).show();
			break;

		default: return super.onOptionsItemSelected(item);
			
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
