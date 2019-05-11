package th.ac.dusit.dbizcom.smartshrimp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import th.ac.dusit.dbizcom.smartshrimp.R;
import th.ac.dusit.dbizcom.smartshrimp.etc.Utils;
import th.ac.dusit.dbizcom.smartshrimp.model.Farm;
import th.ac.dusit.dbizcom.smartshrimp.net.ApiClient;
import th.ac.dusit.dbizcom.smartshrimp.net.GetFarmInfoResponse;
import th.ac.dusit.dbizcom.smartshrimp.net.MyRetrofitCallback;
import th.ac.dusit.dbizcom.smartshrimp.net.WebServices;

public class FarmInfoFragment extends Fragment {

    private static final String TITLE = "ข้อมูลฟาร์ม";

    private FarmInfoFragmentListener mListener;

    private View mProgressView;

    public FarmInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_farm_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressView = view.findViewById(R.id.progress_view);

        if (mListener != null) {
            mListener.setupRefreshButton(true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doGetFarmInfo(view);
                }
            });
        }

        view.findViewById(R.id.pond_info_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onClickPondInfoButton();
                }
            }
        });

        doGetFarmInfo(view);
    }

    private void doGetFarmInfo(final View view) {
        mProgressView.setVisibility(View.VISIBLE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        Call<GetFarmInfoResponse> call = services.getFarmInfo();
        call.enqueue(new MyRetrofitCallback<>(
                getActivity(),
                null,
                mProgressView,
                new MyRetrofitCallback.MyRetrofitCallbackListener<GetFarmInfoResponse>() {
                    @Override
                    public void onSuccess(GetFarmInfoResponse responseBody) {
                        List<Farm> farmList = responseBody.farmList;
                        if (farmList.size() > 0) {
                            Farm farmInfo = farmList.get(0);
                            ((EditText) view.findViewById(R.id.farm_name_edit_text)).setText(farmInfo.name);
                            ((EditText) view.findViewById(R.id.address_edit_text)).setText(farmInfo.address);
                            ((EditText) view.findViewById(R.id.sub_district_edit_text)).setText(farmInfo.subDistrict);
                            ((EditText) view.findViewById(R.id.district_edit_text)).setText(farmInfo.district);
                            ((EditText) view.findViewById(R.id.province_edit_text)).setText(farmInfo.province);
                            ((EditText) view.findViewById(R.id.postal_code_edit_text)).setText(farmInfo.postalCode);
                            ((EditText) view.findViewById(R.id.farm_reg_id_edit_text)).setText(farmInfo.farmRegId);
                        } else {
                            String msg = "ยังไม่มีข้อมูลฟาร์ม กรุณาใส่ข้อมูลที่ระบบเว็บหลังบ้าน";
                            Utils.showOkDialog(getActivity(), "Farm Information", msg);
                        }
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
        if (context instanceof FarmInfoFragmentListener) {
            mListener = (FarmInfoFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FarmInfoFragmentListener");
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

    public interface FarmInfoFragmentListener {
        void setTitle(String title);

        void setupRefreshButton(boolean visible, View.OnClickListener listener);

        void onClickPondInfoButton();
    }
}
