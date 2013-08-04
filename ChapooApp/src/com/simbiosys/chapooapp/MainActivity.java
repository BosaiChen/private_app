package com.simbiosys.chapooapp;

import org.apache.http.HttpResponse;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		/*SharedPreferences sp = getSharedPreferences("LoginFile", MODE_PRIVATE);
		boolean isLogged = sp.getBoolean("isLogged", false);
		if(isLogged){
			String username = sp.getString("username", "");
			String password = sp.getString("password", "");
			new AutoLoginAsyncTask(this).execute(username, password);
			return;
		} */
		
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onclickLogin(View v) {
		Intent i = new Intent(this,LoginActivity.class);
		//	i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(i);
	}

	public void onclickSignup(View v) {
		Intent i = new Intent(this,SignupActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(i);
	}
	
	private class AutoLoginAsyncTask extends AsyncTask<String, Void, HttpResponse> {
		
		Activity activity;
		
		public AutoLoginAsyncTask(Activity activity) {
			this.activity = activity;
		}
		
		@Override
		protected HttpResponse doInBackground(String... params) {
			String username = params[0];
			String password = params[1];
			String loginURL = Constants.getLoginURL(username, password);
			return SingletonHttpClient.getInstance().executeGet(loginURL);
		}

		@Override
		protected void onPostExecute(HttpResponse response) {
			Intent i = new Intent(activity,FolderActivity.class);
			Bundle b = new Bundle();
			b.putBoolean("isRoot", true);
			b.putString("name", "Chapoo");
			i.putExtras(b);

			activity.startActivity(i);
		}
	}
}
