package th.ac.dusit.dbizcom.smartshrimp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Retrofit;
import th.ac.dusit.dbizcom.smartshrimp.R;
import th.ac.dusit.dbizcom.smartshrimp.etc.MyDateFormatter;
import th.ac.dusit.dbizcom.smartshrimp.etc.Utils;
import th.ac.dusit.dbizcom.smartshrimp.model.Pond;
import th.ac.dusit.dbizcom.smartshrimp.model.Summary;
import th.ac.dusit.dbizcom.smartshrimp.net.ApiClient;
import th.ac.dusit.dbizcom.smartshrimp.net.GetSummaryResponse;
import th.ac.dusit.dbizcom.smartshrimp.net.MyRetrofitCallback;
import th.ac.dusit.dbizcom.smartshrimp.net.UpdateSummaryResponse;
import th.ac.dusit.dbizcom.smartshrimp.net.WebServices;

public class SummaryFragment extends Fragment {

    private static final String TITLE = "สรุปผลการเลี้ยง";
    private static final String ARG_POND_JSON = "feeding_json";

    private Pond mPond;

    private SummaryFragmentListener mListener;

    private View mProgressView;
    private EditText mSalePriceEditText, mCostEditText, mFinalWeightEditText;
    private EditText mSizeEditText, mProfitEditText;
    private View mMainLayout;
    private TextView mErrorMessageTextView;

    public SummaryFragment() {
        // Required empty public constructor
    }

