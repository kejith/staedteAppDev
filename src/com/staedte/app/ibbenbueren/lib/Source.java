package com.staedte.app.ibbenbueren.lib;

import java.io.File;

import com.staedte.app.ibbenbueren.constants.Constants;
import com.staedte.app.ibbenbueren.database.tables.SourceTableInterface;

import android.content.ContentValues;
import android.util.Log;

public class Source implements SourceTableInterface {	
	public static final int ENTRY_IMAGE 	= 1;
	public static final int CATEGORY_IMAGE 	= 2;
	public static final int WE_DONT_KNOW 	= 3;
	public static final int PDF				= 4;
	
	private long id			= 0;
	private long parentID 	= 0;
	private int parentType 	= 0;
	
	String link = "";	
	
	public Source(){}
	
	public Source(String link){
		this.setLink(link);
	}
	
	public ContentValues getValues(){
		ContentValues values = new ContentValues();
		
		values.put(this.COLUMN_SOURCES_LINK, this.link);
		values.put(this.COLUMN_SOURCES_PARENT, this.parentID);
		values.put(this.COLUMN_SOURCES_PARENT_TYPE, this.parentType);

		return values;
	}
	
	public void setLink(String link){		
		this.link = link;
	}
	
	public void setParentID(long parentID){
		this.parentID = parentID;
	}
	
	public void setParentType(int parentType){
		this.parentType = parentType;
	}
	
	public String getLink(){
		return this.link;
	}
	
	public long getParentID(){
		return this.parentID;
	}
	
	public int getParentType(){
		return this.parentType;
	}
	
	public long getID(){
		return this.id;
	}
	
	@Override
	public String toString(){
		return String.format("Source[ID: %d; link: %s; parentID: %d; parentType: %d];", 
				this.id, this.link, this.parentID, this.parentType);
	}
	
	public String download(String fileName, File directory){
		if(directory == null){
			directory = new File(FileAdapter.EXTERNAL_PICTURE_DIR, fileName);
		}
		
		if(!directory.isDirectory())
			FileAdapter.downloadFile(Source.getPdfUrlByFilename(this.link), directory);
		
		return directory.getPath();
	}
	
	public static String getPdfUrlByFilename(String fileName){
		return String.format("%s/%s/%s", Constants.SERVER_ROOT_URL, Constants.PDF_RELATIVE_DIR, fileName);
	}
}