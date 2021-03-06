package com.designproject.controllers;

import com.designproject.FireAlertApplication;
import com.designproject.R;
import com.designproject.models.Equipment;
import com.designproject.models.HelperMethods;
import com.designproject.models.Room;

import android.os.Bundle;
import android.app.ListActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class RoomController extends NavigationDrawerActivity {

	private String ACTION_CONTENT_NOTIFY = "android.intent.action.CONTENT_NOTIFY";
	private DataReceiver dataScanner = null;
	private EditText editText;
	private String IDvalue = "";
	private Room mRoom;
	private Equipment[] equipment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Check if Logged in
		HelperMethods.logOutHandler(HelperMethods.CHECK_IF_LOGGED_IN, this);

		setContentView(R.layout.activity_equipment_inspection_list);
		super.onCreate(savedInstanceState);

		//Get application and location
		FireAlertApplication a = (FireAlertApplication) getApplication();
		a = (FireAlertApplication) getApplication();
		mRoom = (Room) a.getLocation();
		equipment = mRoom.getEquipment();

		ListView myList=(ListView)findViewById(android.R.id.list);
		myList.setAdapter(new ArrayAdapter<Equipment>(this,
				R.layout.equipment_list_item, equipment));

		myList.setTextFilterEnabled(true);

		myList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				FireAlertApplication app = (FireAlertApplication) getApplication();
				app.setLocation(equipment[position]);
				Intent inspectionForm = new Intent(RoomController.this,
						EquipmentController.class);
				inspectionForm.putExtra("Page Number", 1);
				startActivity(inspectionForm);
			}
		});

		// how many pieces of equipment in the room
		final int length = equipment.length;

		// create button to select equipment for inspection
		Button okButton = (Button) findViewById(R.id.OK);

		// set action listener for 'Select' button
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// assign String from editText to IDvalue
				IDvalue = editText.getText().toString();

				// find which equipment has an id that matches IDvalue
				// move to appropriate inspection form
				for (int i = 0; i < length; i++) {
					if (IDvalue.equals(equipment[i].getID())) {
						FireAlertApplication a = (FireAlertApplication) getApplication();
						a.setLocation(equipment[i]);
						Intent inspectionForm = new Intent(RoomController.this,
								EquipmentController.class);
						inspectionForm.putExtra("Page Number", 1);
						startActivity(inspectionForm);
						break;
					}
				}
			}
		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	// given Scanner code
	protected void onResume() {
		// Check if Logged in
		HelperMethods.logOutHandler(HelperMethods.CHECK_IF_LOGGED_IN, this);

		registerScanner();
		initialComponent();
		super.onResume();

		// Set the location back to the room
		FireAlertApplication a = (FireAlertApplication) getApplication();
		a.setLocation(mRoom);
		ListView myList=(ListView)findViewById(android.R.id.list);
		myList.post(new Runnable() {
			 @Override
			 public void run() {
			 	updateStatus();
			 }
		});
	}

	private void updateStatus() {
		
		for (int index = 0; index < mRoom.getEquipment().length; index++) {
			if (mRoom.getEquipment()[index].isCompleted()) {
				ListView myList=(ListView)findViewById(android.R.id.list);
				if (myList.getChildCount() > 0) {
					TextView toColour = (TextView) myList.getChildAt(index);
					toColour.setTextColor(getResources()
							.getColor(R.color.green));
				}
			}
		}
	}

	// given Scanner code
	@Override
	protected void onDestroy() {
		unregisterReceiver();
		super.onResume();
		FireAlertApplication a = (FireAlertApplication) getApplication();
		a.setLocation(mRoom);
		super.onDestroy();
	}

	// given Scanner code
	private void initialComponent() {
		editText = (EditText) findViewById(R.id.editText);
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		editText.addTextChangedListener(textWatcher);
	}

	// given Scanner code
	private void registerScanner() {
		if (dataScanner == null) {
			dataScanner = new DataReceiver();
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(ACTION_CONTENT_NOTIFY);
			registerReceiver(dataScanner, intentFilter);
		}
	}

	// given Scanner code
	private void unregisterReceiver() {
		if (dataScanner != null) {
			unregisterReceiver(dataScanner);
			dataScanner = null;
		}
	}

	// given Scanner code
	// catches if text is entered (i.e. from scanner)
	private TextWatcher textWatcher = new TextWatcher() {
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void afterTextChanged(Editable s) {
			String textEntered = editText.getText().toString();
			if (textEntered != null && !(textEntered.isEmpty())) {
				// check if entered value is a valid id
				for (final Equipment item : equipment) {
					if (item.getID().equals(textEntered))
					{
						FireAlertApplication app = (FireAlertApplication) getApplication();
						app.setLocation(item);
						Intent inspectionForm = new Intent(RoomController.this,
								EquipmentController.class);
						inspectionForm.putExtra("Page Number", 1);
						startActivity(inspectionForm);
					}
				}
			}
		}
	};

	// given Scanner code
	private class DataReceiver extends BroadcastReceiver {
		String content = "";

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION_CONTENT_NOTIFY)) {
				Bundle bundle = new Bundle();
				bundle = intent.getExtras();

				// data that Scanner picks up is assigned to "content"
				// EditText that has <requestFocus/> tag will be updated with
				// "content" value
				content = bundle.getString("CONTENT");

			}
		}
	}

	// from the link above
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i("debugger", "Onconfigurationchanged called");
		super.onConfigurationChanged(newConfig);
	}
}
