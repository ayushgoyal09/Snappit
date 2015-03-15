package com.ayushgoyal.snappit.image;

import com.ayushgoyal.snappit.R;
import com.ayushgoyal.snappit.Snappit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class FullScreenImage extends Activity{
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.image_full_screen);
	ImageView imageView = (ImageView) findViewById(R.id.imageView);
	
	Intent intent = getIntent();
	int position = intent.getIntExtra("position", -1);
	if(position!=-1){
		Toast.makeText(FullScreenImage.this, "Position: "+position+"\nImage URL: "+Snappit.allImages.get(position), Toast.LENGTH_SHORT).show();
		imageView.setImageBitmap(Snappit.allImages.get(position));
		imageView.setScaleType(ScaleType.FIT_XY);
		
		
	}else{
		Toast.makeText(FullScreenImage.this, "Invalid touch event", Toast.LENGTH_SHORT).show();
	}
}
}
