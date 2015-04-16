package com.example.myfirstapp;

import java.util.ArrayList;
import java.util.List;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseException;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FoundItemActivity extends ListActivity {
	
	private List<ItemDetails> items;
	private ArrayList<String> titleList;
	private String title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_item);
		
		// check if network available
        if(!isNetworkAvailable()){
        	
        	showNetToast();
        }
		// array list of item details
		items = new ArrayList<ItemDetails>();
		//Get the bundle
		Bundle bundle = getIntent().getExtras();
		
		//Extract the data…
		if(bundle != null){
			title = bundle.getString("term");
			title = title.toLowerCase();
		}
		
		// search the item
		searchItem(title);

		titleList = new ArrayList<String>();
		
		// array for list view
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_layout, titleList);
		setListAdapter(adapter);
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
	
	
	
	// search for the item based on the title
	private void searchItem(String theTitle) {

		// connect to database
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
					// If there are results, update the list of items
					// and notify the adapter
		        	for (ParseObject ob : itemList) {
		        		titleList.add(ob.getString("title"));
						ItemDetails note = new ItemDetails(ob.getObjectId(), ob.getString("title"), ob.getString("description"), ob.getString("category"), ob.getParseFile("photo"), ob.getString("releaseDate"), ob.getString("author_artist"));
						items.add(note);
		        	}
					((ArrayAdapter<ItemDetails>) getListAdapter()).notifyDataSetChanged();
				} else {
					Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
				}
			}
		});
	}
	
	// on item click
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
	    startActivity(intent);
	    finish();
	}
	
	// check for network
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
    private void showNetToast(){
		 Toast toast = Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG);
		 View view = toast.getView();
		 view.setBackgroundResource(R.color.toast_nointernet_color);
		 TextView text = (TextView) view.findViewById(android.R.id.message);
		 /*here you can do anything with text*/
		 toast.show();

    }
	
	
	
	
	
}
