package com.simbiosys.chapooapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SharedPreferences settings=this.getApplicationContext().getSharedPreferences("LoginFile", MODE_PRIVATE);
		boolean isLogged=settings.getBoolean("isLogged", false);
		/*if(isLogged){
			Intent i = new Intent(getBaseContext(),FolderActivity.class);
			startActivity(i);
			return;
		}*/
		
		Log.v("main","created");
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("main","ondestroy");
	}
}
