package th.ac.dusit.dbizcom.smartshrimp.net;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import th.ac.dusit.dbizcom.smartshrimp.model.Farm;

public class GetFarmInfoResponse extends BaseResponse {

    @SerializedName("data_list")
    public List<Farm> farmList;
}
