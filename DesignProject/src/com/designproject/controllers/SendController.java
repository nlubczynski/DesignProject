package com.designproject.controllers;

import com.designproject.R;
import com.designproject.models.Sender;
import com.designproject.models.XMLReaderWriter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParserException;

public class SendController extends NavigationDrawerActivity {

	Sender sender;
	boolean connected;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_sender_view);
		super.onCreate(savedInstanceState);

		SharedPreferences preferences = getSharedPreferences("Connection",
				Context.MODE_PRIVATE);

		String ip = preferences.getString("ip", "");


		int port = preferences.getInt("port", -1);


		if(ip.length() > 0)
			((EditText)findViewById(R.id.ipText)).setText(ip);
		if(port > -1)
			((EditText)findViewById(R.id.portText)).setText(String.valueOf(port));;

		this.setSendButton(false);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (sender != null)
			sender.close();

		TextView connected = (TextView) findViewById(R.id.connectedView);
		connected.setText("Not Connected");
		connected.setTextColor(getResources().getColor(R.color.red));

		super.onDestroy();
	}

	public void connectClick(View view) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;

		String ip = ((EditText) findViewById(R.id.ipText)).getText().toString();
		String port = ((EditText) findViewById(R.id.portText)).getText()
				.toString();
		int portNumb;

		try {
			portNumb = Integer.parseInt(port);
		} catch (Exception e) {
			Toast toast = Toast.makeText(context, "Not Valid port", duration);
			toast.show();
			return;
		}

		IPAddressValidator val = new IPAddressValidator();
		if (!val.validate(ip)) {
			Toast toast = Toast.makeText(context, "Not Valid ip", duration);
			toast.show();
		} else if (portNumb > 65535 || portNumb < 0) {
			Toast toast = Toast.makeText(context, "Not Valid port", duration);
			toast.show();
		} else {
			if (connect(portNumb, ip)) {
				setSendButton(true);
				Toast toast = Toast.makeText(context, "Connection Valid",
						duration);
				toast.show();

				SharedPreferences preferences = getSharedPreferences(
						"Connection", Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putString("ip", ip);
				editor.putInt("port", portNumb);
				editor.commit();
			}
		}
	}

	public void sendClick(View view) {

		if (connected) {
			Context context = getApplicationContext();
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, "Connected, Sending",
					duration);
			toast.show();

			XMLReaderWriter reader;
			try {
				reader = new XMLReaderWriter(getApplicationContext());
			} catch (XmlPullParserException e) {
				toast = Toast.makeText(context, "XmlPullParserException",
						duration);
				toast.show();
				return;
			}
			try {
				sender.RTSPSend(reader.getXML());
			} catch (IOException e) {
				toast = Toast.makeText(context, "IOException", duration);
				toast.show();
				return;
			}

			toast = Toast.makeText(context, "Data Sent!", duration);
			toast.show();
			return;

		} else {

			Context context = getApplicationContext();
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, "Not connected", duration);
			toast.show();
		}

	}

	public void scanClick(View view) {
		Context context = getApplicationContext();
		CharSequence text = "Scan!";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	private boolean connect(int port, String ip) {

		try {
			sender = new Sender(ip, port);
		} catch (Exception e) {
			Context context = getApplicationContext();
			CharSequence text = "Can't Connect";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();

			return false;
		}

		TextView connected = (TextView) findViewById(R.id.connectedView);
		connected.setText("Connected");
		connected.setTextColor(getResources().getColor(R.color.green));

		return true;
	}

	private void setSendButton(boolean set) {
		connected = set;
		Button button = (Button) findViewById(R.id.sendButton);
		button.setEnabled(connected);
	}

	private class IPAddressValidator {

		private Pattern pattern;
		private Matcher matcher;

		private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

		public IPAddressValidator() {
			pattern = Pattern.compile(IPADDRESS_PATTERN);
		}

		/**
		 * Validate ip address with regular expression
		 * 
		 * @param ip
		 *            ip address for validation
		 * @return true valid ip address, false invalid ip address
		 */
		public boolean validate(final String ip) {
			matcher = pattern.matcher(ip);
			return matcher.matches();
		}
	}

}
