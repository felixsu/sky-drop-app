package felix.com.skydrop.constant;

/**
 * Created by fsoewito on 11/26/2015.
 */
public interface WeatherConstant {
    String url = "https://api.forecast.io/forecast";
    String apiKey = "5c150722b10107dcbd3b1c60931cacc9";

    String KEY = "forecast_key";
    //master data
    String KEY_CURRENT_WEATHER = "key_weatherData";

    //data
    String KEY_ADDRESS = "address";
    String KEY_INITIALIZED = "initialized";
    String KEY_TIMEZONE = "timezone";
    String KEY_LATITUDE = "latitude";
    String KEY_LONGITUDE = "longitude";

    String KEY_CURRENT = "currently";
    String KEY_TEMPERATURE = "temperature";
    String KEY_APPARENT_TEMPERATURE = "apparentTemperature";
    String KEY_SUMMARY = "summary";
    String KEY_TIME = "time";
    String KEY_PRESSURE = "pressure";
    String KEY_WIND_SPEED = "windSpeed";
    String KEY_WIND_DIRECTION = "windBearing";
    String KEY_HUMIDITY = "humidity";
    String KEY_PRECIP = "precipProbability";
    String KEY_ICON = "icon";

    String KEY_DATA = "data";
    String KEY_HOURLY = "hourly";
    String KEY_PRECIP_INTENSITY = "precipIntensity";
    String KEY_PRECIP_TYPE = "precipType";


}
