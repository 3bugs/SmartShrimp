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

public class FormulaFcrFragment extends Fragment {

    private static final String TITLE = "อัตราแลกเนื้อ (FCR)";

    private FormulaFcrFragmentListener mListener;

    private EditText mFeedEditText, mWeightEditText;
    private TextView mResultTextView;

    public FormulaFcrFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_formula_fcr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mListener != null) {
            mListener.setupRefreshButton(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFeedEditText.setText("");
                    mWeightEditText.setText("");
                    mResultTextView.setText("");
                    mFeedEditText.requestFocus();
                }
            });
        }

        mFeedEditText = view.findViewById(R.id.feed_edit_text);
        mWeightEditText = view.findViewById(R.id.weight_edit_text);
        mResultTextView = view.findViewById(R.id.result_text_view);

        view.findViewById(R.id.calculate_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValid()) {
                    double feed = Double.parseDouble(mFeedEditText.getText().toString().trim());
                    double weight = Double.parseDouble(mWeightEditText.getText().toString().trim());
                    double result = feed / weight;
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

        String weight = mWeightEditText.getText().toString().trim();
        if (weight.isEmpty()) {
            mWeightEditText.setError("กรอกน้ำหนักกุ้งที่ได้");
            valid = false;
        }
        String feed = mFeedEditText.getText().toString().trim();
        if (feed.isEmpty()) {
            mFeedEditText.setError("กรอกจำนวนอาหารที่ใช้");
            valid = false;
        }
        return valid;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FormulaFcrFragmentListener) {
            mListener = (FormulaFcrFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FormulaFcrFragmentListener");
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

    public interface FormulaFcrFragmentListener {
        void setTitle(String title);

        void setupRefreshButton(View.OnClickListener listener);

        void onClickBackButton();
    }
}
