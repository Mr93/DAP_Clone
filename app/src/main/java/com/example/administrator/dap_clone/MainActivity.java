package com.example.administrator.dap_clone;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.dap_clone.CustomView.BottomNavigationViewHelper;
import com.example.administrator.dap_clone.SettingsManager.SettingsActivity;

import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

	private static final String TAG = MainActivity.class.getSimpleName();
	private ViewPager viewPager;
	private BottomNavigationView bottomNavigationView;
	private static final String POSITION = "POSITION";
	private int[] menuBottomNavigationBarId = {
			R.id.download_bottom_menu,
			R.id.upload_bottom_menu,
			R.id.folder_bottom_menu,
			R.id.setting_bottom_menu
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), this));
		bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
		bottomNavigationView.setOnNavigationItemSelectedListener(this);
		BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
		viewPager.addOnPageChangeListener(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.download_bottom_menu:
				viewPager.setCurrentItem(0);
				break;
			case R.id.upload_bottom_menu:
				viewPager.setCurrentItem(1);
				break;
			case R.id.folder_bottom_menu:
				viewPager.setCurrentItem(2);
				break;
			case R.id.setting_bottom_menu:
				viewPager.setCurrentItem(3);
				break;
			default:
				viewPager.setCurrentItem(0);
				break;
		}
		return true;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		bottomNavigationView.setSelectedItemId(menuBottomNavigationBarId[position]);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}
}
