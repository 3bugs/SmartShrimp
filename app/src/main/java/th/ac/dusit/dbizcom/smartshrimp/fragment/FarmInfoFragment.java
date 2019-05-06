package th.ac.dusit.dbizcom.smartshrimp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import th.ac.dusit.dbizcom.smartshrimp.R;

public class FarmInfoFragment extends Fragment {

    private FarmInfoFragmentListener mListener;

    public FarmInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_farm_info, container, false);
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

    public interface FarmInfoFragmentListener {
    }
}
