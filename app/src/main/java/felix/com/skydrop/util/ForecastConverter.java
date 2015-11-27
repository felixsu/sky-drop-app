package felix.com.skydrop.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import felix.com.skydrop.R;

/**
 * Created by fsoewito on 11/26/2015.
 */
public class ForecastConverter {

    public static String getString(double numeric, boolean isInteger) {
        if (isInteger) {
            return String.valueOf(Math.round(numeric));
        } else {
            return String.format("%.2f", numeric);
        }
    }

    public static int getIcon(String icon){
        switch (icon){
            case ("clear-day"):
                return R.drawable.ic_weather_sunny;
            case ("clear-night"):
                return R.drawable.ic_weather_clear_night;
            case ("rain"):
                return R.drawable.ic_weather_heavey_rain;
            case ("snow"):
                return R.drawable.ic_weather_snowy;
            case ("partly-cloudy-day") :
                return R.drawable.ic_weather_sunny_partly_cloudy;
            case ("cloudy"):
                return R.drawable.ic_weather_sunny_partly_cloudy;
            case ("partly-cloudy-night") :
                return R.drawable.ic_weather_sunny_partly_cloudy_night;
            case ("fog"):
                return R.drawable.ic_weather_haze;
            case ("sleet"):
                return R.drawable.ic_weather_snowy;
            default:
                return R.drawable.ic_weather_sunny;
        }
    }

    public static String getString(long time, String timeZone) {
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        String timeString = formatter.format(new Date(time * 1000));
        return timeString;
    }
}
