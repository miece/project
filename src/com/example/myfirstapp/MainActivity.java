package com.example.myfirstapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
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

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.jsoup.Jsoup;


import com.example.myfirstapp.amazonSigner.SignedRequestsHelper;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import jim.h.common.android.lib.zxing.config.ZXingLibConfig;
import jim.h.common.android.lib.zxing.integrator.IntentIntegrator;
//import jim.h.common.android.lib.zxing.sample.ZXingLibSampleActivity;
//import jim.h.common.android.lib.zxing.sample.R;
import jim.h.common.android.lib.zxing.integrator.IntentResult;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;



public class MainActivity extends Activity {
	
	Context context = this;
	boolean gotCode = false;
	String thecode = "";
	String theTitle = "";
	
	org.jsoup.nodes.Document doc1;
	
    private Handler        handler = new Handler();
    private TextView       txtScanResult;
    private ZXingLibConfig zxingLibConfig;
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	if (android.os.Build.VERSION.SDK_INT > 9) {
    	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	    StrictMode.setThreadPolicy(policy);
    	}
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            loadLoginView();
        }
        
        
		txtScanResult = (TextView) findViewById(R.id.scan_result);
        zxingLibConfig = new ZXingLibConfig();
        zxingLibConfig.useFrontLight = true;
        
        View btnScan = findViewById(R.id.scan_button);
        


        
        btnScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator.initiateScan(MainActivity.this, zxingLibConfig);
                
            }
        });
        
        
        View btnCategory = findViewById(R.id.category_button);
        btnCategory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
			    Intent intent = new Intent(context, ListCategoryActivity.class);
                startActivity(intent);   
                
            }
        });
        
        
        
        addListenerOnButton();
        openViewItem();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_logout) {
        	ParseUser.logOut();
            loadLoginView();
        }
        return super.onOptionsItemSelected(item);
    }
    
    
	public void addListenerOnButton() {
		 
		final Context context = this;
 
		Button button = (Button) findViewById(R.id.help_button);
 
		button.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
 
			    //Intent intent = new Intent(context, AddItemActivity.class);
                //startActivity(intent);   
			}
 
		});
 
	}
	
	public void openViewItem() {
		 
		final Context context = this;
 
		Button button = (Button) findViewById(R.id.inventory_button);
 
		button.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
 
			    Intent intent = new Intent(context, ListItemActivity.class);
                startActivity(intent);   
			}
 
		});
 
	}
	
	
	
	
	
	
	private String amazonReturnDetails(String barcode) throws InvalidKeyException, NoSuchAlgorithmException, ParserConfigurationException, SAXException, IOException{
		
		
		SignedRequestsHelper hq = new SignedRequestsHelper();
		
		Map<String, String> map = new HashMap<String, String>();
	
		map.put("AssociateTag", "PutYourAssociateTagHere");
		map.put("Keywords", barcode);
		map.put("Operation", "ItemSearch");
		map.put("SearchIndex", "All");
		map.put("ResponseGroup", "EditorialReview, Small, Images");
		map.put("Service", "AWSECommerceService");
		map.put("Version", "2011-08-01");
		
		
		String signedUrl = hq.sign(map);
		
		URL url = null;
		String inputLine;

		String theXML = "";
		
		
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
		        //System.out.println(inputLine);
		    }
		    in.close();

		} catch (IOException e) {
		    e.printStackTrace();
		}
		

		return theXML;
				//theTest(theXML);
		
	}
	
	
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
        
        return xmlValues;

	}
	
	

    /**
     * A placeholder fragment containing a simple view.
     */
    /*
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// parse init
    	
    	
    	final Context context = this;
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE: 
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (scanResult == null) {
                    return;
                }
                final String result = scanResult.getContents();
                if (result != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //txtScanResult.setText(result);
                            String value = "";
                            String test = "";
                            
                            String item = "";
                            String title = "";
                            String manufacturer = "";
                            String artist = "";
                            String author = "";
                            String description = "";
                            String price = "";
                            String image = "";
                            try {
                            	// scrape html to get result of title

                            	//txtScanResult.setText(value);
                            	test = amazonReturnDetails(result);
                            	
                        		item = parseXML(test).get("ProductGroup");
                        		title = parseXML(test).get("Title");
                        		manufacturer =  parseXML(test).get("Manufacturer");
                        		artist =  parseXML(test).get("Artist");
                        		author =  parseXML(test).get("Author");
                        		description =  parseXML(test).get("Content");
                        		price =  parseXML(test).get("Price");
                        		image =  parseXML(test).get("Image");
                        		
                        		doc1 = Jsoup.parse(description);
                        		description = doc1.text();
                            	
                        		
                            	
								//Log.d("---------", getBlogStats());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	
                            thecode = txtScanResult.toString();
                            Intent intent = new Intent(context, AddItemActivity.class);
                            
                            /****
                            Bundle extras = new Bundle();
                            //extras.putString("barcode", result);

                            extras.putString("title", value);
                            // parse create object & create field called barcode and title

                            	//intent.putExtra("barcode",result);
                            intent.putExtras(extras);
                            ****/
                            
                            
                            intent.putExtra("Uniqid","from_Main"); 
                            intent.putExtra("title", title);
                            intent.putExtra("description", description);
                            intent.putExtra("category", item);                        
                            intent.putExtra("price", price);
                            intent.putExtra("image", image);
                            intent.putExtra("manufacturer", manufacturer);
                            
                            if(item.equals("Book") || item.equals("eBooks")){
                            	intent.putExtra("author", author);
                            }
                            if(item.equals("Music")){
                            	intent.putExtra("artist", artist);
                            }
                            
                            

                            
                            startActivity(intent); 
                            finish();

                        }
                    });
                }
                break;
            default:
        }
    }
    
	
	private void loadLoginView() {
		Intent intent = new Intent(this, LoginActivity.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	    startActivity(intent);
	}
	
}
