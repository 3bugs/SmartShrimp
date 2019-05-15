package th.ac.dusit.dbizcom.smartshrimp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import th.ac.dusit.dbizcom.smartshrimp.R;

public class FormulaMainFragment extends Fragment implements View.OnClickListener {

    private static final String TITLE = "สูตรคำนวณ";

    private FormulaMainFragmentListener mListener;

    public FormulaMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_formula_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mListener != null) {
            mListener.setupRefreshButton(null);
        }

        view.findViewById(R.id.fcr_button).setOnClickListener(this);
        view.findViewById(R.id.size_button).setOnClickListener(this);
        view.findViewById(R.id.adg_button).setOnClickListener(this);
        view.findViewById(R.id.survival_rate_button).setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FormulaMainFragmentListener) {
            mListener = (FormulaMainFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FormulaMainFragmentListener");
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

    @Override
    public void onClick(View view) {
        int which = 0;
        switch (view.getId()) {
            case R.id.fcr_button:
                which = 0;
                break;
            case R.id.size_button:
                which = 1;
                break;
            case R.id.adg_button:
                which = 2;
                break;
            case R.id.survival_rate_button:
                which = 3;
                break;
        }
        if (mListener != null) {
            mListener.onClickFormulaButton(which);
        }
    }

    public interface FormulaMainFragmentListener {
        void setTitle(String title);

        void setupRefreshButton(View.OnClickListener listener);

        void onClickFormulaButton(int which);
    }
}
