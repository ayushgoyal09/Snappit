package com.ayushgoyal.snappit.album;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

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

//		new GetAlbums().execute(user);
	}

	private ArrayList<ImageItem> getData() {
		ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
		Bitmap Test1 = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		for(AlbumBean album: Constants.ALBUM_LIST){
			imageItems.add(new ImageItem(Test1, album.getName()));
		}
		
		
		
		
		return imageItems;
	}

	

}
