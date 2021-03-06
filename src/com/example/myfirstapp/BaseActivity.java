package com.example.myfirstapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import jim.h.common.android.lib.zxing.config.ZXingLibConfig;
import jim.h.common.android.lib.zxing.integrator.IntentIntegrator;
import jim.h.common.android.lib.zxing.integrator.IntentResult;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myfirstapp.amazonSigner.SignedRequestsHelper;
import com.example.myfirstapp.camerabase.CaptureActivity;
import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;
import com.parse.ParseUser;

public class BaseActivity extends Activity {

    protected DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private LinearLayout mLinear;
    
    FrameLayout frameLayout;
    
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;

    Context context = this;
	boolean gotCode = false;
	String thecode = "";
	String theTitle = "";
	String ocrText = "";
	Boolean scan = false;
    
	org.jsoup.nodes.Document doc1;
	
    private Handler        handler = new Handler();
    private ZXingLibConfig zxingLibConfig;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	 
    	 // is network available
    	if(!isNetworkAvailable()){
    		 showNetToast();
    	}
    	
    	
    	if (android.os.Build.VERSION.SDK_INT > 9) {
    	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	    StrictMode.setThreadPolicy(policy);
    	}
    	
    	// check for user
    	ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            loadLoginView();
        }
        
        // barcode scanner obj
        zxingLibConfig = new ZXingLibConfig();
        zxingLibConfig.useFrontLight = true;
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        // material drawer setup
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);
        mLinear = (LinearLayout) findViewById(R.id.header);

        drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
            drawerArrow, R.string.drawer_open,
            R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // drawer menu items
        String[] values = new String[]{
            "Inventory",
            "Scan Barcode",
            "Scan ISBN",
            "Categories",
            "Search",
            "Export",
            "Help"
        };
        // set drawer list
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, android.R.id.text1, values);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ResourceAsColor") @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
        			    Intent intent = new Intent(context, ListItemActivity.class);
                        startActivity(intent);   
                        break;
                    case 1:
                    	IntentIntegrator.initiateScan(BaseActivity.this, zxingLibConfig);
                        break;
                    case 2:
        			    Intent intent2 = new Intent(context, CaptureActivity.class);
        			    intent2.putExtra("Uniqid","from_Main"); 
        			    startActivityForResult(intent2, 11);  
                        break;
                    case 3:
        			    Intent intent3 = new Intent(context, ListCategoryActivity.class);
                        startActivity(intent3);   
                        break;
                    case 4:
        			    Intent intent6 = new Intent(context, SearchActivity.class);
                        startActivity(intent6);     
                        break;
                    case 5:
        			    Intent intent5 = new Intent(context, ExportActivity.class);
                        startActivity(intent5);   
                        break;
                    case 6:
        			    Intent intent7 = new Intent(context, HelpActivity.class);
                        startActivity(intent7);   
                        break;
                }
            }
        });
    }
    
    //  get the xml from amazon
