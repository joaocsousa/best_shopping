package com.tinycoolthings.hiperprecos.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.tinycoolthings.hiperprecos.R;

public class Storage {

	public static String getFileName(String url, String prodName, String prodMarca) {
		String extension = url.substring(url.lastIndexOf("."));
		String currProdNome = Slugify.slugify(prodName);
		String currProdMarca = Slugify.slugify(prodMarca);
		String fileName = "";
		fileName = currProdNome + "-" + currProdMarca + extension;
		return fileName;
	}
	
	public static String getFileNameCompressed(String fileName) {
		Integer lastDotPos = fileName.lastIndexOf(".");
		String extension = fileName.substring(lastDotPos);
		String nameCmp = fileName.substring(0, lastDotPos) + ".cmp" + extension;
		return nameCmp;
	}
	
	/**
	 * Read file from storage. Priority is given to EXTERNAL storage, then the internal storage.
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static Bitmap getFileFromStorage(Context context, String fileName) {
		Bitmap bMap = null;
		boolean mExternalStorageAvailable = false;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    mExternalStorageAvailable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    mExternalStorageAvailable = true;
		}
		
		boolean fileFound = false;

		if (mExternalStorageAvailable) {
			//External storage available
			File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
			try {
				FileInputStream fis = new FileInputStream(file);
				bMap = BitmapFactory.decodeStream(fis);
		        Debug.PrintDebug(Storage.class, "Read file " + fileName + " from external storage.");
				fileFound = true;
			} catch (FileNotFoundException e) {
		        Debug.PrintWarning(Storage.class, "File " + fileName + " not found in external storage.");
			}
		}

		if (!mExternalStorageAvailable && !fileFound) {
			//try internal storage instead
			try {
				FileInputStream fis = context.openFileInput(fileName);
				bMap = BitmapFactory.decodeStream(fis);
		        Debug.PrintDebug(Storage.class, "Read file " + fileName + " from internal storage.");
			} catch (FileNotFoundException e) {
		        Debug.PrintWarning(Storage.class, "File " + fileName + " not found in internal storage.");
			}
		}

		if (bMap == null) {
			Debug.PrintWarning(Storage.class, "File " + fileName + " not found. Use Android default.");
			bMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        }
		
		return bMap;
	}
	
	/**
	 * Stores a file to storage
	 * @param context
	 * @param imageURL
	 * @param fileName
	 */
	public static void storeFileToStorage(Context context, String imageURL, String fileName) {

		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}

		try {
			URL url = new URL(imageURL);
			HttpURLConnection urlConnection;
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
	        urlConnection.connect();

	        if (mExternalStorageAvailable && mExternalStorageWriteable) {
				//External storage available and writable
				File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
				file.createNewFile();
				try {
					FileOutputStream fos = new FileOutputStream(file);
			        InputStream inputStream = urlConnection.getInputStream();
			        byte[] buffer = new byte[1024];
			        int bufferLength = 0;
			        while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
			        	fos.write(buffer, 0, bufferLength);
			        }
			        fos.close();
			        Debug.PrintDebug(Storage.class, "Saved " + fileName + " to external storage.");
			        Bitmap bitmap = Storage.getFileFromStorage(context, fileName);
			        File fileCmp = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), Storage.getFileNameCompressed(fileName));
			        FileOutputStream outScaled = new FileOutputStream(fileCmp);
			        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outScaled);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				//Use internal Storage instead
				FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
				InputStream inputStream = urlConnection.getInputStream();
		        byte[] buffer = new byte[1024];
		        int bufferLength = 0;
		        while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
		        	fos.write(buffer, 0, bufferLength);
		        }
		        fos.close();
		        Debug.PrintDebug(Storage.class, "Saved " + fileName + " to internal storage.");
			}

		} catch (MalformedURLException e) {
			Debug.PrintError(Storage.class, "Error downloading user image: Invalid URL.");
			e.printStackTrace();
		} catch (IOException e) {
			Debug.PrintError(Storage.class, "Error downloading user image: No Internet?");
			e.printStackTrace();
		}
	}
	
	public static Boolean fileExists(Context context, String fileName) {
		
		boolean mExternalStorageAvailable = false;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    mExternalStorageAvailable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    mExternalStorageAvailable = true;
		}

		boolean fileFound = false;

		if (mExternalStorageAvailable) {
			//External storage available
			File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
			if (file.exists()) {
				fileFound = true;
		        Debug.PrintInfo(Storage.class, "File " + fileName + " found in external storage.");
			} else {
		        Debug.PrintWarning(Storage.class, "File " + fileName + " not found in external storage.");
			}
		}
		
		if (!mExternalStorageAvailable && !fileFound) {
			//try internal storage instead
			File file = context.getFileStreamPath(fileName);
			if (file.exists()) {
				fileFound = true;
				Debug.PrintInfo(Storage.class, "File " + fileName + " found in internal storage.");
			} else {
		        Debug.PrintWarning(Storage.class, "File " + fileName + " not found in internal storage.");
			}
		}
		
		return fileFound;
	
	}
	
}
