package edu.ua.lib.acumen.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {
	
	public static DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a yyyy/MM/dd ");
	public static DecimalFormat twoDec = new DecimalFormat("#.##");
	public static long startTime, stopTime;
	
	public static String now(){
		return now(System.currentTimeMillis());
	}
	
	public static String now(long milli){
		return dateFormat.format(new Date(milli));
	}
	
	public static String start(){
		startTime = System.currentTimeMillis();
		return now(startTime);
	}
	
	public static String stop(){
		stopTime = System.currentTimeMillis();
		return now(stopTime);
	}
	
	public static String elapsedInHours(){
		float tDiff = (stopTime - startTime)/(60*60*1000F);
		return twoDec.format(tDiff)+" hours";
	}
	
}