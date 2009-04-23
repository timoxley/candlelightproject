package com.candlelightproject.lifemap;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.candlelightproject.lifemap.contacts.ContactManager;
import com.candlelightproject.lifemap.graph.ContactNode;
import com.candlelightproject.lifemap.view.graphview.GraphView;
import com.candlelightproject.lifemap.R;

public class LifeMap extends Activity {
	
	/** This static field is used such that other classes can use this activities' context */
	public static Activity instance = null; 
	
	/** A handle to the thread that's actually running the animation. */
    private Thread mMapThread;

    /** A handle to the View in which the game is running. */
    private View mView;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
    	
    	// turn off the window's title bar
	    requestWindowFeature(Window.FEATURE_NO_TITLE);    
        setContentView(R.layout.main);
	
	   
	
	    if (savedInstanceState == null) {
	        // we were just launched: set up a new map
	    	
	    } else {
	        // we are being restored: resume a previous map
	        mMapThread.restoreState(savedInstanceState);
	    }
        
    }
    
    private void loadDefaultView() {
    	
    	 // get handles to the LunarView from XML, and its LunarThread
	    mView = (GraphView) findViewById(R.id.lifemap);
	    //mMapThread = mMapView.getThread();
    	
    }
    
    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mView.getThread().pause(); // pause game when Activity pauses
    }
    
    public static Activity getInstance() {
    	return instance;
    }
	
	
}