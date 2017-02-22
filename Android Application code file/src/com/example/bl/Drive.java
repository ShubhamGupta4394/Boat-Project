package com.example.bl;

import java.io.IOException;

import java.io.OutputStream;
import java.util.UUID;
 
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
 
public class Drive extends Activity {
 
  //Declare buttons & editText
  Button forward, backward, left, right, start, stop;
 
  //Memeber Fields
  private BluetoothAdapter btAdapter = null;
  private BluetoothSocket btSocket = null;
  private OutputStream outStream = null;
 
  // UUID service - This is the type of Bluetooth device that the BT module is
  // It is very likely yours will be the same, if not google UUID for your manufacturer
  private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
 
  // MAC-address of Bluetooth module
  public String newAddress = null;
 
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.drive);
 
   
 
    //Initialising buttons in the view
    //mDetect = (Button) findViewById(R.id.mDetect);
    forward = (Button) findViewById(R.id.button1);
    left = (Button) findViewById(R.id.button3);
    right = (Button) findViewById(R.id.button5);
    backward = (Button) findViewById(R.id.button8);
    start = (Button) findViewById(R.id.button2);
    stop = (Button) findViewById(R.id.button4);
    //getting the bluetooth adapter value and calling checkBTstate function
    btAdapter = BluetoothAdapter.getDefaultAdapter();
    checkBTState();
 
    /**************************************************************************************************************************8
     *  Buttons are set up with onclick listeners so when pressed a method is called
     *  In this case send data is called with a value and a toast is made
     *  to give visual feedback of the selection made
     */
 
    forward.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          sendData("1");
          Toast.makeText(getBaseContext(), "1", Toast.LENGTH_SHORT).show();
        }
      });
 
     left.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          sendData("2");
          Toast.makeText(getBaseContext(), "2", Toast.LENGTH_SHORT).show();
        }
      });
     right.setOnClickListener(new OnClickListener() {
         public void onClick(View v) {
           sendData("3");
           Toast.makeText(getBaseContext(), "3", Toast.LENGTH_SHORT).show();
         }
       });
     backward.setOnClickListener(new OnClickListener() {
         public void onClick(View v) {
           sendData("4");
           Toast.makeText(getBaseContext(), "4", Toast.LENGTH_SHORT).show();
         }
       });
     stop.setOnClickListener(new OnClickListener() {
         public void onClick(View v) {
           sendData("0");
           Toast.makeText(getBaseContext(), "0", Toast.LENGTH_SHORT).show();
         }
       });
     start.setOnClickListener(new OnClickListener() {
         public void onClick(View v) {
           sendData("5");
           Toast.makeText(getBaseContext(), "5", Toast.LENGTH_SHORT).show();
         }
       });
  }
 
  @Override
  public void onResume() {
    super.onResume();
    // connection methods are best here in case program goes into the background etc
 
    //Get MAC address from DeviceListActivity
    Intent intent = getIntent();
    newAddress = intent.getStringExtra(MainActivity.EXTRA_DEVICE_ADDRESS);
 
    // Set up a pointer to the remote device using its address.
    BluetoothDevice device = btAdapter.getRemoteDevice(newAddress);
 
    //Attempt to create a bluetooth socket for comms
    try {
        btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
    } catch (IOException e1) {
        Toast.makeText(getBaseContext(), "ERROR - Could not create Bluetooth socket", Toast.LENGTH_SHORT).show();
    }
 
    // Establish the connection.
    try {
      btSocket.connect();
    } catch (IOException e) {
      try {
        btSocket.close();        //If IO exception occurs attempt to close socket
      } catch (IOException e2) {
          Toast.makeText(getBaseContext(), "ERROR - Could not close Bluetooth socket", Toast.LENGTH_SHORT).show();
      }
    }
 
    // Create a data stream so we can talk to the device
    try {
      outStream = btSocket.getOutputStream();
    } catch (IOException e) {
        Toast.makeText(getBaseContext(), "ERROR - Could not create bluetooth outstream", Toast.LENGTH_SHORT).show();
    }
    //When activity is resumed, attempt to send a piece of junk data ('x') so that it will fail if not connected
    // i.e don't wait for a user to press button to recognise connection failure
    
    //sendData("x");
  }
 
  @Override
  public void onPause() {
	   super.onPause();
    //Pausing can be the end of an app if the device kills it or the user doesn't open it again
    //close all connections so resources are not wasted
 
    //Close BT socket to device
    try     {
      btSocket.close();
    } catch (IOException e2) {
        Toast.makeText(getBaseContext(), "ERROR - Failed to close Bluetooth socket", Toast.LENGTH_SHORT).show();
    }
  }
  //takes the UUID and creates a comms socket
  private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
 
      return  device.createRfcommSocketToServiceRecord(MY_UUID);
  }
 
  //same as in device list activity
  private void checkBTState() {
    // Check device has Bluetooth and that it is turned on
    if(btAdapter==null) {
        Toast.makeText(getBaseContext(), "ERROR - Device does not support bluetooth", Toast.LENGTH_SHORT).show();
        finish();
    } else {
      if (btAdapter.isEnabled()) {
      } else {
        //Prompt user to turn on Bluetooth
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, 1);
      }
    }
  }
 
  // Method to send data
  private void sendData(String message) {
    byte[] msgBuffer = message.getBytes();
 
    try {
    //attempt to place data on the outstream to the BT device
      outStream.write(msgBuffer);
    } catch (IOException e) {
       //if the sending fails this is most likely because device is no longer there
       Toast.makeText(getBaseContext(), "ERROR - Device not found", Toast.LENGTH_SHORT).show();
       finish();
    }
  }
}

