package com.example.myfirstapp;

import java.util.ArrayList;
import java.util.List;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;

public class SearchActivity extends BaseActivity {

	// array list for storing item details
	ArrayList<ItemDetails> itemList = new ArrayList<ItemDetails>();
	private List<ItemDetails> items;
	Context context = this;
	
	private EditText seachEditText;
	private String itemTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// drawer setup
		LayoutInflater inflater = (LayoutInflater) this
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View contentView = inflater.inflate(R.layout.activity_search, null, false);
	    mDrawerLayout.addView(contentView, 0); 
	    
		// clear the background
	    LinearLayout  linearLayout = (LinearLayout) findViewById(R.id.linearLayoutid);
		 linearLayout.setBackgroundResource(0);

		seachEditText = (EditText) findViewById(R.id.myFilter);
		
		// search button
        View btnSearch = findViewById(R.id.search_button);
        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	itemTitle = seachEditText.getText().toString();
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
	
	// search item via title
	private void searchItem(String theTitle) {

		// query
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Inventory");
		query.whereEqualTo("author", ParseUser.getCurrentUser());
		query.whereEqualTo("title", theTitle);

		// progress spinner
		setProgressBarIndeterminateVisibility(true);

		query.findInBackground(new FindCallback<ParseObject>() {

			@SuppressWarnings("unchecked")
			@Override
			public void done(List<ParseObject> postList, ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					// If there are results, update the list of items
					// and notify the adapter
					items.clear();
					for (ParseObject post : postList) {
						ItemDetails note = new ItemDetails(post.getObjectId(), post.getString("title"), post.getString("description"), post.getString("category"), post.getParseFile("photo"), post.getString("author_artist"),post.getString("releaseDate"));
						items.add(note);
					}
				} else {
					Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
				}
			}
		});
	}
}