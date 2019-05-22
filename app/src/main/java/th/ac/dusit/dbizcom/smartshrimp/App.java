package th.ac.dusit.dbizcom.smartshrimp;

import android.app.Application;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import th.ac.dusit.dbizcom.smartshrimp.model.Pond;
import th.ac.dusit.dbizcom.smartshrimp.net.ApiClient;
import th.ac.dusit.dbizcom.smartshrimp.net.GetPondResponse;
import th.ac.dusit.dbizcom.smartshrimp.net.MyRetrofitCallback;
import th.ac.dusit.dbizcom.smartshrimp.net.WebServices;

public class App extends Application {

    private static App mApp;
    private List<Pond> mPondList = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

    public static App getInstance() {
        return mApp;
    }

    public void getPondList(PondListListener listener) {
        if (mPondList != null) {
            listener.onPondListReady(mPondList);
        } else {
            doGetPond(listener);
        }
    }

    private void doGetPond(final PondListListener listener) {
        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        Call<GetPondResponse> call = services.getPond();
        call.enqueue(new MyRetrofitCallback<>(
                this,
                null,
                null,
                new MyRetrofitCallback.MyRetrofitCallbackListener<GetPondResponse>() {
                    @Override
                    public void onSuccess(GetPondResponse responseBody) {
                        mPondList = responseBody.pondList;
                        listener.onPondListReady(mPondList);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        listener.onError(errorMessage);
                    }
                }
        ));
    }

    public interface PondListListener {
        void onPondListReady(List<Pond> pondList);
        void onError(String errorMessage);
    }
}
