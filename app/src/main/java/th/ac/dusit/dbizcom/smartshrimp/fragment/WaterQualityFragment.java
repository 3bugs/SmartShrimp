package th.ac.dusit.dbizcom.smartshrimp.fragment;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Retrofit;
import th.ac.dusit.dbizcom.smartshrimp.R;
import th.ac.dusit.dbizcom.smartshrimp.etc.MyDateFormatter;
import th.ac.dusit.dbizcom.smartshrimp.etc.Utils;
import th.ac.dusit.dbizcom.smartshrimp.model.WaterQuality;
import th.ac.dusit.dbizcom.smartshrimp.net.AddWaterQualityResponse;
import th.ac.dusit.dbizcom.smartshrimp.net.ApiClient;
import th.ac.dusit.dbizcom.smartshrimp.net.GetWaterQualityResponse;
import th.ac.dusit.dbizcom.smartshrimp.net.MyRetrofitCallback;
import th.ac.dusit.dbizcom.smartshrimp.net.WebServices;

public class WaterQualityFragment extends Fragment {

    private static final String TITLE = "คุณภาพน้ำในบ่อเลี้ยง";

    private Calendar mCalendar = Calendar.getInstance();

    private WaterQualityFragmentListener mListener;

    private View mProgressView;
    private EditText mTestDateEditText;
    private EditText mPhMorningEditText, mPhEveningEditText, mSaltyEditText, mAmmoniaEditText;
    private EditText mNitriteEditText, mAlkalineEditText, mCalciumEditText, mMagnesiumEditText;

    public WaterQualityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_water_quality, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressView = view.findViewById(R.id.progress_view);

        mPhMorningEditText = view.findViewById(R.id.ph_morning_edit_text);
        mPhEveningEditText = view.findViewById(R.id.ph_evening_edit_text);
        mSaltyEditText = view.findViewById(R.id.salty_edit_text);
        mAmmoniaEditText = view.findViewById(R.id.ammonia_edit_text);
        mNitriteEditText = view.findViewById(R.id.nitrite_edit_text);
        mAlkalineEditText = view.findViewById(R.id.alkaline_edit_text);
        mCalciumEditText = view.findViewById(R.id.calcium_edit_text);
        mMagnesiumEditText = view.findViewById(R.id.magnesium_edit_text);

        if (mListener != null) {
            mListener.setupRefreshButton(null);
        }

