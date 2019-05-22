package th.ac.dusit.dbizcom.smartshrimp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import th.ac.dusit.dbizcom.smartshrimp.MainActivity;
import th.ac.dusit.dbizcom.smartshrimp.R;

import static th.ac.dusit.dbizcom.smartshrimp.MainActivity.KEY_FRAGMENT;
import static th.ac.dusit.dbizcom.smartshrimp.MainActivity.TAG_FRAGMENT_BREED_SOURCE;
import static th.ac.dusit.dbizcom.smartshrimp.MainActivity.TAG_FRAGMENT_FARM_INFO;
import static th.ac.dusit.dbizcom.smartshrimp.MainActivity.TAG_FRAGMENT_FEEDING_RECORD_PAGER;
import static th.ac.dusit.dbizcom.smartshrimp.MainActivity.TAG_FRAGMENT_FORMULA_MAIN;
import static th.ac.dusit.dbizcom.smartshrimp.MainActivity.TAG_FRAGMENT_REPORT_PAGER;
import static th.ac.dusit.dbizcom.smartshrimp.MainActivity.TAG_FRAGMENT_SUMMARY_PAGER;
import static th.ac.dusit.dbizcom.smartshrimp.MainActivity.TAG_FRAGMENT_WATER_QUALITY;

public class MenuHomeFragment extends Fragment implements View.OnClickListener {

    private MenuHomeFragmentListener mListener;

    public MenuHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.farm_info_image_view).setOnClickListener(this);
        view.findViewById(R.id.feeding_record_image_view).setOnClickListener(this);
        view.findViewById(R.id.water_quality_image_view).setOnClickListener(this);
        view.findViewById(R.id.breed_source_image_view).setOnClickListener(this);
        view.findViewById(R.id.formula_image_view).setOnClickListener(this);
        view.findViewById(R.id.summary_image_view).setOnClickListener(this);
        view.findViewById(R.id.report_image_view).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        String fragmentTag = null;
        switch (view.getId()) {
            case R.id.farm_info_image_view:
                fragmentTag = TAG_FRAGMENT_FARM_INFO;
                break;
            case R.id.feeding_record_image_view:
                fragmentTag = TAG_FRAGMENT_FEEDING_RECORD_PAGER;
                break;
            case R.id.water_quality_image_view:
                fragmentTag = TAG_FRAGMENT_WATER_QUALITY;
                break;
            case R.id.breed_source_image_view:
                fragmentTag = TAG_FRAGMENT_BREED_SOURCE;
                break;
            case R.id.formula_image_view:
                fragmentTag = TAG_FRAGMENT_FORMULA_MAIN;
                break;
            case R.id.summary_image_view:
                fragmentTag = TAG_FRAGMENT_SUMMARY_PAGER;
                break;
            case R.id.report_image_view:
                fragmentTag = TAG_FRAGMENT_REPORT_PAGER;
                break;
        }
        intent.putExtra(KEY_FRAGMENT, fragmentTag);
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MenuHomeFragmentListener) {
            mListener = (MenuHomeFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MenuHomeFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface MenuHomeFragmentListener {

    }
}
