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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddItemActivity extends BaseActivity  {
	
	// declare variables
	byte[] byteArray;
	Bitmap bp;
	boolean notPhoto = false;
	
	private ParseFile imageFile;
	private ItemDetails item;
	
	private EditText titleEditText;
	private EditText detailsEditText;
	private EditText categoryEditText;
	private EditText authorEditText;
	private EditText publishEditText;
	
	private String itemTitle;
	private String itemDetails;
	private String itemCategory;
	private String itemImage;
	private String itemAuthor_artist;
	private String barcode;
	private String itemReleaseDate;
	
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
		// progress spinner
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// thread
        new Thread(new Runnable() { 
            public void run(){
               //All your heavy stuff here!!!
            }
        }).start();
		
        
		super.onCreate(savedInstanceState);
		
		// set the background to empty
		LinearLayout  linearLayout = (LinearLayout) findViewById(R.id.linearLayoutid);
		linearLayout.setBackgroundResource(0);
		
		// layout for list view
		LayoutInflater inflater = (LayoutInflater) this
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View contentView = inflater.inflate(R.layout.add_item, null, false);
	    mDrawerLayout.addView(contentView, 0); 
		
	    // create an intent
		Intent intent = this.getIntent();
		
		
		authorEditText = (EditText) findViewById(R.id.author_artist);
		titleEditText = (EditText) findViewById(R.id.itemTitle);
	    detailsEditText = (EditText) findViewById(R.id.itemDetailsInput);
	    categoryEditText = (EditText) findViewById(R.id.itemCategory);
	    publishEditText = (EditText) findViewById(R.id.publish_release_date);

	    // setting imgview details
	    imgView = (ImageView) findViewById(R.id.imageView1);
	    int width = 260;
	    int height = 326;
	    LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
	    imgView.setLayoutParams(parms);
	    
	    // if received intent from another activity
	    if(intent !=null)
        {
	    	// which activity
	        String strdata = intent.getExtras().getString("Uniqid");
		    if(strdata.equals("from_Main"))
	        {
				if (intent.getExtras() != null) {
					// item not found
					if(intent.getExtras().containsKey("notFound")){
						Toast.makeText(getApplicationContext(), "Item not found. Please enter it manually", Toast.LENGTH_SHORT).show();
					}
					else{
					// get item details
					String title = intent.getStringExtra("title");
					String descritpion = intent.getStringExtra("description");
					String category = intent.getStringExtra("category");
					String image = intent.getStringExtra("image");
					String author = intent.getStringExtra("author");
					String date = intent.getStringExtra("release");
					
					// set details
					titleEditText.setText(title);
				    detailsEditText.setText(descritpion);
				    categoryEditText.setText(category);
				    authorEditText.setText(author);
				    publishEditText.setText(date);
				    itemImage = image;
				    
				    // set the image into a ParseFile and set imgview
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
				    	// Error
				    }
				}
			  }
	        }
	        if(strdata.equals("from_OCR"))
	        {
	        	// From Action settings Menu
	        	// Do Nothing
	        	
	        }
	        // if item was click on in the list
	        if(strdata.equals("from_List_Activity"))
	        {
	    	    if (intent.getExtras() != null) {
	    	    	// get item details
	    	        item = new ItemDetails(intent.getStringExtra("itemId"), intent.getStringExtra("itemTitle"), intent.getStringExtra("itemContent"), intent.getStringExtra("itemCategory"), intent.getStringExtra("itemAuthor_artist"), intent.getStringExtra("itemReleaseDate"));
	    	        String imageUrl = intent.getStringExtra("itemImage");
	    	        titleEditText.setText(item.getTitle());
	    	        detailsEditText.setText(item.getContent());
	    	        categoryEditText.setText(item.getCategory());
	    	        authorEditText.setText(item.getAuthor_Artist());
	    	        publishEditText.setText(item.getReleaseDate());
	    	        // load img view from url
	    	        Picasso.with(this).load(imageUrl).into(imgView);
	    	    }
	        }
	        if(strdata.equals("from_List_Activity_Menu"))
	        {
	        	// From Action settings Menu
	        	// Do Nothing
	        	
	        }
        }
	    // cancel button
		cancelButton = (Button)findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	finish();
	        }
	    });
		// save item button
	    saveNoteButton = (Button)findViewById(R.id.save_button);
	    saveNoteButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            saveItem();
	        }
	    });
	    // open camera button
	    photoButton = (ImageButton)findViewById(R.id.camera_button);
	    photoButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            openCamera();
	        }
	    });
	    
	    // which textbox was clicked
        titleEditText.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				evalue = "1";
				return false;
			}
		});
        detailsEditText.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				evalue = "2";
				return false;
			}
		});
       
        // put ocr text into whichever text box clicked on
        ocrButton = (Button)findViewById(R.id.ocr_button);
        ocrButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0) {
                if(evalue=="1")
                {
                	Intent intent = new Intent(context, com.example.myfirstapp.camerabase.CaptureActivity.class);
                	startActivityForResult(intent, 1);

                }
                else if(evalue=="2")
                {
                	Intent intent = new Intent(context, com.example.myfirstapp.camerabase.CaptureActivity.class);
                	startActivityForResult(intent, 2);
                }
                else{
                	Toast.makeText(getApplicationContext(), "Please select a text box first then click the ocr button", Toast.LENGTH_SHORT).show();
                }
            }
        });
	}
	
	// save the item to the cloud
	private void saveItem() {
		 
        itemTitle = titleEditText.getText().toString();
        itemDetails = detailsEditText.getText().toString();
        itemCategory = categoryEditText.getText().toString();
        itemAuthor_artist = authorEditText.getText().toString();
        itemReleaseDate = publishEditText.getText().toString();
        
        itemTitle = itemTitle.trim();
        itemDetails = itemDetails.trim();
        itemCategory = itemCategory.trim();
        itemAuthor_artist = itemAuthor_artist.trim();
        itemReleaseDate = itemReleaseDate.trim();
 
        // If user doesn't enter a title or content, do nothing
        // If user enters title, but no content, save
        // If user enters content with no title, give warning
        // If user enters both title and content, save
 
        if (!itemTitle.isEmpty() && !itemCategory.isEmpty()) {
 
            // Check if item is being created or edited
             if (item == null) {
                // create new item
 
                ParseObject post = new ParseObject("Inventory");
                itemTitle = itemTitle.toLowerCase();
                itemDetails = itemDetails.toLowerCase();
                itemCategory = itemCategory.toLowerCase();
                itemAuthor_artist = itemAuthor_artist.toLowerCase();
                itemReleaseDate = itemReleaseDate.toLowerCase();
                
                // save item details
                post.put("title", itemTitle);
                post.put("description", itemDetails);
                post.put("category", itemCategory);
                post.put("author_artist", itemAuthor_artist);
                post.put("releaseDate", itemReleaseDate);
                post.put("barcode", "0");
                if(!notPhoto){
                	// if try to save item with no image - give defualt image
                	if(imageFile == null){
                    	bp = BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
            	        ByteArrayOutputStream stream = new ByteArrayOutputStream();
            	        bp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            	        byteArray = stream.toByteArray();
            	        imageFile = new ParseFile("photo.jpg",byteArray);
            	        itemImage = "blank";
                	}
                	post.put("image", itemImage);
                    post.put("photo", imageFile);
                }
                // else put blank image
                else{
			        ByteArrayOutputStream stream = new ByteArrayOutputStream();
			        bp.compress(Bitmap.CompressFormat.PNG, 100, stream);
			        byteArray = stream.toByteArray();
			        imageFile = new ParseFile("photo.jpg",byteArray);
                    post.put("photo", imageFile);
                    notPhoto = false;
                }
                // save for current user
                post.put("author", ParseUser.getCurrentUser());
                setProgressBarIndeterminateVisibility(true);
                post.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                    	setProgressBarIndeterminateVisibility(false);
                        if (e == null) {
                            // Saved successfully.
                            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
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
                // update item
            	// select from database
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Inventory");
 
                // Retrieve the object by id
                query.getInBackground(item.getId(), new GetCallback<ParseObject>() {
                  public void done(ParseObject post, ParseException e) {
                    if (e == null) {
                      // Now let's update it with some new data.
                        itemTitle = itemTitle.toLowerCase();
                        itemDetails = itemDetails.toLowerCase();
                        itemCategory = itemCategory.toLowerCase();
                        itemAuthor_artist = itemAuthor_artist.toLowerCase();
                        itemReleaseDate = itemReleaseDate.toLowerCase();
                        post.put("title", itemTitle);
                        post.put("description", itemDetails);
                        post.put("category", itemCategory);
                        post.put("author_artist", itemAuthor_artist);
                        post.put("releaseDate", itemReleaseDate);
                        post.put("barcode", "0");
                        if(notPhoto){
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
        // validation for user not entering data and pressing save
        else if (itemTitle.isEmpty() || itemCategory.isEmpty()) {
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

        
    }
	
	public void openCamera(){
	      Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	      startActivityForResult(intent, 0);
	}

		// check which activity returned a result and get the data
		protected void onActivityResult(int requestCode, int resultCode, Intent data){
		    switch(requestCode){
		        case 0:
		        	if(resultCode != RESULT_CANCELED){
		  	      super.onActivityResult(requestCode, resultCode, data);
		  	      notPhoto = true;
		  	      bp = (Bitmap) data.getExtras().get("data");
		  	      imgView.setImageBitmap(bp);
		        	}
		        break;
		        case 1:
		        	if(data!=null){
	                    ocrText = data.getStringExtra("ocr");
	                    titleEditText.setText(ocrText);
                    }
		        break;
		        case 2:
		        	if(data != null){
                    ocrText = data.getStringExtra("ocr");
                    detailsEditText.setText(ocrText);
		        	}
		        break;
		    }
		}
	}