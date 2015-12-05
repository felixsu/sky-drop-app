package felix.com.skydrop.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import felix.com.skydrop.R;
import felix.com.skydrop.constant.Color;

/**
 * Created by fsoewito on 11/26/2015.
 *
 */
public class ForecastConverter {
    public static final int SHORT_MODE = 0;
    public static final int LONG_MODE = 1;

    public static String getString(double numeric, boolean isInteger, boolean isPercent) {
        if (isPercent){
            numeric*= 100;
        }
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

    public static int getColor(String icon) {
        switch (icon) {
            case ("clear-day"):
                return Color.YELLOW;
            case ("clear-night"):
                return Color.YELLOW;
            case ("rain"):
                return 0xFF03A9F4;
            case ("snow"):
                return Color.GREY_LIGHT;
            case ("partly-cloudy-day"):
                return 0xFFB6B6B6;
            case ("cloudy"):
                return 0xFFB6B6B6;
            case ("partly-cloudy-night"):
                return 0xFFB6B6B6;
            case ("fog"):
                return 0xFFB6B6B6;
            case ("sleet"):
                return Color.GREY_LIGHT;
            default:
                return Color.YELLOW;
        }
    }

    public static String getString(long time, String timeZone, int mode) {
        SimpleDateFormat formatter;
        if (mode == LONG_MODE) {
            formatter = new SimpleDateFormat("h:mma");
        }else {
            formatter = new SimpleDateFormat("ha");
        }
        formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        return formatter.format(new Date(time * 1000));
    }

    public static String getDirection(double value){
        int x = (int)value;
        if (x<0)
            return "NA";
        if (x<8)
            return "N";
        if (x<38)
            return "NNE";
        if (x < 53)
            return "NE";
        if (x < 82)
            return "NEE";
        if (x<98)
            return "E";
        if (x<127)
            return "SEE";
        if (x<143)
            return "SE";
        if (x<172)
            return "SSE";
        if (x<188)
            return "S";
        if (x < 217)
            return "SSW";
        if (x<233)
            return "SW";
        if (x < 262)
            return "SWW";
        if (x<278)
            return "W";
        if (x < 307)
            return "NWW";
        if (x<323)
            return "NW";
        if (x<352)
            return "NNW";
        if (x < 361)
            return "N";
        else
            return "NA";
    }
}
