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
import android.widget.TextView;

import java.util.Locale;

import th.ac.dusit.dbizcom.smartshrimp.R;

public class FormulaAdgFragment extends Fragment {

    private static final String TITLE = "อัตราการเจริญเติบโต (ADG)";

    private FormulaAdgFragmentListener mListener;

    private EditText mInitialWeightEditText, mFinalWeightEditText, mPeriodEditText;
    private TextView mResultTextView;

    public FormulaAdgFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_formula_adg, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mListener != null) {
            mListener.setupRefreshButton(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mInitialWeightEditText.setText("");
                    mFinalWeightEditText.setText("");
                    mPeriodEditText.setText("");
                    mResultTextView.setText("");
                    mFinalWeightEditText.requestFocus();
                }
            });
        }

        mInitialWeightEditText = view.findViewById(R.id.initial_weight_edit_text);
        mFinalWeightEditText = view.findViewById(R.id.final_weight_edit_text);
        mPeriodEditText = view.findViewById(R.id.period_edit_text);
        mResultTextView = view.findViewById(R.id.result_text_view);

        view.findViewById(R.id.calculate_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValid()) {
                    double initialWeight = Double.parseDouble(mInitialWeightEditText.getText().toString().trim());
                    double finalWeight = Double.parseDouble(mFinalWeightEditText.getText().toString().trim());
                    double period = Double.parseDouble(mPeriodEditText.getText().toString().trim());
                    double result = (finalWeight - initialWeight) / period;
                    mResultTextView.setText(String.format(Locale.getDefault(), "%.2f", result));
                }
            }
        });

        view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onClickBackButton();
                }
            }
        });
    }

    private boolean isFormValid() {
        boolean valid = true;

        String period = mPeriodEditText.getText().toString().trim();
        if (period.isEmpty()) {
            mPeriodEditText.setError("กรอกระยะเวลาเลี้ยงกุ้ง");
            valid = false;
        }
        String initialWeight = mInitialWeightEditText.getText().toString().trim();
        if (initialWeight.isEmpty()) {
            mInitialWeightEditText.setError("กรอกน้ำหนักเริ่มต้น");
            valid = false;
        }
        String finalWeight = mFinalWeightEditText.getText().toString().trim();
        if (finalWeight.isEmpty()) {
            mFinalWeightEditText.setError("กรอกน้ำหนักกุ้งสุดท้าย");
            valid = false;
        }
        return valid;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FormulaAdgFragmentListener) {
            mListener = (FormulaAdgFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FormulaAdgFragmentListener");
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

    public interface FormulaAdgFragmentListener {
        void setTitle(String title);

        void setupRefreshButton(View.OnClickListener listener);

        void onClickBackButton();
    }
}
