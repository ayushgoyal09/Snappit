package com.ayushgoyal.snappit.album;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayushgoyal.snappit.R;

public class AlbumsGridViewAdapter extends ArrayAdapter {
	private Context context;
	private int layoutResourceId;
	private ArrayList data = new ArrayList();

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
	public View getView(int position, View convertView, ViewGroup parent) {
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
		return row;
	}

	static class ViewHolder {
		TextView albumName;
		ImageView albumImage;
	}

}
