package com.example.myfirstapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.parse.FindCallback;
import com.parse.ParseException;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListCategoryActivity extends ListActivity {
	
	//private List<ItemDetails> items;
	private ArrayList<String> categoryList;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_item);
		
		categoryList = new ArrayList<String>();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_layout, categoryList);
		setListAdapter(adapter);
		refreshItemList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_category, menu);
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
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> scoreList, ParseException e) {
		        if (e == null) {

		        	for (ParseObject ob : scoreList) {
		        		categoryList.add(ob.getString("category"));
		        	}
		        	Set<String> set = new HashSet<String>();
		        	set.addAll(categoryList);
		        	categoryList.clear();
		        	categoryList.addAll(set);
		        	//System.out.println(Arrays.toString(categoryList.toArray()));
		        	((ArrayAdapter<ItemDetails>) getListAdapter()).notifyDataSetChanged();
		        } else {
		            Log.d("score", "Error: " + e.getMessage());
		        }
		    }
		});
		
		/*
		ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Inventory");
		parseQuery.whereEqualTo("title", true);
		List<ParseObject> objects = null;
		try {
		    objects = parseQuery.find();
		    System.out.println(objects.size());
		    }
		catch (ParseException e) {
		    Log.e("Error", e.getMessage());
		    e.printStackTrace();
		}
*/


		

		
	}
	

    protected void onListItemClick(ListView l, View v, int position, long id) {
   super.onListItemClick(l, v, position, id);
   Object o = this.getListAdapter().getItem(position);
   String pen = o.toString();
   
   Intent intent = new Intent(this, ListCategoryItemActivity.class);
   intent.putExtra("theCategory",pen); 
   startActivity(intent);
   //Toast.makeText(this, "You have chosen the item: " + " " + pen, Toast.LENGTH_LONG).show();
   
   }
	
	
	
	
}
