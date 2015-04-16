package com.example.myfirstapp;

import com.parse.Parse;
import android.app.Application;

public class InventoryApplication extends Application {
	
	@Override
	public void onCreate() {
	    super.onCreate();
	 
	    // allow connection to cloud
	    Parse.initialize(this, "saIQWlbzHC5G90hwMY0FdjnuN2dQInrRGMwuCMjy", "bN0TkD4r0ELzZaWX9zlNqoYJnnWUDyK2qSsOTDQT");
	}
}
