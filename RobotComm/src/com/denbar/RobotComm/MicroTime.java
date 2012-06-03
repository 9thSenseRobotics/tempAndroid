package com.denbar.RobotComm;

//package java_xml_library;

public class MicroTime
// Reproduces PHP microtime() function, which gives two versions of response:
// 1. microtime(), which I don't use
// 2. microtime(true) returns a string version of the epoch time in seconds and
// fractions of second,
// so I just called this smicrotime() below.
{

	public String smicrotime()
	// return a string with the millisecond time. It's millisecond time, not
	// microtime. microtime()
	// is what PHP calls it, and I am emulating a PHP function because the
	// protocol was originally
	// implemented in PHP, so, yeah, here we are.
	{
		return String.valueOf(System.currentTimeMillis()); // get the current epoch time in milliseconds
		/*int length = fullTime.length();
		// assuming here that length > 2, otherwise we will get a Number
		String millis = fullTime.substring(length - 2);
		String seconds = fullTime.substring(0,length - 2);
		return seconds + "." + millis;
		*/
	}
}