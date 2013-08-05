package com.simbiosys.chapooapp;

import java.util.List;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.simbiosys.chapooapp.Constants.Action;
import com.simbiosys.chapooapp.Constants.ObjectType;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class EditDialog extends DialogFragment implements OnItemClickListener{

	private Activity context;
	private int type;
	private String fid,fname,pid; // parent folder id
	String[] edits = new String[]{Constants.Edit.EDIT_DELETE, Constants.Edit.EDIT_RENAME};
	private OnEditDialogShownListener listener;
	private Dialog dialog;
	
	public interface OnEditDialogShownListener{
		public abstract void reloadAcitivity();
	}
	
	public static EditDialog newInstance(Bundle data) {
		EditDialog frag = new EditDialog();
		frag.setArguments(data);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		context = getActivity();
		
		Bundle data = getArguments();
		type = data.getInt("type");
	//	pid = data.getString("pid");
		fname = data.getString("name");
		fid = data.getString("id");
		
		Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.edit_dialog);
		dialog.setTitle(fname);
	//	listener.setDialogTitle(dialog);

		ListView list = (ListView)dialog.findViewById(R.id.listViewEdit);
		EditAdapter adapter = new EditAdapter(context, edits);

		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		this.dialog = dialog;
		
		return dialog;
	}



	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnEditDialogShownListener){
			listener = (OnEditDialogShownListener)activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet EditDialog.OnEditDialogShownListener");
		}
	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TextView edit = (TextView)view.findViewById(R.id.textViewEditName);
		String editName = edit.getText().toString();
		if(editName.equals(Constants.Edit.EDIT_DELETE)) {
			this.dialog.dismiss();
			showDeleteDialog();
		} else if (editName.equals(Constants.Edit.EDIT_RENAME)) {
			//click rename
			this.dialog.dismiss();
			showRenameDialog();
		}
	}
	
	private class DeleteAsyncTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			String url = "";
			if(type == ObjectType.OBJECT_TYPE_FOLDER) {
				String fid = params[0];
				url = Constants.getFolderURL(Action.ACTION_FOLDER_DELETE, fid);
			} else if(type == ObjectType.OBJECT_TYPE_DOCUMENT) {
				String fid = params[0]; //file id
				String pid = params[1]; // parent folder id
				url = Constants.getDocumentURL(Action.ACTION_DOCUMENT_DELETE, pid, fid);
			}
			HttpResponse response = SingletonHttpClient.getInstance().executeDelete(url);
			int statusCode = SingletonHttpClient.getStatusCode(response);
			if(SingletonHttpClient.isStatusOK(statusCode)){
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean deleteDone) {
			if(deleteDone) {
				listener.reloadAcitivity();
			} else {
				Toast.makeText(context, "Oops, try again...",  Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/*private class DeleteSubItemAsyncTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			String url = "";
			String type = params[2];
			if(type.equals("folder")) {
				String fid = params[0];
				url = Constants.getFolderURL(Action.ACTION_FOLDER_DELETE, fid);
			} else if(type.equals("doc")) {
				String fid = params[0]; //file id
				String pid = params[1]; // parent folder id
				url = Constants.getDocumentURL(Action.ACTION_DOCUMENT_DELETE, pid, fid);
			}
			HttpResponse response = SingletonHttpClient.getInstance().executeDelete(url);
			int statusCode = SingletonHttpClient.getStatusCode(response);
			if(SingletonHttpClient.isStatusOK(statusCode)){
				return true;
			}
			return false;
		}
	} */
	
	private void showDeleteDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(fname);
		if(type == ObjectType.OBJECT_TYPE_FOLDER)
			builder.setMessage("Are you sure to delete this folder?");
		else if(type == ObjectType.OBJECT_TYPE_DOCUMENT)
			builder.setMessage("Are you sure to delete this file?");
		
		builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog,int id) 
			{
				
				
				if(type == ObjectType.OBJECT_TYPE_FOLDER)
					new DeleteAsyncTask().execute(fid);
			//		deleteSubItem(fid,pid, "folder");
				else if(type == ObjectType.OBJECT_TYPE_DOCUMENT)
					new DeleteAsyncTask().execute(fid, pid);
			}
		});
		Helper.setAlertDialogCancelBtn(builder);
		builder.create().show();
	}
	
	/*private void deleteSubItem(String fid, String pid, String type) {
		List<ItemObject> folders = ItemObject.getChildFolders(fid);
		for(int i = 0; i<folders.size(); i++) {
			String folderid = folders.get(i).getId();
			deleteSubItem(folderid, null, "folder");
		}
		new DeleteSubItemAsyncTask().execute(fid);
		List<ItemObject> docs = ItemObject.getChildDocuments(fid);
		for(int i = 0; i<docs.size(); i++) {
			String folderid = folders.get(i).getId();
			deleteSubItem(folderid, null);
		}
	}*/
	
	private void showRenameDialog() {
		dismiss();
		String title = "";
		if(type == Constants.ObjectType.OBJECT_TYPE_FOLDER) {
			title = "Rename folder";
		} else if(type == Constants.ObjectType.OBJECT_TYPE_DOCUMENT) {
			title = "Rename document";
		}
		View dialogView = getLayoutInflater(null).inflate(R.layout.dialog_rename_object, null);
		final EditText newNameET = (EditText)dialogView.findViewById(R.id.editTextRenamingNewName);
		final TextView msgTV = (TextView)dialogView.findViewById(R.id.textViewMsgRenaming);
		newNameET.setText(fname);

		AlertDialog.Builder builder = Helper.createAlertDialog(context, title, dialogView);
		//create button
		builder.setPositiveButton("Rename",new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog,int id) 
			{
				msgTV.setVisibility(View.VISIBLE);
				String newName = newNameET.getText().toString();
				new RenameAsyncTask().execute(newName);
			}
		});
		//cancel button
		Helper.setAlertDialogCancelBtn(builder);
		AlertDialog dialog = builder.create();
		//listen for text change
		Helper.setEdittextListener(newNameET, dialog);

		dialog.show();
		dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
	}
	
	
	private class RenameAsyncTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			String newName = params[0];
			JSONObject json = new JSONObject();
			try {
				json.put("ID", fid);
				json.put("Name", newName);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if(type == Constants.ObjectType.OBJECT_TYPE_FOLDER) {
				HttpResponse response = SingletonHttpClient.getInstance().executePut(Constants.getFolderURL(Action.ACTION_FOLDER_RENAME, null), json);
				if(SingletonHttpClient.isStatusOK(SingletonHttpClient.getStatusCode(response)))
					return true;
			} else if(type == Constants.ObjectType.OBJECT_TYPE_DOCUMENT) {
				HttpResponse response = SingletonHttpClient.getInstance().executePut(Constants.getDocumentURL(Constants.Action.ACTION_DOCUMENT_RENAME, null, null), json);
				if(SingletonHttpClient.isStatusOK(SingletonHttpClient.getStatusCode(response)))
					return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean renameDone) {
			if(renameDone) {
				listener.reloadAcitivity();
			} else {
				Toast.makeText(context, "Oops, try again...",  Toast.LENGTH_SHORT).show();
			}
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
