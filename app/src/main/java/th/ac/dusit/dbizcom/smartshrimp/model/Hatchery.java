package th.ac.dusit.dbizcom.smartshrimp.model;

import com.google.gson.annotations.SerializedName;

public class Hatchery {

    @SerializedName("id")
    public final int id;
    @SerializedName("name")
    public final String name;
    @SerializedName("address")
    public final String address;
    @SerializedName("sub_district")
    public final String subDistrict;
    @SerializedName("district")
    public final String district;
    @SerializedName("province")
    public final String province;
    @SerializedName("postal_code")
    public final String postalCode;
    @SerializedName("owner")
    public final String owner;
    @SerializedName("fmd_no")
    public final String fmdNo;


    public Hatchery(int id, String name, String address, String subDistrict, String district,
                    String province, String postalCode, String owner, String fmdNo) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.subDistrict = subDistrict;
        this.district = district;
        this.province = province;
        this.postalCode = postalCode;
        this.owner = owner;
        this.fmdNo = fmdNo;
    }

    @Override
    public String toString() {
        return name;
    }
}
