package com.ayushgoyal.snappit.image;

import com.ayushgoyal.snappit.R;
import com.ayushgoyal.snappit.Snappit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class ImageSlideFragment extends Fragment{
	
	int position;
	
	public ImageSlideFragment(int position) {
		this.position=position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.full_image_layout, container, false);
		ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
//		Intent intent = getActivity().getIntent();
//		int position = intent.getIntExtra("position", -1);
//		System.out.println(position);
		if(position!=-1){
//			Toast.makeText(FullScreenImage.this, "Position: "+position+"\nImage URL: "+Snappit.allImages.get(position), Toast.LENGTH_SHORT).show();
			imageView.setImageBitmap(Snappit.allImages.get(position));
			imageView.setScaleType(ScaleType.FIT_XY);
			
			
		}else{
//			Toast.makeText(FullScreenImage.this, "Invalid touch event", Toast.LENGTH_SHORT).show();
		}
		return rootView;
	}

}
