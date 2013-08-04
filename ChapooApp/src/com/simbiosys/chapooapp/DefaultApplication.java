package com.simbiosys.chapooapp;

import org.apache.http.HttpResponse;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class DefaultApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		//auto login
		SharedPreferences sp = getSharedPreferences("LoginFile", MODE_PRIVATE);
		boolean isLogged = sp.getBoolean("isLogged", false);
		if(isLogged){
			String username = sp.getString("username", "");
			String password = sp.getString("password", "");
			new AutoLoginAsyncTask().execute(username, password);
			return;
		} else {
			Intent i = new Intent(getBaseContext(),MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}
	}

	private class AutoLoginAsyncTask extends AsyncTask<String, Void, HttpResponse> {


		@Override
		protected HttpResponse doInBackground(String... params) {
			String username = params[0];
			String password = params[1];
			String loginURL = Constants.getLoginURL(username, password);
			return SingletonHttpClient.getInstance().executeGet(loginURL);
		}

		@Override
		protected void onPostExecute(HttpResponse response) {
			Intent i = new Intent(getBaseContext(),FolderActivity.class);
			Bundle b = new Bundle();
			b.putBoolean("isRoot", true);
			b.putString("name", "Chapoo");
			i.putExtras(b);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getBaseContext().startActivity(i);
		}
	}

}
