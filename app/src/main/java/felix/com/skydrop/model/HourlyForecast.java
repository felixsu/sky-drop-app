package felix.com.skydrop.model;

/**
 * Created by fsoewito on 11/28/2015.
 */
public class HourlyForecast {
    private long mTime;
    private double mTemperature;
    private double mApparentTemperature;
    private double mPrecipProbability;
    private double mPrecipIntensity;
    private String mPrecipType;
    private String mIcon;
    private double mWindSpeed;
    private double mWindDirection;
    private String mSummary;


    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
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

    public double getPrecipProbability() {
        return mPrecipProbability;
    }

    public void setPrecipProbability(double precipProbability) {
        mPrecipProbability = precipProbability;
    }

    public double getPrecipIntensity() {
        return mPrecipIntensity;
    }

    public void setPrecipIntensity(double precipIntensity) {
        mPrecipIntensity = precipIntensity;
    }

    public String getPrecipType() {
        return mPrecipType;
    }

    public void setPrecipType(String precipType) {
        mPrecipType = precipType;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
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

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }
}


