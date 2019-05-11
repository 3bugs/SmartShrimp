package th.ac.dusit.dbizcom.smartshrimp;

import android.app.Application;

public class App extends Application {

    private static App mApp;
    //private List<Pond> mPondList = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

    public static App getInstance() {
        return mApp;
    }

    /*public List<Pond> getPondList() {
        return mPondList;
    }

    public void setPondList(List<Pond> pondList) {
        this.mPondList = pondList;
    }*/
}
