package felix.com.skydrop.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import felix.com.skydrop.constant.WeatherConstant;

/**
 * Created by fsoewito on 11/26/2015.
 */
public class WeatherData extends BaseWeather implements WeatherConstant {
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
        if (container.has(KEY_INITIALIZED)) {
            setInitialized(container.getBoolean(KEY_INITIALIZED));
        } else {
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
        setHourSummary(hourly.getString(KEY_SUMMARY));

        JSONArray hourlyData = hourly.getJSONArray(KEY_DATA);
        HourlyForecast[] hourlyForecasts = new HourlyForecast[hourlyData.length()];

        for (int i = 0; i < FORECAST_DISPLAYED; i++) {
            HourlyForecast d = new HourlyForecast();
            JSONObject o = hourlyData.getJSONObject(i);
            d.setPrecipProbability(o.getDouble(KEY_PRECIP));
            d.setTemperature(o.getDouble(KEY_TEMPERATURE));
            d.setApparentTemperature(o.getDouble(KEY_APPARENT_TEMPERATURE));
            d.setTime(o.getLong(KEY_TIME));
            d.setIcon(o.getString(KEY_ICON));
            d.setSummary(o.getString(KEY_SUMMARY));
            d.setWindSpeed(o.getDouble(KEY_WIND_SPEED));
            d.setWindDirection(o.getDouble(KEY_WIND_DIRECTION));

            double rawIntensity = o.getDouble(KEY_PRECIP_INTENSITY);
            double processedIntensity = (rawIntensity > 1500 ? 1500 : rawIntensity);
            d.setPrecipIntensity(processedIntensity);
            if (o.has(KEY_PRECIP_TYPE)) {
                d.setPrecipType(o.getString(KEY_PRECIP_TYPE));
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
            hourly.put(KEY_SUMMARY, getHourSummary());
            JSONArray hourlyArray = new JSONArray();
            for (int i = 0; i < FORECAST_DISPLAYED; i++) {
                JSONObject o = new JSONObject();
                HourlyForecast data = getHourlyForecasts()[i];
                o.put(KEY_TIME, data.getTime());
                o.put(KEY_TEMPERATURE, data.getTemperature());
                o.put(KEY_APPARENT_TEMPERATURE, data.getApparentTemperature());
                o.put(KEY_PRECIP, data.getPrecipProbability());
                o.put(KEY_PRECIP_INTENSITY, data.getPrecipIntensity());
                o.put(KEY_ICON, data.getIcon());
                o.put(KEY_SUMMARY, data.getSummary());
                o.put(KEY_WIND_SPEED, data.getWindSpeed());
                o.put(KEY_WIND_DIRECTION, data.getWindDirection());
                if (data.getPrecipType() != null) {
                    o.put(KEY_PRECIP_TYPE, data.getPrecipType());
                }
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