    public static SummaryFragment newInstance(Pond pond) {
        SummaryFragment fragment = new SummaryFragment();
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
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressView = view.findViewById(R.id.progress_view);
        mFinalWeightEditText = view.findViewById(R.id.final_weight_edit_text);
        mCostEditText = view.findViewById(R.id.cost_edit_text);
        mSizeEditText = view.findViewById(R.id.size_edit_text);
        mSalePriceEditText = view.findViewById(R.id.sale_price_edit_text);
        mProfitEditText = view.findViewById(R.id.profit_edit_text);

        mMainLayout = view.findViewById(R.id.farm_info_scroll_view);
        mErrorMessageTextView = view.findViewById(R.id.error_message_text_view);

        if (mListener != null) {
            mListener.setupRefreshButton(null);
        }

        view.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValid()) {
                    doUpdateSummary();
                }
            }
        });

        doGetSummary(view);
    }

    private void doGetSummary(final View view) {
        mProgressView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.GONE);
        mMainLayout.setVisibility(View.GONE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        Call<GetSummaryResponse> call = services.getSummary(mPond.id); //todo: **************
        call.enqueue(new MyRetrofitCallback<>(
                getActivity(),
                null,
                mProgressView,
                new MyRetrofitCallback.MyRetrofitCallbackListener<GetSummaryResponse>() {
                    @Override
                    public void onSuccess(GetSummaryResponse responseBody) {
                        final Summary summary = responseBody.summary;

                        if (summary.beginDate == null || summary.endDate == null) {
                            String errorMessage = "ไม่สามารถสรุปผลได้ เนื่องจากไม่มีข้อมูลการให้อาหารของบ่อนี้";
                            mErrorMessageTextView.setText(errorMessage);
                            mErrorMessageTextView.setVisibility(View.VISIBLE);
                            return;
                        }

                        mMainLayout.setVisibility(View.VISIBLE);
                        ((EditText) view.findViewById(R.id.pond_area_edit_text)).setText(String.valueOf(summary.pondArea));

                        String beginDate = MyDateFormatter.formatForUiShortYear(new MyDateFormatter().parseDateString(summary.beginDate));
                        ((EditText) view.findViewById(R.id.begin_date_edit_text)).setText(beginDate);

                        String endDate = MyDateFormatter.formatForUiShortYear(new MyDateFormatter().parseDateString(summary.endDate));
                        ((EditText) view.findViewById(R.id.end_date_edit_text)).setText(endDate);

                        ((EditText) view.findViewById(R.id.count_edit_text)).setText(String.valueOf(summary.shrimpCount));
                        ((EditText) view.findViewById(R.id.period_edit_text)).setText(String.valueOf(summary.period));
                        ((EditText) view.findViewById(R.id.feed_edit_text)).setText(String.valueOf(summary.feed));

                        mFinalWeightEditText.setText(summary.finalWeight < 0 ? "" : String.valueOf(summary.finalWeight));
                        mCostEditText.setText(summary.cost < 0 ? "" : String.valueOf(summary.cost));
                        mSalePriceEditText.setText(summary.salePrice < 0 ? "" : String.valueOf(summary.salePrice));

                        calculateSize(mFinalWeightEditText.getText().toString(), summary.shrimpCount);
                        calculateProfit(mSalePriceEditText.getText().toString(), mCostEditText.getText().toString());

                        mFinalWeightEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                calculateSize(charSequence.toString(), summary.shrimpCount);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                            }
                        });

                        mSalePriceEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                calculateProfit(charSequence.toString(), mCostEditText.getText().toString());
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                            }
                        });

                        mCostEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                calculateProfit(mSalePriceEditText.getText().toString(), charSequence.toString());
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                            }
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Utils.showOkDialog(getActivity(), "ผิดพลาด", errorMessage, null);
                    }
                }
        ));
    }

    private void calculateProfit(String salePriceText, String costText) {
        try {
            int salePrice = Integer.parseInt(salePriceText);
            int cost = Integer.parseInt(costText);

            mProfitEditText.setText(String.valueOf(salePrice - cost));
        } catch (NumberFormatException e) {
            mProfitEditText.setText("");
        }
    }

    private void calculateSize(String finalWeightText, int shrimpCount) {
        int finalWeight = 0;
        try {
            finalWeight = Integer.parseInt(finalWeightText);
        } catch (NumberFormatException e) {
            mSizeEditText.setText("");
        }
        if (finalWeight > 0) {
            float size = (float) shrimpCount / (float) finalWeight;
            mSizeEditText.setText(String.format(Locale.getDefault(), "%.2f", size));
        }
    }

    private boolean isFormValid() {
        boolean valid = true;

        if (mCostEditText.getText().toString().trim().isEmpty()) {
            mCostEditText.setError("กรอกค่าใช้จ่าย");
            valid = false;
        }
        if (mSalePriceEditText.getText().toString().trim().isEmpty()) {
            mSalePriceEditText.setError("กรอกราคากุ้งที่ขายได้");
            valid = false;
        }
        if (mFinalWeightEditText.getText().toString().trim().isEmpty()) {
            mFinalWeightEditText.setError("กรอกผลผลิต");
            valid = false;
        }

        return valid;
    }

    private void doUpdateSummary() {
        mProgressView.setVisibility(View.VISIBLE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        Call<UpdateSummaryResponse> call = services.updateSummary(
                mPond.id,
                Integer.parseInt(mFinalWeightEditText.getText().toString()),
                Integer.parseInt(mCostEditText.getText().toString()),
                Integer.parseInt(mSalePriceEditText.getText().toString())
        );
        call.enqueue(new MyRetrofitCallback<>(
                getActivity(),
                null,
                mProgressView,
                new MyRetrofitCallback.MyRetrofitCallbackListener<UpdateSummaryResponse>() {
                    @Override
                    public void onSuccess(UpdateSummaryResponse responseBody) {
                        if (getActivity() != null) {
                            Utils.showLongToast(getActivity(), responseBody.errorMessage);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Utils.showOkDialog(getActivity(), "ผิดพลาด", errorMessage, null);
                    }
                })
        );
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SummaryFragmentListener) {
            mListener = (SummaryFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SummaryFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /*@Override
    public void onResume() {
        super.onResume();
        if (mListener != null) {
            mListener.setTitle(TITLE);
        }
    }*/

    public interface SummaryFragmentListener {
        void setTitle(String title);

        void setupRefreshButton(View.OnClickListener listener);
    }
}
