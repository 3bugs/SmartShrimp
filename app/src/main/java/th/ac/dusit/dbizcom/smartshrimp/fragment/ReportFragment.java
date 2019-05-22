package th.ac.dusit.dbizcom.smartshrimp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Retrofit;
import th.ac.dusit.dbizcom.smartshrimp.R;
import th.ac.dusit.dbizcom.smartshrimp.etc.Utils;
import th.ac.dusit.dbizcom.smartshrimp.model.Pond;
import th.ac.dusit.dbizcom.smartshrimp.model.Summary;
import th.ac.dusit.dbizcom.smartshrimp.net.ApiClient;
import th.ac.dusit.dbizcom.smartshrimp.net.GetSummaryResponse;
import th.ac.dusit.dbizcom.smartshrimp.net.MyRetrofitCallback;
import th.ac.dusit.dbizcom.smartshrimp.net.WebServices;

public class ReportFragment extends Fragment {

    private static final String TITLE = "รายงานผลข้อมูล";
    private static final String ARG_POND_JSON = "feeding_json";

    private ReportFragmentListener mListener;
    private Pond mPond;

    private View mProgressView;
    private TextView mPondNumberTextView, mFeedTextView, mSizeTextView;
    private TextView mSalePriceTextView, mCostTextView, mProfitTextView;
    private TextView mErrorMessageTextView;

    public ReportFragment() {
        // Required empty public constructor
    }

    public static ReportFragment newInstance(Pond pond) {
        ReportFragment fragment = new ReportFragment();
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
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressView = view.findViewById(R.id.progress_view);
        mPondNumberTextView = view.findViewById(R.id.pond_number_text_view);
        mFeedTextView = view.findViewById(R.id.feed_text_view);
        mSizeTextView = view.findViewById(R.id.size_text_view);
        mSalePriceTextView = view.findViewById(R.id.sale_price_text_view);
        mCostTextView = view.findViewById(R.id.cost_text_view);
        mProfitTextView = view.findViewById(R.id.profit_text_view);
        mErrorMessageTextView = view.findViewById(R.id.error_message_text_view);

        doGetSummary();
    }

    private void doGetSummary() {
        mProgressView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.GONE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        Call<GetSummaryResponse> call = services.getSummary(mPond.id); //todo: ********
        call.enqueue(new MyRetrofitCallback<>(
                getActivity(),
                null,
                mProgressView,
                new MyRetrofitCallback.MyRetrofitCallbackListener<GetSummaryResponse>() {
                    @Override
                    public void onSuccess(GetSummaryResponse responseBody) {
                        Summary summary = responseBody.summary;

                        String title = String.format(
                                Locale.getDefault(),
                                "บ่อที่: %d   ชื่อฟาร์ม: %s",
                                summary.pondNumber, summary.farmName
                        );
                        mPondNumberTextView.setText(title);

                        if (summary.period == 0 || summary.finalWeight <= 0
                                || summary.cost <= 0 || summary.salePrice <= 0) {
                            if (getActivity() != null) {
                                String errorMessage = "ไม่สามารถแสดงรายงานข้อมูลได้ เนื่องจากไม่มีข้อมูลการให้อาหารของบ่อนี้ และ/หรือยังกรอกข้อมูลในหน้าสรุปผลไม่ครบถ้วน";
                                mErrorMessageTextView.setText(errorMessage);
                                mErrorMessageTextView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            String feedText = Utils.formatWholeNumberWithComma(summary.feed)
                                    + " กก.";
                            mFeedTextView.setText(feedText);

                            double shrimpCount = summary.shrimpCount;
                            double finalWeight = summary.finalWeight;
                            double size = shrimpCount / finalWeight;
                            String sizeText = Utils.formatNumber2DecimalDigitsWithComma(size)
                                    + " ตัว/กก.";
                            mSizeTextView.setText(sizeText);

                            String salePriceText = Utils.formatWholeNumberWithComma(summary.salePrice)
                                    + " บาท";
                            mSalePriceTextView.setText(salePriceText);

                            String costText = Utils.formatWholeNumberWithComma(summary.cost)
                                    + " บาท";
                            mCostTextView.setText(costText);

                            String profitText = Utils.formatWholeNumberWithComma(summary.salePrice - summary.cost)
                                    + " บาท";
                            mProfitTextView.setText(profitText);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Utils.showOkDialog(getActivity(), "ผิดพลาด", errorMessage, null);
                    }
                }
        ));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ReportFragmentListener) {
            mListener = (ReportFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ReportFragmentListener");
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
                    doGetSummary();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null) {
            mListener.setTitle(TITLE);
        }
    }

    public interface ReportFragmentListener {
        void setTitle(String title);

        void setupRefreshButton(View.OnClickListener listener);
    }
}
