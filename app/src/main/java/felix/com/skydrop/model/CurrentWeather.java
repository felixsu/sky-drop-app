package felix.com.skydrop.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import felix.com.skydrop.constant.ForecastConstant;

/**
 * Created by fsoewito on 11/26/2015.
 *
 */
public class CurrentWeather extends BaseWeather implements ForecastConstant {
    public static final int FORECAST_DISPLAYED = 6;

    protected boolean initialized;
    protected long mTime;
    protected String mSummary;
    protected String mIcon;
    protected double mTemperature;
    protected double mApparentTemperature;
    protected double mHumidity;
    protected double mWindSpeed;
    protected double mWindDirection;
    protected double mPressure;
    protected double mPrecipProbability;
    protected String mTodaySummary;
    protected HourlyForecast[] mHourlyForecasts;

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public double getTemperature() {
        return mTemperature;
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public double getApparentTemperature() {
        return mApparentTemperature;
    }

    public void setApparentTemperature(double apparentTemperature) {
        mApparentTemperature = apparentTemperature;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public double getWindSpeed() {
        return mWindSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        mWindSpeed = windSpeed;
    }

    public double getWindDirection() {
        return mWindDirection;
    }

    public void setWindDirection(double windDirection) {
        mWindDirection = windDirection;
    }

    public double getPressure() {
        return mPressure;
    }

    public void setPressure(double pressure) {
        mPressure = pressure;
    }

    public double getPrecipProbability() {
        return mPrecipProbability;
    }

    public void setPrecipProbability(double precipProbability) {
        mPrecipProbability = precipProbability;
    }

    public String getTodaySummary() {
        return mTodaySummary;
    }

    public void setTodaySummary(String todaySummary) {
        mTodaySummary = todaySummary;
    }

    public HourlyForecast[] getHourlyForecasts() {
        return mHourlyForecasts;
    }

    public void setHourlyForecasts(HourlyForecast[] hourlyForecasts) {
        mHourlyForecasts = hourlyForecasts;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public void getFromJson(String json) throws JSONException {
        JSONObject container = new JSONObject(json);
        if (container.has(KEY_INITIALIZED)) {
            setInitialized(container.getBoolean(KEY_INITIALIZED));
        }else{
            setInitialized(true);
        }
        setTimezone(container.getString(KEY_TIMEZONE));
        setLatitude(container.getDouble(KEY_LATITUDE));
        setLongitude(container.getDouble(KEY_LONGITUDE));

        JSONObject current = container.getJSONObject(KEY_CURRENT);
        setTime(current.getLong(KEY_TIME));
        setSummary(current.getString(KEY_SUMMARY));
        setIcon(current.getString(KEY_ICON));
        setPrecipProbability(current.getDouble(KEY_PRECIP));
        setTemperature(current.getDouble(KEY_TEMPERATURE));
        setApparentTemperature(current.getDouble(KEY_APPARENT_TEMPERATURE));
        setHumidity(current.getDouble(KEY_HUMIDITY));
        setWindSpeed(current.getDouble(KEY_WIND_SPEED));
        setWindDirection(current.getDouble(KEY_WIND_DIRECTION));
        setPressure(current.getDouble(KEY_PRESSURE));

        JSONObject hourly = container.getJSONObject(KEY_HOURLY);
        setTodaySummary(hourly.getString(KEY_SUMMARY));

        JSONArray hourlyData = hourly.getJSONArray(KEY_DATA);
        HourlyForecast[] hourlyForecasts = new HourlyForecast[hourlyData.length()];

        for (int i = 0 ; i<FORECAST_DISPLAYED; i++){
            HourlyForecast d = new HourlyForecast();
            JSONObject o = hourlyData.getJSONObject(i);
            d.setPrecipProbability(o.getDouble(KEY_PRECIP));
            d.setTemperature(o.getDouble(KEY_TEMPERATURE));
            d.setApparentTemperature(o.getDouble(KEY_APPARENT_TEMPERATURE));
            d.setTime(o.getLong(KEY_TIME));
            hourlyForecasts[i] = d;
        }
        setHourlyForecasts(hourlyForecasts);
    }

    public String toJson(){
        JSONObject result = new JSONObject();
        try {
            result.put(KEY_INITIALIZED, isInitialized());
            result.put(KEY_TIMEZONE, getTimezone());
            result.put(KEY_LATITUDE, getLatitude());
            result.put(KEY_LONGITUDE, getLongitude());

            JSONObject current = new JSONObject();
            current.put(KEY_TEMPERATURE, getTemperature());
            current.put(KEY_APPARENT_TEMPERATURE, getApparentTemperature());
            current.put(KEY_HUMIDITY, getHumidity());
            current.put(KEY_PRESSURE, getPressure());
            current.put(KEY_PRECIP, getPrecipProbability());
            current.put(KEY_SUMMARY, getSummary());
            current.put(KEY_WIND_DIRECTION, getWindDirection());
            current.put(KEY_WIND_SPEED, getWindSpeed());
            current.put(KEY_ICON, getIcon());
            current.put(KEY_TIME, getTime());
            result.put(KEY_CURRENT, current);

            JSONObject hourly = new JSONObject();
            hourly.put(KEY_SUMMARY, getTodaySummary());
            JSONArray hourlyArray = new JSONArray();
            for (int i = 0; i< FORECAST_DISPLAYED; i++){
                JSONObject o = new JSONObject();
                o.put(KEY_TIME, getHourlyForecasts()[i].getTime());
                o.put(KEY_TEMPERATURE, getHourlyForecasts()[i].getTemperature());
                o.put(KEY_APPARENT_TEMPERATURE, getHourlyForecasts()[i].getApparentTemperature());
                o.put(KEY_PRECIP, getHourlyForecasts()[i].getPrecipProbability());
                hourlyArray.put(i, o);
            }
            hourly.put(KEY_DATA, hourlyArray);
            result.put(KEY_HOURLY, hourly);
            return result.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
