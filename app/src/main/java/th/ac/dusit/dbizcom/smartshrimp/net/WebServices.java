package th.ac.dusit.dbizcom.smartshrimp.net;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface WebServices {

    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("register")
    Call<RegisterResponse> register(
            @Field("username") String username,
            @Field("password") String password,
            @Field("email") String email
    );

    @GET("get_farm_info")
    Call<GetFarmInfoResponse> getFarmInfo(
    );

    @GET("get_pond")
    Call<GetPondResponse> getPond(
    );

    @FormUrlEncoded
    @POST("get_feeding")
    Call<GetFeedingResponse> getFeedingByPond(
            @Field("pondId") int pondId
    );

    @FormUrlEncoded
    @POST("add_feeding")
    Call<AddFeedingResponse> addFeeding(
            @Field("pondId") int pondId,
            @Field("feedDate") String feedDate,
            @Field("firstFeed") int firstFeed,
            @Field("secondFeed") int secondFeed,
            @Field("thirdFeed") int thirdFeed
    );
}