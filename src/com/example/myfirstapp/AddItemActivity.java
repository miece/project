package com.example.myfirstapp;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;


import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddItemActivity extends BaseActivity  {
	
	//private TextView       txtScanResult;
	private EditText name;
	byte[] byteArray;
	Bitmap bp;
	boolean notPhoto = false;
	
	private ParseFile imageFile;
	private ItemDetails item;
	
	private EditText titleEditText;
	private EditText detailsEditText;
	private EditText categoryEditText;
	private String itemTitle;
	private String itemDetails;
	private String itemCategory;
	private String itemImage;
	private ImageView imgView;
	
	// edit text focus storage
	private String evalue;
	private String ocrText;
	
	private Button saveNoteButton;
	private Button cancelButton;
	private ImageButton photoButton;
	private Button ocrButton;
	
	Context context = this;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        new Thread(new Runnable() { 
            public void run(){
               //All your heavy stuff here!!!
            }
        }).start();
		
        //FrameLayout linear = (FrameLayout) findViewById(R.id.content_frame);
        //linear.setBackgroundResource(0);
        
		super.onCreate(savedInstanceState);
		
		//requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		//setContentView(R.layout.add_item);
		LayoutInflater inflater = (LayoutInflater) this
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View contentView = inflater.inflate(R.layout.add_item, null, false);
	    mDrawerLayout.addView(contentView, 0); 
		
	    
		Intent intent = this.getIntent();
		
		
		
		titleEditText = (EditText) findViewById(R.id.itemTitle);
	    detailsEditText = (EditText) findViewById(R.id.itemDetailsInput);
	    categoryEditText = (EditText) findViewById(R.id.itemCategory);

	    
	    imgView = (ImageView) findViewById(R.id.imageView1);
	    int width = 260;
	    int height = 326;
	    LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
	    imgView.setLayoutParams(parms);
	    
	    
	    if(intent !=null)
        {
	        String strdata = intent.getExtras().getString("Uniqid");
		    if(strdata.equals("from_Main"))
	        {
		    	
				if (intent.getExtras() != null) {
					String title = intent.getStringExtra("title");
					String descritpion = intent.getStringExtra("description");
					String category = intent.getStringExtra("category");
					String image = intent.getStringExtra("image");
					
					
					
					titleEditText.setText(title);
				    detailsEditText.setText(descritpion);
				    categoryEditText.setText(category);
				    itemImage = image;
				    
				    try{
				    	
				    	URL url = new URL(image);
				    	HttpGet httpRequest = null;
				    	httpRequest = new HttpGet(url.toURI());
				    	
				    	HttpClient httpclient = new DefaultHttpClient();
				        HttpResponse response = (HttpResponse) httpclient
				                .execute(httpRequest);
				        
				        HttpEntity entity = response.getEntity();
				        BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
				        InputStream input = b_entity.getContent();
				        
				        Bitmap bitmap = BitmapFactory.decodeStream(input);
				        
				        ByteArrayOutputStream stream = new ByteArrayOutputStream();
				        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
				        byteArray = stream.toByteArray();
				        imageFile = new ParseFile("photo.jpg",byteArray);
				        
				        imgView.setImageBitmap(bitmap);
				    	
				    } catch (Exception ex){
				    	
				    }
				}
	        }
	        if(strdata.equals("from_OCR"))
	        {
	        	// From Action settings Menu
	        	// Do Nothing
	        	
	        }
	        if(strdata.equals("from_List_Activity"))
	        {
	    	    if (intent.getExtras() != null) {
	    	        item = new ItemDetails(intent.getStringExtra("itemId"), intent.getStringExtra("itemTitle"), intent.getStringExtra("itemContent"), intent.getStringExtra("itemCategory"));
	    	        String imageUrl = intent.getStringExtra("itemImage");
	    	        titleEditText.setText(item.getTitle());
	    	        detailsEditText.setText(item.getContent());
	    	        categoryEditText.setText(item.getCategory());
	    	        Picasso.with(this).load(imageUrl).into(imgView);
	    	    }
	        }
	        if(strdata.equals("from_List_Activity_Menu"))
	        {
	        	// From Action settings Menu
	        	// Do Nothing
	        	
	        }
	        
        }
	    
	    
	    
	    
	    
	    
	    /*
	    if (intent.getExtras() != null) {
	        item = new ItemDetails(intent.getStringExtra("itemId"), intent.getStringExtra("itemTitle"), intent.getStringExtra("itemContent"), intent.getStringExtra("itemCategory"));
	 
	        titleEditText.setText(item.getTitle());
	        detailsEditText.setText(item.getContent());
	        categoryEditText.setText(item.getCategory());
	        
	    }

		*/
		
		/*
		//txtScanResult = (TextView) findViewById(R.id.scan_result);
		//name = (EditText)findViewById(R.id.itemTitle);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String title = extras.getString("title");
		    //String value = extras.getString("barcode");
		    //txtScanResult.setText(title);
			titleEditText.setText(title);
		    
		}
		*/
	    
	    
	    
		cancelButton = (Button)findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	//Intent intent = new Intent(getApplicationContext(), ListItemActivity.class);
	        	//startActivity(intent);
	        	finish();
	        }
	    });
		
		
	    saveNoteButton = (Button)findViewById(R.id.save_button);
	    saveNoteButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            saveItem();
	            
	        }
	    });
	    
	    photoButton = (ImageButton)findViewById(R.id.camera_button);
	    photoButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            openCamera();
	        }
	    });
	    
	    
        titleEditText.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				evalue = "1";
				return false;
			}
		});
        detailsEditText.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				evalue = "2";
				return false;
			}
		});
       
        
        ocrButton = (Button)findViewById(R.id.ocr_button);
        ocrButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0) {
                if(evalue=="1")
                {
                	Intent intent = new Intent(context, com.example.myfirstapp.camerabase.CaptureActivity.class);
            		//startActivity(intent);
                	startActivityForResult(intent, 1);
            		
                	/*
                    Bundle extras = getIntent().getExtras();
                    if (extras != null) {
                        String ocrText = extras.getString("ocr");
                        titleEditText.setText(ocrText);
                    }
                    */
                	//titleEditText.setText(ocrText);
                    //e1.setText("yes");
                }
                if(evalue=="2")
                {
                	Intent intent = new Intent(context, com.example.myfirstapp.camerabase.CaptureActivity.class);
            		//startActivity(intent);
                	startActivityForResult(intent, 2);
                    //e2.setText("yes");
                }
                if(evalue=="3")
                {
                    //e2.setText("yes");
                }
                else{
                	Toast.makeText(getApplicationContext(), "Please select a text box first then click the ocr button", Toast.LENGTH_SHORT).show();
                }
            }
        });



		
	}
	/*
	private void saveItem(){
		
		itemTitle = titleEditText.getText().toString();
        itemDetails = detailsEditText.getText().toString();
        itemCategory = categoryEditText.getText().toString();
 
        itemTitle = itemTitle.trim();
        itemDetails = itemDetails.trim();
        itemCategory = itemCategory.trim();
 
        // If user doesn't enter a title or content, do nothing
        // If user enters title, but no content, save
        // If user enters content with no title, give warning
        // If user enters both title and content, save
 
        if (!itemTitle.isEmpty()) {
 
            // Check if post is being created or edited
 
            if (item == null) {
                // create new post
 
                ParseObject post = new ParseObject("Inventory");
                post.put("title", itemTitle);
                post.put("description", itemDetails);
                post.put("category", itemCategory);
                post.put("author", ParseUser.getCurrentUser());
                setProgressBarIndeterminateVisibility(true);
                post.saveEventually();
                post.pinInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                    	setProgressBarIndeterminateVisibility(false);
                        if (e == null) {
                            // Saved successfully.
                            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                        	Intent intent = new Intent(getApplicationContext(), ListItemActivity.class);
                        	startActivity(intent);
                        } else {
                            // The save failed.
                            Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
                            Log.d(getClass().getSimpleName(), "User update error: " + e);
                        }
                    }
                });
 
            }
            else {
                // update post
 
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Inventory");
                query.fromLocalDatastore();
                // Retrieve the object by id
                query.getInBackground(item.getId(), new GetCallback<ParseObject>() {
                  public void done(ParseObject post, ParseException e) {
                    if (e == null) {
                      // Now let's update it with some new data.
                        post.put("title", itemTitle);
                        post.put("description", itemDetails);
                        post.put("category", itemCategory);
                        post.put("author", ParseUser.getCurrentUser());
                        setProgressBarIndeterminateVisibility(true);
                        post.saveEventually();
                        post.pinInBackground(new SaveCallback() {
                            public void done(ParseException e) {
                            	setProgressBarIndeterminateVisibility(false);
                                if (e == null) {
                                    // Saved successfully.
                                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                                	Intent intent = new Intent(getApplicationContext(), ListItemActivity.class);
                                	startActivity(intent);
                                } else {
                                    // The save failed.
                                    Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
                                    Log.d(getClass().getSimpleName(), "User update error: " + e);
                                }
                            }
                        });
                    }
                  }
                });
            }
        }
        else if (itemTitle.isEmpty() && !itemDetails.isEmpty() || itemTitle.isEmpty() && !itemCategory.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddItemActivity.this);
            builder.setMessage(R.string.edit_error_message)
                .setTitle(R.string.edit_error_title)
                .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

	}
	
	*/
	
	/*
	 * Old saveItem that only works with the cloud server
	 */
	
	
	private void saveItem() {
		 
        itemTitle = titleEditText.getText().toString();
        itemDetails = detailsEditText.getText().toString();
        itemCategory = categoryEditText.getText().toString();
        
        //imageFile = new ParseFile("meal_photo.jpg", byteArray);
        itemTitle = itemTitle.trim();
        itemDetails = itemDetails.trim();
        itemCategory = itemCategory.trim();
 
        // If user doesn't enter a title or content, do nothing
        // If user enters title, but no content, save
        // If user enters content with no title, give warning
        // If user enters both title and content, save
 
        if (!itemTitle.isEmpty() && !itemCategory.isEmpty()) {
 
            // Check if post is being created or edited
 
            if (item == null) {
                // create new post
 
                ParseObject post = new ParseObject("Inventory");
                itemTitle = itemTitle.toLowerCase();
                itemDetails = itemDetails.toLowerCase();
                itemCategory = itemCategory.toLowerCase();
                post.put("title", itemTitle);
                post.put("description", itemDetails);
                post.put("category", itemCategory);
                
                if(!notPhoto){
                	// if try to save item with no image - give defualt image
                	if(imageFile == null){
                    	bp = BitmapFactory.decodeResource(getResources(), R.drawable.temp);
            	        ByteArrayOutputStream stream = new ByteArrayOutputStream();
            	        bp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            	        byteArray = stream.toByteArray();
            	        imageFile = new ParseFile("photo.jpg",byteArray);
            	        itemImage = "blank";
                	}
                	post.put("image", itemImage);
                    post.put("photo", imageFile);
                    
                    
                }
                
                else{
			        ByteArrayOutputStream stream = new ByteArrayOutputStream();
			        bp.compress(Bitmap.CompressFormat.PNG, 100, stream);
			        byteArray = stream.toByteArray();
			        imageFile = new ParseFile("photo.jpg",byteArray);
                    post.put("photo", imageFile);
                    notPhoto = false;
                }
                
                post.put("author", ParseUser.getCurrentUser());
                setProgressBarIndeterminateVisibility(true);
                post.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                    	setProgressBarIndeterminateVisibility(false);
                        if (e == null) {
                            // Saved successfully.
                            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                        	Intent intent = new Intent(getApplicationContext(), ListItemActivity.class);
                        	//startActivity(intent);
                        	finish();
                        } else {
                            // The save failed.
                            Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
                            Log.d(getClass().getSimpleName(), "User update error: " + e);
                        }
                    }
                });
 
            }
            else {
                // update post
 
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Inventory");
 
                // Retrieve the object by id
                query.getInBackground(item.getId(), new GetCallback<ParseObject>() {
                  public void done(ParseObject post, ParseException e) {
                    if (e == null) {
                      // Now let's update it with some new data.
                        itemTitle = itemTitle.toLowerCase();
                        itemDetails = itemDetails.toLowerCase();
                        itemCategory = itemCategory.toLowerCase();
                        post.put("title", itemTitle);
                        post.put("description", itemDetails);
                        post.put("category", itemCategory);
                        if(notPhoto){
                        	System.out.println("Here");
        			        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        			        bp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        			        byteArray = stream.toByteArray();
        			        imageFile = new ParseFile("photo.jpg",byteArray);
                            post.put("photo", imageFile);
                            notPhoto = false;
                        }
                        
                        post.put("author", ParseUser.getCurrentUser());
                        setProgressBarIndeterminateVisibility(true);
                        post.saveInBackground(new SaveCallback() {
                            public void done(ParseException e) {
                            	setProgressBarIndeterminateVisibility(false);
                                if (e == null) {
                                    // Saved successfully.
                                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                                	Intent intent = new Intent(getApplicationContext(), ListItemActivity.class);
                                	//startActivity(intent);
                                	finish();
                                } else {
                                    // The save failed.
                                    Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
                                    Log.d(getClass().getSimpleName(), "User update error: " + e);
                                }
                            }
                        });
                    }
                  }
                });
            }
        }
        else if (itemTitle.isEmpty() || itemCategory.isEmpty()) { // || itemTitle.isEmpty() && !itemCategory.isEmpty()
        	String alertHeader = "Please Enter: ";
        	String alertTitle = "Title";
        	String alertCate = "Category";
            AlertDialog.Builder builder = new AlertDialog.Builder(AddItemActivity.this);
            builder.setMessage(alertHeader + "\n" + alertTitle + "\n" + alertCate)
                .setTitle(R.string.edit_error_title)
                .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        /*
        else if(imageFile == null){
        	System.out.println(imageFile);
        	System.out.println("title: " +itemTitle);
            AlertDialog.Builder builder = new AlertDialog.Builder(AddItemActivity.this);
            builder.setMessage(R.string.edit_error_message)
                .setTitle(R.string.edit_error_title)
                .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
            Toast.makeText(this, "You have chosen the item: " , Toast.LENGTH_LONG).show();
        }
        */
        /*
        if(imageFile == null){
        	bp = BitmapFactory.decodeResource(getResources(), R.drawable.temp);
	        ByteArrayOutputStream stream = new ByteArrayOutputStream();
	        bp.compress(Bitmap.CompressFormat.PNG, 100, stream);
	        byteArray = stream.toByteArray();
	        imageFile = new ParseFile("photo.jpg",byteArray);
	        itemImage = "blank";
        }
	*/

        /*
        if(itemTitle.isEmpty()){
        	String alert1 = "Please enter";
        	String alert2 = "Title";
        	String alert3 = "Category";
        	String alert4 = "Image";
        	
            AlertDialog.Builder builder = new AlertDialog.Builder(AddItemActivity.this);
            builder.setMessage("Please enter:")
            
                .setTitle(R.string.edit_error_title)
                .setPositiveButton(android.R.string.ok, null);
            builder.setMessage(alert1 +"\n"+ alert2 +"\n"+ alert3 +"\n" + alert4);  

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        */
        
    }
	
	public void openCamera(){
	      Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	      startActivityForResult(intent, 0);
	      
	      
	}
	/*
	   @Override
	   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	      // TODO Auto-generated method stub
	      super.onActivityResult(requestCode, resultCode, data);
	      notPhoto = true;
	      bp = (Bitmap) data.getExtras().get("data");
	      imgView.setImageBitmap(bp);
	   }
	   */
		protected void onActivityResult(int requestCode, int resultCode, Intent data){
		    switch(requestCode){
		        case 0: // Do your stuff here...
		        	if(resultCode != RESULT_CANCELED){
		  	      // TODO Auto-generated method stub
		  	      super.onActivityResult(requestCode, resultCode, data);
		  	      notPhoto = true;
		  	      bp = (Bitmap) data.getExtras().get("data");
		  	      imgView.setImageBitmap(bp);
		        	}
		        break;
		        case 1: // Do your other stuff here...
		        	if(resultCode != RESULT_CANCELED){
		        	System.out.println("got here");
                    ocrText = data.getStringExtra("ocr");
                    titleEditText.setText(ocrText);
		        	}
		        break;
		        case 2: // Do your other stuff here...
                    ocrText = data.getStringExtra("ocr");
                    detailsEditText.setText(ocrText);
		        break;
		    }
		}
	
	
	

}
