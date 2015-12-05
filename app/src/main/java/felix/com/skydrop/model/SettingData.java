package felix.com.skydrop.model;

/**
 * Created by fsoewito on 12/4/2015.
 */
public class SettingData {
    public static final int UNIT_CELSIUS = 0;
    public static final int UNIT_FAHRENHEIT = 1;

    int unit;

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }
}
