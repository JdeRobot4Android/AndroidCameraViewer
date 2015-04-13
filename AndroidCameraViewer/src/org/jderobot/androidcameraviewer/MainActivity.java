package org.jderobot.androidcameraviewer;

import java.io.ByteArrayOutputStream;


import org.jderobot.androidcameraviewer.R;
import jderobot.CameraPrx;
import jderobot.DataNotExistException;
import jderobot.HardwareFailedException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener {

	/*Image View declared*/
	private ImageView imag;	
	
	/*Button declared*/
	private RelativeLayout rel_layout;	
	
	/*Declare pointer to camera interface*/
	private CameraPrx cprx;
	
	/*String for port*/
	private String port = "9999";
	
	/*String for ip address*/
	private String ipaddress = "";
	
	/*String for protocol*/
	private String protocol = "tcp";
	
	/*Set the flag to 1*/
	private String NullFlag = "1";
	
	/*Declare the  task*/ 
	DownloadFilesTask runner = new DownloadFilesTask();
	
	/*Declare the default width and height of ImageView*/
	private int imagwidth = 240;
	private int imagheight = 160;
	
	private int executed = 1;
	
	/*Aspect Ratio*/
	private double aspect_ratio = 0;

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        /*Set the ImageView to imag*/
        imag = (ImageView) findViewById(R.id.imageView1);
        
        /*Set click listener to layout*/
        rel_layout = (RelativeLayout)findViewById(R.id.layout); 
        rel_layout.setOnClickListener(this);
        
        /*Call the preferences and set them to the strings*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        port = prefs.getString("Port Number", "9999");
        protocol = prefs.getString("protocol", "tcp");
        ipaddress = prefs.getString("ipkey", "0.0.0.0");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //new CustomTask().execute((Void[])null);
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
        
        setaspectratio();
    }
    

    public void onClick(View v) {  
    	
    	if(NullFlag == "0"){
    		if(executed == 1){
    			runner = new DownloadFilesTask();
    			runner.execute(cprx);	
    			executed = 0;
    		}
    		else{
    			/*As runner is already running we cancel it*/
    			runner.cancel(true);
    			executed = 1;
    		}	
    	}else{
    		/* If NullFlag is not zero then there is no connection*/
    		Toast.makeText(getApplicationContext(), "Connection not established", Toast.LENGTH_SHORT).show();	
    	}
  	}

    private Void setaspectratio(){
    	
    	aspect_ratio = (double) imagwidth/ (double) imagheight ;
    	imagheight = imag.getLayoutParams().height;
    	imagwidth = (int) (aspect_ratio*imagheight);
    	Log.e("baa", imagwidth+" " + imagheight + " "+ aspect_ratio);
    	//Toast.makeText(getApplicationContext(), "imagheight" + imagheight + "imagwidth" + imagwidth + aspect_ratio + imag.getLayoutParams().height + imag.getLayoutParams().width, Toast.LENGTH_LONG).show();
    	imag.getLayoutParams().height = imagheight;
    	imag.getLayoutParams().width = imagwidth;
    	
		return null;	
    }
    

   
    private class DownloadFilesTask extends AsyncTask<CameraPrx, Bitmap, Long> {
    	 protected Long doInBackground(jderobot.CameraPrx... urls) {
    		 /* Execute this loop until button is clicked*/
    		 while(true){
    		 try {
    	    		
    	  			jderobot.ImageData realdata;
    	  			
    	  			/*Get the image data*/
    	  	  		realdata = cprx.getImageData();
    	  	  		
    	  	  		/*Present image format is NV21 and it gives 240 x 160 size images*/
    		  	  	YuvImage img = new YuvImage(realdata.pixelData, ImageFormat.NV21, realdata.description.width, realdata.description.height, null);
    		      	ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
    		      	img.compressToJpeg(new Rect(0, 0, realdata.description.width, realdata.description.height), 100, baoStream);
    	  	  	  	
    		      	
    	  	  	  	/*Convert image to Bitmap*/
    	  	  	  	Bitmap mBitmap = BitmapFactory.decodeByteArray(baoStream.toByteArray(),0,baoStream.size());
    	  	  	  	imagwidth = mBitmap.getWidth();
    	  	  	  	
    	  	  	  	imagheight = mBitmap.getHeight();
    	  	  	  	
    	  	  	  	publishProgress(mBitmap);
    	  	  	  	if(isCancelled())
    	  	  	  		break;
    	  	  	
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
    		 
    	     return (long) 1;
    	 }


    	 protected void onProgressUpdate(Bitmap... mBitmap) {
    		 /*Set the ImageView to Bitmap on ProgressUpdate*/
    	     imag.setImageBitmap(mBitmap[0]);
    	 }

    	 protected void onPostExecute(Long result) {
    	     /*Do nothing*/
    	 }
    	}
  	
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
    	
    	NullFlag = "1";
    	/*Initialize Ice communicator*/
        Ice.Communicator communicator;
        communicator = Ice.Util.initialize();
        
        /*Get the object proxy*/
        Ice.ObjectPrx base = communicator.stringToProxy("cameraA:"+protocol+ " -h "+ipaddress+" -p " + port);
      	//Toast.makeText(getApplicationContext(), base.toString(), Toast.LENGTH_LONG).show();
        
      	//Toast.makeText(getApplicationContext(), cprx.toString(), Toast.LENGTH_LONG).show();
        cprx = jderobot.CameraPrxHelper.uncheckedCast(base);
        
  		jderobot.ImageData realdata = cprx.getImageData();
  		
  		if(cprx.getImageData() != null){
  			/*This code is reached only when connection is established so set NullFlag to 0*/
  			NullFlag = "0";
  		}
  		
      	//Toast.makeText(getApplicationContext(), realdata.toString(), Toast.LENGTH_LONG).show();
      	//jderobot.ImageData realdata = cprx.begin_getImageData();
        //Toast.makeText(getApplicationContext(), realdata.description.format, Toast.LENGTH_LONG).show();
      	
      	/*Convert NV21 image data to Jpeg*/
  		YuvImage img = new YuvImage(realdata.pixelData, ImageFormat.NV21, realdata.description.width, realdata.description.height, null);
      	ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
      	img.compressToJpeg(new Rect(0, 0, realdata.description.width, realdata.description.height), 100, baoStream);
//  		byte[] yuv420sp = new byte[realdata.description.height * realdata.description.width*3/2];;
//  		int yIndex = 0;
//  		final int frameSize = realdata.description.height * realdata.description.width;
//        int uvIndex = frameSize;
//      	int a, R, G, B, Y, U, V;
//        int index = 0;
//        for (int j = 0; j < realdata.description.height; j++) {
//            for (int i = 0; i < realdata.description.width; i++) {
//
//                a = (realdata.pixelData[index] & 0xff000000) >> 24; // a is not used obviously
//                R = (realdata.pixelData[index] & 0xff0000) >> 16;
//                G = (realdata.pixelData[index] & 0xff00) >> 8;
//                B = (realdata.pixelData[index] & 0xff) >> 0;
//
//                // well known RGB to YUV algorithm
//                Y = ( (  66 * R + 129 * G +  25 * B + 128) >> 8) +  16;
//                U = ( ( -38 * R -  74 * G + 112 * B + 128) >> 8) + 128;
//                V = ( ( 112 * R -  94 * G -  18 * B + 128) >> 8) + 128;
//
//                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
//                //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
//                //    pixel AND every other scanline.
//                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
//                if (j % 2 == 0 && index % 2 == 0) { 
//                    yuv420sp[uvIndex++] = (byte)((V<0) ? 0 : ((V > 255) ? 255 : V));
//                    yuv420sp[uvIndex++] = (byte)((U<0) ? 0 : ((U > 255) ? 255 : U));
//                }
//
//                index ++;
//            }
//        }
//      	YuvImage img = new YuvImage(yuv420sp, ImageFormat.NV21, realdata.description.width, realdata.description.height, null);
//      	ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
//      	img.compressToJpeg(new Rect(0, 0, realdata.description.width, realdata.description.height), 100, baoStream);
//      	/*Convert Jpeg to Bitmap*/
//      	Bitmap mBitmap = BitmapFactory.decodeByteArray(baoStream.toByteArray(),0,baoStream.size());
  		
//	  	  	
//      	
	  	  /*Convert image to Bitmap*/
	  	  Bitmap mBitmap = BitmapFactory.decodeByteArray(baoStream.toByteArray(),0,baoStream.size());
	  	  
      	//Bitmap mBitmap = BitmapFactory.decodeByteArray(realdata.pixelData,0,realdata.pixelData.length);
      	//Toast.makeText(getApplicationContext(), mBitmap.toString(), Toast.LENGTH_LONG).show();
      	
      	/* Get the Height and Width of the Bitmap Image*/
	  	imagwidth = mBitmap.getWidth(); 	
	  	imagheight = mBitmap.getHeight();
      	//Log.e("aaa", imagwidth+" " + imagheight + " " + realdata.description.height);
      	/*Set Bitmap image*/
		imag.setImageBitmap(mBitmap);
      	
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

      /* Get the port, protocol and ip address */
      port = prefs.getString("Port Number", "9999");
      protocol = prefs.getString("protocol", "default");
      ipaddress = prefs.getString("ipkey", "172.10.2.102");
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
    	//imag.setLayoutParams(new LayoutParams(imagwidth, imagheight));
      	setaspectratio();
//      final float scale = this.getResources().getDisplayMetrics().density;
          
//  		imag.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, imagwidth, getResources().getDisplayMetrics());
//  		abcd =(int) (imagwidth * scale + 0.5f);
//  		Log.e(NullFlag, abcd +"  "+ imagwidth + "  " + scale);
//  		//Toast.makeText(getApplicationContext(), (int) (imagwidth * scale + 0.5f) , Toast.LENGTH_LONG);
//  		imag.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, imagheight, getResources().getDisplayMetrics());
//  		abcd =(int) (imagheight * scale + 0.5f);
//  		Log.e(NullFlag, abcd +"  " + imagheight);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	/* Move to Preferences when settings is clicked*/
        	Intent i = new Intent(this, Preferences.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
