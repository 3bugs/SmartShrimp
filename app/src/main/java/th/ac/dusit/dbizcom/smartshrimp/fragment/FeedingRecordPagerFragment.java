package th.ac.dusit.dbizcom.smartshrimp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import th.ac.dusit.dbizcom.smartshrimp.App;
import th.ac.dusit.dbizcom.smartshrimp.R;
import th.ac.dusit.dbizcom.smartshrimp.etc.Utils;
import th.ac.dusit.dbizcom.smartshrimp.model.Pond;

public class FeedingRecordPagerFragment extends Fragment {

    private static final String TAG = FeedingRecordPagerFragment.class.getName();
    private static final String TITLE = "บันทึกการให้อาหารกุ้ง";

    private FeedingRecordPagerFragmentListener mListener;

    public FeedingRecordPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feeding_record_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated()");

        if (getActivity() != null) {
            App app = (App) getActivity().getApplication();
            app.getPondList(new App.PondListListener() {
                @Override
                public void onPondListReady(List<Pond> pondList) {
                    ViewPager viewPager = view.findViewById(R.id.view_pager);
                    final FeedingRecordPagerAdapter adapter = new FeedingRecordPagerAdapter(
                            getChildFragmentManager(),
                            getContext(),
                            pondList
                    );
                    viewPager.setAdapter(adapter);
                    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int i, float v, int i1) {
                        }

                        @Override
                        public void onPageSelected(int position) {
                            FeedingRecordFragment fragment =
                                    (FeedingRecordFragment) adapter.getRegisteredFragment(position);
                            if (fragment != null) {
                                fragment.setupRefreshButton();
                            }
                        }

                        @Override
                        public void onPageScrollStateChanged(int i) {
                        }
                    });

                    TabLayout tabLayout = view.findViewById(R.id.tab_layout);
                    tabLayout.setupWithViewPager(viewPager);
                }

                @Override
                public void onError(String errorMessage) {
                    if (getActivity() != null) {
                        Utils.showOkDialog(getActivity(), "ผิดพลาด", errorMessage);
                    }
                }
            });
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FeedingRecordPagerFragmentListener) {
            mListener = (FeedingRecordPagerFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FeedingRecordPagerFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mListener != null) {
            mListener.setTitle(TITLE);
        }
    }

    public interface FeedingRecordPagerFragmentListener {
        void setTitle(String title);
    }

    private static class FeedingRecordPagerAdapter extends FragmentStatePagerAdapter {

        private Context mContext;
        private List<Pond> mPondList;
        private SparseArray<Fragment> mRegisteredFragments = new SparseArray<>();

        FeedingRecordPagerAdapter(FragmentManager fm, Context context, List<Pond> pondList) {
            super(fm);
            mContext = context;
            mPondList = pondList;
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
            return FeedingRecordFragment.newInstance(mPondList.get(position));
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "บ่อที่ " + mPondList.get(position).number;
        }
    }
}
