package felix.com.skydrop.model;

/**
 * Created by fsoewito on 12/4/2015.
 */
public class ApplicationData {
    private boolean mInitialized;
    private String mAddress;

    public boolean isInitialized() {
        return mInitialized;
    }

    public void setInitialized(boolean initialized) {
        mInitialized = initialized;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }
}
