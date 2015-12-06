package felix.com.skydrop.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import felix.com.skydrop.constant.WeatherConstant;

/**
 * Created by fsoewito on 11/26/2015.
 * store every value of weather data and some basics method to handle the data
 */
public class WeatherData extends BaseWeather {
    public static final int N_FORECAST = 8;

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
    protected String mHourSummary;

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

    public String getHourSummary() {
        return mHourSummary;
    }

    public void setHourSummary(String hourSummary) {
        mHourSummary = hourSummary;
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
        if (container.has(WeatherConstant.KEY_INITIALIZED)) {
            setInitialized(container.getBoolean(WeatherConstant.KEY_INITIALIZED));
        } else {
            setInitialized(true);
        }
        setTimezone(container.getString(WeatherConstant.KEY_TIMEZONE));
        setLatitude(container.getDouble(WeatherConstant.KEY_LATITUDE));
        setLongitude(container.getDouble(WeatherConstant.KEY_LONGITUDE));

        JSONObject current = container.getJSONObject(WeatherConstant.KEY_CURRENT);
        setTime(current.getLong(WeatherConstant.KEY_TIME));
        setSummary(current.getString(WeatherConstant.KEY_SUMMARY));
        setIcon(current.getString(WeatherConstant.KEY_ICON));
        setPrecipProbability(current.getDouble(WeatherConstant.KEY_PRECIP));
        setTemperature(current.getDouble(WeatherConstant.KEY_TEMPERATURE));
        setApparentTemperature(current.getDouble(WeatherConstant.KEY_APPARENT_TEMPERATURE));
        setHumidity(current.getDouble(WeatherConstant.KEY_HUMIDITY));
        setWindSpeed(current.getDouble(WeatherConstant.KEY_WIND_SPEED));
        setWindDirection(current.getDouble(WeatherConstant.KEY_WIND_DIRECTION));
        setPressure(current.getDouble(WeatherConstant.KEY_PRESSURE));

        JSONObject hourly = container.getJSONObject(WeatherConstant.KEY_HOURLY);
        setHourSummary(hourly.getString(WeatherConstant.KEY_SUMMARY));

        JSONArray hourlyData = hourly.getJSONArray(WeatherConstant.KEY_DATA);
        HourlyForecast[] hourlyForecasts = new HourlyForecast[N_FORECAST];

        for (int i = 0; i < hourlyForecasts.length; i++) {
            HourlyForecast d = new HourlyForecast();
            JSONObject o = hourlyData.getJSONObject(i);
            d.setPrecipProbability(o.getDouble(WeatherConstant.KEY_PRECIP));
            d.setTemperature(o.getDouble(WeatherConstant.KEY_TEMPERATURE));
            d.setApparentTemperature(o.getDouble(WeatherConstant.KEY_APPARENT_TEMPERATURE));
            d.setTime(o.getLong(WeatherConstant.KEY_TIME));
            d.setIcon(o.getString(WeatherConstant.KEY_ICON));
            d.setSummary(o.getString(WeatherConstant.KEY_SUMMARY));
            d.setWindSpeed(o.getDouble(WeatherConstant.KEY_WIND_SPEED));
            d.setWindDirection(o.getDouble(WeatherConstant.KEY_WIND_DIRECTION));

            double rawIntensity = o.getDouble(WeatherConstant.KEY_PRECIP_INTENSITY);
            double processedIntensity = (rawIntensity > 1500 ? 1500 : rawIntensity);
            d.setPrecipIntensity(processedIntensity);
            if (o.has(WeatherConstant.KEY_PRECIP_TYPE)) {
                d.setPrecipType(o.getString(WeatherConstant.KEY_PRECIP_TYPE));
            } else {
                d.setPrecipType(null);
            }
            hourlyForecasts[i] = d;
        }
        setHourlyForecasts(hourlyForecasts);
    }

    public String toJson() {
        JSONObject result = new JSONObject();
        try {
            result.put(WeatherConstant.KEY_INITIALIZED, isInitialized());
            result.put(WeatherConstant.KEY_TIMEZONE, getTimezone());
            result.put(WeatherConstant.KEY_LATITUDE, getLatitude());
            result.put(WeatherConstant.KEY_LONGITUDE, getLongitude());

            JSONObject current = new JSONObject();
            current.put(WeatherConstant.KEY_TEMPERATURE, getTemperature());
            current.put(WeatherConstant.KEY_APPARENT_TEMPERATURE, getApparentTemperature());
            current.put(WeatherConstant.KEY_HUMIDITY, getHumidity());
            current.put(WeatherConstant.KEY_PRESSURE, getPressure());
            current.put(WeatherConstant.KEY_PRECIP, getPrecipProbability());
            current.put(WeatherConstant.KEY_SUMMARY, getSummary());
            current.put(WeatherConstant.KEY_WIND_DIRECTION, getWindDirection());
            current.put(WeatherConstant.KEY_WIND_SPEED, getWindSpeed());
            current.put(WeatherConstant.KEY_ICON, getIcon());
            current.put(WeatherConstant.KEY_TIME, getTime());
            result.put(WeatherConstant.KEY_CURRENT, current);

            JSONObject hourly = new JSONObject();
            hourly.put(WeatherConstant.KEY_SUMMARY, getHourSummary());
            JSONArray hourlyArray = new JSONArray();
            for (int i = 0; i < N_FORECAST; i++) {
                JSONObject o = new JSONObject();
                HourlyForecast data = getHourlyForecasts()[i];
                o.put(WeatherConstant.KEY_TIME, data.getTime());
                o.put(WeatherConstant.KEY_TEMPERATURE, data.getTemperature());
                o.put(WeatherConstant.KEY_APPARENT_TEMPERATURE, data.getApparentTemperature());
                o.put(WeatherConstant.KEY_PRECIP, data.getPrecipProbability());
                o.put(WeatherConstant.KEY_PRECIP_INTENSITY, data.getPrecipIntensity());
                o.put(WeatherConstant.KEY_ICON, data.getIcon());
                o.put(WeatherConstant.KEY_SUMMARY, data.getSummary());
                o.put(WeatherConstant.KEY_WIND_SPEED, data.getWindSpeed());
                o.put(WeatherConstant.KEY_WIND_DIRECTION, data.getWindDirection());
                if (data.getPrecipType() != null) {
                    o.put(WeatherConstant.KEY_PRECIP_TYPE, data.getPrecipType());
                }
                hourlyArray.put(i, o);
            }
            hourly.put(WeatherConstant.KEY_DATA, hourlyArray);
            result.put(WeatherConstant.KEY_HOURLY, hourly);
            return result.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
