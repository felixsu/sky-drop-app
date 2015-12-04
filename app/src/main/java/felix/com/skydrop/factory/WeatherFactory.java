package felix.com.skydrop.factory;

import android.content.SharedPreferences;

import org.json.JSONException;

import felix.com.skydrop.constant.WeatherConstant;
import felix.com.skydrop.model.HourlyForecast;
import felix.com.skydrop.model.WeatherData;

/**
 * Created by fsoewito on 11/26/2015.
 */
public class WeatherFactory {

    public static WeatherData getInstance() {
        WeatherData weatherData = new WeatherData();
        weatherData.setInitialized(false);
        weatherData.setHourSummary("none");
        weatherData.setIcon("clear-day");
        weatherData.setSummary("none");
        weatherData.setTimezone("America/Los_Angeles");
        weatherData.setAddress("none");

        HourlyForecast[] hourlyForecasts = new HourlyForecast[WeatherData.FORECAST_DISPLAYED];
        for (int i = 0; i < hourlyForecasts.length; i++) {
            HourlyForecast forecast = new HourlyForecast();
            forecast.setSummary("none");
            forecast.setIcon("clear-day");
            hourlyForecasts[i] = forecast;
        }
        weatherData.setHourlyForecasts(hourlyForecasts);
        return weatherData;
    }

    public static WeatherData getInstance(SharedPreferences preferences) throws JSONException {
        WeatherData weatherData = new WeatherData();
        if (preferences == null) {
            weatherData.setInitialized(false);
            weatherData.setHourSummary("none");
            weatherData.setIcon("clear-day");
            weatherData.setSummary("none");
            weatherData.setTimezone("America/Los_Angeles");
            weatherData.setAddress("none");

            HourlyForecast[] hourlyForecasts = new HourlyForecast[WeatherData.FORECAST_DISPLAYED];
            for (int i = 0; i < hourlyForecasts.length; i++) {
                HourlyForecast forecast = new HourlyForecast();
                forecast.setSummary("none");
                forecast.setIcon("clear-day");
                hourlyForecasts[i] = forecast;
            }
            weatherData.setHourlyForecasts(hourlyForecasts);
        } else {
            weatherData.getFromJson(preferences.getString(WeatherConstant.KEY_CURRENT_WEATHER, null));
        }
        return weatherData;
    }
}
