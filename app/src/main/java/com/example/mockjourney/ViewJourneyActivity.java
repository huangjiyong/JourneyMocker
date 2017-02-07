package com.example.mockjourney;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.PolylineOptions;
import com.example.mockjourney.MockLocation.IMockLocation;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ViewJourneyActivity extends Activity  implements IMockLocation{
	private MapView mapView;
    private AMap aMap;
	private MockLocation  ml;
	private String mFilename;
	private Handler  mHandler;
	private PolylineOptions   mPolylines;
	private Button  start, stop;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // R 需要引用包import com.amapv2.apis.R;
        setContentView(R.layout.view_journey);
        
        mFilename = this.getIntent().getStringExtra("filename");
        
        mapView = (MapView) findViewById(R.id.map);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);

        mapView.onCreate(savedInstanceState);// 必须要写
        init();
        
        ml = new MockLocation( this );
        
        
        mPolylines = new PolylineOptions();
		mPolylines.color(0xffff0000);
		mPolylines.width( 8 );
		
        mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				synchronized(mPolylines){
					aMap.clear();
					aMap.addPolyline( mPolylines );
				}
			}        	
        };
        start.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if( !areMockLocationsEnabled() ){
					Toast.makeText(ViewJourneyActivity.this, "Please open the setting to allow mock location", Toast.LENGTH_LONG).show();
					return;
				}
				start.setEnabled(false);
				stop.setEnabled(true);
				ml.playJourney(mFilename);
			}
        });
        
        stop.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ml.stopPlay();
				start.setEnabled(true);
				stop.setEnabled(false);
			}	
        });
        
        
    }
    
	public boolean areMockLocationsEnabled() {
		
		boolean retVal = false;
		
		if(Settings.Secure.getString( getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("1")){
			retVal = true;
		}
		
		return true;
	}
	 
    
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		LatLng latlng = new LatLng( location.getLatitude(), location.getLongitude());
		LatLng newlatLng = Utils.transformFromWGSToGCJ( latlng );
		synchronized(mPolylines){
			mPolylines.add(newlatLng);
		}
		mHandler.sendEmptyMessage(0);
	}
	
	@Override
	public void onFinished() {
		// TODO Auto-generated method stub
		finish();
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
        ml.stopPlay();
        mapView.onDestroy();
    }
 
}
