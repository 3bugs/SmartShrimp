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

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Retrofit;
import th.ac.dusit.dbizcom.smartshrimp.R;
import th.ac.dusit.dbizcom.smartshrimp.etc.MyDateFormatter;
import th.ac.dusit.dbizcom.smartshrimp.etc.Utils;
import th.ac.dusit.dbizcom.smartshrimp.model.Summary;
import th.ac.dusit.dbizcom.smartshrimp.net.ApiClient;
import th.ac.dusit.dbizcom.smartshrimp.net.GetSummaryResponse;
import th.ac.dusit.dbizcom.smartshrimp.net.MyRetrofitCallback;
import th.ac.dusit.dbizcom.smartshrimp.net.WebServices;

public class SummaryFragment extends Fragment {

    private static final String TITLE = "สรุปผลการเลี้ยง";

    private SummaryFragmentListener mListener;

    private View mProgressView;

    public SummaryFragment() {
        // Required empty public constructor
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

        if (mListener != null) {
            mListener.setupRefreshButton(null);
        }

        doGetSummary(view);
    }

    private void doGetSummary(final View view) {
        mProgressView.setVisibility(View.VISIBLE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        Call<GetSummaryResponse> call = services.getSummary();
        call.enqueue(new MyRetrofitCallback<>(
                getActivity(),
                null,
                mProgressView,
                new MyRetrofitCallback.MyRetrofitCallbackListener<GetSummaryResponse>() {
                    @Override
                    public void onSuccess(GetSummaryResponse responseBody) {
                        final Summary summary = responseBody.summary;
                        ((EditText) view.findViewById(R.id.pond_area_edit_text)).setText(String.valueOf(summary.pondArea));

                        String beginDate = MyDateFormatter.formatForUiShortYear(new MyDateFormatter().parseDateString(summary.beginDate));
                        ((EditText) view.findViewById(R.id.begin_date_edit_text)).setText(beginDate);

                        String endDate = MyDateFormatter.formatForUiShortYear(new MyDateFormatter().parseDateString(summary.endDate));
                        ((EditText) view.findViewById(R.id.end_date_edit_text)).setText(endDate);

                        ((EditText) view.findViewById(R.id.count_edit_text)).setText(String.valueOf(summary.shrimpCount));
                        ((EditText) view.findViewById(R.id.period_edit_text)).setText(String.valueOf(summary.period));
                        ((EditText) view.findViewById(R.id.feed_edit_text)).setText(String.valueOf(summary.feed));

                        ((EditText) view.findViewById(R.id.final_weight_edit_text)).addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                EditText sizeEditText = view.findViewById(R.id.size_edit_text);
                                int finalWeight = 0;
                                try {
                                    finalWeight = Integer.parseInt(charSequence.toString());
                                } catch (NumberFormatException e) {
                                    sizeEditText.setText("");
                                }
                                if (finalWeight > 0) {
                                    float size = (float) summary.shrimpCount / (float) finalWeight;
                                    sizeEditText.setText(String.format(Locale.getDefault(), "%.2f", size));
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        final EditText profitEditText = view.findViewById(R.id.profit_edit_text);
                        final EditText salePriceEditText = view.findViewById(R.id.sale_price_edit_text);
                        final EditText costEditText = view.findViewById(R.id.cost_edit_text);

                        salePriceEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                try {
                                    int salePrice = Integer.parseInt(charSequence.toString());
                                    int cost = Integer.parseInt(costEditText.getText().toString());

                                    profitEditText.setText(String.valueOf(salePrice - cost));
                                } catch (NumberFormatException e) {
                                    profitEditText.setText("");
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        costEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                try {
                                    int salePrice = Integer.parseInt(salePriceEditText.getText().toString());
                                    int cost = Integer.parseInt(charSequence.toString());

                                    profitEditText.setText(String.valueOf(salePrice - cost));
                                } catch (NumberFormatException e) {
                                    profitEditText.setText("");
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
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

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null) {
            mListener.setTitle(TITLE);
        }
    }

    public interface SummaryFragmentListener {
        void setTitle(String title);

        void setupRefreshButton(View.OnClickListener listener);
    }
}
