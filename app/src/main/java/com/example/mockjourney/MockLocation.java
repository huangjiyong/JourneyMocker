package com.example.mockjourney;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import com.amap.api.location.AMapLocation;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
@SuppressLint("NewApi")
public class MockLocation implements LocationListener{
	
	private String gps_provider =  LocationManager.GPS_PROVIDER; // 
	
	private LocationManager lm = null;
	private Context    mContext = null;
	private FileOutputStream  mFos = null;
	
	private boolean  bMock = false;
	boolean  bRun = true;
	
	public interface IMockLocation {
		public abstract void onLocationChanged(Location location);
		public abstract void onFinished();
	}
	
	public MockLocation(Context context ){
		if( context instanceof IMockLocation ){
			mContext = context;
			lm = (LocationManager)mContext.getSystemService(Service.LOCATION_SERVICE);
		} else {
			// must implement the interface "IMockLocation"
		}
	}
	
	public static Criteria getLocationCriteria() {

		Criteria criteria = new Criteria();
		criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
		criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
		criteria.setCostAllowed(false);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		
		return criteria;
    }
	
	// the following methods are used to mock a location
	public void playJourney(String filename ){
		bMock = true;
		gps_provider = lm.getBestProvider(getLocationCriteria(), true);
		//if( !lm.isProviderEnabled( gps_provider )){
		    lm.addTestProvider(gps_provider,
		    		 false, //requiresNetwork,
	                 false, // requiresSatellite,
	                 false, // requiresCell,
	                 false, // hasMonetaryCost,
	                 true, // supportsAltitude,
	                 true, // supportsSpeed, s
	                 true, // upportsBearing,
	                 Criteria.POWER_MEDIUM, // powerRequirement
	                 Criteria.ACCURACY_FINE); // accuracy
			lm.setTestProviderEnabled(gps_provider, true);
			
			{
				/*lm.addTestProvider(LocationProviderProxy.AMapNetwork,
			    		 false, //requiresNetwork,
		                 false, // requiresSatellite,
		                 false, // requiresCell,
		                 false, // hasMonetaryCost,
		                 true, // supportsAltitude,
		                 true, // supportsSpeed, s
		                 true, // upportsBearing,
		                 Criteria.POWER_MEDIUM, // powerRequirement
		                 Criteria.ACCURACY_FINE); // accuracy
				lm.setTestProviderEnabled(gps_provider, true);*/
			}
			
		//}
		if( !lm.isProviderEnabled( gps_provider )){
			Toast.makeText(mContext, "Failed to set test provider", Toast.LENGTH_LONG).show();
			return;
		}
		
		//lm.setTestProviderStatus(gps_provider,LocationProvider.AVAILABLE, null, System.currentTimeMillis());
		//lm.requestLocationUpdates(gps_provider, 0, 0, this);
		
		bRun = true;
		playJourneyTask task = new playJourneyTask();
		task.execute(filename);
	}
	
	public void stopPlay(){
		bRun = false;
		stop();
	}
	
