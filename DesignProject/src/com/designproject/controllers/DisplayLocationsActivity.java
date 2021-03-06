package com.designproject.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.designproject.FireAlertApplication;
import com.designproject.R;
import com.designproject.models.Building;
import com.designproject.models.Client;
import com.designproject.models.Contract;
import com.designproject.models.Franchise;

/**
 * 
 * @author Jess
 * 
 */
public class DisplayLocationsActivity extends NavigationDrawerActivity {

	private Franchise theFranchise;
	private Client[] clients;
	private Contract[] contracts;
	private Building[] buildings;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// Content View must be set before making call to
		// NavigationDrawerActivity
		setContentView(R.layout.activity_display_locations);
		super.onCreate(savedInstanceState);

		// Get listview from XML
		ListView list = (ListView) findViewById(R.id.android_locationlist);

		// Create a container for the double itemed list
		List<Map<String, String>> locationClientList = new ArrayList<Map<String, String>>();

		// Get the application context
		FireAlertApplication a = (FireAlertApplication) getApplication();
		a = (FireAlertApplication) getApplication();
		theFranchise = (Franchise) a.getFranchise();
		clients = theFranchise.getClients();

		// Add the location and client name for all contracts to the map
		for (Client theClient : clients) {
			contracts = theClient.getContracts();

			for (Contract theContract : contracts) {
				buildings = theContract.getBuildings();

				for (Building theBuilding : buildings) {
					Map<String, String> pairList = new HashMap<String, String>(
							2);
					pairList.put("location", theBuilding.getAddress());
					pairList.put("client", theClient.getName());
					locationClientList.add(pairList);
				}
			}
		}

		// Bind the map to the adapter
		SimpleAdapter adapter = new SimpleAdapter(this, locationClientList,
				android.R.layout.simple_list_item_2, new String[] { "location",
						"client" }, new int[] { android.R.id.text1,
						android.R.id.text2 });

		list.setAdapter(adapter);
	}
}
