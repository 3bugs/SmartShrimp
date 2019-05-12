package th.ac.dusit.dbizcom.smartshrimp.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Retrofit;
import th.ac.dusit.dbizcom.smartshrimp.R;
import th.ac.dusit.dbizcom.smartshrimp.etc.MyDateFormatter;
import th.ac.dusit.dbizcom.smartshrimp.etc.Utils;
import th.ac.dusit.dbizcom.smartshrimp.model.Feeding;
import th.ac.dusit.dbizcom.smartshrimp.net.AddFeedingResponse;
import th.ac.dusit.dbizcom.smartshrimp.net.ApiClient;
import th.ac.dusit.dbizcom.smartshrimp.net.MyRetrofitCallback;
import th.ac.dusit.dbizcom.smartshrimp.net.UpdateFeedingResponse;
import th.ac.dusit.dbizcom.smartshrimp.net.WebServices;

public class AddFeedingRecordFragment extends Fragment {

    private static final String TITLE = "บันทึกการให้อาหารกุ้ง";
    private static final String ARG_POND_ID = "pond_id";
    private static final String ARG_FEEDING_JSON = "feeding_json";

    private int mPondId;
    private Feeding mFeeding;
    private Calendar mCalendar = Calendar.getInstance();

    private AddFeedingRecordFragmentListener mListener;

    private View mProgressView;
    private EditText mFeedDateEditText;
    private EditText mFirstFeedEditText, mSecondFeedEditText, mThirdFeedEditText;

    public AddFeedingRecordFragment() {
        // Required empty public constructor
    }

