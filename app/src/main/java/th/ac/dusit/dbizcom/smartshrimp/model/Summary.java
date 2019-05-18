package th.ac.dusit.dbizcom.smartshrimp.model;

import com.google.gson.annotations.SerializedName;

public class Summary {

    @SerializedName("pond_area")
    public final int pondArea;
    @SerializedName("shrimp_count")
    public final int shrimpCount;
    @SerializedName("begin_date")
    public final String beginDate;
    @SerializedName("end_date")
    public final String endDate;
    @SerializedName("period")
    public final int period;
    @SerializedName("feed")
    public final int feed;
    @SerializedName("final_weight")
    public final int finalWeight;
    @SerializedName("cost")
    public final int cost;
    @SerializedName("sale_price")
    public final int salePrice;

    public Summary(int pondArea, int shrimpCount, String beginDate, String endDate,
                   int period, int feed, int finalWeight, int cost, int salePrice) {
        this.pondArea = pondArea;
        this.shrimpCount = shrimpCount;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.period = period;
        this.feed = feed;
        this.finalWeight = finalWeight;
        this.cost = cost;
        this.salePrice = salePrice;
    }
}
