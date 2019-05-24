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

import th.ac.dusit.dbizcom.smartshrimp.model.Hatchery;

public class HatcheryListPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = HatcheryListPagerAdapter.class.getName();

    private Context mContext;
    private List<Hatchery> mHatcheryList;
    private Class mFragmentClass;
    private SparseArray<Fragment> mRegisteredFragments = new SparseArray<>();

    public HatcheryListPagerAdapter(FragmentManager fm, Context context, List<Hatchery> hatcheryList,
                                    Class fragmentClass) {
        super(fm);
        mContext = context;
        mHatcheryList = hatcheryList;
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
        return mHatcheryList.size();
    }

    @Override
    public Fragment getItem(int position) {
        Method method = null;
        try {
            method = mFragmentClass.getMethod("newInstance", Hatchery.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (method != null) {
            try {
                return (Fragment) method.invoke(null, mHatcheryList.get(position));
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
        return mHatcheryList.get(position).name;
    }
}