	private void stop(){
		lm.removeUpdates( this );
		//lm.setTestProviderEnabled(gps_provider, false);
		//lm.removeTestProvider( gps_provider );
	}
	private class playJourneyTask extends AsyncTask<String, Integer, String>{

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(String... params) {
			Looper.prepare();
			// TODO Auto-generated method stub
			String filename = params[0];				
			try {
				FileInputStream mFis = new FileInputStream(filename);
				InputStreamReader isr = new InputStreamReader( mFis );
				BufferedReader br = new BufferedReader( isr );
				String line = br.readLine();
				// data is started from second line
				;
				Toast.makeText(mContext, "Start MockLocation", Toast.LENGTH_LONG).show();
				while( (line = br.readLine() ) != null && bRun){
					simulateLocation( line);
					Thread.sleep(1000);
				}
				br.close();
				isr.close();
				mFis.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stop();
			//((IMockLocation)mContext).onFinished();
			return "OK";
		}
		
		private void simulateLocation(String latlngaltStr){
			// get the location from file
			// altitude longitude latitude heading speed accuracy
			if( latlngaltStr == null || latlngaltStr.length() <= 0){
				return;
			}
			String[] mockLoc = latlngaltStr.split(",");
			if( mockLoc.length <= 1) {
				mockLoc = latlngaltStr.split("\t");
			}
			Location location = new Location( gps_provider );
			//PulseID,UserToken,JourneyID,Raw_LocalDTM,Raw_Lat,Raw_Long,Raw_Speed,Raw_Heading,Raw_HAccuracy,Raw_Altitude,Raw_VAccuracy,Raw_BatteryLevel,Raw_BatteryCharging,Raw_BatteryHealth,Raw_IsScreenOn,Raw_CallState,Raw_AccessoryConnected,Raw_SMSReceived,Raw_SMSSent,Raw_WifiCount,Raw_ActiveApp
			// time provider latitude longitude altitude heading speed accuracy
			location.setLatitude(Double.valueOf(mockLoc[4]));
			location.setLongitude(Double.valueOf(mockLoc[5]));
			if( mockLoc[9] == null || mockLoc[9].length() <= 0 ){
				location.setAltitude( 0);
			} else {
				location.setAltitude(Double.valueOf(mockLoc[9]));
			}
			if( mockLoc[7] == null ||  mockLoc[7].length() <= 0 ){
				location.setBearing( 0);
			} else {
				location.setBearing(Float.valueOf(mockLoc[7]));
			}
			if( mockLoc[6] == null ||  mockLoc[6].length() <= 0 ){
				location.setSpeed( 9 );
			} else {
				location.setSpeed(Float.valueOf(mockLoc[6]));
			}
			if( mockLoc[8] == null ||  mockLoc[8].length() <= 0 ){
				location.setAccuracy( 0);
			} else {
				location.setAccuracy(Float.valueOf(mockLoc[8]));
			}
			location.setTime( System.currentTimeMillis());
			//location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
			
			try
		    {
		        Method method = Location.class.getMethod("makeComplete");
		        if (method != null)
		        {
		            method.invoke(location);
		        }
		    }
		    catch (NoSuchMethodException e)
		    {
		        e.printStackTrace();
		    }
		    catch (Exception e)
		    {
		        e.printStackTrace();
		    }
			System.out.println( "Latitude " + mockLoc[4] + " longitude " + mockLoc[5]);
			// set the Mock location to LocationManager
			lm.setTestProviderLocation(gps_provider, location);
			((IMockLocation)mContext).onLocationChanged(location);
		}
	}
	
	/*-----------------------------------------------------------------------------------------------
	-----------------------------------------------------------------------------------------------
	-----------------------------------------------------------------------------------------------
	-----------------------------------------------------------------------------------------------
	-----------------------------------------------------------------------------------------------
	-----------------------------------------------------------------------------------------------
	-----------------------------------------------------------------------------------------------
	*/
	/*
	 * the following methods are used to record location
	 *
     */
	public void recordJourney( String filename) {
		// save the location to file
		if( mFos == null ) {
		    try {
		    	File file = new File(filename);
		    	mFos = new FileOutputStream( file );
				//mFos = mContext.openFileOutput( filename, Context.MODE_PRIVATE );
				mFos.write( "PulseID,UserToken,JourneyID,Raw_LocalDTM,Raw_Lat,Raw_Long,Raw_Speed,Raw_Heading,Raw_HAccuracy,Raw_Altitude,Raw_VAccuracy,Raw_BatteryLevel,Raw_BatteryCharging,Raw_BatteryHealth,Raw_IsScreenOn,Raw_CallState,Raw_AccessoryConnected,Raw_SMSReceived,Raw_SMSSent,Raw_WifiCount,Raw_ActiveApp".getBytes());
				mFos.write( "\r\n".getBytes());	
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		lm.requestLocationUpdates(gps_provider, 1000, 5, this);
	}
	
	public void stopRecord(){
		if( mFos != null ){
			try {
				mFos.flush();
				mFos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		lm.removeUpdates(this);
	}
	
	protected void writePulse(Location location ){
		try {	
			//latitude longitude altitude heading speed accuracy
			mFos.write(  "0,0,0,0,".getBytes());
			mFos.write( String.valueOf(location.getLatitude()).getBytes());
			mFos.write(  ",".getBytes());
			mFos.write( String.valueOf(location.getLongitude()).getBytes());
			mFos.write(  ",".getBytes());
			mFos.write( String.valueOf(location.getAltitude()).getBytes());
			mFos.write(  ",".getBytes());
			mFos.write( String.valueOf(location.getBearing()).getBytes());
			mFos.write(  ",".getBytes());
			mFos.write( String.valueOf(location.getSpeed()).getBytes());
			mFos.write(  ",".getBytes());
			mFos.write( String.valueOf(location.getAccuracy()).getBytes());
			mFos.write( ",0,0,0,0,0,0,0,0,0,0,0\r\n".getBytes());	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if( !bMock ){
			writePulse(location);
			((IMockLocation)mContext).onLocationChanged(location);
		} else {
			System.out.println("Receive Location гнгнгнгн Latitude:" + location.getLatitude() + " Longitude:" + location.getLongitude());
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

}
