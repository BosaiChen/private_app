package com.simbiosys.chapooapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.simbiosys.chapooapp.Constants.ObjectType;
import com.simbiosys.chapooapp.FolderAdapter.ViewHolder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FolderActivity extends FragmentActivity implements EditDialog.OnEditDialogShownListener{

	private Bundle item;
	private String fid, fname;
	TextView loadingMsg;
	Boolean isRoot;
	private ListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sub_folder);

		loadingMsg = (TextView)findViewById(R.id.TextViewLoadingFolder);
		listview = (ListView)findViewById(R.id.listViewFolderSub);

		item = getIntent().getExtras();
		isRoot = item.getBoolean("isRoot");
		fname = item.getString("name");
		if(isRoot) 
			new GetRootFolderAsyncTask().execute();
		else {
			fid = item.getString("id");
			new DisplayItemAsyncTask().execute(fid);
		}

		TextView topBar = (TextView)findViewById(R.id.textViewHeadFolderName);
		topBar.setText(fname);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.folder, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.item_new_folder:
			showNewFolderDialog();
			return true;
		case R.id.item_refresh:
			finish();
			Intent i = new Intent(this, FolderActivity.class);
			//	i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			i.putExtras(this.item);
			startActivity(i);
			return true;
		case R.id.item_Logout:
			logout();
			return true;
		case R.id.action_settings:
			startActivity(new Intent(this, SettinglistActivity.class));
			return true;
		case R.id.item_upload:
			startActivity(new Intent(this, UploadActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public List<ItemObject> displayItems(String folderID) {
		List<ItemObject> list = new ArrayList<ItemObject>();
		list.addAll(ItemObject.getChildFolders(folderID));
		list.addAll(ItemObject.getChildDocuments(folderID));
		return list;
	}

	protected void showNewFolderDialog() {
		View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_folder, null);
		final EditText folderNameET = (EditText)dialogView.findViewById(R.id.editTextDialogNewFolderName);
		final TextView msgTV = (TextView)dialogView.findViewById(R.id.textViewMsgFolderCreating);

		AlertDialog.Builder builder = Helper.createAlertDialog(this, "Create new folder", dialogView);
		//create button
		builder.setPositiveButton("Create",new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog,int id) 
			{
				msgTV.setVisibility(View.VISIBLE);
				String folderName = folderNameET.getText().toString();
				createNewFolder(folderName, fid, Constants.Folder.Type.FOLDER_TYPE_DOCUMENTS, "-1");
			}
		});
		//cancel button
		Helper.setAlertDialogCancelBtn(builder);
		final AlertDialog dialog = builder.create();

		//listen for text change
		Helper.setEdittextListener(folderNameET, dialog);
		dialog.show();
		//"create" button is disabled by default
		dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
	}

	private void createNewFolder(String folderName, String parentID, String type, String afterID) {

		JSONObject json = new JSONObject();
		try {
			json.put("Name", folderName);
			json.put("Type", type);
			json.put("ParentID", parentID);
			json.put("AfterID", afterID);
			Log.v("folder",folderName + " " + parentID);
			new NewFolderAsyncTask(this).execute(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void logout() {
		SingletonHttpClient.getInstance().executeGet(Constants.URL_LOGOFF);

		/*SharedPreferences settings=context.getApplicationContext().getSharedPreferences("LoginFile", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		Log.v("isLogged",Boolean.toString(settings.getBoolean("isLogged", false)));
		editor.remove("isLogged");
		editor.commit();*/

		Intent i = new Intent(this, MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	public String getRootFolderID(String projectID) {
		HttpResponse response = SingletonHttpClient.getInstance().executeGet(Constants.getFolderURL(projectID));
		String responseText = SingletonHttpClient.getResponseText(response);
		try {
			JSONArray projectsJSON = new JSONArray(responseText);
			for(int i = 0; i < projectsJSON.length(); i++){
				JSONObject folder = projectsJSON.getJSONObject(i);
				if(folder.getString(Constants.Folder.JSON_TAG_FOLDER_NAME).contains("Documents")) {
					return folder.getString(Constants.Folder.JSON_TAG_FOLDER_ID);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private class NewFolderAsyncTask extends AsyncTask<JSONObject, Void, String> {

		Activity activity;
		private int status;
		private NewFolderAsyncTask(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected String doInBackground(JSONObject... json) {
			String url = Constants.getFolderURL(Constants.Action.ACTION_FOLDER_CREATE_NEW, null);
			Log.v("folder",url);
			HttpResponse response = SingletonHttpClient.getInstance().executePost(url, json[0]);
			status = SingletonHttpClient.getStatusCode(response);
			return SingletonHttpClient.getResponseText(response);
		}

		@Override
		protected void onPostExecute(String responseText) {
			if(status ==  HttpStatus.SC_OK) {
				//new folder created
				String id = MyJSONParser.getValueByKey(responseText, Constants.Folder.JSON_TAG_FOLDER_ID);
				String name = MyJSONParser.getValueByKey(responseText, Constants.Folder.JSON_TAG_FOLDER_NAME);

				Bundle b = new Bundle();
				b.putString("id", id);
				b.putString("name", name);
				b.putBoolean("isRoot",false);
				finish();
				//refresh parent folder
				Intent pFolder = new Intent(activity, FolderActivity.class);
				pFolder.putExtras(item);
				startActivity(pFolder);
				//go to new folder
				Intent newFolder = new Intent(activity, FolderActivity.class);
				newFolder.putExtras(b);
				startActivity(newFolder);
				return;
			} else {
				Toast.makeText(activity, "Oops,try again... The folder may already exist!", Toast.LENGTH_SHORT).show();
			}	
		}
	}

	private class GetRootFolderAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				//get project id
				HttpResponse response = SingletonHttpClient.getInstance().executeGet(Constants.URL_PROJECTS);
				String responseText = SingletonHttpClient.getResponseText(response);
				String projectID = null;

				JSONArray jsonArray = new JSONArray(responseText);
				for(int i = 0; i < jsonArray.length(); i++){
					JSONObject json = jsonArray.getJSONObject(i);
					projectID = json.getString(Constants.Project.JSON_TAG_ID);
				}

				//get root folder id using project id
				if(projectID != null) {
					return getRootFolderID(projectID);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String rootid) {
			fid = rootid;
			new DisplayItemAsyncTask().execute(rootid);
		}
	}

	private class DisplayItemAsyncTask extends AsyncTask<String, Void, List<ItemObject>> {

		@Override
		protected List<ItemObject> doInBackground(String... fids) {
			Log.v("pid","onpost");
			return displayItems(fids[0]);

		}

		@Override
		protected void onPostExecute(final List<ItemObject> items) {
			FolderAdapter adapter = new FolderAdapter(getBaseContext(), items);
			listview.setEmptyView(findViewById(R.id.TextViewEmptyList));
			listview.setAdapter(adapter);
			loadingMsg.setVisibility(View.GONE);
			listview.setVisibility(View.VISIBLE);

			listview.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					ViewHolder viewholder = (ViewHolder)view.getTag();
					int type = viewholder.type;
					if(type == ObjectType.OBJECT_TYPE_FOLDER) {
						startSubFolder(viewholder);
					} else if(type == ObjectType.OBJECT_TYPE_DOCUMENT) {
						Toast.makeText(getBaseContext(), "Document clicked", Toast.LENGTH_SHORT).show();
					}

				}
			});

			//long click item to show edit list
			listview.setOnItemLongClickListener(new OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
					ViewHolder viewholder = (ViewHolder)v.getTag();

					Bundle data = new Bundle();
					data.putString("name", viewholder.name);
					data.putString("pid", fid);
					data.putInt("type", viewholder.type);
					data.putString("id", viewholder.id); //folder id
					EditDialog edit = EditDialog.newInstance(data);
					edit.show(getSupportFragmentManager(), "edit dialog");

					return true;
				}
			});
		}
	}

	@Override
	public void onBackPressed() {
		if(isRoot){
			moveTaskToBack(true);
		} else {
			super.onBackPressed();
		}
	}

	public void startSubFolder(ViewHolder viewholder){
		Intent i = new Intent(this, FolderActivity.class);
		Bundle b = new Bundle();
		b.putString("id", viewholder.id);
		b.putString("name", viewholder.name);
		b.putBoolean("isRoot", false);
		i.putExtras(b);
		startActivity(i);
	}

	@Override
	public void reloadAcitivity() {
		finish();
		Intent i = new Intent(this, FolderActivity.class);
		i.putExtras(this.item);
		startActivity(i);
	}
}
