package th.ac.dusit.dbizcom.smartshrimp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Retrofit;
import th.ac.dusit.dbizcom.smartshrimp.R;
import th.ac.dusit.dbizcom.smartshrimp.etc.Utils;
import th.ac.dusit.dbizcom.smartshrimp.model.Hatchery;
import th.ac.dusit.dbizcom.smartshrimp.net.AddHatcheryResponse;
import th.ac.dusit.dbizcom.smartshrimp.net.ApiClient;
import th.ac.dusit.dbizcom.smartshrimp.net.MyRetrofitCallback;
import th.ac.dusit.dbizcom.smartshrimp.net.UpdateHatcheryResponse;
import th.ac.dusit.dbizcom.smartshrimp.net.WebServices;

public class AddHatcheryFragment extends Fragment {

    private static final String TITLE = "แหล่งพันธุ์ลูกกุ้ง";
    private static final String ARG_HATCHERY_JSON = "hatchery_json";
    
    private Hatchery mHatchery;

    private AddHatcheryFragmentListener mListener;

    private View mProgressView;
    private EditText mHatcheryNameEditText, mAddressEditText, mSubDistrictEditText, mDistrictEditText;
    private EditText mProvinceEditText, mPostalCodeEditText, mOwnerEditText, mFmdNoEditText;

    private String mHatcheryName, mAddress, mSubDistrict, mDistrict;
    private String mProvince, mPostalCode, mOwner, mFmdNo;

    public AddHatcheryFragment() {
        // Required empty public constructor
    }
    
    public static AddHatcheryFragment newInstance(Hatchery hatchery) {
        AddHatcheryFragment fragment = new AddHatcheryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HATCHERY_JSON, new Gson().toJson(hatchery));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String hatcheryJson = getArguments().getString(ARG_HATCHERY_JSON);
            mHatchery = new Gson().fromJson(hatcheryJson, Hatchery.class);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hatchery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mListener != null) {
            mListener.setupRefreshButton(null);
        }

        mProgressView = view.findViewById(R.id.progress_view);

        mHatcheryNameEditText = view.findViewById(R.id.hatchery_name_edit_text);
        mAddressEditText = view.findViewById(R.id.address_edit_text);
        mSubDistrictEditText = view.findViewById(R.id.sub_district_edit_text);
        mDistrictEditText = view.findViewById(R.id.district_edit_text);
        mProvinceEditText = view.findViewById(R.id.province_edit_text);
        mPostalCodeEditText = view.findViewById(R.id.postal_code_edit_text);
        mOwnerEditText = view.findViewById(R.id.owner_edit_text);
        mFmdNoEditText = view.findViewById(R.id.fmd_no_edit_text);

        if (mHatchery != null) {
            mHatcheryNameEditText.setText(mHatchery.name);
            mAddressEditText.setText(mHatchery.address);
            mSubDistrictEditText.setText(mHatchery.subDistrict);
            mDistrictEditText.setText(mHatchery.district);
            mProvinceEditText.setText(mHatchery.province);
            mPostalCodeEditText.setText(mHatchery.postalCode);
            mOwnerEditText.setText(mHatchery.owner);
            mFmdNoEditText.setText(mHatchery.fmdNo);
        }

        Button saveButton = view.findViewById(R.id.edit_save_button);
        saveButton.setText("บันทึก");
        saveButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_save, 0, 0, 0);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValid()) {
                    doSaveHatchery();
                }
            }
        });
    }

    private boolean isFormValid() {
        boolean valid = true;

        mHatcheryName = mHatcheryNameEditText.getText().toString().trim();
        if (mHatcheryName.isEmpty()) {
            mHatcheryNameEditText.setError("กรอกชื่อโรงเพาะฟัก");
            valid = false;
        }
        mAddress = mAddressEditText.getText().toString().trim();
        if (mAddress.isEmpty()) {
            mAddressEditText.setError("กรอกที่อยู่");
            valid = false;
        }
        mSubDistrict = mSubDistrictEditText.getText().toString().trim();
        if (mSubDistrict.isEmpty()) {
            mSubDistrictEditText.setError("กรอกแขวง/ตำบล");
            valid = false;
        }
        mDistrict = mDistrictEditText.getText().toString().trim();
        if (mDistrict.isEmpty()) {
            mDistrictEditText.setError("กรอกเขต/อำเภอ");
            valid = false;
        }
        mProvince = mProvinceEditText.getText().toString().trim();
        if (mProvince.isEmpty()) {
            mProvinceEditText.setError("กรอกจังหวัด");
            valid = false;
        }
        mPostalCode = mPostalCodeEditText.getText().toString().trim();
        if (mPostalCode.isEmpty()) {
            mPostalCodeEditText.setError("กรอกรหัสไปรษณีย์");
            valid = false;
        }
        mOwner = mOwnerEditText.getText().toString().trim();
        if (mOwner.isEmpty()) {
            mOwnerEditText.setError("กรอกชื่อเจ้าของโรงฟัก");
            valid = false;
        }
        mFmdNo = mFmdNoEditText.getText().toString().trim();
        if (mFmdNo.isEmpty()) {
            mFmdNoEditText.setError("กรอกเลขที่ใบกำกับพันธุ์ลูกกุ้ง");
            valid = false;
        }
        return valid;
    }

    private void doSaveHatchery() {
        mProgressView.setVisibility(View.VISIBLE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        if (mHatchery != null) {
            Call<UpdateHatcheryResponse> call = services.updateHatchery(
                    mHatchery.id,
                    mHatcheryName,
                    mAddress,
                    mSubDistrict,
                    mDistrict,
                    mProvince,
                    mPostalCode,
                    mOwner,
                    mFmdNo
            );
            call.enqueue(new MyRetrofitCallback<>(
                    getActivity(),
                    null,
                    mProgressView,
                    new MyRetrofitCallback.MyRetrofitCallbackListener<UpdateHatcheryResponse>() {
                        @Override
                        public void onSuccess(UpdateHatcheryResponse responseBody) {
                            Utils.showLongToast(getContext(), responseBody.errorMessage);
                            if (mListener != null) {
                                mListener.onSaveHatcherySuccess();
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Utils.showOkDialog(getActivity(), "ผิดพลาด", errorMessage, null);
                        }
                    }
            ));
        } else {
            Call<AddHatcheryResponse> call = services.addHatchery(
                    mHatcheryName,
                    mAddress,
                    mSubDistrict,
                    mDistrict,
                    mProvince,
                    mPostalCode,
                    mOwner,
                    mFmdNo
            );
            call.enqueue(new MyRetrofitCallback<>(
                    getActivity(),
                    null,
                    mProgressView,
                    new MyRetrofitCallback.MyRetrofitCallbackListener<AddHatcheryResponse>() {
                        @Override
                        public void onSuccess(AddHatcheryResponse responseBody) {
                            Utils.showLongToast(getContext(), responseBody.errorMessage);
                            if (mListener != null) {
                                mListener.onSaveHatcherySuccess();
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Utils.showOkDialog(getActivity(), "ผิดพลาด", errorMessage, null);
                        }
                    }
            ));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddHatcheryFragmentListener) {
            mListener = (AddHatcheryFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AddHatcheryFragmentListener");
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
            String title = TITLE;
            if (mHatchery == null) {
                title = "เพิ่ม" + title;
            } else {
                title = "แก้ไข" + title;
            }
            mListener.setTitle(title);
        }
    }

    public interface AddHatcheryFragmentListener {
        void setTitle(String title);

        void setupRefreshButton(View.OnClickListener listener);

        void onSaveHatcherySuccess();
    }
}