private String amazonReturnDetails(String barcode) throws InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException, SAXException, IOException{
		
		// create amazon request obj
		SignedRequestsHelper hq = new SignedRequestsHelper();
		
		Map<String, String> map = new HashMap<String, String>();
	
		map.put("AssociateTag", "PutYourAssociateTagHere");
		map.put("Keywords", barcode);
		map.put("Operation", "ItemSearch");
		map.put("SearchIndex", "All");
		map.put("ResponseGroup", "EditorialReview, Medium, Images");
		map.put("Service", "AWSECommerceService");
		map.put("Version", "2011-08-01");
		
		// pass map to helper obj to get the signed url
		String signedUrl = hq.sign(map);
		
		URL url = null;
		String inputLine;

		String theXML = "";
		
		// get the xml from the signed url and reutrn it
		try {
		    url = new URL(signedUrl);
		} catch (MalformedURLException e) {
		    e.printStackTrace();
		}
		BufferedReader in;
		try {
		    URLConnection con = url.openConnection();
		    con.setReadTimeout( 1000 ); //1 second
		    in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		    while ((inputLine = in.readLine()) != null) {
		    	theXML = inputLine;
		    }
		    in.close();

		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		return theXML;
	}

	// parse the xml to get the data
private static Map<String,String> parseXML(String xml) throws ParserConfigurationException, SAXException, IOException{
	
	Map<String,String> xmlValues = new HashMap<String,String>();
	
	boolean isFirst = true;
	
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    InputSource is = new InputSource(new StringReader(xml));
    Document doc = builder.parse(is);

    // normalize text representation
    doc.getDocumentElement ().normalize ();

    NodeList listOfPersons = doc.getElementsByTagName("ItemAttributes");

    for(int s=0; s<listOfPersons.getLength() ; s++){

        Node firstPersonNode = listOfPersons.item(s);
        if(firstPersonNode.getNodeType() == Node.ELEMENT_NODE && isFirst){

            Element firstPersonElement = (Element)firstPersonNode;

         // PRODUCT GROUP
            NodeList ProductGroupNodeElement = firstPersonElement.getElementsByTagName("ProductGroup");
            if(ProductGroupNodeElement.getLength() == 1){
                Element ProductGroupElement = (Element)ProductGroupNodeElement.item(0);

                NodeList ProductGroupText = ProductGroupElement.getChildNodes();
                xmlValues.put("ProductGroup", ((Node)ProductGroupText.item(0)).getNodeValue().trim());
            }
            else
            {
            	xmlValues.put("ProductGroup", "");
            }
            
            //TITLE
            NodeList lastNameList = firstPersonElement.getElementsByTagName("Title");
            if(lastNameList.getLength() == 1){
            	
            Element lastNameElement = (Element)lastNameList.item(0);

            NodeList textLNList = lastNameElement.getChildNodes();
            		xmlValues.put("Title", ((Node)textLNList.item(0)).getNodeValue().trim());
            }
            else
            {
            	xmlValues.put("Title", "");
            }

            //MANUFACTURER
            NodeList firstNameList = firstPersonElement.getElementsByTagName("Manufacturer");
            if(firstNameList.getLength() == 1){
            	
            Element firstNameElement = (Element)firstNameList.item(0);

            NodeList textFNList = firstNameElement.getChildNodes();
            		xmlValues.put("Manufacturer", ((Node)textFNList.item(0)).getNodeValue().trim());
            }
            else
            {
            	xmlValues.put("Manufacturer", "");           	
            }
            
            // AUTHOR
            NodeList authorNodeElement = firstPersonElement.getElementsByTagName("Author");
            if(authorNodeElement.getLength() == 1){
                Element authorNameElement = (Element)authorNodeElement.item(0);

                NodeList authorText = authorNameElement.getChildNodes();
                xmlValues.put("Author", ((Node)authorText.item(0)).getNodeValue().trim());
            }
            else
            {
            	xmlValues.put("Author", "");
            }
            
            // ARTIST
            NodeList artistNodeElement = firstPersonElement.getElementsByTagName("Artist");
            if(artistNodeElement.getLength() == 1){
                Element artistNameElement = (Element)artistNodeElement.item(0);

                NodeList artistText = artistNameElement.getChildNodes();
                xmlValues.put("Artist", ((Node)artistText.item(0)).getNodeValue().trim());
            }
            else
            {
            	xmlValues.put("Artist", "");
            }
            
            // RELEASE DATE
            NodeList releaseNodeElement = firstPersonElement.getElementsByTagName("ReleaseDate");
            if(releaseNodeElement.getLength() == 1){
                Element releaseElement = (Element)releaseNodeElement.item(0);

                NodeList releaseText = releaseElement.getChildNodes();
                xmlValues.put("Release", ((Node)releaseText.item(0)).getNodeValue().trim());       	
            }
            else
            {
            	xmlValues.put("Release", "");
            }
            
            // PUBLICATION DATE
            NodeList publishNodeElement = firstPersonElement.getElementsByTagName("PublicationDate");
            if(publishNodeElement.getLength() == 1){
                Element publishElement = (Element)publishNodeElement.item(0);

                NodeList publishText = publishElement.getChildNodes();
                xmlValues.put("Publish", ((Node)publishText.item(0)).getNodeValue().trim());
            }
            else
            {
            	xmlValues.put("Publish", "");
            }
            isFirst = false;

        }//end of if clause
    }//end of for loop with s var
    
    isFirst = true;
    
    NodeList listofDesc = doc.getElementsByTagName("EditorialReviews");
    
    for(int s=0; s<listofDesc.getLength() ; s++){

        Node firstPersonNode = listofDesc.item(s);
        if(firstPersonNode.getNodeType() == Node.ELEMENT_NODE && isFirst){

            Element firstPersonElement = (Element)firstPersonNode;

            //CONTENT
            NodeList firstNameList = firstPersonElement.getElementsByTagName("Content");
            
                Element firstNameElement = (Element)firstNameList.item(0);

                NodeList textFNList = firstNameElement.getChildNodes();
                xmlValues.put("Content", ((Node)textFNList.item(0)).getNodeValue().trim());

            isFirst = false;
        }
    }
    
    isFirst = true;
    
    NodeList offerSummary = doc.getElementsByTagName("OfferSummary");

    for(int s=0; s<offerSummary.getLength() ; s++){

        Node firstPersonNode = offerSummary.item(s);
        if(firstPersonNode.getNodeType() == Node.ELEMENT_NODE && isFirst){

            Element firstPersonElement = (Element)firstPersonNode;

            //CONTENT
            NodeList firstNameList = firstPersonElement.getElementsByTagName("FormattedPrice");
            
                Element firstNameElement = (Element)firstNameList.item(0);

                NodeList textFNList = firstNameElement.getChildNodes();
                xmlValues.put("Price", ((Node)textFNList.item(0)).getNodeValue().trim());
            
            isFirst = false;
        }
    }
    
    isFirst = true;
    
    NodeList mediumImage = doc.getElementsByTagName("LargeImage");

    for(int s=0; s<mediumImage.getLength() ; s++){

        Node medImageNode = mediumImage.item(s);
        if(medImageNode.getNodeType() == Node.ELEMENT_NODE && isFirst){

            Element medElement = (Element)medImageNode;

            //MEDIUM IMAGE
            NodeList firstNameList = medElement.getElementsByTagName("URL");
            
                Element firstNameElement = (Element)firstNameList.item(0);

                NodeList textFNList = firstNameElement.getChildNodes();
                xmlValues.put("Image", ((Node)textFNList.item(0)).getNodeValue().trim());
            
            isFirst = false;
        }
    }
    // return the xml value map
    return xmlValues;
}


@Override
// get result from returned activity
protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	// first barcode scanner activity
	final Context context = this;
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
        case IntentIntegrator.REQUEST_CODE: 
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
                    resultCode, data);

            final String result = scanResult.getContents();
            
            if (result != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                    	
                        String test = "";
                        String item = "";
                        String title = "";
                        String manufacturer = "";
                        String artist = "";
                        String author = "";
                        String description = "";
                        String price = "";
                        String image = "";
                        String publish = "";
                        String release = "";
                        try {
                        	// get the xml data from the map
                        	test = amazonReturnDetails(result);
                        	
                    		item = parseXML(test).get("ProductGroup");
                    		title = parseXML(test).get("Title");
                    		manufacturer =  parseXML(test).get("Manufacturer");
                    		artist =  parseXML(test).get("Artist");
                    		author =  parseXML(test).get("Author");
                    		description =  parseXML(test).get("Content");
                    		price =  parseXML(test).get("Price");
                    		image =  parseXML(test).get("Image");
                    		publish =  parseXML(test).get("Publish");
                    		release =  parseXML(test).get("Release");
                    		
                    		doc1 = Jsoup.parse(description);
                    		description = doc1.text();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("ERROR");
						}
                        
                        Intent intent = new Intent(context, AddItemActivity.class);

                        intent.putExtra("Uniqid","from_Main"); 
                        intent.putExtra("title", title);
                        intent.putExtra("description", description);
                        intent.putExtra("category", item);                        
                        intent.putExtra("price", price);
                        intent.putExtra("image", image);
                        intent.putExtra("manufacturer", manufacturer);

                        // check what type of item, add more fields if necessary
                        if(item != null){
	                        if(item.equals("Book") || item.equals("eBooks")){
	                        	
	                        	intent.putExtra("author", author);
	                        	intent.putExtra("release", publish);
	                        }
	                        if(item.equals("Music")){
	                        	intent.putExtra("author", artist);
	                        	intent.putExtra("release", release);
	                        }
                            if(item.equals("DVD") || item.equals("VideoGames")){
                            	intent.putExtra("release", release);
                            }
                        }
                        else{
                        	intent.putExtra("notFound", "No item found. Please enter it manually");
                        }
                        startActivity(intent); 
                    }
                });
            }
            break;
            // OCR scanner
        case 11: 
        	if(data != null){
        	// replace letters in the text
            ocrText = data.getStringExtra("ocr");
            ocrText = ocrText.replaceAll("[\\s\\-()]", "");
        	
            if (ocrText != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                    	
                        String test = "";
                        
                        String item = "";
                        String title = "";
                        String manufacturer = "";
                        String artist = "";
                        String author = "";
                        String description = "";
                        String price = "";
                        String image = "";
                        String publish = "";
                        String release = "";
                        try {
                        	// get the data from the xml
                        	ocrText = ocrText.replaceAll("[\\s\\-()]", "");

                        	test = amazonReturnDetails(ocrText);
                        	System.out.println(test);
                    		item = parseXML(test).get("ProductGroup");
                    		title = parseXML(test).get("Title");
                    		manufacturer =  parseXML(test).get("Manufacturer");
                    		artist =  parseXML(test).get("Artist");
                    		author =  parseXML(test).get("Author");
                    		description =  parseXML(test).get("Content");
                    		price =  parseXML(test).get("Price");
                    		image =  parseXML(test).get("Image");
                    		publish =  parseXML(test).get("Publish");
                    		release =  parseXML(test).get("Release");
                    		
                    		doc1 = Jsoup.parse(description);
                    		description = doc1.text();
						} catch (Exception e) {
							e.printStackTrace();
						}
                        // add the data to the intent
                        Intent intent = new Intent(context, AddItemActivity.class);

                        intent.putExtra("Uniqid","from_Main"); 
                        intent.putExtra("title", title);
                        intent.putExtra("description", description);
                        intent.putExtra("category", item);                        
                        intent.putExtra("price", price);
                        intent.putExtra("image", image);
                        intent.putExtra("manufacturer", manufacturer);
                        intent.putExtra("publish", publish);
                        
                        // check for item type and add other details if needed
                        if(item != null){
                            if(item.equals("Book") || item.equals("eBooks")){
                            	intent.putExtra("author", author);
                            	intent.putExtra("release", publish);
                            }
                            if(item.equals("Music")){
                            	intent.putExtra("artist", artist);
                            	intent.putExtra("release", release);
                            }
                            if(item.equals("DVD") || item.equals("VideoGames")){
                            	intent.putExtra("release", release);
                            }
                        }
                        else
                        {
                        	intent.putExtra("notFound", "No item found for isbn: " + ocrText + ". Please check isbn number or enter item manually");
                        }
                        // start the activity
                        startActivity(intent); 
                    }
                });
            }
        	}
            break;
        default:
    }
}

// load the login view if logged out
private void loadLoginView() {
	Intent intent = new Intent(this, LoginActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.main_menu, menu);
	return true;
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mLinear)) {
                mDrawerLayout.closeDrawer(mLinear);
            } else {
                mDrawerLayout.openDrawer(mLinear);
            }
        }
        if (item.getItemId() == R.id.action_logout) {
        	ParseUser.logOut();
            loadLoginView();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    // check if network available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
    // show network error message
    private void showNetToast(){
		 Toast toast = Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG);
		 View view = toast.getView();
		 view.setBackgroundResource(R.color.toast_nointernet_color);
		 TextView text = (TextView) view.findViewById(android.R.id.message);
		 /*here you can do anything with text*/
		 toast.show();
		 //Toast.makeText(context,"No Internet Connection",1000).show();
    }
    

    
}


