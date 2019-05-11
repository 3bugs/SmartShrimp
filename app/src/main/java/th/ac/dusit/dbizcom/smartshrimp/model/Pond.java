package th.ac.dusit.dbizcom.smartshrimp.model;

import com.google.gson.annotations.SerializedName;

public class Pond {

    @SerializedName("id")
    public final int id;
    @SerializedName("number")
    public final int number;
    @SerializedName("area")
    public final int area;

    public Pond(int pondId, int pondNumber, int pondArea) {
        this.id = pondId;
        this.number = pondNumber;
        this.area = pondArea;
    }
}