    public static AddFeedingRecordFragment newInstance(int pondId, Feeding feeding) {
        AddFeedingRecordFragment fragment = new AddFeedingRecordFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POND_ID, pondId);
        args.putString(ARG_FEEDING_JSON, new Gson().toJson(feeding));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPondId = getArguments().getInt(ARG_POND_ID);
            String feedingJson = getArguments().getString(ARG_FEEDING_JSON);
            mFeeding = new Gson().fromJson(feedingJson, Feeding.class);
        }
        if (mFeeding != null) {
            Date date = new MyDateFormatter().parseDateString(mFeeding.feedDate);
            mCalendar.setTime(date);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_feeding_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressView = view.findViewById(R.id.progress_view);

        mFirstFeedEditText = view.findViewById(R.id.first_feed_edit_text);
        mSecondFeedEditText = view.findViewById(R.id.second_feed_edit_text);
        mThirdFeedEditText = view.findViewById(R.id.third_feed_edit_text);

        if (mFeeding != null) {
            mFirstFeedEditText.setText(
                    mFeeding.firstFeed == 0 ? "" : String.valueOf(mFeeding.firstFeed)
            );
            mSecondFeedEditText.setText(
                    mFeeding.secondFeed == 0 ? "" : String.valueOf(mFeeding.secondFeed)
            );
            mThirdFeedEditText.setText(
                    mFeeding.thirdFeed == 0 ? "" : String.valueOf(mFeeding.thirdFeed)
            );
        }

        mFeedDateEditText = view.findViewById(R.id.feed_date_edit_text);
        if (mFeeding == null) {
            mFeedDateEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final DatePickerDialog.OnDateSetListener dateSetListener =
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    mCalendar.set(Calendar.YEAR, year);
                                    mCalendar.set(Calendar.MONTH, monthOfYear);
                                    mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    updateFeedDateEditText();
                                }
                            };
                    if (getActivity() != null) {
                        new DatePickerDialog(
                                getActivity(),
                                dateSetListener,
                                mCalendar.get(Calendar.YEAR),
                                mCalendar.get(Calendar.MONTH),
                                mCalendar.get(Calendar.DAY_OF_MONTH)
                        ).show();
                    }
                }
            });
        } else {
            mFeedDateEditText.setTextColor(Color.GRAY);
        }

        view.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValid()) {
                    doSaveFeedingRecord();
                } else {

                }
            }
        });

        if (mListener != null) {
            mListener.setupRefreshButton(false, null);
        }

        updateFeedDateEditText();
    }

    private void updateFeedDateEditText() {
        String formatDate = MyDateFormatter.formatForUi(mCalendar.getTime());
        mFeedDateEditText.setText(formatDate);
    }

    private String getFormatFeedDateForMysql() {
        Date date = mCalendar.getTime();

        SimpleDateFormat monthFormatter = new SimpleDateFormat("MM", Locale.US);
        String month = monthFormatter.format(date);

        SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy", Locale.US);
        String year = yearFormatter.format(date);

        SimpleDateFormat dayFormatter = new SimpleDateFormat("dd", Locale.US);
        String day = dayFormatter.format(date);

        return String.format(
                Locale.getDefault(),
                "%s-%s-%s",
                year, month, day
        );
    }

    private boolean isFormValid() {
        boolean valid = true;
        String firstFeed = mFirstFeedEditText.getText().toString().trim();
        String secondFeed = mSecondFeedEditText.getText().toString().trim();
        String thirdFeed = mThirdFeedEditText.getText().toString().trim();

        if (firstFeed.isEmpty() && secondFeed.isEmpty() && thirdFeed.isEmpty()) {
            Utils.showLongToast(getContext(), "ต้องกรอกจำนวนอาหารอย่างน้อย 1 ช่อง");

            //mFirstFeedEditText.setError("");
            //mSecondFeedEditText.setError("");
            //mThirdFeedEditText.setError("");

            valid = false;
        }
        return valid;
    }

    private void doSaveFeedingRecord() {
        mProgressView.setVisibility(View.VISIBLE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        String firstFeed = mFirstFeedEditText.getText().toString().trim();
        String secondFeed = mSecondFeedEditText.getText().toString().trim();
        String thirdFeed = mThirdFeedEditText.getText().toString().trim();

        if (mFeeding != null) {
            Call<UpdateFeedingResponse> call = services.updateFeeding(
                    mFeeding.id,
                    firstFeed.isEmpty() ? 0 : Integer.parseInt(firstFeed),
                    secondFeed.isEmpty() ? 0 : Integer.parseInt(secondFeed),
                    thirdFeed.isEmpty() ? 0 : Integer.parseInt(thirdFeed)
            );
            call.enqueue(new MyRetrofitCallback<>(
                    getActivity(),
                    null,
                    mProgressView,
                    new MyRetrofitCallback.MyRetrofitCallbackListener<UpdateFeedingResponse>() {
                        @Override
                        public void onSuccess(UpdateFeedingResponse responseBody) {
                            Utils.showLongToast(getContext(), responseBody.errorMessage);
                            if (mListener != null) {
                                mListener.onSaveFeedingRecordSuccess();
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Utils.showOkDialog(getActivity(), "ผิดพลาด", errorMessage);
                        }
                    }
            ));
        } else {
            Call<AddFeedingResponse> call = services.addFeeding(
                    mPondId,
                    getFormatFeedDateForMysql(),
                    firstFeed.isEmpty() ? 0 : Integer.parseInt(firstFeed),
                    secondFeed.isEmpty() ? 0 : Integer.parseInt(secondFeed),
                    thirdFeed.isEmpty() ? 0 : Integer.parseInt(thirdFeed)
            );
            call.enqueue(new MyRetrofitCallback<>(
                    getActivity(),
                    null,
                    mProgressView,
                    new MyRetrofitCallback.MyRetrofitCallbackListener<AddFeedingResponse>() {
                        @Override
                        public void onSuccess(AddFeedingResponse responseBody) {
                            Utils.showLongToast(getContext(), responseBody.errorMessage);
                            if (mListener != null) {
                                mListener.onSaveFeedingRecordSuccess();
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Utils.showOkDialog(getActivity(), "ผิดพลาด", errorMessage);
                        }
                    }
            ));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddFeedingRecordFragmentListener) {
            mListener = (AddFeedingRecordFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AddFeedingRecordFragmentListener");
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

    public interface AddFeedingRecordFragmentListener {
        void setTitle(String title);

        void setupRefreshButton(boolean visible, View.OnClickListener listener);

        void onSaveFeedingRecordSuccess();
    }
}
