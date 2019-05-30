package th.ac.dusit.dbizcom.smartshrimp.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Retrofit;
import th.ac.dusit.dbizcom.smartshrimp.R;
import th.ac.dusit.dbizcom.smartshrimp.adapter.SpinnerWithHintArrayAdapter;
import th.ac.dusit.dbizcom.smartshrimp.model.Feeding;
import th.ac.dusit.dbizcom.smartshrimp.model.Pond;
import th.ac.dusit.dbizcom.smartshrimp.net.ApiClient;
import th.ac.dusit.dbizcom.smartshrimp.net.GetFeedingResponse;
import th.ac.dusit.dbizcom.smartshrimp.net.MyRetrofitCallback;
import th.ac.dusit.dbizcom.smartshrimp.net.WebServices;

public class FeedingRecordFragment extends Fragment {

    private static final String TAG = FeedingRecordFragment.class.getName();
    private static final String TITLE = "บันทึกการให้อาหารกุ้ง";
    private static final String ARG_POND_JSON = "feeding_json";

    private List<Feeding> mFeedingList = null;
    private Pond mPond;

    private FeedingRecordFragmentListener mListener;

    private View mProgressView;
    private TextView mErrorMessageTextView;
    private RecyclerView mFeedingRecyclerView;

    public FeedingRecordFragment() {
        // Required empty public constructor
    }

