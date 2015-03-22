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
		//setContentView(R.layout.activity_export);
		
		/*

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
		*/
		
		deleteItem();
		
		
		File exportDir = new File(Environment.getExternalStorageDirectory()
                .getPath(), "/sqliteDB");
		
		File file = new File(exportDir, "exportInv.csv");
		
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
		final List<String> title_list = new ArrayList<String>();
		final List<String> desc_list = new ArrayList<String>();
		final List<String> category_list = new ArrayList<String>();
		final List<String> tester = new ArrayList<String>();
		final List<String[]> database = new ArrayList<String[]>();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Inventory");
		query.whereEqualTo("author", ParseUser.getCurrentUser());
		query.setLimit(1000);
		query.findInBackground(new FindCallback<ParseObject>() {

			@SuppressWarnings("unchecked")
			@Override
			public void done(List<ParseObject> postList, ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					// If there are results, update the list of posts
					// and notify the adapter
					// iterate over all messages and delete them
		            for(ParseObject message : postList)
		            {
		            	 //tester.add(message.get("title").toString() + "," + message.get("description").toString() + "," + message.get("category").toString());
		                 //title_list.add(message.get("title").toString());
		                 //desc_list.add(message.get("description").toString());
		                 //category_list.add(message.get("category").toString());
		                 database.add(new String[] {message.get("title").toString(), message.get("description").toString(), message.get("category").toString()});
		            }
		            for (String[] strings : database) {
		                System.out.println(Arrays.toString(strings));
		            }
		             //System.out.println(tester);
		             
		             /*ArrayList to Array Conversion */
		     		String array[] = new String[tester.size()];              
		     		for(int j =0;j<tester.size();j++){
		     		  array[j] = tester.get(j);
		     		}
		     		
		     		//System.out.println("arr 0: " + array[0]);
		     		
		     		/*Displaying Array elements*/
		    		for(String k: array)
		    		{
		    			//System.out.println(k);
		    		}
		            
		            String allIds = TextUtils.join(",", title_list);
		            //System.out.println(allIds);
		            String[] ary = allIds.split(",");

		            
		            Log.i("Result", allIds);
		            
		            String[] header= new String[]{"Title","Description","Category"};
		            
		            /*
		        	for (int i = 0; i < title_list.size(); i++) {
		        	    
		        	    System.out.println("Element: " + title_list);
		        	}
		        	*/
		    		File exportDir = new File(Environment.getExternalStorageDirectory()
		                    .getPath(), "/sqliteDB");
		    		
		    		if (!exportDir.exists())
		            {
		                exportDir.mkdirs();
		            }
		            
		    		File file = new File(exportDir, "exportInv.csv");
		    		
		    		
		    		try{
		    			
		    			file.createNewFile();               
		                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
		                csvWrite.writeNext(header);
		                csvWrite.writeAll(database);
		                csvWrite.close();
		    			
		    			
		    		}
		    		catch(Exception ex){
		    			
		    		}
		            
		            //System.out.println("title size: " + title_list.size());
		            //System.out.println("desc size: " + description_list.size());
		            //System.out.println("category size: " + category_list.size());
				} else {
					Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
					System.out.println(e.getMessage());
				}

			}

		});
	}
	
	
	
	
	
	
	
	
	
	
}



