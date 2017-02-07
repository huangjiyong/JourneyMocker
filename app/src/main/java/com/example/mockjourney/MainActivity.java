package com.example.mockjourney;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.amap.api.maps2d.model.LatLng;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private static final String ASSET_LIST_FILENAME = "journey";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// load history journey
		// TODO
		String toPath = getApplicationContext().getExternalCacheDir().getPath();
		
		AssetManager mAssetManager = this.getAssets();
	  
		copyAssetToDataFolder(mAssetManager, ASSET_LIST_FILENAME, toPath);
		
		Button start = (Button)findViewById( R.id.record);
		Button mock = (Button)findViewById( R.id.history );
		
		start.setOnClickListener( new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,  RecordJourneyActivity.class );

				MainActivity.this.startActivity(intent);
			}
			
		});
		
		
		mock.setOnClickListener( new OnClickListener(){
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,  HistoryActivity.class );
				MainActivity.this.startActivity(intent);
			}
			
		});
	}
	
	private boolean copyAssetToDataFolder(AssetManager assetManager, String from, String toPath ){
		try {
			String[] files = assetManager.list( from );
			new File(toPath).mkdirs();
            boolean res = true;
            for (String file : files)
                if (file.contains("."))
                    res &= copyAsset(assetManager, 
                    		from + "/" + file,
                            toPath + "/" + file);
                else 
                    res &= copyAssetToDataFolder(assetManager, 
                    		from + "/" + file,
                            toPath + "/" + file);
            return res;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean copyAsset(AssetManager assetManager,
            String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
          in = assetManager.open(fromAssetPath);
          new File(toPath).createNewFile();
          out = new FileOutputStream(toPath);
          copyFile(in, out);
          in.close();
          in = null;
          out.flush();
          out.close();
          out = null;
          return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
          out.write(buffer, 0, read);
        }
    }
}
