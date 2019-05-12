package th.ac.dusit.dbizcom.smartshrimp.net;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import th.ac.dusit.dbizcom.smartshrimp.model.Feeding;

public class GetFeedingResponse extends BaseResponse {

    @SerializedName("data_list")
    public List<Feeding> feedingList;
}
