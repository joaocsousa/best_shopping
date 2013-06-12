package com.tinycoolthings.hiperprecos.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Utils {
	
	/**
	 * Converts a Calendar to "yyyy-MM-dd HH:mm:ss.SSS"
	 * @param cal - Calendar in local TZ
	 * @return String in format "yyyy-MM-dd HH:mm:ss.SSS"
	 */
	public static String calendarToStr(Calendar cal) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
		return(sdf.format(cal.getTime()));
	}
	
	/**
	 * Converts a Date to "yyyy-MM-dd HH:mm:ss"
	 * @param cal - Calendar in local TZ
	 * @return String in format "yyyy-MM-dd HH:mm:ss.SSS"
	 */
	public static String dateToStr(Date date) {
		if (date==null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		return(sdf.format(date));
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
	
	public static Date convertLongToDate(Long dateTimeStamp) {
		Date date = new Date(dateTimeStamp);
		return date;
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
		if (text.length()<3) {
			return false;
		}
		return true;
	}
	
	public static String capitalizeFirstLetter(String input) {
		input = input.toLowerCase(Locale.FRENCH);
		return Character.toUpperCase(input.charAt(0)) + input.substring(1, input.length());
	}

}
