package com.simbiosys.chapooapp;

import org.json.JSONException;
import org.json.JSONObject;

public class MyJSONParser {
	
	public static String getValueByKey(String jsonString, String key) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonString);
			return jsonObject.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
