package com.example.myfirstapp;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.ParseException;

public class ListCategoryItemActivity extends ListActivity {
	
	// declare a class variable that will hold a list of items.
	private List<ItemDetails> items;
	String theCategory = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.list_item);
		Intent intent = this.getIntent();
		theCategory = intent.getStringExtra("theCategory");
		
        items = new ArrayList<ItemDetails>();
        ArrayAdapter<ItemDetails> adapter = new ArrayAdapter<ItemDetails>(this, R.layout.list_item_layout, items);
        setListAdapter(adapter);
     
        refreshItemList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_category_item, menu);
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
	
	
	private void refreshItemList() {

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Inventory");
		query.whereEqualTo("author", ParseUser.getCurrentUser());
		query.whereEqualTo("category", theCategory);
		setProgressBarIndeterminateVisibility(true);

		query.findInBackground(new FindCallback<ParseObject>() {

			@SuppressWarnings("unchecked")
			@Override
			public void done(List<ParseObject> postList, ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					// If there are results, update the list of posts
					// and notify the adapter
					items.clear();
					for (ParseObject post : postList) {
						ItemDetails note = new ItemDetails(post.getObjectId(), post.getString("title"), post.getString("description"), post.getString("category"), post.getParseFile("photo"));
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
