package com.simbiosys.chapooapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ForgetPassActivity extends Activity{

	private String username;
	private ProgressDialog pd;
	//private Button sendPassButton;
	//private TextView loginTV;
	private EditText usernameET;
	private Activity context;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_pass);
		context = this;
		usernameET = (EditText)findViewById(R.id.emailET);
		Log.v("forget","oncreate");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.forget_pass, menu);
		return true;
	}

	public void sendPass(View v){
		username = usernameET.getText().toString();

		username = "bchen04@syr.edu";
		
		if(username.isEmpty()) 
			Toast.makeText(this, "Username required...", Toast.LENGTH_SHORT).show();
		else {
			//send password
			pd = ProgressDialog.show(this, "",
					"Sending Password...", true);

			new sendPassAsync().execute(username);
		}
	}

	private class sendPassAsync extends AsyncTask<String, Void, Boolean>{

		@Override
		protected Boolean doInBackground(String... arg) {
			username = arg[0];
			List<NameValuePair> postData = new ArrayList<NameValuePair>();
			postData.add(new BasicNameValuePair("fu", username));
			SingletonHttpClient myHttpClient = SingletonHttpClient.getInstance();
			HttpResponse response = myHttpClient.executePost(Constants.URL_FORGET_PASSWORD, postData);
			String responseText = myHttpClient.getResponseText(response);
			if(responseText.contains("error")) {
				return false;
			}
			
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			pd.dismiss();
			if(result == true) {
				Toast.makeText(getBaseContext(), "Please check email...", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(getBaseContext(), LoginActivity.class);
				startActivity(i);
				context.finish();
			} else {
				Toast.makeText(getBaseContext(), "Oops,please try again...", Toast.LENGTH_SHORT).show();
			}
		}
	}
}


