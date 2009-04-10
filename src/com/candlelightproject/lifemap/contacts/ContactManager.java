package com.candlelightproject.lifemap.contacts;

import java.util.ArrayList;

import com.candlelightproject.lifemap.LifeMap;
import com.candlelightproject.lifemap.graph.ContactNode;
import com.candlelightproject.lifemap.util.StringUtils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.util.Log;

public class ContactManager {
	
	public static void updateContactRecordOnPhone(int recNo, String field, String updatedText) {
    	
		Uri uri = ContentUris.withAppendedId(People.CONTENT_URI, recNo);
		ContentValues values = new ContentValues();
		values.put(field, updatedText);
		((ContextWrapper) LifeMap.me).getContentResolver().update(uri, values, null, null);
	
	}
	
	public static ContactNode[] getContactDataFromPhone() {
		
		// Form an array specifying which columns to return. 
        String[] columns = new String[] {
                                     People._ID,
                                     People.NAME,
                                     People.NUMBER,
                                     People.PRIMARY_EMAIL_ID
                                  };

        // Get the base URI for the People table in the Contacts content provider.
        Uri contacts =  People.CONTENT_URI;

        // Make the query. 
        Cursor managedCursor = LifeMap.me.managedQuery(contacts,
                                 columns, // Which columns to return 
                                 null,       // WHERE clause: Which rows to return (all rows)
                                 null,       // WHERE clause: Selection arguments (none)
                                 null);		 // Order-by clause (sort by name)
        
        ContactNode[] contactData = new ContactNode[managedCursor.getCount()];
		
		if (managedCursor.moveToFirst()) {
			
			int id;
			String name;
			String phoneNumber;
			String primaryEmail;
	        
			int idColumnIndex = managedCursor.getColumnIndex(People._ID); 
			int nameColumnIndex = managedCursor.getColumnIndex(People.NAME);
			int phoneNumberColumnIndex = managedCursor.getColumnIndex(People.NUMBER);
			int primaryEmailColumnIndex = managedCursor.getColumnIndex(People.PRIMARY_EMAIL_ID);
	        
	        do {
	            id = managedCursor.getInt(idColumnIndex);
	            name = managedCursor.getString(nameColumnIndex);
	            phoneNumber = managedCursor.getString(phoneNumberColumnIndex);
	            primaryEmail = managedCursor.getString(primaryEmailColumnIndex);
	            
	            // Any fields that return null (have no entries), convert to empty strings
	            if (name == null) name = "";
	            if (phoneNumber == null) phoneNumber = "";
	            if (primaryEmail == null) primaryEmail = "";
	            
	            if (!StringUtils.isEmptyOrWhitespace(primaryEmail)) {
	            	
	            	// Now we want to retrieve the actual email from the contact_methods table:
		            Uri email = Contacts.ContactMethods.CONTENT_URI;
		            
		            Cursor emailCursor = LifeMap.me.managedQuery(email, 
		            								  new String[] { Contacts.People.ContactMethods.DATA }, 
		            								  // This also works: Contacts.ContactMethods.PERSON_ID+"=\"" + id + "\"",
		            								  Contacts.People.ContactMethods.CONTENT_DIRECTORY + "." + Contacts.People.ContactMethods._ID + "=" + primaryEmail,
		            								  null,
		            								  null);
		            
		            if (emailCursor.moveToFirst()) {
		            	
		            	int dataColumnIndex = emailCursor.getColumnIndex(Contacts.People.ContactMethods.DATA);
		            	primaryEmail = emailCursor.getString(dataColumnIndex);
		            	
		            }  // todo: should we handle multiple email addresses?
		            
	            }
	            
	            contactData[managedCursor.getPosition()] = new ContactNode(0, id, name, phoneNumber, primaryEmail);
	            Log.v("getContactDataFromPhone: field", contactData[managedCursor.getPosition()].toString());
	
	        } while (managedCursor.moveToNext());
	
	    }
	
		return contactData;
		
	}
	
	public static int getContactId(ContactNode[] contacts, String aName) {
		
		for (ContactNode aNode : contacts) {
			
			if(aNode.getName().equals(aName)) {
				return aNode.getId();
				
			}
			
		}
		
		return -1;
		
	}
	
	public static void setupTestContactsIfNeeded(ContactNode[] phoneContacts) {
		
		ArrayList<ContactNode> testContacts = new ArrayList<ContactNode>();
		
		testContacts.add(new ContactNode(-1, -1, "Douglas Catchpole", "0400 000 000", "douglas.catchpole@gmail.com"));
		testContacts.add(new ContactNode(-1, -1, "Duane Edwards", "0400 111 111", "dyuein@gmail.com"));
		testContacts.add(new ContactNode(-1, -1, "Eric Faccer", "0400 222 222", "eric.faccer@gmail.com"));
		testContacts.add(new ContactNode(-1, -1, "Tim Oxley", "0400 333 333", "tim.oxley@gmail.com"));
		testContacts.add(new ContactNode(-1, -1, "Shin Phuong", "0400 444 444", "shin.phuong@gmail.com"));
		
		
		for (ContactNode aTestContact : testContacts) {
		
			if (getContactId(phoneContacts, aTestContact.getName()) == -1) {
				createContact(aTestContact.getName(), aTestContact.getPhoneNumber(), aTestContact.getPrimaryEmail());
			
			} else {
				// todo: the contact exists, should we update the contact details to the defaults?
				
			}
			
		}
		
	}
	
	public static void createContact(String aName, String aPhoneNumber, String aEmail) {

		ContentValues values = new ContentValues();
		values.put(People.NAME, aName);
		Uri newContact = Contacts.People.createPersonInMyContactsGroup(LifeMap.me.getContentResolver(), values);
		
		if (newContact != null) {

			ContentValues mobileValues = new ContentValues();
			Uri mobileUri = Uri.withAppendedPath(newContact, Contacts.People.Phones.CONTENT_DIRECTORY);
			mobileValues.put(Contacts.Phones.NUMBER, aPhoneNumber);
			mobileValues.put(Contacts.Phones.TYPE, Contacts.Phones.TYPE_MOBILE);
			Uri phoneUpdate = LifeMap.me.getContentResolver().insert(mobileUri, mobileValues);
			if (phoneUpdate == null) {
				Log.d("FAIL", "Failed to insert mobile phone number");
			}
			
			ContentValues emailValues = new ContentValues();
			Uri emailUri = Uri.withAppendedPath(newContact, Contacts.People.ContactMethods.CONTENT_DIRECTORY);	
			emailValues.put(Contacts.ContactMethods.KIND, Contacts.KIND_EMAIL);
			emailValues.put(Contacts.ContactMethods.TYPE, Contacts.ContactMethods.TYPE_HOME);
			emailValues.put(Contacts.ContactMethods.DATA, aEmail);
			Uri emailUpdate = LifeMap.me.getContentResolver().insert(emailUri, emailValues);
			if (emailUpdate == null) {
				Log.d("FAIL", "Failed to insert email");
			}

		}
		
	}
	
	private String[] getColumnData(Cursor cur, String column){ 
	    
	    String[] columnData = new String[cur.getCount()];
		
		if (cur.moveToFirst()) {
			
	        String field; 
	        int columnIndex = cur.getColumnIndex(column); 
	    
	        do {
	            field = cur.getString(columnIndex);
	            
	            // convert to empty string, for fields with no entries
	            if (field == null) field = "";
	            
	            columnData[cur.getPosition()] = field;
	            Log.v("getColumnData: field", field);
	
	        } while (cur.moveToNext());
	
	    }
		
		return columnData;
		
	}
	
}
