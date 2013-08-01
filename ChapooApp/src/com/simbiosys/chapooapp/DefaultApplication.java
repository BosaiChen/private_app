package com.simbiosys.chapooapp;

import org.apache.http.HttpResponse;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

public class DefaultApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		//auto login
		/*SharedPreferences sp= getSharedPreferences("LoginFile", MODE_PRIVATE);
		String username = sp.getString("username", "");
		String password = sp.getString("password", "");
		if(!username.equals("")){
			//login
			new LoginAsyncTask().execute(username, password);
		} else {
			Intent i = new Intent(getBaseContext(),MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}*/
	}

	private class LoginAsyncTask extends AsyncTask<String, Void, HttpResponse> {
		@Override
		protected HttpResponse doInBackground(String... params) {
			String username = params[0];
			String password = params[1];
			String loginURL = Constants.getLoginURL(username, password);
			return SingletonHttpClient.getInstance().executeGet(loginURL);
		}

		@Override
		protected void onPostExecute(HttpResponse response) {
			int statusCode = SingletonHttpClient.getInstance().getStatusCode(response);
			if(SingletonHttpClient.getInstance().isStatusOK(statusCode)) {
				/*Intent i = new Intent(getBaseContext(),FolderActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);*/
			} else {
				Toast.makeText(getBaseContext(), "Oops, please sign up first...", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
}
