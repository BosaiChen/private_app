package com.simbiosys.chapooapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.simbiosys.chapooapp.Constants.ObjectType;
import com.simbiosys.chapooapp.FolderAdapter.ViewHolder;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.annotation.TargetApi;
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

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class FolderActivity extends FragmentActivity {

	private Bundle item;
	private String fid;
	TextView loadingMsg;
	Boolean isRoot;
	private ListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sub_folder);

		/*if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}*/

		loadingMsg = (TextView)findViewById(R.id.TextViewLoadingFolder);
		listview = (ListView)findViewById(R.id.listViewFolderSub);
		

		//	Log.v("pid",getProjectID());

		item = getIntent().getExtras();
		isRoot = item.getBoolean("isRoot");
		if(isRoot) 
			new GetProjectAsyncTask().execute();
		//		fid = getRootFolderID(getProjectID());
		else {
			fid = item.getString("id");
			new DisplayItemAsyncTask().execute(fid);
		}


		TextView topBar = (TextView)findViewById(R.id.textViewHeadFolderName);
		topBar.setText(item.getString("name"));
		//	displayItems(fid);
	}


	private class GetProjectAsyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			return getProjectID();
		}

		@Override
		protected void onPostExecute(String pid) {
			new GetRootFolderAsyncTask().execute(pid);
			Log.v("get project",pid);
		}
	}

	private class GetRootFolderAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... pid) {
			return getRootFolderID(pid[0]);
		}

		@Override
		protected void onPostExecute(String fid) {
			new DisplayItemAsyncTask().execute(fid);
			Log.v("get root folder",fid);
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

			listview.setOnItemLongClickListener(new OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
					EditDialog edit = new EditDialog();
					edit.show(getSupportFragmentManager(), "edit dialog");
					return true;
				}
			});
			
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
		}


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
		String url = Constants.getFolderURL(Constants.Action.ACTION_FOLDER_CREATE_NEW, null);
		JSONObject json = new JSONObject();
		try {
			json.put("Name", folderName);
			json.put("Type", type);
			json.put("ParentID", parentID);
			json.put("AfterID", afterID);
			HttpResponse response = SingletonHttpClient.getInstance().executePost(url, json);
			if(SingletonHttpClient.getStatusCode(response) ==  HttpStatus.SC_UNAUTHORIZED) {
				//if not authorized, creating new folder will fail
				Toast.makeText(this, "Oops,unauthorized to create folder...", Toast.LENGTH_SHORT).show();
			} else {
				//new folder created
				String responseText = SingletonHttpClient.getResponseText(response);
				String id = MyJSONParser.getValueByKey(responseText, Constants.Folder.JSON_TAG_FOLDER_ID);
				String name = MyJSONParser.getValueByKey(responseText, Constants.Folder.JSON_TAG_FOLDER_NAME);

				Bundle b = new Bundle();
				b.putString("id", id);
				b.putString("name", name);
				b.putBoolean("isRoot",false);
				finish();
				//refresh parent folder
				Helper.startActivity(this, FolderActivity.class, item);
				//go to new folder
				Helper.startActivity(this, FolderActivity.class, b);
			}	
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

	private String getProjectID(){
		//		HttpResponse response = SingletonHttpClient.getInstance().executeGet(Constants.URL_PROJECTS);

		//get project
		String projectID = null;
		try {
			HttpResponse response = new ConnAsyncTask().execute().get();
			String responseText = SingletonHttpClient.getResponseText(response);
			JSONArray jsonArray = new JSONArray(responseText);
			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject json = jsonArray.getJSONObject(i);
				projectID = json.getString(Constants.Project.JSON_TAG_ID);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return projectID;
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

	private class ConnAsyncTask extends AsyncTask<String, Void, HttpResponse> {

		/*private HttpGet httpGet;

		public ConnAsyncTask(HttpGet get) {
			this.httpGet = get;
		}*/

		@Override
		protected HttpResponse doInBackground(String... params) {
			try {
				return SingletonHttpClient.getInstance().executeGet(Constants.URL_PROJECTS);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
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
	
}
