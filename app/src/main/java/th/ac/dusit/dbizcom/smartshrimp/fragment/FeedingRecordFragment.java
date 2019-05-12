package th.ac.dusit.dbizcom.smartshrimp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Retrofit;
import th.ac.dusit.dbizcom.smartshrimp.R;
import th.ac.dusit.dbizcom.smartshrimp.etc.Utils;
import th.ac.dusit.dbizcom.smartshrimp.model.Feeding;
import th.ac.dusit.dbizcom.smartshrimp.net.ApiClient;
import th.ac.dusit.dbizcom.smartshrimp.net.GetFeedingResponse;
import th.ac.dusit.dbizcom.smartshrimp.net.MyRetrofitCallback;
import th.ac.dusit.dbizcom.smartshrimp.net.WebServices;

public class FeedingRecordFragment extends Fragment {

    private static final String TITLE = "บันทึกการให้อาหารกุ้ง";

    private FeedingRecordFragmentListener mListener;

    private View mProgressView;
    private RecyclerView mFeedingRecyclerView;

    public FeedingRecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feeding_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressView = view.findViewById(R.id.progress_view);
        mFeedingRecyclerView = view.findViewById(R.id.feeding_recycler_view);

        if (mListener != null) {
            mListener.setupRefreshButton(true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doGetFeeding();
                }
            });
        }

        doGetFeeding();
    }

    private void doGetFeeding() {
        mProgressView.setVisibility(View.VISIBLE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        Call<GetFeedingResponse> call = services.getFeedingByPond(1);
        call.enqueue(new MyRetrofitCallback<>(
                getActivity(),
                null,
                mProgressView,
                new MyRetrofitCallback.MyRetrofitCallbackListener<GetFeedingResponse>() {
                    @Override
                    public void onSuccess(GetFeedingResponse responseBody) {
                        List<Feeding> feedingList = responseBody.feedingList;
                        FeedingListAdapter adapter = new FeedingListAdapter(
                                getContext(),
                                feedingList
                        );
                        mFeedingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        mFeedingRecyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Utils.showOkDialog(getActivity(), "ผิดพลาด", errorMessage);
                    }
                }
        ));
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

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null) {
            mListener.setTitle(TITLE);
        }
    }

    public interface FeedingRecordFragmentListener {
        void setTitle(String title);

        void setupRefreshButton(boolean visible, View.OnClickListener listener);
    }

    private static class FeedingListAdapter extends RecyclerView.Adapter<FeedingRecordFragment.FeedingListAdapter.FeedingViewHolder> {

        private final Context mContext;
        private final List<Feeding> mFeedingList;

        FeedingListAdapter(Context context, List<Feeding> feedingList) {
            mContext = context;
            mFeedingList = feedingList;

            for (Feeding feeding : mFeedingList) {
                feeding.parseFeedDate();
                feeding.calculateDayTotal();
            }
            for (Feeding feeding : mFeedingList) {
                calculateTotalForFeeding(feeding);
            }
        }

        private void calculateTotalForFeeding(Feeding feeding) {
            for (Feeding f : mFeedingList) {
                if (f.feedDate.compareTo(feeding.feedDate) <= 0) {
                    feeding.setTotal(feeding.getTotal() + f.getDayTotal());
                }
            }
        }

        @NonNull
        @Override
        public FeedingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_feeding_light, parent, false
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
            holder.mFirstFeedTextView.setText(String.valueOf(feeding.firstFeed));
            holder.mSecondFeedTextView.setText(String.valueOf(feeding.secondFeed));
            holder.mThirdFeedTextView.setText(String.valueOf(feeding.thirdFeed));
            holder.mDayTotalTextView.setText(String.valueOf(feeding.getDayTotal()));
            holder.mTotalTextView.setText(String.valueOf(feeding.getTotal()));
        }

        private String formatThaiDate(String dateString) {
            String[] datePart = dateString.split("-");
            String day = datePart[2];
            String month = datePart[1];
            String year = String.valueOf(Integer.parseInt(datePart[0]) + 543).substring(2);
            return String.format(Locale.getDefault(), "%s-%s-%s", day, month, year);
        }

        @Override
        public int getItemCount() {
            return mFeedingList.size();
        }

        class FeedingViewHolder extends RecyclerView.ViewHolder {

            private final TextView mDayTextView;
            private final TextView mFeedDateTextView;
            private final TextView mFirstFeedTextView;
            private final TextView mSecondFeedTextView;
            private final TextView mThirdFeedTextView;
            private final TextView mDayTotalTextView;
            private final TextView mTotalTextView;

            FeedingViewHolder(View itemView) {
                super(itemView);

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
                        //todo:
                    }
                });
            }
        }
    }
}
