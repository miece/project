package com.example.myfirstapp;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordReset extends Activity {
	// variables
	private EditText emailEditBox;
	private Button resetButton;
	private String email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_reset);
		
		// set up boxes
		emailEditBox = (EditText) findViewById(R.id.emailField);
		resetButton = (Button)findViewById(R.id.resetPasswordButton);
    	
		// reset button click
		resetButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	
	    		email = emailEditBox.getText().toString(); 
	    		// send the email
	    		ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
	    		    public void done(ParseException e) {
	    		        if (e == null) {
	    		            // An email was successfully sent with reset instructions.
	    		        	Toast.makeText(getApplicationContext(), "Password reset instructions sent", Toast.LENGTH_SHORT).show();
	    		        } else {
	    		        	// Something went wrong. Look at the ParseException to see what's up.
	    		        	Toast.makeText(getApplicationContext(), "Error sending Password reset instructions", Toast.LENGTH_SHORT).show();
	    		        }
	    		    }
	    		});
	        }
	    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.password_reset, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}