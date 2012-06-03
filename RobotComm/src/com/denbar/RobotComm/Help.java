package com.denbar.RobotComm;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Help extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.xml.help);
		TextView MyHelpView = (TextView) findViewById(R.id.HelpView);
		String HelpMessage = " Welcome to 9th Sense's Robot Communication"
				+ "\n\nDrive a robot from anywhere in the world" +

				"\n\nYou can set user preferences for"
				+ "\n 1) Your robot's bluetooth address"
				+ "\n 2) Your login information"
				+ "\n 3) If you want to auto connect "
				+ "\n 4) To display connection events";
		MyHelpView.setText(HelpMessage);
	}
}