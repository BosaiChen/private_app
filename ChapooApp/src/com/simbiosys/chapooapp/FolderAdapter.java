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
	
	public FolderAdapter(Context context, List<ItemObject> objects) {
		super(context, R.layout.folder_row, objects);
		this.context = context;
		this.items = objects;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.folder_row, parent, false);
		
		ItemObject item = items.get(position);
		final int type = item.getType();
		final String id= item.getId();
		final String name = item.getName();
		
		ImageView icon = (ImageView)rowView.findViewById(R.id.imageViewTypeIcon);
		TextView itemName = (TextView)rowView.findViewById(R.id.textViewObjectName);
		itemName.setText(name);
		switch(type) {
			case Constants.ObjectType.OBJECT_TYPE_FOLDER:
				icon.setImageResource(R.drawable.folder);
				break;
			case Constants.ObjectType.OBJECT_TYPE_DOCUMENT:
				icon.setImageResource(R.drawable.pdf);
				break;
		}
		rowView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(type == ObjectType.OBJECT_TYPE_FOLDER) {
					Intent i = new Intent(context, FolderActivity.class);
					Bundle b = new Bundle();
					b.putString("id", id);
					b.putString("name", name);
					b.putBoolean("isRoot", false);
					i.putExtras(b);
					context.startActivity(new Intent(i));
				} else if(type == ObjectType.OBJECT_TYPE_DOCUMENT) {
					Toast.makeText(context, "Document clicked", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		/*rowView.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				EditDialog edit = new EditDialog();
				edit.show(context, tag)
				return false;
			}
			
		});*/
		
		return rowView;
	}
	
}
