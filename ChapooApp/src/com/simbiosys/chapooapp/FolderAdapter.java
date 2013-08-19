package com.simbiosys.chapooapp;

import java.util.List;

import com.simbiosys.chapooapp.Constants.ObjectType;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FolderAdapter extends ArrayAdapter<ItemObject>{

	private final Context context;
	private List<ItemObject> items;
	private boolean isRoot;
	
	public FolderAdapter(Context context, List<ItemObject> objects, boolean isRoot) {
		super(context, R.layout.folder_row, objects);
		this.context = context;
		this.items = objects;
		this.isRoot = isRoot;
	}
	
	public class ViewHolder{
		public int type;
		public String id;
		public String name;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.folder_row, parent, false);
		
		ItemObject item = items.get(position);
		
		ViewHolder viewholder = new ViewHolder();
		viewholder.type = item.getType();;
		viewholder.id = item.getId();
		viewholder.name = item.getName();
		
		ImageView icon = (ImageView)rowView.findViewById(R.id.imageViewTypeIcon);
		TextView itemName = (TextView)rowView.findViewById(R.id.textViewObjectName);
		itemName.setText(viewholder.name);
		switch(viewholder.type) {
			case Constants.ObjectType.OBJECT_TYPE_FOLDER:
				icon.setImageResource(R.drawable.folder);
				break;
			case Constants.ObjectType.OBJECT_TYPE_DOCUMENT:
				icon.setImageResource(R.drawable.pdf);
				break;
			case Constants.ObjectType.OBJECT_TYPE_PARENT_FOLDER:
				icon.setImageResource(R.drawable.blue_arrow_up);
				break;
		}
		
		rowView.setTag(viewholder);
		return rowView;
	}
	
}
