package felix.com.skydrop.factory;

import felix.com.skydrop.model.CurrentWeather;

/**
 * Created by fsoewito on 11/26/2015.
 */
public class CurrentWeatherFactory {

    public static CurrentWeather getInstance(){
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTodaySummary("none");
        currentWeather.setIcon("clear-day");
        currentWeather.setSummary("none");
        currentWeather.setTimezone("");
        return currentWeather;
    }
}
