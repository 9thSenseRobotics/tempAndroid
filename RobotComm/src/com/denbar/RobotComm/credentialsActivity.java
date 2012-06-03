package com.denbar.RobotComm;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class credentialsActivity extends Activity {

	private static final String LOG = "credentialsActivity";
	private EditText editRobotName, editUserID, editPassword, editBluetooth;
	private EditText editStatus;
	private String robotName, userid, password, bluetooth;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.credentials);

		editRobotName = (EditText) findViewById(R.id.robotname);
		editUserID = (EditText) findViewById(R.id.userid);
		editPassword = (EditText) findViewById(R.id.password);
		editBluetooth = (EditText) findViewById(R.id.bluetooth);
		editStatus = (EditText) findViewById(R.id.status);

		Resources robotResources = getResources();
		SharedPreferences prefs = getSharedPreferences("RobotPreferences",
				MODE_WORLD_WRITEABLE);
		robotName = prefs.getString("robotname", robotResources
				.getString(R.string.robot_name));
		userid = prefs.getString("userid", robotResources
				.getString(R.string.userid));
		password = prefs.getString("password", robotResources
				.getString(R.string.password));
		bluetooth = prefs.getString("bluetooth", robotResources
				.getString(R.string.bluetooth));

		editRobotName.setText(robotName);
		;
		editUserID.setText(userid);
		editPassword.setText(password);
		editBluetooth.setText(bluetooth);
		editStatus.setText("Not saved yet");

		// Set a button listener to save
		Button btnSave = (Button) this.findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				robotName = editRobotName.getText().toString();
				userid = editUserID.getText().toString();
				password = editPassword.getText().toString();
				bluetooth = editBluetooth.getText().toString();

				if (EntriesTest()) {
					SharedPreferences newprefs = getSharedPreferences(
							"RobotPreferences", MODE_WORLD_WRITEABLE);
					SharedPreferences.Editor editor = newprefs.edit();
					editor.putString("robotname", robotName);
					editor.putString("userid", userid);
					editor.putString("password", password);
					editor.putString("bluetooth", bluetooth);
					editor.commit();
					editStatus.setText("Entries saved");
				}
			}
		});
	}

	private boolean EntriesTest() {
		boolean returnResult = true;
		String message = "NOT saved! Error in ";

		if (userid.contains("@") || userid.contains("not set")) {
			message += "userid,";
			returnResult = false;
		}

		if (password.contains("not set")) {
			message += "password,";
			returnResult = false;
		}

		if (!BluetoothAdapter.checkBluetoothAddress(bluetooth)) {
			message += "bluetooth ";
			returnResult = false;
		}
		if (!returnResult) {
			Log.d(LOG, "failed EntriesTest");
			editStatus.setText(message);
		} else
			Log.d(LOG, "passed EntriesTest");
		return returnResult;
	}
}
