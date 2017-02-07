package com.example.mockjourney;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HistoryAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<String> mHistory = new ArrayList<String>();
	
	public HistoryAdapter(Context context, List<String> history) {
		mContext = context;
		mHistory = history;
		this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mHistory.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mHistory.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		if( convertView ==  null ){
			convertView = mInflater.inflate(R.layout.item_history, parent, false);
		}
		((TextView)convertView.findViewById(R.id.item_history_filename)).setText( mHistory.get(position));
		return convertView;
	}
	
	class ViewHolder{
		TextView  tv;
	}

}
