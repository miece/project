package com.example.myfirstapp;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;

public class SearchActivity extends Activity {

	ArrayList<ItemDetails> itemList = new ArrayList<ItemDetails>();
	private List<ItemDetails> items;
	Context context = this;
	
	private EditText seachEditText;
	private String itemTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		

		
		seachEditText = (EditText) findViewById(R.id.myFilter);
		
		
        View btnSearch = findViewById(R.id.search_button);
        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	itemTitle = seachEditText.getText().toString();
            	System.out.println(itemTitle);
			    Intent intent = new Intent(context, FoundItemActivity.class);
			    intent.putExtra("term", itemTitle);
                startActivity(intent);   
                
            }
        });
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
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
		query.whereEqualTo("title", theTitle);

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
					//((ArrayAdapter<ItemDetails>) getListAdapter()).notifyDataSetChanged();
				} else {
					Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
				}

			}

		});

	}
	
	
	
	
	
	
	
	
	
	
	
}
