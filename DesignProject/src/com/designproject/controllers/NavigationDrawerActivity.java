package com.designproject.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.designproject.R;
import com.designproject.models.HelperMethods;

public class NavigationDrawerActivity extends SherlockActivity {

	private String[] mDrawerListTitles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;

	private CharSequence mTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated constructor stub
		super.onCreate(savedInstanceState);

		// Get the titles of navigation drawer items
		mDrawerListTitles = getResources().getStringArray(
				R.array.drawer_list_options);

		// Get the necessary view items
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Get title of activity
		mTitle = getTitle();

		// Set the adapter for the list view through custom adapter
		AdapterClass adpClass = new AdapterClass(this, mDrawerListTitles);
		mDrawerList.setAdapter(adpClass);

		// Bind a listener to the drawer list
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// Get the action bar-using action bar sherlock as the support library
		ActionBar actionBar = getSupportActionBar();

		// Inflate a custom view
		View actionBarView = getLayoutInflater().inflate(
				R.layout.action_bar_layout, null);

		actionBar.setCustomView(actionBarView);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		((TextView) actionBarView.findViewById(R.id.navDrawerTitle))
				.setText(mTitle);

		// disable all default options
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	/**
	 * listener for the nav icon to open/close navigation drawer
	 * 
	 * @param view
	 */
	public void navDrawerButtonListener(View view) {

		if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			mDrawerLayout.openDrawer(mDrawerList);
		}
	}

	/**
	 * Listener for the logo to open/close navigation drawer
	 * 
	 * @param view
	 */
	public void navDrawerLogoButtonListener(View view) {

		if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			mDrawerLayout.openDrawer(mDrawerList);
		}
	}

	private void loadSettingsPage() {
		Intent openSettingsPage = new Intent(NavigationDrawerActivity.this,
				SettingsController.class);
		startActivity(openSettingsPage);
	}

	private void loadConnectionSettingsPage() {
		Intent openSettingsPage = new Intent(NavigationDrawerActivity.this,
				SendController.class);
		startActivity(openSettingsPage);
	}

	/* The click listener for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	/**
	 * Custom adapter class to build list view data
	 * 
	 * @author BradenRules
	 * 
	 */
	public class AdapterClass extends ArrayAdapter<String> {
		Context context;
		private String[] TextValue;

		public AdapterClass(Context context, String[] mDrawerListTitles) {
			super(context, R.layout.drawer_list_item, mDrawerListTitles);
			this.context = context;
			this.TextValue = mDrawerListTitles;

		}

		/**
		 * Gets the view and sets the text and icons for each navigation drawer
		 * item
		 */
		@Override
		public View getView(int position, View coverView, ViewGroup parent) {
			// TODO Auto-generated method stub

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.drawer_list_item, parent,
					false);

			TextView text1 = (TextView) rowView.findViewById(R.id.txtNavDrawer);

			// the position corresponds to the text that is outputted
			text1.setText(TextValue[position]);
			ImageView image = (ImageView) rowView
					.findViewById(R.id.imageViewNavDrawer);

			// Set icons for each navigation drawer item
			if (position == 0)
				image.setImageResource(R.drawable.ic_client_light);
			else if (position == 1)
				image.setImageResource(R.drawable.ic_tab_spec_selected);
			else if (position == 2)
				image.setImageResource(R.drawable.ic_inspection_light);
			else if (position == 3)
				image.setImageResource(R.drawable.ic_account_settings_light);
			else if (position == 4)
				image.setImageResource(R.drawable.ic_connection_settings_light);
			else if (position == 5)
				image.setImageResource(R.drawable.ic_logout_light);

			return rowView;
		}
	}

	private void displayClients() {
		Intent displayClientList = new Intent(NavigationDrawerActivity.this,
				DisplayListActivity.class);
		startActivity(displayClientList);
	}

	private void displayLocations() {
		Intent displayLocationList = new Intent(NavigationDrawerActivity.this,
				DisplayLocationsActivity.class);
		startActivity(displayLocationList);
	}

	private void displayInspections() {
		Intent openInspectionView = new Intent(NavigationDrawerActivity.this,
				InspectionController.class);
		startActivity(openInspectionView);
	}

	private void selectItem(int position) {
		// update the main content by redirecting to pages
		// position is the 0-based placement in the toolbox
		switch (position) {

		// display list of clients
		case 0:
			displayClients();
			break;
		// display list of locations
		case 1:
			displayLocations();
			break;
		// display list of inspections
		case 2:
			displayInspections();
			break;
		// settings
		case 3:
			loadSettingsPage();
			break;
		case 4:
			loadConnectionSettingsPage();
			break;
		// logout
		case 5:
			signOut();
			break;
		}
	}

	public void signOut() {
		// Replace current code with LogOutHelper code
		HelperMethods.logOutHandler(HelperMethods.LOGOUT, this);
	}

}
