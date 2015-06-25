package com.ayushgoyal.snappit.image;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.ayushgoyal.snappit.R;
import com.ayushgoyal.snappit.Snappit;

public class ImageSlidePagerActivity extends FragmentActivity{
	
	private static int NUM_PAGES = Snappit.allImages.size();
	private static int POSITION = 0;
	
	private ViewPager mPager;
	
	private PagerAdapter mPagerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_slide);
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new ImageSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		Intent intent = getIntent();
		POSITION = intent.getIntExtra("position", -1);
		mPager.setCurrentItem(POSITION);
		
		
	}
	
	@Override
	public void onBackPressed() {
			super.onBackPressed();
	}
	

	private class ImageSlidePagerAdapter extends FragmentStatePagerAdapter{

		public ImageSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
//			position = POSITION;
			Log.i("POSITON NUMBER: ", ""+position);
			return new ImageSlideFragment(position);
		}
		

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return NUM_PAGES;
		}
		
	}

}
