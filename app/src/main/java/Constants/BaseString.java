package Constants;

import android.annotation.SuppressLint;
import android.text.format.DateFormat;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BaseString {
    public static String userPhone;

    public static String getUserPhone() {
        return userPhone;
    }

    public static void setUserPhone(String userPhone) {
        BaseString.userPhone = userPhone;
    }

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("EEE, d MMM yyyy hh:mm aaa", cal).toString();
    }

    public static String getDateForReview(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("d MMM yyyy", cal).toString();
    }

    public static long getTimeLong(String timeStamp) {


        long before = Long.parseLong(timeStamp) ;

        Date date1 = new Date();
        Date date = new Date(before) ;


        long dif =  7200000 - (date1.getTime() - date.getTime()) ;
        if(dif<=0) return  0 ;
        return dif;
    }
}
