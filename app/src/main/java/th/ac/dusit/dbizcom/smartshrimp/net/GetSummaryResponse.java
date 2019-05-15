package th.ac.dusit.dbizcom.smartshrimp.net;

import com.google.gson.annotations.SerializedName;

import th.ac.dusit.dbizcom.smartshrimp.model.Summary;

public class GetSummaryResponse extends BaseResponse {

    @SerializedName("summary")
    public Summary summary;
}
