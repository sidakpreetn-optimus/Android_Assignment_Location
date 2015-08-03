package com.example.locationdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements TabListener {

	private ActionBar actionbar;
	private ViewPager viewpager;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupTabsAndViewPager();
	}

	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Private method for setting up UI
	 */
	private void setupTabsAndViewPager() {

		actionbar = getSupportActionBar();
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		viewpager = (ViewPager) findViewById(R.id.view_pager);
		viewpager.setAdapter(new MyAdapter(getSupportFragmentManager()));
		viewpager.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageSelected(int arg0) {
				actionbar.setSelectedNavigationItem(arg0);
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			public void onPageScrollStateChanged(int arg0) {

			}
		});
		viewpager.setPageTransformer(true, new ZoomOutPageTransformer());

		Tab tab1 = actionbar.newTab();
		tab1.setText("Search");
		tab1.setTabListener(this);

		Tab tab2 = actionbar.newTab();
		tab2.setText("Location");
		tab2.setTabListener(this);

		Tab tab3 = actionbar.newTab();
		tab3.setText("Account(s)");
		tab3.setTabListener(this);

		actionbar.addTab(tab1);
		actionbar.addTab(tab2);
		actionbar.addTab(tab3);
	}

	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
	}

	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		// syncs viewpager with tabs
		viewpager.setCurrentItem(arg0.getPosition());

	}

	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
	}

}

/**
 * Adapter Class for ViewPager
 */
class MyAdapter extends FragmentPagerAdapter {

	public MyAdapter(FragmentManager fm) {
		super(fm);
	}

	public Fragment getItem(int arg0) {
		Fragment fragment = null;
		if (arg0 == 0) {
			fragment = new Search();
		}
		if (arg0 == 1) {
			fragment = new Locations();
		}
		if (arg0 == 2) {
			fragment = new Accounts();
		}
		return fragment;
	}

	public int getCount() {
		return 3;
	}

}