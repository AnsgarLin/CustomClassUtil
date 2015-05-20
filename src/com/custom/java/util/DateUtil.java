package com.custom.java.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	/**
	 * Get the date after days from today in format
	 */
	public static String getDateFromToday(int range, SimpleDateFormat format) {
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DATE, range);

		return format.format(today.getTime());
	}

	/**
	 * Get today's date info by field
	 */
	public static String getToday(int field) {
		Calendar today = Calendar.getInstance();
		if (field == Calendar.MONTH) {
			return String.valueOf(today.get(field) + 1);
		} else {
			return String.valueOf(today.get(field));
		}
	}

	/**
	 * Get calendar instance by format
	 */
	public static Calendar getCalenderByFormat(String dateString, String format) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Calendar calender = Calendar.getInstance();
		calender.setTime(simpleDateFormat.parse(dateString));
		return calender;
	}

	/**
	 * Get date instance by format
	 * 
	 * @throws ParseException
	 */
	public static Date getDateByFormat(String dateString, String format) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.parse(dateString);
	}

	/**
	 * Get date duration, use date as basic unit
	 * 
	 * @throws ParseException
	 */
	public static int getDateDuration(Date startDate, Date endDate) {
		return (int) Math.ceil((endDate.getTime() - startDate.getTime()) / (1000f * 3600f * 24f)); // A day in milliseconds
	}
}
