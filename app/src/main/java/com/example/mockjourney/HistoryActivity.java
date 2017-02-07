package com.example.mockjourney;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class HistoryActivity extends ListActivity implements OnItemClickListener {


	private String        mBasePath;
	private List<String>  history = new ArrayList<String>();
	private HistoryAdapter  adapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// scan folder
		mBasePath = getApplicationContext().getExternalCacheDir().getPath() + "/" ;
		scanFolder( mBasePath );
		
		adapter = new HistoryAdapter(this, history);
		setListAdapter(adapter);
		getListView().setOnItemClickListener( this );

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.journey, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	

	 
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		// TODO Auto-generated method stub
		String filename = mBasePath + history.get(position);

		Intent intent = new Intent(this,  ViewJourneyActivity.class );
		intent.putExtra("filename", filename);
		
		startActivity(intent);
	}
	
	private void scanFolder(String path ) {
		File file = new File( path );
		if( file.exists() && file.isDirectory() ){
			File[] files = file.listFiles();
			for( File  f : files) {
				history.add( f.getName());
			}
		}
	}
}
