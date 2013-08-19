package com.simbiosys.chapooapp;

import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

public class Helper {
	
	public static void startActivity(Context from, Class<?> to, Bundle data) {
		Intent i = new Intent(from, to);
		i.putExtras(data);
		from.startActivity(i);
	}
	
	public static boolean isLoggedIn(Activity context) {
		/*SharedPreferences settings = context.getApplicationContext().getSharedPreferences("LoginFile", Context.MODE_PRIVATE);
		boolean isLogged=settings.getBoolean("isLogged", false);
		if(isLogged){
			Intent i = new Intent(getBaseContext(),FolderActivity.class);
			startActivity(i);
			return;
		}*/
		return false;
	}
	
	public static void setEdittextListener(EditText et, final AlertDialog dialog) {
		et.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.toString().isEmpty()){
					//disable "create" button if folder name is not specified
					dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
				} else {
					dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
				}
			}
		});
	}
	
	public static AlertDialog.Builder createAlertDialog(Context context, String title, View dialogView){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setCancelable(false);
		builder.setView(dialogView);
		return builder;
	}
	
	public static void setAlertDialogCancelBtn(AlertDialog.Builder builder) {
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog,int id) 
			{
				dialog.cancel();
			}
		});
	}
	
	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}
}
