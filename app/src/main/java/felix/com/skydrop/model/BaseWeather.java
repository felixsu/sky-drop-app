package felix.com.skydrop.model;

/**
 * Created by fsoewito on 11/26/2015.
 */
public class BaseWeather {
    protected String mTimezone;
    protected double mLatitude;
    protected double mLongitude;

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }
}
