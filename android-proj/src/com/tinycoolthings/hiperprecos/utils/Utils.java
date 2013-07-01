package com.tinycoolthings.hiperprecos.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Utils {

    /**
	 * Converts a Date to "yyyy-MM-dd HH:mm:ss"
	 * @param date - Calendar in local TZ
	 * @return String in format "yyyy-MM-dd HH:mm:ss.SSS"
	 */
	public static String dateToStr(Date date) {
		if (date==null) {
			return "";
		}
        return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(date);
	}
	
	/**
	 * Converts a date string to Calendar
	 * @param date - Date in format "yyyy-MM-dd HH:mm:ss"
	 * @return Calendar
	 * @throws ParseException 
	 */
	public static Calendar convertStringToCal(String date) throws ParseException {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	    cal.setTime(sdf.parse(date));// all done
		return cal;
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
	
	public static boolean validSearch(String text) {
		return text.length()>=3;
	}
	
	public static String capitalizeFirstLetter(String input) {
		input = input.toLowerCase(Locale.FRENCH);
		return Character.toUpperCase(input.charAt(0)) + input.substring(1, input.length());
	}

}
