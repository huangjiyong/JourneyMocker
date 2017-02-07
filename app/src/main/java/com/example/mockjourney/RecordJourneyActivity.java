package com.example.mockjourney;

import java.text.DecimalFormat;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.PolylineOptions;
import com.example.mockjourney.MockLocation.IMockLocation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class RecordJourneyActivity extends Activity implements IMockLocation{
	private Button  mStart, mStop;
	private EditText mInfo;;
	
	private MapView mapView;
    private AMap aMap;
    private MockLocation ml;
 
    private PolylineOptions   mPolylines;
    private Handler  mHandler;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // R 需要引用包import com.amapv2.apis.R;
        setContentView(R.layout.record_journey);
        mapView = (MapView) findViewById(R.id.map);
        
        mStart = (Button)findViewById( R.id.start);
        mStop = (Button)findViewById( R.id.stop);
        mInfo = (EditText)findViewById( R.id.accuracy);
        
        ml = new MockLocation( this );
        mapView.onCreate(savedInstanceState);// 必须要写
        init();
        
        mStart.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mStart.setEnabled(false);
				mStop.setEnabled(true);
				String path = getApplicationContext().getExternalCacheDir().getPath() + "/";
				String filename = path + System.currentTimeMillis() + ".txt";
				mPolylines = new PolylineOptions();
				mPolylines.color(0xff0000ff);
				mPolylines.width( 8 );
				ml.recordJourney(filename);
			}
        });
        
        mStop.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ml.stopRecord();
				mStart.setEnabled(true);
				mStop.setEnabled(false);
			}	
        });
        
        mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				aMap.clear();
				DecimalFormat df = new DecimalFormat("0.0");
				mInfo.setText(df.format( msg.getData().getFloat("speed" )) + "-" +
										 df.format( msg.getData().getFloat("accuracy" )) +"-" +
										 df.format( msg.getData().getFloat("heading" )));
				synchronized(mPolylines){
					aMap.addPolyline( mPolylines );
				}
			}        	
        };

    }

    @Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
    	
		LatLng latlng = new LatLng( location.getLatitude(), location.getLongitude());
		LatLng newlatLng = Utils.transformFromWGSToGCJ( latlng );
		synchronized(mPolylines){
			mPolylines.add(newlatLng);
		}
		Bundle bundle = new Bundle();
		bundle.putFloat("speed" , location.getSpeed());
		bundle.putFloat("accuracy", location.getAccuracy());
		bundle.putFloat("heading", location.getBearing());
		Message msg = new Message();
		msg.what = 0;
		msg.setData(bundle); 

		mHandler.sendMessage(msg);
	}
    
    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
    }
 
    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
 
    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
     
    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
 
    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ml.stopRecord();
        mapView.onDestroy();
    }

	@Override
	public void onFinished() {
		// TODO Auto-generated method stub
		
	}

}
