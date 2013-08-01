package com.simbiosys.chapooapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.simbiosys.chapooapp.Constants.Action;

public class ItemObject {
	private int type;
	private String id;
	private String name;
	private String parentId;

	public ItemObject() {}

	public ItemObject(String id, String name, int type) {
		this.type = type;
		this.id = id;
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public static List<ItemObject> getChildFolders(String parentID) {
		HttpResponse response = SingletonHttpClient.getInstance().executeGet(Constants.getFolderURL(Action.ACTION_FOLDER_GET_CHILD_FOLDERS, parentID));
		String responseText = SingletonHttpClient.getResponseText(response);
		List<ItemObject>  list = new ArrayList<ItemObject>();
		try {
			JSONArray jsonArray = new JSONArray(responseText);
			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject json = jsonArray.getJSONObject(i);
				String id = json.getString(Constants.Folder.JSON_TAG_FOLDER_ID);
				String name = json.getString(Constants.Folder.JSON_TAG_FOLDER_NAME);
				ItemObject item= new ItemObject(id, name, Constants.ObjectType.OBJECT_TYPE_FOLDER);
				list.add(item);
			}
		} catch (JSONException e) {
			Log.v("exception", "getChildFolders()");
			e.printStackTrace();
		}
		return list;
	}

	public static List<ItemObject> getChildDocuments(String parentID) {
		HttpResponse response = SingletonHttpClient.getInstance().executeGet(Constants.getDocumentURL(parentID));
		String responseText = SingletonHttpClient.getResponseText(response);
		List<ItemObject>  list = new ArrayList<ItemObject>();
		try {
			JSONArray docJSON = new JSONArray(responseText);
			for(int i = 0; i < docJSON.length(); i++){
				JSONObject doc = docJSON.getJSONObject(i);
				String id = doc.getString(Constants.Document.JSON_TAG_DOCUMENT_ID);
				String name = doc.getString(Constants.Document.JSON_TAG_DOCUMENT_NAME);
				ItemObject item = new ItemObject(id, name, Constants.ObjectType.OBJECT_TYPE_DOCUMENT);
				list.add(item);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
}
