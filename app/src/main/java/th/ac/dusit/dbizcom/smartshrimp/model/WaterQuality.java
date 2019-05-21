package th.ac.dusit.dbizcom.smartshrimp.model;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WaterQuality {

    @SerializedName("id")
    public final int id;
    @SerializedName("pond_id")
    public final int pondId;
    @SerializedName("test_date")
    public final String testDate;
    @SerializedName("ph_morning")
    public final double phMorning;
    @SerializedName("ph_evening")
    public final double phEvening;
    @SerializedName("salty")
    public final double salty;
    @SerializedName("ammonia")
    public final double ammonia;
    @SerializedName("nitrite")
    public final double nitrite;
    @SerializedName("alkaline")
    public final double alkaline;
    @SerializedName("calcium")
    public final double calcium;
    @SerializedName("magnesium")
    public final double magnesium;

    //private Date mTestDate = null;

    public WaterQuality(int id, int pondId, String testDate, double phMorning, double phEvening, double salty,
                        double ammonia, double nitrite, double alkaline, double calcium, double magnesium) {
        this.id = id;
        this.pondId = pondId;
        this.testDate = testDate;
        this.phMorning = phMorning;
        this.phEvening = phEvening;
        this.salty = salty;
        this.ammonia = ammonia;
        this.nitrite = nitrite;
        this.alkaline = alkaline;
        this.calcium = calcium;
        this.magnesium = magnesium;
    }

    /*public void parseFeedDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            mTestDate = format.parse(testDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }*/

    public Date getFeedDate() {
        Date parsedDate = null;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            parsedDate = format.parse(testDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return parsedDate;
    }
}
