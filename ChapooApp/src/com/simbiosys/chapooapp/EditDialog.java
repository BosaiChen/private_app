package com.simbiosys.chapooapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class EditDialog extends DialogFragment implements OnItemClickListener{

	private Activity context;
	String[] edits = new String[]{Constants.Operation.OP_DELETE, Constants.Operation.OP_RENAME};

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		context = getActivity();
		Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.edit_dialog);
		//		dialog.setTitle(object.objectName);

		ListView list = (ListView)dialog.findViewById(R.id.listViewEdit);
		EditAdapter adapter = new EditAdapter(context, edits);

		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		return dialog;
	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TextView edit = (TextView)view.findViewById(R.id.textViewEditName);
		String editName = edit.getText().toString();
		if(editName.equals(Constants.Operation.OP_DELETE)) {
	//		deleteFolder(folderID);
			Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show();
		} else if (editName.equals(Constants.Operation.OP_RENAME)) {
			//click rename
//			showRenameDialog();
		}

	}

	private class EditAdapter extends ArrayAdapter<String> {

		private final String[] edits;

		public EditAdapter(Context context, String[] edits) {
			super(context, R.layout.list_edit, edits);
			this.edits = edits;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.list_edit, parent, false);
			TextView editItem = (TextView)rowView.findViewById(R.id.textViewEditName);
			editItem.setText(edits[position]);
			return rowView;
		}
	}
}
