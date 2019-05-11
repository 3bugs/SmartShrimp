package th.ac.dusit.dbizcom.smartshrimp.model;

import com.google.gson.annotations.SerializedName;

public class Farm {

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
    @SerializedName("farm_reg_id")
    public final String farmRegId;

    public Farm(String name, String address, String subDistrict, String district, String province, String postalCode, String farmRegId) {
        this.name = name;
        this.address = address;
        this.subDistrict = subDistrict;
        this.district = district;
        this.province = province;
        this.postalCode = postalCode;
        this.farmRegId = farmRegId;
    }
}
