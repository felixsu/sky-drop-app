package felix.com.skydrop.model;

/**
 * Created by fsoewito on 12/7/2015.
 */
public class SettingElement {
    private String mTitle;
    private String mDescriptionTrue;
    private String mDescriptionFalse;
    private boolean mValue;
    private boolean mEditable;

    public SettingElement() {
        mTitle = "Undefined";
        mDescriptionTrue = "Unknown";
        mDescriptionFalse = "Unknown";
        mValue = false;
        mEditable = true;
    }

    public SettingElement(String title, String descriptionTrue, String descriptionFalse, boolean value) {
        mTitle = title;
        mDescriptionTrue = descriptionTrue;
        mDescriptionFalse = descriptionFalse;
        mValue = value;
        mEditable = true;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescriptionTrue() {
        return mDescriptionTrue;
    }

    public void setDescriptionTrue(String descriptionTrue) {
        mDescriptionTrue = descriptionTrue;
    }

    public String getDescriptionFalse() {
        return mDescriptionFalse;
    }

    public void setDescriptionFalse(String descriptionFalse) {
        mDescriptionFalse = descriptionFalse;
    }

    public boolean isValue() {
        return mValue;
    }

    public void setValue(boolean value) {
        mValue = value;
    }

    public boolean isEditable() {
        return mEditable;
    }

    public void setEditable(boolean editable) {
        mEditable = editable;
    }
}