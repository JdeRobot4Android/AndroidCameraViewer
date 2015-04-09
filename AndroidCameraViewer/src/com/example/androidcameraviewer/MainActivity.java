package com.example.androidcameraviewer;

import java.io.ByteArrayOutputStream;
import java.util.Timer;
import java.util.TimerTask;


import jderobot.CameraPrx;
import jderobot.DataNotExistException;
import jderobot.HardwareFailedException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener {

	private ImageView imag;
	private Button btn;
	private CameraPrx cprx;
	private String port = "9999";
	private String ipaddress = "";
	private String protocol = "tcp";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imag = (ImageView) findViewById(R.id.imageView1);
        btn = (Button)findViewById(R.id.Button); 
        btn.setOnClickListener(this);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        port = prefs.getString("Port Number", "9999");
        protocol = prefs.getString("protocol", "tcp");
        ipaddress = prefs.getString("ipkey", "172.10.2.102");
//        new Thread(new Runnable() {
//          public void run() {
//        	  
//          }
//        }).start();

//        Timer timer = new Timer(); 
//    	timer.scheduleAtFixedRate(new TimerTask() 
//    	    { 
//    	        public void run() 
//    	        { 
//    	        	 
//    	        } 
//    	    }, 0, 1000); 
        try {
  			initializeCommunicator();
  			//Toast.makeText(getApplicationContext(), "Communicator initialized", Toast.LENGTH_LONG).show();
  		} catch (DataNotExistException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} catch (HardwareFailedException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
        
    }

    public void onClick(View v) { // Parameter v stands for the view that was clicked.  
    	try {
  			jderobot.ImageData realdata;
  	  		realdata = cprx.getImageData();
  	  		YuvImage img = new YuvImage(realdata.pixelData, ImageFormat.NV21, 240, 160, null);
  	  	  	ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
  	  	  	img.compressToJpeg(new Rect(0, 0, 240, 160), 50, baoStream);
  	  	  	Bitmap mBitmap = BitmapFactory.decodeByteArray(baoStream.toByteArray(),0,baoStream.size());
  	  	  	
  	  	  	imag.setImageBitmap(mBitmap);
  	  	} catch (DataNotExistException e) {
  	  		// TODO Auto-generated catch block
  	  		e.printStackTrace();
  	  	} catch (HardwareFailedException e) {
  	  		// TODO Auto-generated catch block
  	  		e.printStackTrace();
  	  		} catch (Exception e){
  	  			e.printStackTrace();
  	  		} 
    	
  	
  	}
    
    private Runnable mMyRunnable = new Runnable()
    {
        @Override
        public void run()
        {
        	try {
      			jderobot.ImageData realdata;
      	  		realdata = cprx.getImageData();
      	  		YuvImage img = new YuvImage(realdata.pixelData, ImageFormat.NV21, 240, 160, null);
      	  	  	ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
      	  	  	img.compressToJpeg(new Rect(0, 0, 240, 160), 50, baoStream);
      	  	  	Bitmap mBitmap = BitmapFactory.decodeByteArray(baoStream.toByteArray(),0,baoStream.size());
      	  	  	
      	  	  	imag.setImageBitmap(mBitmap);
      	  	} catch (DataNotExistException e) {
      	  		// TODO Auto-generated catch block
      	  		e.printStackTrace();
      	  	} catch (HardwareFailedException e) {
      	  		// TODO Auto-generated catch block
      	  		e.printStackTrace();
      	  		} catch (Exception e){
      	  			e.printStackTrace();
      	  		}
        }
        
     };
  	
    /* Implementation of ICE */
    interface CommunicatorCallback {
      void onWait();

      void onCreate(Ice.Communicator communicator);

      void onError(Ice.LocalException ex);
    }
    
    private Ice.Communicator _communicator;
    private CommunicatorCallback _cb;

    private void initializeCommunicator() throws DataNotExistException, HardwareFailedException {
      try {
        Ice.Communicator communicator;
        communicator = Ice.Util.initialize();
      	Ice.ObjectPrx base = communicator.stringToProxy("cameraA:"+protocol+ " -h "+ipaddress+" -p " + port);
      	
      	cprx = jderobot.CameraPrxHelper.checkedCast(base);
      	
      	jderobot.ImageData realdata = cprx.getImageData();
      	//Toast.makeText(getApplicationContext(), realdata.toString(), Toast.LENGTH_LONG).show();
      	//jderobot.ImageData realdata = cprx.begin_getImageData();
      	
      	YuvImage img = new YuvImage(realdata.pixelData, ImageFormat.NV21, 240, 160, null);
      	ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
      	img.compressToJpeg(new Rect(0, 0, 240, 160), 50, baoStream);
      	Bitmap mBitmap = BitmapFactory.decodeByteArray(baoStream.toByteArray(),0,baoStream.size());
      	
      	imag.setImageBitmap(mBitmap);
      	//Toast.makeText(getApplicationContext(), mBitmap.toString(), Toast.LENGTH_LONG).show();
        
        synchronized (this) {
          _communicator = communicator;
          if (_cb != null) {
            _cb.onCreate(_communicator);
          }
        }
      } catch (Ice.LocalException ex) {
        synchronized (this) {
          if (_cb != null) {
            _cb.onError(ex);
          }
        }
      }
    }
    
    public void onStop() {
        super.onStop();
      }

      public void onPause() {
        super.onPause();
      }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    protected void onResume() {
      super.onResume();
      
      /* We call the Preferences and get the selected values */
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

      // Get the port and protocol
      port = prefs.getString("Port Number", "9999");
      protocol = prefs.getString("protocol", "default");
      ipaddress = prefs.getString("ipkey", "172.10.2.102");
      //Toast.makeText(getApplicationContext(), ipaddress, Toast.LENGTH_LONG).show();
//      new Thread(new Runnable() {
//          
//          }
//        }).start();
    	  try {
  			initializeCommunicator();
  			//Toast.makeText(getApplicationContext(), "Communicator initialized", Toast.LENGTH_LONG).show();
  		} catch (DataNotExistException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} catch (HardwareFailedException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
      
      

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	Intent i = new Intent(this, Preferences.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
