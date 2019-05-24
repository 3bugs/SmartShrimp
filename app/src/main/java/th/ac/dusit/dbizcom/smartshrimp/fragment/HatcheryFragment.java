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

import com.google.gson.Gson;

import th.ac.dusit.dbizcom.smartshrimp.R;
import th.ac.dusit.dbizcom.smartshrimp.model.Hatchery;

public class HatcheryFragment extends Fragment {

    private static final String TITLE = "แหล่งพันธุ์ลูกกุ้ง";
    private static final String ARG_HATCHERY_JSON = "hatchery_json";

    private Hatchery mHatchery;

    private HatcheryFragmentListener mListener;

    private View mProgressView;

    public HatcheryFragment() {
        // Required empty public constructor
    }

    public static HatcheryFragment newInstance(Hatchery hatchery) {
        HatcheryFragment fragment = new HatcheryFragment();
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

        mProgressView = view.findViewById(R.id.progress_view);

        if (mListener != null) {
            //mListener.setupRefreshButton(null);
        }

        ((EditText) view.findViewById(R.id.hatchery_name_edit_text)).setText(mHatchery.name);
        ((EditText) view.findViewById(R.id.address_edit_text)).setText(mHatchery.address);
        ((EditText) view.findViewById(R.id.sub_district_edit_text)).setText(mHatchery.subDistrict);
        ((EditText) view.findViewById(R.id.district_edit_text)).setText(mHatchery.district);
        ((EditText) view.findViewById(R.id.province_edit_text)).setText(mHatchery.province);
        ((EditText) view.findViewById(R.id.postal_code_edit_text)).setText(mHatchery.postalCode);
        ((EditText) view.findViewById(R.id.owner_edit_text)).setText(mHatchery.owner);
        ((EditText) view.findViewById(R.id.fmd_no_edit_text)).setText(mHatchery.fmdNo);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HatcheryFragmentListener) {
            mListener = (HatcheryFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HatcheryFragmentListener");
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

    public interface HatcheryFragmentListener {
        void setTitle(String title);

        void setupRefreshButton(View.OnClickListener listener);
    }
}
