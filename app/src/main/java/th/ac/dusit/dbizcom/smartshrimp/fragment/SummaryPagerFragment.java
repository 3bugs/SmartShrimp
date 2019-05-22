package th.ac.dusit.dbizcom.smartshrimp.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import th.ac.dusit.dbizcom.smartshrimp.App;
import th.ac.dusit.dbizcom.smartshrimp.R;
import th.ac.dusit.dbizcom.smartshrimp.adapter.PondListPagerAdapter;
import th.ac.dusit.dbizcom.smartshrimp.etc.Utils;
import th.ac.dusit.dbizcom.smartshrimp.model.Pond;

public class SummaryPagerFragment extends Fragment {

    private static final String TAG = SummaryPagerFragment.class.getName();
    private static final String TITLE = "สรุปผลการเลี้ยง";

    private SummaryPagerFragmentListener mListener;

    public SummaryPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summary_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            App app = (App) getActivity().getApplication();
            app.getPondList(new App.PondListListener() {
                @Override
                public void onPondListReady(List<Pond> pondList) {
                    ViewPager viewPager = view.findViewById(R.id.view_pager);
                    final PondListPagerAdapter adapter = new PondListPagerAdapter(
                            getChildFragmentManager(),
                            getContext(),
                            pondList,
                            SummaryFragment.class
                    );
                    viewPager.setAdapter(adapter);
                    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int i, float v, int i1) {
                        }

                        @Override
                        public void onPageSelected(int position) {
                            SummaryFragment fragment =
                                    (SummaryFragment) adapter.getRegisteredFragment(position);
                            if (fragment != null) {
                                //fragment.setupRefreshButton();
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
                        Utils.showOkDialog(getActivity(), "ผิดพลาด", errorMessage, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (getActivity() != null) {
                                    getActivity().finish();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SummaryPagerFragmentListener) {
            mListener = (SummaryPagerFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SummaryPagerFragmentListener");
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

    public interface SummaryPagerFragmentListener {
        void setTitle(String title);
    }
}
