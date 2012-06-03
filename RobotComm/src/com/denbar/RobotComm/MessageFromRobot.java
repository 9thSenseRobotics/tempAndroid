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

class MessageFromRobot extends RobotMessages
// For messages that are sent from a driver or controller to the robot
// properties:
// timeStamp (the sequence number/time stamp)
// driverAddr (the jabber address of the driver)
// robotAddr (the jabber address of the robot)
// responseValue (the response value of the message. This is anything the robot
// wants to tell you; it's not necessarily a response to a direct query.)
// comment (any comment or further information; can contain whatever you want,
// and is blank by default)
// XMLStr = the string containing the raw XML
//
// Usage (three constructors)
//
// mfr = new messageFromRobot(rawXMLString); // build from the raw XML
// mfr = new messageToRobot(driverAddr, robotAddr, responseValue [, comment]);

{
	public MessageFromRobot(String xmlStr)
	// single-argument constructor: Build this class from its XML string
	{
		// parse XML string to create Document
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			// dbf.setFeature("http://xml.org/sax/features/namespaces", false);
			// dbf.setFeature("http://xml.org/sax/features/validation", false);
			// dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
			// false);
			// dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
			// false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			ByteArrayInputStream stream = new ByteArrayInputStream(xmlStr
					.getBytes());
			XML = db.parse(stream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		NodeList nl = XML.getElementsByTagName("t");
		Node n = nl.item(0);
		Element e = (Element) n;
		try {
			timeStamp = e.getTextContent();
		} catch (Exception e1) {
			timeStamp = null;
		}

		try {
			// <d>driverAddr</d>
			nl = XML.getElementsByTagName("d");
			n = nl.item(0);
			e = (Element) n;
			driverAddr = e.getTextContent();
		} catch (Exception e1) {
			driverAddr = null;
		}

		try {
			// <r>robotAddr</r>
			nl = XML.getElementsByTagName("r");
			n = nl.item(0);
			e = (Element) n;
			robotAddr = e.getTextContent();
		} catch (Exception e1) {
			robotAddr = null;
		}

		try {
			// <re>responseValue</re>
			nl = XML.getElementsByTagName("re");
			n = nl.item(0);
			e = (Element) n;
			responseValue = e.getTextContent();
		} catch (Exception e1) {
			responseValue = null;
		}

		try {
			// <co>comment</co>
			nl = XML.getElementsByTagName("co");
			n = nl.item(0);
			e = (Element) n;
			comment = e.getTextContent();
		} catch (Exception e1) {
			comment = null;
		}

		// now re-create XMLStr from the DOM (this makes absolutely sure the
		// syntax was correct)
		try {
			DOMSource source = new DOMSource(XML);
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);
			XMLStr = writer.getBuffer().toString();

		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	public MessageFromRobot(String da, String ra, String rv)
	// 3-argument constructor: build this class from its component data with a
	// response but without a comment
	{
		// set the class' properties from the passed-in values; if they're
		// unspecified, set as empty
		timeStamp = new MicroTime().smicrotime();
		driverAddr = da;
		robotAddr = ra;
		responseValue = rv;
		comment = "";

		// parse XML string to create Document
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			dbf.setFeature("http://xml.org/sax/features/namespaces", false);
			dbf.setFeature("http://xml.org/sax/features/validation", false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			XML = db.newDocument();

			Element m = XML.createElement("m");
			XML.appendChild(m);
			Element rootElement = XML.getDocumentElement();

			// <t>timeStamp</t>
			Element t = XML.createElement("t");
			t.appendChild(XML.createTextNode(timeStamp));
			rootElement.appendChild(t);

			// $this->XML->addChild('d', $this->driverAddr);
			Element d = XML.createElement("d");
			d.appendChild(XML.createTextNode(driverAddr));
			rootElement.appendChild(d);

			// $this->XML->addChild('r', $this->robotAddr);
			Element r = XML.createElement("r");
			r.appendChild(XML.createTextNode(robotAddr));
			rootElement.appendChild(r);

			// $this->XML->addChild('re', $this->responseValue);
			Element re = XML.createElement("re");
			re.appendChild(XML.createTextNode(responseValue));
			rootElement.appendChild(re);

			// $this->XML->addChild('co', $this->comment);
			Element co = XML.createElement("co");
			co.appendChild(XML.createTextNode(comment));
			rootElement.appendChild(co);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			DOMSource source = new DOMSource(XML);
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);
			XMLStr = writer.getBuffer().toString();

		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	public MessageFromRobot(String da, String ra, String rv, String cm)
	// 4-argument constructor: build this class from its component data with a
	// response and a comment
	{
		// set the class' properties from the passed-in values; if they're
		// unspecified, set as empty
		timeStamp = new MicroTime().smicrotime();
		driverAddr = da;
		robotAddr = ra;
		responseValue = rv;
		comment = cm;

		// parse XML string to create Document
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			dbf.setFeature("http://xml.org/sax/features/namespaces", false);
			dbf.setFeature("http://xml.org/sax/features/validation", false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			XML = db.newDocument();

			Element m = XML.createElement("m");
			XML.appendChild(m);
			Element rootElement = XML.getDocumentElement();

			// <t>timeStamp</t>
			Element t = XML.createElement("t");
			t.appendChild(XML.createTextNode(timeStamp));
			rootElement.appendChild(t);

			// $this->XML->addChild('d', $this->driverAddr);
			Element d = XML.createElement("d");
			d.appendChild(XML.createTextNode(driverAddr));
			rootElement.appendChild(d);

			// $this->XML->addChild('r', $this->robotAddr);
			Element r = XML.createElement("r");
			r.appendChild(XML.createTextNode(robotAddr));
			rootElement.appendChild(r);

			// $this->XML->addChild('re', $this->responseValue);
			Element re = XML.createElement("re");
			re.appendChild(XML.createTextNode(responseValue));
			rootElement.appendChild(re);

			// $this->XML->addChild('co', $this->comment);
			Element co = XML.createElement("co");
			co.appendChild(XML.createTextNode(comment));
			rootElement.appendChild(co);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			DOMSource source = new DOMSource(XML);
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);
			XMLStr = writer.getBuffer().toString();

		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
}