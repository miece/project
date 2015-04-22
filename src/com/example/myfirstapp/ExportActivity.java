package com.example.myfirstapp;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVWriter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

public class ExportActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		deleteItem();
		
		// create a folder on the device
		File exportDir = new File(Environment.getExternalStorageDirectory()
                .getPath(), "/sqliteDB");
		
		// create a new file
		File file = new File(exportDir, "exportInv.csv");
		
		// pass the file to the intent
		Uri u1 = Uri.fromFile(file);
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Inventory CSV");
		sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
		sendIntent.setType("text/richtext");
		startActivity(sendIntent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.export, menu);
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
	
	public void deleteItem(){
		final List<String[]> database = new ArrayList<String[]>();
		
		// query
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Inventory");
		query.whereEqualTo("author", ParseUser.getCurrentUser());
		query.setLimit(1000);
		query.findInBackground(new FindCallback<ParseObject>() {

			@SuppressWarnings("unchecked")
			@Override
			public void done(List<ParseObject> postList, ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e == null) {
		            for(ParseObject message : postList)
		            {
		                 database.add(new String[] {message.get("title").toString(), message.get("description").toString(), message.get("category").toString()});
		            }
		            // header for csv file
		            String[] header= new String[]{"Title","Description","Category"};

		            // export dir
		    		File exportDir = new File(Environment.getExternalStorageDirectory()
		                    .getPath(), "/sqliteDB");
		    		
		    		// create a folder in none exists
		    		if (!exportDir.exists())
		            {
		                exportDir.mkdirs();
		            }
		            
		    		// create file
		    		File file = new File(exportDir, "exportInv.csv");
		    		
		    		try{
		    			file.createNewFile();               
		                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
		                csvWrite.writeNext(header);
		                csvWrite.writeAll(database);
		                csvWrite.close();
		    		}
		    		catch(Exception ex){
		    			// error
		    		}
				} else {
					Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
					System.out.println(e.getMessage());
				}
			}
		});
	}
}