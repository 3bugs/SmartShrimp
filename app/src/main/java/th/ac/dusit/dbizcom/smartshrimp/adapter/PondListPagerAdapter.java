package th.ac.dusit.dbizcom.smartshrimp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import th.ac.dusit.dbizcom.smartshrimp.model.Pond;

public class PondListPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = PondListPagerAdapter.class.getName();

    private Context mContext;
    private List<Pond> mPondList;
    private Class mFragmentClass;
    private SparseArray<Fragment> mRegisteredFragments = new SparseArray<>();

    public PondListPagerAdapter(FragmentManager fm, Context context, List<Pond> pondList,
                                Class fragmentClass) {
        super(fm);
        mContext = context;
        mPondList = pondList;
        mFragmentClass = fragmentClass;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Log.i(TAG, "instantiateItem(): " + position);

        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mRegisteredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        mRegisteredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return mRegisteredFragments.get(position);
    }

    @Override
    public int getCount() {
        return mPondList.size();
    }

    @Override
    public Fragment getItem(int position) {
        //return FeedingRecordFragment.newInstance(mPondList.get(position));

        Method method = null;
        try {
            method = mFragmentClass.getMethod("newInstance", Pond.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (method != null) {
            try {
                return (Fragment) method.invoke(null, mPondList.get(position));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "บ่อที่ " + mPondList.get(position).number;
    }
}
