/*"""
	robotMessages.py - specifies PHP classes for the XML exchanged between robots and
						the controllers over the XMPP/jabberd2 interface

         Copyright (c) 2012, 9th Sense, Inc.
         All rights reserved.

     This program is free software: you can redistribute it and/or modify
     it under the terms of the GNU General Public License as published by
     the Free Software Foundation, either version 3 of the License, or
     (at your option) any later version.

     This program is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     GNU General Public License for more details.

     You should have received a copy of the GNU General Public License
     along with this program.  If not, see <http://www.gnu.org/licenses/>.

	 ----------------------------------------------------------------------------------
	 Once instantiated, you can reference the contents by theObject.driverAddr,
	 theObject.robotAddr, etc.

	 To output the contents as XML for sending, use:
	 	mfr = messageFromRobot(theXMLStringToParse)
		outputXMLString = tostring(mfr.XML)
	 ----------------------------------------------------------------------------------

	 Here are the XML formats for messages to and from robot.

	<!-- messageToRobot -->
	<?xml version="1.0" standalone="yes"?>
	<m>
		<t>timeStamp</t>
		<d>driverAddr</d>
		<dn>driverName</dn>
		<r>robotAddr</r>
		<c>commandChar</c>
		<a>commandArguments</a>
		<co>comment</co>
	</m>

	<!-- messageFromRobot -->
	<?xml version="1.0" standalone="yes"?>
	<m>
		<t>timeStamp</t>
		<d>driverAddr</d>
		<r>robotAddr</r>
		<re>responseValue</re>
		<co>comment</co>
	</m>

"""*/

// import inspect, time, math
//
// import pprint
//
// from xml.etree.ElementTree import ElementTree, fromstring, tostring, dump, SubElement
package com.denbar.RobotComm;
//package java_xml_library;


import org.w3c.dom.Document;

public class RobotMessages
{
	// in every RobotMessage (comment defaults to blank)
	public String driverAddr = "";
	public String robotAddr = "";
	public String comment = "";

	// for messages to robot from driver
	public String commandChar = "";
	public String commandArguments = "";
	public String driverName = "";

	// additional for messages from robot to driver
	public String responseValue = "";

	public Document XML; //# XML type
	public String XMLStr;

	public String timeStamp = ""; //when it was created  - time.microtime() (float)
}
