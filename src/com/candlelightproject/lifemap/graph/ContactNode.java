package com.candlelightproject.lifemap.graph;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.Contacts.People;

/**
 * @author Duane Edwards
 *
 */
public class ContactNode extends Node {
		
	private int id;
	private String name;
	private String phoneNumber;
	private String primaryEmail;
	
	public ContactNode(int parent) {
		super(parent);
		name = "";
		phoneNumber = "";
		primaryEmail = "";
		
	}
	
	public ContactNode(int parent, int aId, String aName, String aPhoneNumber, String aPrimaryEmail) {
		super(parent);
		id = aId;
		name = aName;
		phoneNumber = aPhoneNumber;
		primaryEmail = aPrimaryEmail;
		
	}
		
	@Override
	public String toString() {
		return id + "," + name + "," + phoneNumber + "," + primaryEmail;
		
	}
	
	@Override
	public String saveString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
		
}