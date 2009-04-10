package com.candlelightproject.lifemap;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.util.Log;

import com.candlelightproject.lifemap.contacts.ContactManager;
import com.candlelightproject.lifemap.graph.ContactNode;

public class LifeMap extends Activity {
	
	// This static field is used such that other
	// classes can use this activities' context
	public static Activity me = null; 
	
	// Temporary internal data model for the contacts
	// currently on the phone.
	public ContactNode[] contacts;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        me = this;
        
        contacts = ContactManager.getContactDataFromPhone();
        ContactManager.setupTestContactsIfNeeded(contacts);
        
        // Search and update contact field example:
        int foundId = ContactManager.getContactId(contacts, "Shin Phuong");
        if (foundId > 0) {
        	ContactManager.updateContactRecordOnPhone(foundId, People.NAME, "UpdatedName");
        }
        
        // LogCat Example (use this instead of System.out):
        Log.d("We have", "made it this far!");
        
    }
	
}














































