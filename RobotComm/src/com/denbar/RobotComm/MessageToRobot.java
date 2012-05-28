/*
	xmltest.java - test the Java library for creating/parsing MessageToRobot and MessageFromRobot messages

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

	 To get a string containing the contents as XML for sending, use theObject->XMLStr;

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

 */
package com.denbar.RobotComm;

//package java_xml_library;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class MessageToRobot extends RobotMessages
// For messages that are sent from a driver or controller to the robot
// properties:
//	timeStamp (the sequence number/time stamp)
//	driverAddr (the jabber address of the driver)
//	driverName(the name of the driver; could also be their email)
//	robotAddr (the jabber address of the robot)
//	commandChar (the command we want to send to the robot)
//	commandArguments (any arguments for the command specified: e.g. speed, angle, type, etc.; blank by default)
//	comment (any comment or further information; can contain whatever you want; blank by default)
//  XMLStr = the string containing the raw XML
//
// Usage (four constructors)
//
//	mtr = new MessageToRobot(rawXMLString); // build from the raw XML
//	mtr = new messageToRobot(driverAddr, driverName, robotAddr, responseValue [, commandArguments] [, comment]);

{


	public MessageToRobot(String xmlStr)
	// single-argument constructor: Build this class from its XML string
	{
		// parse XML string to create Document
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			//dbf.setFeature("http://xml.org/sax/features/namespaces", false);
			//dbf.setFeature("http://xml.org/sax/features/validation", false);
			//dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			//dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			ByteArrayInputStream stream = new ByteArrayInputStream(xmlStr.getBytes());
			XML = db.parse(stream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		NodeList nl = XML.getElementsByTagName("t");
		Node n = nl.item(0);
		Element e = (Element)n;
		try {
			timeStamp = e.getTextContent();
		} catch (Exception e1) {
			timeStamp = null;
		}

		try {
			// 		<d>driverAddr</d>
			nl = XML.getElementsByTagName("d");
			n = nl.item(0);
			e = (Element)n;
			driverAddr = e.getTextContent();
		} catch (Exception e1) {
			driverAddr = null;
		}

		try {
			// 		<dn>driverName</dn>
			nl = XML.getElementsByTagName("dn");
			n = nl.item(0);
			e = (Element)n;
			driverName = e.getTextContent();
		} catch (Exception e1) {
			driverName= null;
		}



		try {
			// <r>robotAddr</r>
			nl = XML.getElementsByTagName("r");
			n = nl.item(0);
			e = (Element)n;
			robotAddr = e.getTextContent();
		} catch (Exception e1) {
			robotAddr= null;
		}

		try {
			// 	<c>commandChar</c>
			nl = XML.getElementsByTagName("c");
			n = nl.item(0);
			e = (Element)n;
			commandChar = e.getTextContent();
		} catch (Exception e1) {
			commandChar= null;
		}

		try {
			// <a>commandArguments</a>
			nl = XML.getElementsByTagName("a");
			n = nl.item(0);
			e = (Element)n;
			commandArguments= e.getTextContent();
		} catch (Exception e1) {
			commandArguments= null;
		}

		try {
			// <co>comment</co>
			nl = XML.getElementsByTagName("co");
			n = nl.item(0);
			e = (Element)n;
			comment = e.getTextContent();
		} catch (Exception e1) {
			comment= null;
		}

		// now re-create XMLStr from the DOM (this makes absolutely sure the syntax was correct)
		try
		{
			DOMSource source = new DOMSource(XML);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform (source, result);
			XMLStr = writer.getBuffer().toString();


		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	public MessageToRobot(String da, String dn, String ra, String cc)
	// 4-argument constructor: build this class from its component data without a commandArgument or a comment
	{
		// set the class' properties from the passed-in values; if they're unspecified, set as empty
		timeStamp = new MicroTime().smicrotime();
		driverAddr = da;
		driverName = dn;
		robotAddr = ra;
		commandChar = cc;
		commandArguments= "";
		comment = "";

		// parse XML string to create Document
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			dbf.setFeature("http://xml.org/sax/features/namespaces", false);
			dbf.setFeature("http://xml.org/sax/features/validation", false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			XML = db.newDocument();

			Element m = XML.createElement("m");
			XML.appendChild(m);
			Element rootElement = XML.getDocumentElement();

			// 		<t>timeStamp</t>
			Element t = XML.createElement("t");
			t.appendChild (XML.createTextNode(timeStamp));
			rootElement.appendChild(t);

			// 		$this->XML->addChild('d', $this->driverAddr);
			Element d = XML.createElement("d");
			d.appendChild (XML.createTextNode(driverAddr));
			rootElement.appendChild(d);

			// 		$this->XML->addChild('d', $this->driverName);
			Element ddn = XML.createElement("dn");
			ddn.appendChild (XML.createTextNode(driverName));
			rootElement.appendChild(ddn);

			// 		$this->XML->addChild('r', $this->robotAddr);
			Element r = XML.createElement("r");
			r.appendChild (XML.createTextNode(robotAddr));
			rootElement.appendChild(r);

			// 		$this->XML->addChild('c', $this->commandChar);
			Element c = XML.createElement("c");
			c.appendChild (XML.createTextNode(commandChar));
			rootElement.appendChild(c);

			// 		$this->XML->addChild('a', $this->commandArguments);
			Element a = XML.createElement("a");
			a.appendChild (XML.createTextNode(commandArguments));
			rootElement.appendChild(a);


			// 		$this->XML->addChild('co', $this->comment);
			Element co = XML.createElement("co");
			co.appendChild (XML.createTextNode(comment));
			rootElement.appendChild(co);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try
		{
			DOMSource source = new DOMSource(XML);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform (source, result);
			XMLStr = writer.getBuffer().toString();


		} catch (Exception e2) {
			e2.printStackTrace();
		}
	} // end four-constructor method

	public MessageToRobot(String da, String dn, String ra, String cc, String ca)
	// 5-argument constructor: build this class from its component data with a command argument but without a comment
	{
		// set the class' properties from the passed-in values; if they're unspecified, set as empty
		timeStamp = new MicroTime().smicrotime();
		driverAddr = da;
		driverName = dn;
		robotAddr = ra;
		commandChar = cc;
		commandArguments= ca;
		comment = "";

		// parse XML string to create Document
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			dbf.setFeature("http://xml.org/sax/features/namespaces", false);
			dbf.setFeature("http://xml.org/sax/features/validation", false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			XML = db.newDocument();

			Element m = XML.createElement("m");
			XML.appendChild(m);
			Element rootElement = XML.getDocumentElement();

			// 		<t>timeStamp</t>
			Element t = XML.createElement("t");
			t.appendChild (XML.createTextNode(timeStamp));
			rootElement.appendChild(t);

			// 		$this->XML->addChild('d', $this->driverAddr);
			Element d = XML.createElement("d");
			d.appendChild (XML.createTextNode(driverAddr));
			rootElement.appendChild(d);

			// 		$this->XML->addChild('d', $this->driverName);
			Element ddn = XML.createElement("dn");
			ddn.appendChild (XML.createTextNode(driverName));
			rootElement.appendChild(ddn);

			// 		$this->XML->addChild('r', $this->robotAddr);
			Element r = XML.createElement("r");
			r.appendChild (XML.createTextNode(robotAddr));
			rootElement.appendChild(r);

			// 		$this->XML->addChild('c', $this->commandChar);
			Element c = XML.createElement("c");
			c.appendChild (XML.createTextNode(commandChar));
			rootElement.appendChild(c);

			// 		$this->XML->addChild('a', $this->commandArguments);
			Element a = XML.createElement("a");
			a.appendChild (XML.createTextNode(commandArguments));
			rootElement.appendChild(a);


			// 		$this->XML->addChild('co', $this->comment);
			Element co = XML.createElement("co");
			co.appendChild (XML.createTextNode(comment));
			rootElement.appendChild(co);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try
		{
			DOMSource source = new DOMSource(XML);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform (source, result);
			XMLStr = writer.getBuffer().toString();


		} catch (Exception e2) {
			e2.printStackTrace();
		}
	} // end five-argument constructor

	public MessageToRobot(String da, String dn, String ra, String cc, String ca, String co)
	// 6-argument constructor: build this class from its component data
	{
		// set the class' properties from the passed-in values; if they're unspecified, set as empty
		timeStamp = new MicroTime().smicrotime();
		driverAddr = da;
		driverName = dn;
		robotAddr = ra;
		commandChar = cc;
		commandArguments= ca;
		comment = co;

		// parse XML string to create Document
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			dbf.setFeature("http://xml.org/sax/features/namespaces", false);
			dbf.setFeature("http://xml.org/sax/features/validation", false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			XML = db.newDocument();

			Element m = XML.createElement("m");
			XML.appendChild(m);
			Element rootElement = XML.getDocumentElement();

			// 		<t>timeStamp</t>
			Element t = XML.createElement("t");
			t.appendChild (XML.createTextNode(timeStamp));
			rootElement.appendChild(t);

			// 		$this->XML->addChild('d', $this->driverAddr);
			Element d = XML.createElement("d");
			d.appendChild (XML.createTextNode(driverAddr));
			rootElement.appendChild(d);

			// 		$this->XML->addChild('d', $this->driverName);
			Element ddn = XML.createElement("dn");
			ddn.appendChild (XML.createTextNode(driverName));
			rootElement.appendChild(ddn);

			// 		$this->XML->addChild('r', $this->robotAddr);
			Element r = XML.createElement("r");
			r.appendChild (XML.createTextNode(robotAddr));
			rootElement.appendChild(r);

			// 		$this->XML->addChild('c', $this->commandChar);
			Element c = XML.createElement("c");
			c.appendChild (XML.createTextNode(commandChar));
			rootElement.appendChild(c);

			// 		$this->XML->addChild('a', $this->commandArguments);
			Element a = XML.createElement("a");
			a.appendChild (XML.createTextNode(commandArguments));
			rootElement.appendChild(a);


			// 		$this->XML->addChild('co', $this->comment);
			Element cm = XML.createElement("co");
			cm.appendChild (XML.createTextNode(comment));
			rootElement.appendChild(cm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try
		{
			DOMSource source = new DOMSource(XML);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform (source, result);
			XMLStr = writer.getBuffer().toString();


		} catch (Exception e2) {
			e2.printStackTrace();
		}
	} // end five-argument constructor

}