package com.example.myfirstapp;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseException;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FoundItemActivity extends ListActivity {
	
	private List<ItemDetails> items;
	private ArrayList<String> titleList;
	private String title;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_item);
		
		
		items = new ArrayList<ItemDetails>();
		//Get the bundle
		Bundle bundle = getIntent().getExtras();
		
		//Extract the data…
		if(bundle != null){
			title = bundle.getString("term");
			title = title.toLowerCase();
			
		}
		searchItem(title);

		
		
		/*
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			title = extras.getString("title");
		    //String value = extras.getString("barcode");
		    //txtScanResult.setText(title);
			//titleEditText.setText(title);
			
		    
		}
		*/
		
		titleList = new ArrayList<String>();
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_layout, titleList);
		setListAdapter(adapter);
		//searchItem(title);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.found_item, menu);
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
	
	
	
	
	private void searchItem(String theTitle) {

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Inventory");
		query.whereEqualTo("author", ParseUser.getCurrentUser());
		query.whereContains("title", title);

		setProgressBarIndeterminateVisibility(true);

		query.findInBackground(new FindCallback<ParseObject>() {

			@SuppressWarnings("unchecked")
			@Override
			public void done(List<ParseObject> itemList, ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					// If there are results, update the list of posts
					// and notify the adapter
		        	for (ParseObject ob : itemList) {
		        		titleList.add(ob.getString("title"));
						ItemDetails note = new ItemDetails(ob.getObjectId(), ob.getString("title"), ob.getString("description"), ob.getString("category"), ob.getParseFile("photo"));
						items.add(note);
		        	}
					((ArrayAdapter<ItemDetails>) getListAdapter()).notifyDataSetChanged();
				} else {
					Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
				}

			}

		});

	}
	
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	 
		ItemDetails theItems = items.get(position);

	    Intent intent = new Intent(this, AddItemActivity.class);
	    intent.putExtra("Uniqid","from_List_Activity"); 
	    intent.putExtra("itemId", theItems.getId());
	    intent.putExtra("itemTitle", theItems.getTitle());
	    intent.putExtra("itemContent", theItems.getContent());
	    intent.putExtra("itemCategory", theItems.getCategory());
	    intent.putExtra("itemImage", theItems.getPhoto().getUrl());
	    //System.out.println(theItems.getPhoto().getUrl());
	    startActivity(intent);
	    finish();
	 
	}
	
	
	
	
	
	
}
