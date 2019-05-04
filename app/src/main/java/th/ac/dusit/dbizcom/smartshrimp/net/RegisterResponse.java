package th.ac.dusit.dbizcom.smartshrimp.net;

import com.google.gson.annotations.SerializedName;

import th.ac.dusit.dbizcom.smartshrimp.model.User;

public class RegisterResponse extends BaseResponse {

    @SerializedName("user")
    public User user;
}
