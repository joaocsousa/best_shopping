package com.tinycoolthings.bestshopping.utils;

/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

import java.util.Locale;

public class Slugify {
	
	public static String slugify(String input) {
		
		if (input==null || input.equals("")) {
			return "";
		}
		
		String ret = input.trim();

		ret = normalize(ret);
		ret = removeDuplicateWhiteSpaces(ret);
		return ret.replace(" ", "-").toLowerCase(Locale.getDefault());
	}

	private static String normalize(String input) {
		String ret = input.trim();
		if (ret.equals("")) {
			return "";
		}
        return ret.replaceAll("[^\\p{L}\\p{N}]", "");
	}

	private static String removeDuplicateWhiteSpaces(String input) {
		String ret = input.trim();
		if (ret.equals("")) {
			return "";
		}
		return ret.replaceAll( "\\s+", " " );
	}
}