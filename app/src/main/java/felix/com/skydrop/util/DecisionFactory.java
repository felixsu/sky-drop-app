package felix.com.skydrop.util;

import felix.com.skydrop.model.WeatherData;
import felix.com.skydrop.model.HourlyForecast;

/**
 * Created by fsoewito on 12/1/2015.
 */
public class DecisionFactory {
    private static final int LOW_TEMP_THRESHOLD = 22;
    private static final int HIGH_TEMP_THRESHOLD = 32;
    private static final int HIGH_CHANCE_RAIN = 75;
    private static final int CHANCE_RAIN = 30;
    private static final int NO_RAIN = 0;


    private static final String IT_WILL_BE_CLEAR_TITLE = "Bright Day is Coming";
    private static final String IT_WILL_BE_RAIN_TITLE = "Rain is Coming";
    private static final String IT_MAY_BE_RAIN_TITLE = "Rain may Coming";
    private static final String IT_WILL_BE_NOTHING_TITLE = "No Preparation Need";

    private static final String IT_WILL_BE_RAIN = "uh oh rain will fall in hours, get ur umbrella ready";
    private static final String IT_MAY_BE_RAIN = "rain may caught you in hours, take a preparation :)";
    private static final String IT_WILL_BE_CLEAR = "next hours will be very bright,drink well and have a good day";
    private static final String IT_WILL_BE_CLOUDY = "sky seems to be thicker in hours, good luck";
    private static final String IT_WILL_BE_EXTREMELY_HOT = "get your ice cream!! save your life";
    private static final String IT_WILL_BE_EXTREMELY_COLD = "get inside your blanket in few hours, don't get flu";

    public static String generateForecastDecision(HourlyForecast[] hourlyForecasts) {
        double precipProbArray[] = new double[WeatherData.FORECAST_DISPLAYED];
        double tempArray[] = new double[WeatherData.FORECAST_DISPLAYED];
        for (int i = 0; i < WeatherData.FORECAST_DISPLAYED; i++) {
            precipProbArray[i] = hourlyForecasts[i].getPrecipProbability()*100;
            tempArray[i] = hourlyForecasts[i].getTemperature();
        }

        boolean highChanceRainFlag = false;
        boolean chanceRainFlag = false;
        boolean hotFlag = false;
        boolean coldFlag = false;

        for (int i = 0; i< precipProbArray.length; i++){
            if (precipProbArray[i] > HIGH_CHANCE_RAIN){
                highChanceRainFlag = true;
                break;
            }else if (precipProbArray[i] > CHANCE_RAIN){
                chanceRainFlag = true;
                break;
            }
        }

        for (int i = 0; i<tempArray.length; i++){
            if (tempArray[i] < LOW_TEMP_THRESHOLD){
                coldFlag = true;
                break;
            }
            if (tempArray[i] > HIGH_CHANCE_RAIN){
                hotFlag = true;
                break;
            }
        }
        String title;
        String body;
        if (highChanceRainFlag) {
            title = IT_WILL_BE_RAIN_TITLE;
            body = IT_WILL_BE_RAIN;
        }
        else if (chanceRainFlag && hotFlag) {
            title = IT_MAY_BE_RAIN_TITLE;
            body = IT_MAY_BE_RAIN;
        }else if (chanceRainFlag && !hotFlag){
            title = IT_MAY_BE_RAIN_TITLE;
            body = IT_WILL_BE_CLOUDY;
        } else if (!chanceRainFlag && hotFlag) {
            title = IT_WILL_BE_CLEAR_TITLE;
            body = IT_WILL_BE_EXTREMELY_HOT;
        }else if (coldFlag){
            title = IT_WILL_BE_CLEAR_TITLE;
            body = IT_WILL_BE_EXTREMELY_COLD;
        }else{
            title = IT_WILL_BE_NOTHING_TITLE;
            body = IT_WILL_BE_CLEAR;
        }

        return title+"-"+body;
    }
}
