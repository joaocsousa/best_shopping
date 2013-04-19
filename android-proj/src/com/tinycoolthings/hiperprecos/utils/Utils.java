package com.tinycoolthings.hiperprecos.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.view.View;

public class Utils {
	
	/**
	 * Converts a cal to "yyyy-MM-dd HH:mm:ss.SSS"
	 * @param cal - Calendar in local TZ
	 * @return String in format "yyyy-MM-dd HH:mm:ss.SSS"
	 */
	public static String convertCalToString(Calendar cal) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
		return(sdf.format(cal.getTime()));
	}
	
	public static Integer getRandomInt() {
		int maxValue = 100000;
		Random random = new Random();
		return random.nextInt(maxValue);
	}
	
	public static Integer getRandomInt(Integer n) {
		Random random = new Random();
		return random.nextInt(n);
	}

}
