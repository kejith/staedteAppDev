package com.staedte.app.ibbenbueren.lib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.staedte.app.ibbenbueren.DownloadImageTask;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class FileAdapter {
	
	public static final String DEFAULT_DIR_NAME = "staedteApp";
	public static final String CACHE_DIR = "cache";
	public static final String PICTURE_CACHE_DIR = CACHE_DIR + File.separator + "pictures";
	public static final String PDF_CACHE_DIR = CACHE_DIR + File.separator + "pdfs";
	public static final String EXTERNAL_PICTURE_DIR = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES.toString() + File.separator + DEFAULT_DIR_NAME;
	
	public File getFile(String path, String fileName) throws IOException{
		if(!this.isExternalStorageReadable())
			throw new IOException("File could not be read, because Storage is not readable");
		
		return new File(path, fileName);
	}
	
	public File getFile(String fileName) throws IOException{
		if(!this.isExternalStorageReadable())
			throw new IOException("File could not be read, because Storage is not readable");
		
		File file = new File(EXTERNAL_PICTURE_DIR, fileName);
		
		return file;
	}
	
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWriteable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	public void createExternalPicture(Bitmap b, String fileName) throws IOException{
		if(isExternalStorageWriteable()){
			File dir = new File(EXTERNAL_PICTURE_DIR);
			dir.mkdirs();
			File file = new File(EXTERNAL_PICTURE_DIR, fileName);
			try {
				FileOutputStream out = new FileOutputStream(file);
				b.compress(Bitmap.CompressFormat.PNG, 90, out);
				out.flush();
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public File getExternalPicture(String fileName, int width, int height) throws IOException{
		if(!this.isExternalStorageReadable())
			throw new IOException("File could not be read, because Storage is not readable");
		

		String imageNameWithSize = width +"x"+ height +"-"+ fileName;
		File file = new File(EXTERNAL_PICTURE_DIR, imageNameWithSize);
		
		if(file.exists()){
			return file;
		} else {
			String url = "http://144.76.155.172/dev/app-api/getImage.php?image_name="+ fileName +"&width="+width+"&height="+height;
			new DownloadImageTask(imageNameWithSize).execute(url);
			return new File(EXTERNAL_PICTURE_DIR, imageNameWithSize);
		}		
	}
	
	public void bindImageToView(String imageName, ImageView v, int width, int height){
		String imageNameWithSize = "";
		String url = "http://144.76.155.172/app/ibbenbueren-mettingen-recke/getImage.php?image_name="+ imageName +"&width="+width+"&height="+height;
		
		if(width == 0 && height == 0){
			imageNameWithSize = imageName;
			url = "http://144.76.155.172/app/ibbenbueren-mettingen-recke/getImage.php?image_name="+ imageName;
		} else {
			imageNameWithSize = width +"x"+ height +"-"+ imageName;		
		}
		
		try {
			File file = this.getFile(imageNameWithSize);
			if(file.exists()){
				Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
				v.setImageBitmap(b);
			} else {
				new DownloadImageTask(v, imageNameWithSize).execute(url);
			}
		} catch(IOException e) {
			e.printStackTrace();
			new DownloadImageTask(v, imageNameWithSize).execute(url);
		}
	}
	
	/**
	 * This method converts dp unit to equivalent pixels, depending on device density. 
	 * 
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static int convertDpToPixel(float dp, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi / 160f);
	    return (int) px;
	}

	/**
	 * This method converts device specific pixels to density independent pixels.
	 * 
	 * @param px A value in px (pixels) unit. Which we need to convert into db
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static int convertPixelsToDp(float px, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float dp = px / (metrics.densityDpi / 160f);
	    return (int) dp;
	}
	
	public static String getFileNameByUrl(String url){        
        return url.substring(url.lastIndexOf('/')+1);
	}
	
	public static String getCacheDir(){
		return getDir(FileAdapter.CACHE_DIR);
	}
	
	public static String getPictureCacheDir(){
		return getDir(FileAdapter.PICTURE_CACHE_DIR);
	}
	
	private static String getDir(String dirName){
		return Environment.getExternalStorageDirectory() + File.separator + dirName;
	}
	
    public static void downloadFile(String fileURL, File directory) {
    	FileOutputStream f = null;
    	String filePath = "";
    	
    	Log.d("FileAdapter.java", fileURL);
    	
    	try {
            f = new FileOutputStream(directory);
            URL u = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            InputStream in = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            
            f.flush();
            f.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        } 

    }
}
