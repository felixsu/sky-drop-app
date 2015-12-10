package felix.com.skydrop.model;

import java.io.Serializable;

/**
 * Created by fsoewito on 12/4/2015.
 */
public class SettingData implements Serializable {

    private boolean mTemperatureUnit;
    private boolean mAutoUpdate;
    private boolean mWindUnit;
    private boolean mPressureUnit;
    private boolean mPaidVersion;

    public SettingData() {
    }

    public boolean isTemperatureUnit() {
        return mTemperatureUnit;
    }

    public void setTemperatureUnit(boolean temperatureUnit) {
        mTemperatureUnit = temperatureUnit;
    }

    public boolean isAutoUpdate() {
        return mAutoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        mAutoUpdate = autoUpdate;
    }

    public boolean isWindUnit() {
        return mWindUnit;
    }

    public void setWindUnit(boolean windUnit) {
        mWindUnit = windUnit;
    }

    public boolean isPressureUnit() {
        return mPressureUnit;
    }

    public void setPressureUnit(boolean pressureUnit) {
        mPressureUnit = pressureUnit;
    }

    public boolean isPaidVersion() {
        return mPaidVersion;
    }

    public void setPaidVersion(boolean paidVersion) {
        mPaidVersion = paidVersion;
    }

    public void update(SettingData settingData) {
        mAutoUpdate = settingData.isAutoUpdate();
        mWindUnit = settingData.isWindUnit();
        mPressureUnit = settingData.isPressureUnit();
        mPaidVersion = settingData.isPaidVersion();
        mTemperatureUnit = settingData.isTemperatureUnit();
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s, %s",
                String.valueOf(mTemperatureUnit),
                String.valueOf(mAutoUpdate),
                String.valueOf(mWindUnit),
                String.valueOf(mPressureUnit),
                String.valueOf(mPaidVersion));
    }
}
