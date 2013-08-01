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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends Activity {

	private EditText firstnameET,lastnameET,emailET;
	private Button signupButton;
	private CheckBox acceptterm;
	private Activity context;

	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		context = this;

		firstnameET = (EditText)findViewById(R.id.firstnameET);
		lastnameET = (EditText)findViewById(R.id.lastnameET);
		emailET = (EditText)findViewById(R.id.signupemailET);
		acceptterm = (CheckBox)findViewById(R.id.checkBoxagree);
		signupButton = (Button)findViewById(R.id.signupButton);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.signup, menu);
		return true;
	}

	public void onclickSignup(View v){

		String firstname = firstnameET.getText().toString();
		String lastname = lastnameET.getText().toString();
		String email = emailET.getText().toString();

		
		if(firstname.isEmpty()) 
			Toast.makeText(this, "firstname required...", Toast.LENGTH_SHORT).show();
		else if(lastname.isEmpty()) 
			Toast.makeText(this, "lastname required...", Toast.LENGTH_SHORT).show();
		else if(email.isEmpty()) 
			Toast.makeText(this, "email required...", Toast.LENGTH_SHORT).show();
		else if(!(acceptterm.isChecked()))
			Toast.makeText(this, "Please Check agree terms of use...", Toast.LENGTH_SHORT).show();
		else {
			//signup
			pd = ProgressDialog.show(this, "","Signup...", true);
			new SignupAsyncTask().execute(firstname, lastname, email);
		}
	}

	private class SignupAsyncTask extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			String firstname = params[0];
			String lastname = params[1];
			String email = params[2];
			Log.v("email", email);
			List<NameValuePair> postData = new ArrayList<NameValuePair>();
			postData.add(new BasicNameValuePair("First name", firstname));
			postData.add(new BasicNameValuePair("Last name", lastname));
			postData.add(new BasicNameValuePair("Email", email));
			postData.add(new BasicNameValuePair("Country", "220"));
			postData.add(new BasicNameValuePair("agreeTorTermsOfUse", "true"));

			return loginServer(postData);
		}

		private boolean loginServer(List<NameValuePair> postData){
			SingletonHttpClient myHttpClient = SingletonHttpClient.getInstance();
			HttpResponse response = myHttpClient.executePost(Constants.URL_SIGNUP, postData);
			String responseText = myHttpClient.getResponseText(response);
			Log.v("response", responseText);
			if(responseText.contains("Email sent to")) {
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			pd.dismiss();
			if(result == true) {
				//startActivity(new Intent(getBaseContext(),LoginActivity.class));
				Toast.makeText(getBaseContext(), "Signup succeeded", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(getBaseContext(), LoginActivity.class);
				startActivity(i);
				context.finish();
			} else {
				Toast.makeText(getBaseContext(), "SignUp failed", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
