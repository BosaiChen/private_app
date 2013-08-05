package com.simbiosys.chapooapp;

import org.apache.http.HttpResponse;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener{

	private String username, password;
	private EditText usernameET, passwordET;
	private ProgressDialog pd;
	private CheckBox rememberCB;
	private Boolean ifRemember;
	private SingletonHttpClient myHttpClient;
	private TextView forgetTV;
	private Button loginBT;
	private Activity context;

	SharedPreferences sp;


	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("login","ondestroy");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		context = this;

		usernameET = (EditText)findViewById(R.id.usernameET);
		passwordET = (EditText)findViewById(R.id.passwordET);
		rememberCB = (CheckBox)findViewById(R.id.checkBox1);

		forgetTV = (TextView)findViewById(R.id.forgetTV);
		loginBT = (Button)findViewById(R.id.loginButton);
		
		forgetTV.setOnClickListener(this);
		loginBT.setOnClickListener(this);
		
		Log.v("login","oncreate");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void login() {

		username = usernameET.getText().toString();
		password = passwordET.getText().toString();
		ifRemember = rememberCB.isChecked();

	//	username = "bchen04@syr.edu";
	//	password = "19880227";

		if(username.equals("")) 
			Toast.makeText(this, "Username required...", Toast.LENGTH_SHORT).show();
		else if(password.equals("")) 
			Toast.makeText(this, "Password required...", Toast.LENGTH_SHORT).show();
		else {
			//login
			pd = ProgressDialog.show(this, "",
					"Login...", true);

			new LoginAsyncTask().execute(username, password);
		}
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
			if(pd != null) 
				pd.dismiss();
			int statusCode = SingletonHttpClient.getStatusCode(response);
			if(SingletonHttpClient.isStatusOK(statusCode)) {
				//logged in
				if(ifRemember == true){
					//store username&password for auto login
					sp= getSharedPreferences("LoginFile", MODE_PRIVATE); 
					SharedPreferences.Editor spEditor= sp.edit();
					spEditor.putString("username", username);
					spEditor.putString("password", password);
					spEditor.putBoolean("isLogged", true);
					spEditor.commit();	
				}
				/*SharedPreferences settings=context.getApplicationContext().getSharedPreferences("LoginFile", MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("isLogged", true);
				Log.v("isLogged",Boolean.toString(settings.getBoolean("isLogged", false)));
				editor.commit();*/
				
				Intent i = new Intent(getBaseContext(),FolderActivity.class);
				Bundle data = new Bundle();
				data.putBoolean("isRoot",true);
				data.putString("name","Chapoo");
				i.putExtras(data);
				startActivity(i);
				
				
			//	Helper.startActivity(getBaseContext(), SubFolderActivity.class, data);
			} else {
				//login failed
				Toast.makeText(getBaseContext(), "Oops, please sign up first...", Toast.LENGTH_SHORT).show();
			}
			
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id) {
			case R.id.forgetTV:
				startActivity(new Intent(this, ForgetPassActivity.class));
				break;
			case R.id.loginButton:
				login();
				break;
		}

	}
}