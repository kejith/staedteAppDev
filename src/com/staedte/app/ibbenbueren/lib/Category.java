package com.staedte.app.ibbenbueren.lib;

import com.staedte.app.ibbenbueren.contentProvider.CategoryListProvider;
import com.staedte.app.ibbenbueren.database.tables.CategoryTableInterface;

import android.content.Context;
import android.database.Cursor;

public class Category {

	private Context context = null;
	private CategoryListProvider provider = null;
	
	public Category(Context context) {
		this.context = context;
		this.provider = new CategoryListProvider(context);
	}
	
	public static Cursor getCategoriesByParent(int parentId, Context context){
		CategoryListProvider provider = new CategoryListProvider(context);
		
		// we only accept positive integers
		parentId = (parentId < 0) ? 0 : parentId;
		
		String selection = String.format(" %s != '' AND %s = %d", 
				CategoryTableInterface.COLUMN_NAME, CategoryTableInterface.COLUMN_PARENT, parentId);
		
		return provider.query(CategoryListProvider.CONTENT_URI, null, selection, null, null);
	}

}
