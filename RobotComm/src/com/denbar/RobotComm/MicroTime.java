package com.denbar.RobotComm;

//package java_xml_library;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MicroTime
//Reproduces PHP microtime() function, which gives two versions of response:
//1. microtime(), which I don't use
//2. microtime(true) returns a string version of the epoch time in seconds and fractions of second,
//		so I just called this smicrotime() below.
{

	public String smicrotime ()
	// return a string with the millisecond time. It's millisecond time, not microtime. microtime()
	// is what PHP calls it, and I am emulating a PHP function because the protocol was originally
	// implemented in PHP, so, yeah, here we are.
	{
		long fullTime = System.currentTimeMillis(); // get the current epoch time in milliseconds

		long millis = fullTime % 1000; // take the milliseconds

		long seconds = fullTime / 1000; // shift the decimal four places left.
										// Assignment as a long removes the remainder.
										// Long to long loses no precision.

		// I don't need to do any fancy formatting with this one. It's just a whole number.
		// All I need is a string conversion.
		String sec = String.valueOf(seconds);

		// However, this requires some fancy formatting. Because it's a long, the
		// it can be 9ms (.0009s), 158ms (.0158s), etc. PHP shows those leading zeroes.
		// A conversion from long to string does not. Therefore, I use DecimalFormat to specify
		// the format, and NumberFormat to give me a formatting tool that will take a number and
		// format it in the specified way. This is kind of like the stuff you could do with sprintf,
		// just a class-y way of doing it.
		NumberFormat f = new DecimalFormat("000");

		// Now return a string containing the current epoch time in seconds, a decimal place,
		// and the four-place millisecond fraction of the current time.
		return (sec + "." + f.format(millis));
	}
}