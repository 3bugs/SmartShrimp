package th.ac.dusit.dbizcom.smartshrimp.model;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Feeding {

    @SerializedName("pond_id")
    public final int pondId;
    @SerializedName("feed_date")
    public final String feedDate;
    @SerializedName("first_feed")
    public final int firstFeed;
    @SerializedName("second_feed")
    public final int secondFeed;
    @SerializedName("third_feed")
    public final int thirdFeed;

    private Date mFeedDate = null;
    private int mDayTotal;
    private int mTotal = 0;

    public Feeding(int pondId, String feedDate, int firstFeed, int secondFeed, int thirdFeed) {
        this.pondId = pondId;
        this.feedDate = feedDate;
        this.firstFeed = firstFeed;
        this.secondFeed = secondFeed;
        this.thirdFeed = thirdFeed;
    }

    public void parseFeedDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            mFeedDate = format.parse(feedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getFeedDate() {
        return mFeedDate;
    }

    public void calculateDayTotal() {
        this.mDayTotal = firstFeed + secondFeed + thirdFeed;
    }

    public int getDayTotal() {
        return mDayTotal;
    }

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int total) {
        this.mTotal = total;
    }
}
