package com.tinycoolthings.hiperprecos.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import android.os.Environment;
import android.util.Log;

import com.tinycoolthings.hiperprecos.utils.Constants.Debug.MsgType;

public class Debug {

	public static void PrintError(Object caller, String msg) {
		String errorMsg = "";
		if (caller instanceof Class) {
			errorMsg = ((Class)caller).getSimpleName() + ": "+ msg;
		} else {
			errorMsg = caller.getClass().getSimpleName() + ": "+ msg;
		}
		Log.e(Constants.Debug.GENERAL_TAG, errorMsg);
		if (Constants.Debug.WRITE_TO_FILE) {
			writeToFile(Constants.Debug.MsgType.ERROR, errorMsg);
		}
	}

	public static void PrintWarning(Object caller, String msg) {
		String warningMsg = "";
		if (caller instanceof Class) {
			warningMsg = ((Class)caller).getSimpleName() + ": "+ msg;
		} else {
			warningMsg = caller.getClass().getSimpleName() + ": "+ msg;
		}
		Log.w(Constants.Debug.GENERAL_TAG, warningMsg);
		if (Constants.Debug.WRITE_TO_FILE) {
			writeToFile(Constants.Debug.MsgType.WARNING, warningMsg);
		}
	}

	public static void PrintDebug(Object caller, String msg) {
		String debugMsg = "";
		if (caller instanceof Class) {
			debugMsg = ((Class)caller).getSimpleName() + ": "+ msg;
		} else {
			debugMsg = caller.getClass().getSimpleName() + ": "+ msg;
		}
		Log.d(Constants.Debug.GENERAL_TAG, debugMsg);
		if (Constants.Debug.WRITE_TO_FILE) {
			writeToFile(Constants.Debug.MsgType.DEBUG, debugMsg);
		}
	}

	public static void PrintInfo(Object caller, String msg){
		String infoMsg = "";
		if (caller instanceof Class) {
			infoMsg = ((Class)caller).getSimpleName() + ": "+ msg;
		} else {
			infoMsg = caller.getClass().getSimpleName() + ": "+ msg;
		}
		Log.i(Constants.Debug.GENERAL_TAG, infoMsg);
		if (Constants.Debug.WRITE_TO_FILE) {
			writeToFile(Constants.Debug.MsgType.INFO, infoMsg);
		}
	}

	private static void writeToFile(MsgType error, String msg) {
		try {
		    File root = Environment.getExternalStorageDirectory();
		    if (root.canWrite()) {

		        File log = new File(root, Constants.Debug.GENERAL_TAG+".txt");
		    	BufferedWriter writer = new BufferedWriter(new FileWriter(log, true));

		        String time = Utils.convertCalToString(Calendar.getInstance());
		        String msgType = "";
		        switch (error) {
			        case DEBUG:
			        	msgType = "Debug";
			        	break;
			        case ERROR:
			        	msgType = "Error";
			        	break;
			        case INFO:
			        	msgType = "Info";
			        	break;
			        case WARNING:
			        	msgType = "Warning";
			        	break;
		        }

		        writer.write(time + "|" + msgType + "|" + msg);
		        writer.newLine();
		        writer.flush();
		        writer.close();
		    }
		} catch (IOException e) {
		    Log.e(Constants.Debug.GENERAL_TAG, "Could not write file " + e.getMessage());
		}
	}
}