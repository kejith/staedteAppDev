package com.staedte.app.ibbenbueren.lib;

import java.lang.reflect.Field;

import android.content.ContentValues;

import com.staedte.app.ibbenbueren.database.tables.AddressTableInterface;

public class Address {
	
	public int ID;
	public int entryId;
	
	public String title;
	
	public String street;
	public String streetNumber;
	
	public int zipcode;
	public String city;
	
	public String country;
	
	// === CONSTRUCTOR
	public Address() {}
	
	public Address(String title, String street, String streetNumber,
			int zipcode, String city, String country) {
		super();
		this.title = title;
		this.street = street;
		this.streetNumber = streetNumber;
		this.zipcode = zipcode;
		this.city = city;
		this.country = country;
	}

	// === GETTERS/SETTERS
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public int getZipcode() {
		return zipcode;
	}

	public void setZipcode(int zipcode) {
		this.zipcode = zipcode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public ContentValues getValues(){
		ContentValues values = new ContentValues();
		values.put(AddressTableInterface.COLUMN_ID, this.ID);
		values.put(AddressTableInterface.COLUMN_NAME_ZIPCODE, this.zipcode);
		values.put(AddressTableInterface.COLUMN_NAME_ENTRY_ID, this.entryId);
		values.put(AddressTableInterface.COLUMN_NAME_TITLE, this.title);
		values.put(AddressTableInterface.COLUMN_NAME_STREET, this.street);
		values.put(AddressTableInterface.COLUMN_NAME_STREET_NUMBER, this.streetNumber);
		values.put(AddressTableInterface.COLUMN_NAME_CITY, this.city);
		values.put(AddressTableInterface.COLUMN_NAME_COUNTRY, this.country);
		
		return values;
	}
	
	@Override
	public String toString() {
	  StringBuilder sb = new StringBuilder();
	  sb.append(getClass().getName());
	  sb.append(": ");
	  for (Field f : getClass().getDeclaredFields()) {
	    sb.append(f.getName());
	    sb.append("=");
	    
	    try {
			sb.append(f.get(this));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	    
	    sb.append(", ");
	  }
	  return sb.toString();
	}
}
