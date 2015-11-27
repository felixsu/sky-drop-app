package felix.com.skydrop.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fsoewito on 11/26/2015.
 */
public class CurrentWeather extends BaseWeather {
    public static final String TAG = "currently";
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

    public void getFromJson(String json) throws JSONException {
        JSONObject container = new JSONObject(json);
        setTimezone(container.getString("timezone"));
        setLatitude(container.getDouble("latitude"));
        setLongitude(container.getDouble("longitude"));

        JSONObject current = container.getJSONObject("currently");
        setTime(current.getLong("time"));
        setSummary(current.getString("summary"));
        setIcon(current.getString("icon"));
        setPrecipProbability(current.getDouble("precipProbability"));
        setTemperature(current.getDouble("temperature"));
        setApparentTemperature(current.getDouble("apparentTemperature"));
        setHumidity(current.getDouble("humidity"));
        setWindSpeed(current.getDouble("windSpeed"));
        setWindDirection(current.getDouble("windBearing"));
        setPressure(current.getDouble("pressure"));

        JSONObject daily = container.getJSONObject("daily");
        setTodaySummary(daily.getString("summary"));
    }
}