        mTestDateEditText = view.findViewById(R.id.test_date_edit_text);
        mTestDateEditText.setOnClickListener(new View.OnClickListener() {
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
                                updateTestDateEditText();
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

        mTestDateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                doGetWaterQuality();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        view.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValid()) {
                    doAddOrUpdateWaterQuality();
                }
            }
        });

        updateTestDateEditText();
    }

    private boolean isFormValid() {
        boolean valid = true;
        String mPhMorning = mPhMorningEditText.getText().toString().trim();
        String mPhEvening = mPhEveningEditText.getText().toString().trim();
        String mSalty = mSaltyEditText.getText().toString().trim();
        String mAmmonia = mAmmoniaEditText.getText().toString().trim();
        String mNitrite = mNitriteEditText.getText().toString().trim();
        String mAlkaline = mAlkalineEditText.getText().toString().trim();
        String mCalcium = mCalciumEditText.getText().toString().trim();
        String mMagnesium = mMagnesiumEditText.getText().toString().trim();

        if (mMagnesium.isEmpty()) {
            mMagnesiumEditText.setError("กรอกค่าแมกนีเซียม");
            valid = false;
        }
        if (mCalcium.isEmpty()) {
            mCalciumEditText.setError("กรอกค่าแคลเซียม");
            valid = false;
        }
        if (mAlkaline.isEmpty()) {
            mAlkalineEditText.setError("กรอกค่าอัลคาไลน์");
            valid = false;
        }
        if (mNitrite.isEmpty()) {
            mNitriteEditText.setError("กรอกค่าไนไตรท์");
            valid = false;
        }
        if (mAmmonia.isEmpty()) {
            mAmmoniaEditText.setError("กรอกค่าแอมโมเนีย");
            valid = false;
        }
        if (mSalty.isEmpty()) {
            mSaltyEditText.setError("กรอกค่าความเค็ม");
            valid = false;
        }
        if (mPhEvening.isEmpty() && mPhMorning.isEmpty()) {
            mPhMorningEditText.setError("กรอกค่า PH อย่างน้อย 1 เวลา");
            mPhEveningEditText.setError("กรอกค่า PH อย่างน้อย 1 เวลา");
            valid = false;
        }
        return valid;
    }

    private void doAddOrUpdateWaterQuality() {
        String phMorningText = mPhMorningEditText.getText().toString().trim();
        String phEveningText = mPhEveningEditText.getText().toString().trim();

        double phMorning = Double.parseDouble("".equals(phMorningText) ? "0" : phMorningText);
        double phEvening = Double.parseDouble("".equals(phEveningText) ? "0" : phEveningText);
        double salty = Double.parseDouble(mSaltyEditText.getText().toString());
        double ammonia = Double.parseDouble(mAmmoniaEditText.getText().toString());
        double nitrite = Double.parseDouble(mNitriteEditText.getText().toString());
        double alkaline = Double.parseDouble(mAlkalineEditText.getText().toString());
        double calcium = Double.parseDouble(mCalciumEditText.getText().toString());
        double magnesium = Double.parseDouble(mMagnesiumEditText.getText().toString());

        mProgressView.setVisibility(View.VISIBLE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        String testDate = new MyDateFormatter().formatForDb(mCalendar.getTime());

        Call<AddWaterQualityResponse> call = services.addWaterQuality(
                9, //todo: *************************************************************************
                testDate,
                phMorning, phEvening, salty, ammonia, nitrite, alkaline, calcium, magnesium
        );
        call.enqueue(new MyRetrofitCallback<AddWaterQualityResponse>(
                getActivity(),
                null,
                mProgressView,
                new MyRetrofitCallback.MyRetrofitCallbackListener<AddWaterQualityResponse>() {
                    @Override
                    public void onSuccess(AddWaterQualityResponse responseBody) {
                        Utils.showOkDialog(getActivity(), "สำเร็จ", responseBody.errorMessage, null);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Utils.showOkDialog(getActivity(), "ผิดพลาด", errorMessage, null);
                    }
                }
        ));
    }

    private void updateTestDateEditText() {
        String formatDate = MyDateFormatter.formatForUi(mCalendar.getTime());
        mTestDateEditText.setText(formatDate);
    }

    private void doGetWaterQuality() {
        mProgressView.setVisibility(View.VISIBLE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        int pondId = 9; // todo: ************************************************************
        String testDate = new MyDateFormatter().formatForDb(mCalendar.getTime());

        Call<GetWaterQualityResponse> call = services.getWaterQuality(
                pondId,
                testDate
        );
        call.enqueue(new MyRetrofitCallback<>(
                getActivity(),
                null,
                mProgressView,
                new MyRetrofitCallback.MyRetrofitCallbackListener<GetWaterQualityResponse>() {
                    @Override
                    public void onSuccess(GetWaterQualityResponse responseBody) {
                        List<WaterQuality> waterQualityList = responseBody.waterQualityList;
                        if (waterQualityList.size() > 0) {
                            WaterQuality waterQuality = waterQualityList.get(0);

                            mPhMorningEditText.setText(waterQuality.phMorning == 0 ? "" : String.format(Locale.getDefault(), "%.1f", waterQuality.phMorning));
                            mPhEveningEditText.setText(waterQuality.phEvening == 0 ? "" : String.format(Locale.getDefault(), "%.1f", waterQuality.phEvening));
                            mSaltyEditText.setText(waterQuality.salty == 0 ? "" : String.format(Locale.getDefault(), "%.1f", waterQuality.salty));
                            mAmmoniaEditText.setText(waterQuality.ammonia == 0 ? "" : String.format(Locale.getDefault(), "%.1f", waterQuality.ammonia));
                            mNitriteEditText.setText(waterQuality.nitrite == 0 ? "" : String.format(Locale.getDefault(), "%.1f", waterQuality.nitrite));
                            mAlkalineEditText.setText(waterQuality.alkaline == 0 ? "" : String.format(Locale.getDefault(), "%.1f", waterQuality.alkaline));
                            mCalciumEditText.setText(waterQuality.calcium == 0 ? "" : String.format(Locale.getDefault(), "%.1f", waterQuality.calcium));
                            mMagnesiumEditText.setText(waterQuality.magnesium == 0 ? "" : String.format(Locale.getDefault(), "%.1f", waterQuality.magnesium));
                        } else {
                            mPhMorningEditText.setText("");
                            mPhEveningEditText.setText("");
                            mSaltyEditText.setText("");
                            mAmmoniaEditText.setText("");
                            mNitriteEditText.setText("");
                            mAlkalineEditText.setText("");
                            mCalciumEditText.setText("");
                            mMagnesiumEditText.setText("");
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
        if (context instanceof WaterQualityFragmentListener) {
            mListener = (WaterQualityFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement WaterQualityFragmentListener");
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

    public interface WaterQualityFragmentListener {
        void setTitle(String title);

        void setupRefreshButton(View.OnClickListener listener);
    }
}
