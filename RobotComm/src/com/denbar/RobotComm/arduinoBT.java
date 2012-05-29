package com.denbar.RobotComm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class arduinoBT
{
	private static final String LOG = "ArduinoBT";
	private BluetoothDevice _bluetoothTarget;
	private BluetoothAdapter _bluetoothAdapter;
	private BluetoothSocket _socket;
	private OutputStream _outputStream;
	private InputStream _inputStream;
	Thread workerThread;
	byte[] readBuffer;
	int readBufferPosition;
	int counter;
	volatile boolean stopWorker;
	boolean _isConnected;
	String _BTaddress;
	Context _context;
	public String _echoData = "";
	//common machine UUID that we need to communicate with FireFly Bluetooth module:
	private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	public arduinoBT(Context CallingContext)
    {
		_context = CallingContext;
    	_BTaddress = "invalid address";	// just so it is not an empty string
    	_isConnected = false;
    }

	public void setBTaddress(String BTaddress)
	{
		_BTaddress = BTaddress;
	}

	public String getBTaddress()
	{
		return _BTaddress;
	}

	public boolean getConnectionState()
	{
		return _isConnected;
	}

	public void closeBT() throws IOException
	{
		stopWorker = true;
		if (_outputStream != null) _outputStream.close();
		if (_inputStream != null) _inputStream.close();
		if (_socket != null) _socket.close();
		_socket = null;
	}

/*
	// every other call should give a result > 0 if you wait long enough between the calls
	public long checkLatency()
	{
		long latency;
		if (_echoReceivedTime > 0) {
			latency = _echoReceivedTime - _ArduinoCharForEchoSentTime;
			_echoSent = false;
			_echoReceivedTime = 0;
			return latency;
		}
		else {
			if (!_echoSent) {
				_ArduinoCharForEchoSentTime = System.currentTimeMillis();
				sendMessage("echo");
				_echoSent = true;
			}
		return 0;
		}
	}
*/

	public boolean resetConnection()
	{
		// start by forcing close
		try {
			closeBT();
		}
		catch (IOException ex) {
			Log.d(LOG, "closeBT returned exception" + ex);
		}
		_isConnected = false;
		if (!Connect()) return false;
		return true;
	}


    public boolean Connect()
    {
    	if (_isConnected)
    	{
    		// we got a call to Connect() even though we think we are already connected
    			Log.d(LOG, "Connect requested when already connected");
    			return true;
    	}
    	try
		{
			if (!findBT())
			{
				Log.d(LOG, "findBT returned false");
				return false;
			}
		}
		catch (IOException ex) {
			Log.d(LOG, "FindBT returned exception" + ex);
			return false;

		}

		try
		{
			if (!openBT())
			{
				Log.d(LOG, "openBT returned false");
				return false;
			}
		}
		catch (IOException ex) {
			Log.d(LOG, "OpenBT returned exception" + ex);
			return false;
		}
		_isConnected = true;
		ListenForData();
		return true;
	}

	private boolean findBT() throws IOException
	{
		_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(_bluetoothAdapter == null)
		{
			Log.d(LOG, "No BT adapter available");
			return false;
		}
		if (!_bluetoothAdapter.isEnabled())
		{
			if (_bluetoothAdapter.enable())
			{
				// The adapter state will immediately transition from STATE_OFF to STATE_TURNING_ON,
				// and some time later transition to either STATE_OFF or STATE_ON.
				// If this call returns false then there was an immediate problem that
				// will prevent the adapter from being turned on - such as Airplane mode,
				// or the adapter is already turned on.
				// So, basically, the bluetooth device is turning on and we need to wait for it to finish
				int counter = 0;
				while (_bluetoothAdapter.getState() != BluetoothAdapter.STATE_ON)
				{
					try{
						Thread.sleep(1000);
					} catch (Exception e) {
					}
					if (counter > 3)
					{
						return false;
					}
					counter++;
				}

				if (_bluetoothAdapter.getState() != BluetoothAdapter.STATE_ON)
					Log.d(LOG, "BT sucessfully powered up");
				else
				{
					if (_bluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON)
						Log.d(LOG, "BT is turning on, wait a bit and try again");
					else Log.d(LOG, "BT failed to turn on");
					return false;
				}
			}
			else
			{
				Log.d(LOG, "BT did not enable");
				return false;
			}
		}
		else Log.d(LOG, "BT previously enabled");

		// get remote BTs that are paired with us
		Set<BluetoothDevice> pairedDevices = _bluetoothAdapter.getBondedDevices();
		if (pairedDevices == null)
		{
			Log.d(LOG, "No paired BT devices");
			return false;
		}
		if(pairedDevices.size() > 0)
		{
			for(BluetoothDevice device : pairedDevices)
			{
				if (device.getName().startsWith("FireFly-") || device.getName().startsWith("RN42-"))
				{
					_bluetoothTarget = device;
					Log.d(LOG, "Found ardunio BT device named " + _bluetoothTarget.getName());
					Log.d(LOG, "device address is " + _bluetoothTarget.getAddress());
					break;
				}
			}
		}
		else
		{
			Log.d(LOG, "BT adapter is not STATE_ON so we could not list paired BT devices");
			return false;
		}

		if (_bluetoothTarget == null)
		{
			Log.d(LOG, "no BT target found that matches our BT device");
			return false;
		}

		if (_bluetoothTarget.getBondState() != BluetoothDevice.BOND_BONDED)
		{
			Log.d(LOG, "Unable to find a paired arduino BT");
			return false;
		}

		// for some reason, the device found through the above routine does not work properly
		// when we use it and call connect, it generates an exception "Service failed discovery"
		// so we have to find the device using the MAC address instead.

        if (BluetoothAdapter.checkBluetoothAddress(_BTaddress))
        {
        	_bluetoothTarget = _bluetoothAdapter.getRemoteDevice(_BTaddress);
        	// A BluetoothDevice will always be returned for a valid hardware address,
        	// even if this adapter has never seen that device
        	// so we need to check that we are paired with this one
    		if (_bluetoothTarget.getBondState() == BluetoothDevice.BOND_BONDED)
    		{
    			Log.d(LOG, "Our arduino BT device found successfully");
    		}
    		else
    		{
    			Log.d(LOG, "The BT found with our address is not paired");
    			return false;
    		}
        }
        else
        {
        	Log.d(LOG, "invalid BT address");
        	return false;
        }
        return true;
	}

	// open the connection
	private boolean openBT() throws IOException
	{
		// in case discovery is still going on,
		// it is important to cancel it before trying to connect or the connect may timeout.
		if (_bluetoothAdapter != null) _bluetoothAdapter.cancelDiscovery();
		else
		{
			Log.d(LOG, "trying to connect without a BT adapter");
			return false;
		}
		// if the socket was used before, we have to close it before trying to reconnect
		// otherwise _scoket.connect() throws exception java.io.IOException: Device or resource busy
		if (_socket != null)
		{
			try {
			_socket.close();
			}
			catch (IOException ex) {
				Log.d(LOG, "socket close exeception" + ex);
			}
		}

		try {
			_socket = _bluetoothTarget.createRfcommSocketToServiceRecord(uuid);
		}
		catch (IOException ex) {
			Log.d(LOG, "createRf returned exception " + ex);
			return false;
		}

		// if we try to connect multiple times very fast
		// _socket.connect still throws the exception: java.io.IOException: Service discovery failed
		// but the app survives and works OK if we just try connecting again
		try {
			_socket.connect();
		}
		catch (IOException ex) {
			Log.d(LOG, "socket.connect returned exception " + ex);
			return false;
		}

		try {
			_outputStream = _socket.getOutputStream();
			_inputStream = _socket.getInputStream();
		}
		catch (IOException ex) {
			Log.d(LOG, "getting streams returned exception " + ex.getMessage());
			return false;
		}
		return true;
	}

	// send a message to the arduino
	public boolean sendMessage(String msg) {
		if (_outputStream == null)
		{
			Log.d(LOG, "tried to send message with null outputStream ");
			return false;
		}
			String msgTest = msg + "#";
			Log.d(LOG, "message to arduino: " + msgTest);
			try {
			byte[] byteString = (msgTest + " ").getBytes();
			byteString[byteString.length - 1] = 0;
			_outputStream.write(byteString);
		} catch (IOException e) {
			Log.d(LOG, "exception from sendMessage: " + e.getMessage());
			return false;
		}
		return true;
	}

	private void ListenForData()
	{
		final Handler handler = new Handler();
		final byte delimiter = 10; //This is the ASCII code for a newline character

		stopWorker = false;
		readBufferPosition = 0;
		readBuffer = new byte[1024];
		workerThread = new Thread(new Runnable()
		{
			public void run()
			{
				while(!Thread.currentThread().isInterrupted() && !stopWorker)
				{
					try
					{
						int bytesAvailable = _inputStream.available();
						if(bytesAvailable > 0)
						{
							byte[] packetBytes = new byte[bytesAvailable];
							_inputStream.read(packetBytes);
							for(int i=0;i<bytesAvailable;i++)
							{
								byte b = packetBytes[i];
								if(b == delimiter)
								{
									byte[] encodedBytes = new byte[readBufferPosition];
									System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
									final String data = new String(encodedBytes, "US-ASCII");
									readBufferPosition = 0;
									handler.post(new Runnable()
									{
										public void run()
										{
											Log.d(LOG, "message from arduino: " + data);
											Intent serviceIntent = new Intent();
							        		serviceIntent.setAction("com.denbar.RobotComm.RobotCommService");
							        		serviceIntent.putExtra("messageFromRobot", data);
							        		//if (data.startsWith("echo")) _echoReceivedTime = System.currentTimeMillis();
							        		 _context.startService(serviceIntent);
										}
									});
								}
								else
								{
									readBuffer[readBufferPosition++] = b;
								}
							}
						}
					}
					catch (IOException ex)
					{
						stopWorker = true;
					}
				}
			}
		});

		workerThread.start();
	}
}