    public static FeedingRecordFragment newInstance(Pond pond) {
        FeedingRecordFragment fragment = new FeedingRecordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POND_JSON, new Gson().toJson(pond));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String pondJson = getArguments().getString(ARG_POND_JSON);
            mPond = new Gson().fromJson(pondJson, Pond.class);
            Log.i(TAG, "onCreate(): บ่อ " + mPond.number);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView(): บ่อ " + mPond.number);
        return inflater.inflate(R.layout.fragment_feeding_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressView = view.findViewById(R.id.progress_view);
        mErrorMessageTextView = view.findViewById(R.id.error_message_text_view);
        mFeedingRecyclerView = view.findViewById(R.id.feeding_recycler_view);
        view.findViewById(R.id.add_feeding_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onClickAddFeedingButton(mPond); //todo: *****
                }
            }
        });

        /*if (mFeedingList == null) {
            doGetFeeding();
        } else {
            showResult();
        }*/
        doGetFeeding();

        Spinner cycleSpinner = view.findViewById(R.id.cycle_spinner);

        final SpinnerWithHintArrayAdapter<String> adapter = new SpinnerWithHintArrayAdapter<>(
                getActivity(),
                R.layout.item_cycle,
                new String[]{"01/05/62 - ปัจจุบัน", "01/03/62 - 31/05/62", "01/01/62 - 31/03/62", "-- เลือกรอบการเลี้ยง --"}
        );
        adapter.setDropDownViewResource(R.layout.item_cycle_drop_down);
        cycleSpinner.setAdapter(adapter);
    }

    public void doGetFeeding() {
        if (mProgressView != null) {
            mProgressView.setVisibility(View.VISIBLE);
        }
        mErrorMessageTextView.setVisibility(View.GONE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        Call<GetFeedingResponse> call = services.getFeedingByPond(mPond.id); //todo: *****
        call.enqueue(new MyRetrofitCallback<>(
                getActivity(),
                null,
                mProgressView,
                new MyRetrofitCallback.MyRetrofitCallbackListener<GetFeedingResponse>() {
                    @Override
                    public void onSuccess(GetFeedingResponse responseBody) {
                        mFeedingList = responseBody.feedingList;

                        for (Feeding feeding : mFeedingList) {
                            feeding.calculateDayTotal();
                        }
                        for (Feeding feeding : mFeedingList) {
                            calculateTotalForFeeding(feeding);
                        }

                        showResult();
                    }

                    private void calculateTotalForFeeding(Feeding feeding) {
                        for (Feeding f : mFeedingList) {
                            if (f.feedDate.compareTo(feeding.feedDate) <= 0) {
                                feeding.setTotal(feeding.getTotal() + f.getDayTotal());
                            }
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        //Utils.showOkDialog(getActivity(), "ผิดพลาด", errorMessage);
                        mErrorMessageTextView.setText(errorMessage);
                        mErrorMessageTextView.setVisibility(View.VISIBLE);
                    }
                }
        ));
    }

    private void showResult() {
        if (mFeedingList.size() > 0) {
            setupRecyclerView();
        } else {
            mErrorMessageTextView.setText("ไม่มีข้อมูล");
            mErrorMessageTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setupRecyclerView() {
        if (getContext() != null) {
            FeedingListAdapter adapter = new FeedingListAdapter(
                    getContext(),
                    mPond,
                    mFeedingList,
                    mListener
            );
            mFeedingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mFeedingRecyclerView.addItemDecoration(new SpacingDecoration(getContext()));
            mFeedingRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FeedingRecordFragmentListener) {
            mListener = (FeedingRecordFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FeedingRecordFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setupRefreshButton() {
        if (mListener != null) {
            mListener.setupRefreshButton(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doGetFeeding();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume(): บ่อ " + mPond.number);
    }

    public interface FeedingRecordFragmentListener {
        //void setTitle(String title);

        void setupRefreshButton(View.OnClickListener listener);

        void onClickAddFeedingButton(Pond pond);

        void onEditFeeding(Pond pond, Feeding feeding);
    }

    private static class FeedingListAdapter extends RecyclerView.Adapter<FeedingRecordFragment.FeedingListAdapter.FeedingViewHolder> {

        private final Context mContext;
        private final Pond mPond;
        private final List<Feeding> mFeedingList;
        private final FeedingRecordFragmentListener mListener;

        FeedingListAdapter(Context context, Pond pond, List<Feeding> feedingList, FeedingRecordFragmentListener listener) {
            mContext = context;
            mPond = pond;
            mFeedingList = feedingList;
            mListener = listener;
        }

        @NonNull
        @Override
        public FeedingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_feeding, parent, false
            );
            return new FeedingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FeedingViewHolder holder, int position) {
            final Feeding feeding = mFeedingList.get(position);

            long diffInMilliSeconds = feeding.getFeedDate().getTime() - mFeedingList.get(mFeedingList.size() - 1).getFeedDate().getTime();
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMilliSeconds);
            holder.mDayTextView.setText(String.valueOf(diffInDays + 1));

            holder.mFeedDateTextView.setText(formatThaiDate(feeding.feedDate));

            if (feeding.firstFeed == 0) {
                holder.mFirstFeedTextView.setBackgroundResource(R.drawable.bg_table_row_red);
            }
            if (feeding.secondFeed == 0) {
                holder.mSecondFeedTextView.setBackgroundResource(R.drawable.bg_table_row_red);
            }
            if (feeding.thirdFeed == 0) {
                holder.mThirdFeedTextView.setBackgroundResource(R.drawable.bg_table_row_red);
            }

            holder.mFirstFeedTextView.setText(feeding.firstFeed == 0 ? "" : String.valueOf(feeding.firstFeed));
            holder.mSecondFeedTextView.setText(feeding.secondFeed == 0 ? "" : String.valueOf(feeding.secondFeed));
            holder.mThirdFeedTextView.setText(feeding.thirdFeed == 0 ? "" : String.valueOf(feeding.thirdFeed));
            holder.mDayTotalTextView.setText(String.valueOf(feeding.getDayTotal()));
            holder.mTotalTextView.setText(String.valueOf(feeding.getTotal()));

            holder.mFeeding = feeding;

            int rowBgColorRes = position % 2 == 0 ? R.color.row_light_background : R.color.row_dark_background;
            holder.mRootView.setBackgroundResource(rowBgColorRes);
        }

        private String formatThaiDate(String dateString) {
            String[] datePart = dateString.split("-");
            String day = datePart[2];
            String month = datePart[1];
            String year = String.valueOf(Integer.parseInt(datePart[0]) + 543).substring(2);
            return String.format(Locale.getDefault(), "%s/%s/%s", day, month, year);
        }

        @Override
        public int getItemCount() {
            return mFeedingList.size();
        }

        class FeedingViewHolder extends RecyclerView.ViewHolder {

            private final View mRootView;
            private final TextView mDayTextView;
            private final TextView mFeedDateTextView;
            private final TextView mFirstFeedTextView;
            private final TextView mSecondFeedTextView;
            private final TextView mThirdFeedTextView;
            private final TextView mDayTotalTextView;
            private final TextView mTotalTextView;

            private Feeding mFeeding;

            FeedingViewHolder(View itemView) {
                super(itemView);

                mRootView = itemView;
                mDayTextView = itemView.findViewById(R.id.day_text_view);
                mFeedDateTextView = itemView.findViewById(R.id.feed_date_text_view);
                mFirstFeedTextView = itemView.findViewById(R.id.first_feed_text_view);
                mSecondFeedTextView = itemView.findViewById(R.id.second_feed_text_view);
                mThirdFeedTextView = itemView.findViewById(R.id.third_feed_text_view);
                mDayTotalTextView = itemView.findViewById(R.id.day_total_text_view);
                mTotalTextView = itemView.findViewById(R.id.total_text_view);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onEditFeeding(mPond, mFeeding);
                    }
                });
            }
        }
    }

    public class SpacingDecoration extends RecyclerView.ItemDecoration {

        private final static int MARGIN_IN_DP = 88;
        private final int mMarginBottom;

        SpacingDecoration(@NonNull Context context) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            mMarginBottom = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    MARGIN_IN_DP,
                    metrics
            );
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            final int itemPosition = parent.getChildAdapterPosition(view);
            if (itemPosition == RecyclerView.NO_POSITION) {
                return;
            }
            /*if (itemPosition == 0) {
                outRect.top = mMarginBottom;
            }*/
            final RecyclerView.Adapter adapter = parent.getAdapter();
            if ((adapter != null) && (itemPosition == adapter.getItemCount() - 1)) {
                outRect.bottom = mMarginBottom;
            }
        }
    }
}
