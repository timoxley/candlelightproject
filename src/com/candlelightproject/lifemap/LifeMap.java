package com.candlelightproject.lifemap;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.candlelightproject.lifemap.contacts.ContactManager;
import com.candlelightproject.lifemap.graph.ContactNode;
import com.candlelightproject.lifemap.MapView;
import com.candlelightproject.lifemap.R;
import com.candlelightproject.lifemap.MapThread;

public class LifeMap extends Activity {
	
	/** This static field is used such that other classes can use this activities' context */
	public static Activity me = null; 
	
	/** Temporary internal data model for the contacts currently on the phone. */
	public ContactNode[] contacts;
	
	/** A handle to the thread that's actually running the animation. */
    private MapThread mMapThread;

    /** A handle to the View in which the game is running. */
    private MapView mMapView;
    
    
    public static final int INSERT_ID = Menu.FIRST;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
    	
    	// turn off the window's title bar
	    requestWindowFeature(Window.FEATURE_NO_TITLE);    
        setContentView(R.layout.main);
        
        me = this;
        
        contacts = ContactManager.getContactDataFromPhone();
        ContactManager.setupTestContactsIfNeeded(contacts);
        
        // Search and update contact field example:
        int foundId = ContactManager.getContactId(contacts, "Shin Phuong");
        if (foundId > 0) {
        	ContactManager.updateContactRecordOnPhone(foundId, People.NAME, "UpdatedName");
        }
	
	    // get handles to the LunarView from XML, and its LunarThread
	    mMapView = (MapView) findViewById(R.id.lifemap);
	    mMapThread = mMapView.getThread();
	
	    // give the LunarView a handle to the TextView used for messages
	    mMapView.setTextView((TextView) findViewById(R.id.text));
	
	    if (savedInstanceState == null) {
	        // we were just launched: set up a new game
	        mMapThread.setState(MapThread.STATE_READY);
	    } else {
	        // we are being restored: resume a previous game
	        mMapThread.restoreState(savedInstanceState);
	    }
        
    }
    
    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.getThread().pause(); // pause game when Activity pauses
    }

    /**
     * Notification that something is about to happen, to give the Activity a
     * chance to save state.
     * 
     * @param outState a Bundle into which this Activity should save its state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // just have the View's thread save its state into our Bundle
        super.onSaveInstanceState(outState);
        mMapThread.saveState(outState);
        Log.w(this.getClass().getName(), "SIS called");
    }
    
    /**
     * Invoked during init to give the Activity a chance to set up its Menu.
     * 
     * @param menu the Menu to which entries may be added
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return true;
    }

    /**
     * Invoked when the user selects an item from the Menu.
     * 
     * @param item the Menu entry which was selected
     * @return true if the Menu item was legit (and we consumed it), false
     *         otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	
	    	case R.id.contact_node:
				return true;
				
	    	case R.id.calendar_node:
				return true;
    	
    		case R.id.text_node:
    			return true;
        }

        return false;
    }
	
}














































