package com.mattprecious.smsfix.library.util;

import java.util.Date;
import java.util.TimeZone;

import com.mattprecious.smsfix.library.LoggerHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class TimeHelper {
	/**
     * Get the desired offset change based on the user's preferences
     * 
     * @param context
     * @return long
     */
	public static long getOffset(Context context) {
        long offset = 0;
        
        LoggerHelper logger = LoggerHelper.getInstance(context);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        
        // if the user wants us to auto-determine the offset use the negative of
        // their GMT offset
        String method = settings.getString("offset_method", "manual");
        
        logger.info("Adjustment method: " + method);
        
        if (method.equals("automatic") || method.equals("neg_automatic")) {
            offset = TimeZone.getDefault().getRawOffset();
            
            logger.info("Raw offset: " + offset);

            // account for DST
            if (TimeZone.getDefault().useDaylightTime() && TimeZone.getDefault().inDaylightTime(new Date())) {
                offset += 3600000;
                
                logger.info("Adjusting for DST: " + offset);
            }

            if (method.equals("automatic")) {
                offset *= -1;
                
                logger.info("Negate the offset: " + offset);
            }

        // otherwise, use the offset the user has specified
        } else {
            double offsetHours = Double.parseDouble(settings.getString("offset_hours", "0"));
            double offsetMinutes = Double.parseDouble(settings.getString("offset_minutes", "0"));
            
            logger.info("Offset Hours: " + offsetHours);
            logger.info("Offset Minutes: " + offsetMinutes);
            
            offset = (long) (offsetHours * 3600000);
            offset += (long) (offsetMinutes * 60000);
        }

        logger.info("Final offset: " + offset);
        return offset;
    }
}