package com.candlelightproject.lifemap.graph;

import org.xml.sax.Attributes;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.Contacts.People;

/**
 * @author Duane Edwards
 *
 */
public class ContactNode extends Node {
		
	private int contactId;
	private String name;
	private String phoneNumber;
	private String primaryEmail;
	
	public ContactNode() {
		super(-1);
		name = "";
		phoneNumber = "";
		primaryEmail = "";
	}
	
	public ContactNode(int parent) {
		super(parent);
		name = "";
		phoneNumber = "";
		primaryEmail = "";
		
	}
	
	public ContactNode(int parent, int aId, String aName, String aPhoneNumber, String aPrimaryEmail) {
		super(parent);
		contactId = aId;
		name = aName;
		phoneNumber = aPhoneNumber;
		primaryEmail = aPrimaryEmail;
		
	}
	
	@Override
	public boolean load(String name, Attributes atts) {
		try {
			if(name.equals("ContactNode")) {
				String contactId = atts.getValue("contactId");
				String aName = atts.getValue("name");
				String phoneNumber = atts.getValue("phoneNumber");
				String email = atts.getValue("primaryEmail");
				if(aName == null || contactId == null || phoneNumber == null || email == null) {
					System.err.println("Required attribute not specified.");
					return false;
				}
				this.contactId = new Integer(contactId);
				this.name = aName;
				this.phoneNumber = phoneNumber;
				this.primaryEmail = email;
				return true;
			}
		} catch (Exception e) {
			System.err.println("Invalid contact id.");
			return false;
		}
		return super.load(name, atts);
	}
		
	@Override
	public String toString() {
		return contactId + "," + name + "," + phoneNumber + "," + primaryEmail;
		
	}
	
	@Override
	public String saveString() {
		String result = "<ContactNode id=\""+this.id+"\" contactId=\""+contactId+"\"";
		result += " name=\""+name.replace("\"", "&quot;")+"\" phoneNumber=\""+phoneNumber+"\" primayEmail=\""+
				primaryEmail+"\">\n";
		for(int i: neighbours) {
			result += "<neighbour id=\""+i+"\" />\n";
		}
		result += "</ContactNode>";
		
		return result;
	}
	
	public int getContactId() {
		return contactId;
	}

	public void setContactId(int id) {
		this.contactId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPrimaryEmail() {
		return primaryEmail;
	}

	public void setPrimaryEmail(String primaryEmail) {
		this.primaryEmail = primaryEmail;
	}
	
	public Graph.NodeType getType() {
		return Graph.NodeType.CONTACT_NODE;
	}
		
}