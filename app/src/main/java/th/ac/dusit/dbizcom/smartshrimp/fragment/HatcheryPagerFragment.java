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

import retrofit2.Call;
import retrofit2.Retrofit;
import th.ac.dusit.dbizcom.smartshrimp.R;
import th.ac.dusit.dbizcom.smartshrimp.adapter.HatcheryListPagerAdapter;
import th.ac.dusit.dbizcom.smartshrimp.etc.Utils;
import th.ac.dusit.dbizcom.smartshrimp.net.ApiClient;
import th.ac.dusit.dbizcom.smartshrimp.net.GetHatcheryResponse;
import th.ac.dusit.dbizcom.smartshrimp.net.MyRetrofitCallback;
import th.ac.dusit.dbizcom.smartshrimp.net.WebServices;

public class HatcheryPagerFragment extends Fragment {

    private static final String TAG = HatcheryPagerFragment.class.getName();
    private static final String TITLE = "แหล่งพันธุ์ลูกกุ้ง";

    private HatcheryPagerFragmentListener mListener;

    private View mProgressView;

    public HatcheryPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hatchery_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressView = view.findViewById(R.id.progress_view);

        view.findViewById(R.id.add_hatchery_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showShortToast(getActivity(), "Under construction!");
            }
        });

        if (mListener != null) {
            mListener.setupRefreshButton(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doGetHatchery(view);
                }
            });
        }

        doGetHatchery(view);
    }

    private void doGetHatchery(final View view) {
        mProgressView.setVisibility(View.VISIBLE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        Call<GetHatcheryResponse> call = services.getHatchery();
        call.enqueue(new MyRetrofitCallback<GetHatcheryResponse>(
                getActivity(),
                null,
                mProgressView,
                new MyRetrofitCallback.MyRetrofitCallbackListener<GetHatcheryResponse>() {
                    @Override
                    public void onSuccess(GetHatcheryResponse responseBody) {
                        ViewPager viewPager = view.findViewById(R.id.view_pager);
                        final HatcheryListPagerAdapter adapter = new HatcheryListPagerAdapter(
                                getChildFragmentManager(),
                                getContext(),
                                responseBody.hatcheryList,
                                HatcheryFragment.class
                        );
                        viewPager.setAdapter(adapter);
                        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int i, float v, int i1) {
                            }

                            @Override
                            public void onPageSelected(int position) {
                                HatcheryFragment fragment =
                                        (HatcheryFragment) adapter.getRegisteredFragment(position);
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
                }
        ));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HatcheryPagerFragmentListener) {
            mListener = (HatcheryPagerFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HatcheryPagerFragmentListener");
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

    public interface HatcheryPagerFragmentListener {
        void setTitle(String title);

        void setupRefreshButton(View.OnClickListener listener);
    }
}
