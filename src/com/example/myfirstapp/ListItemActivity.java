package com.example.myfirstapp;

import java.util.ArrayList;
import java.util.List;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.ParseException;

public class ListItemActivity extends ListActivity {
	
	// declare a class variable that will hold a list of items.
	private List<ItemDetails> items;
	boolean deleted = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.list_item);
        
        // if network not available show message
        if(!isNetworkAvailable()){
        	showNetToast();
        }
        
        registerForContextMenu(getListView());
        
        /* create an adapter which manages the data model and adapts it to the individual rows in the list view.
         * Use the given adapter and override the object’s toString() method so that it gives the title of the item.
         */
        
        items = new ArrayList<ItemDetails>();
        ArrayAdapter<ItemDetails> adapter = new ArrayAdapter<ItemDetails>(this, R.layout.list_item_layout, items);
        setListAdapter(adapter);
     
        refreshItemList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // action bar option click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        
        switch (id) {
           case R.id.action_refresh: {
        	refreshItemList();
            break;
        }
           // create new item
        case R.id.action_new: {
            Intent intent = new Intent(this, AddItemActivity.class);
            intent.putExtra("Uniqid","from_List_Activity_Menu"); 
            startActivity(intent);
            break;
        }
        case R.id.action_settings: {
            // Do something when user selects Settings from Action Bar overlay
            break;
        }
        }
        return super.onOptionsItemSelected(item);
    }

    // long press to find which item
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        long pos=getListAdapter().getItemId(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
        ItemDetails theItems = items.get((int) pos);
        theItems = items.get((int) pos);
    }
    // long press item options - delete/refresh
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	ItemDetails theItems;
    	switch(item.getItemId()){
    	case R.id.delete:
    		theItems = items.get(info.position);
    		String itemId = theItems.getId();
    		String itemName = theItems.getTitle();
    		deleteItem(itemId, itemName);
    		ParseObject.createWithoutData("Inventory", itemId).deleteEventually();
    		Toast.makeText(ListItemActivity.this, "You have deleted item: " + " " + itemName , Toast.LENGTH_LONG).show();
    		deleted = true;
    		if (deleted = true){
    			refreshItemList();
    			deleted = false;
    		}
    		break;
    	case R.id.edit:
    		theItems = items.get(info.position);
    		System.out.println("Edit item: " + info.position);
    		break;
    	default:
    		refreshItemList();
    		break;
    	}
              return super.onContextItemSelected(item);
        }
    
    	// refresh item list / show items
		private void refreshItemList() {

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Inventory");
		query.whereEqualTo("author", ParseUser.getCurrentUser());

		setProgressBarIndeterminateVisibility(true);

		query.findInBackground(new FindCallback<ParseObject>() {

			@SuppressWarnings("unchecked")
			@Override
			public void done(List<ParseObject> itemList, ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					// If there are results, update the list of items
					// and notify the adapter
					items.clear();
					for (ParseObject theItem : itemList) {
						ItemDetails myitems = new ItemDetails(theItem.getObjectId(), theItem.getString("title"), theItem.getString("description"), theItem.getString("category"), theItem.getParseFile("photo"), theItem.getString("author_artist"), theItem.getString("releaseDate"));
						items.add(myitems);
					}
					((ArrayAdapter<ItemDetails>) getListAdapter()).notifyDataSetChanged();
				} else {
					Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
				}
			}
		});
	}
	
	// if item is clicked on
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
	    intent.putExtra("itemAuthor_artist", theItems.getAuthor_Artist());
	    intent.putExtra("itemReleaseDate", theItems.getReleaseDate());

	    startActivity(intent);
	}
	
	// delete an item
	public void deleteItem(String itemId, final String itemName){
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Inventory");
		query.whereEqualTo("objectid", itemId);
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
		                 message.deleteEventually();
		            }
				} else {
					Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
				}
			}
		});
	}
	// check is the network available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    // error message if the network is not available
    private void showNetToast(){
		 Toast toast = Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG);
		 View view = toast.getView();
		 view.setBackgroundResource(R.color.toast_nointernet_color);
		 TextView text = (TextView) view.findViewById(android.R.id.message);
		 toast.show();
    }
}