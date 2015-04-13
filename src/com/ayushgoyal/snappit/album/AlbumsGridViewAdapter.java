package com.ayushgoyal.snappit.album;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ayushgoyal.snappit.R;
import com.ayushgoyal.snappit.Snappit;

public class AlbumsGridViewAdapter extends ArrayAdapter {
	private Context context;
	private int layoutResourceId;
	private ArrayList data = new ArrayList();
	ImageItem selectedItem;
	String test[];

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
				Toast.makeText(context, "You Selected the Album: "+position, Toast.LENGTH_LONG).show();
				Intent intent = new Intent(context, Snappit.class);
				intent.putExtra("position", position);
				intent.putExtra("album", item.getTitle());
				context.startActivity(intent);
				
				
			}
		});
		
		return row;
	}

	static class ViewHolder {
		TextView albumName;
		ImageView albumImage;
	}

}
