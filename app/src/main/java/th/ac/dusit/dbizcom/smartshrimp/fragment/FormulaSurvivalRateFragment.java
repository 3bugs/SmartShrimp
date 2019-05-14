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

public class FormulaSurvivalRateFragment extends Fragment {

    private static final String TITLE = "อัตรารอด (SURVIVAL RATE)";

    private FormulaSurvivalRateFragmentListener mListener;

    private EditText mWeightEditText, mSizeEditText, mCountEditText;
    private TextView mResultTextView;

    public FormulaSurvivalRateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_formula_survival_rate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mListener != null) {
            mListener.setupRefreshButton(false, null);
        }

        mWeightEditText = view.findViewById(R.id.weight_edit_text);
        mSizeEditText = view.findViewById(R.id.size_edit_text);
        mCountEditText = view.findViewById(R.id.count_edit_text);
        mResultTextView = view.findViewById(R.id.result_text_view);

        view.findViewById(R.id.calculate_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValid()) {
                    double weight = Double.parseDouble(mWeightEditText.getText().toString().trim());
                    double size = Double.parseDouble(mSizeEditText.getText().toString().trim());
                    double count = Double.parseDouble(mCountEditText.getText().toString().trim());
                    double result = (weight * size * 100) / count;
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

        String count = mCountEditText.getText().toString().trim();
        if (count.isEmpty()) {
            mCountEditText.setError("กรอกจำนวนกุ้งที่ปล่อย");
            valid = false;
        }
        String size = mSizeEditText.getText().toString().trim();
        if (size.isEmpty()) {
            mSizeEditText.setError("กรอกขนาดกุ้ง");
            valid = false;
        }
        String weight = mWeightEditText.getText().toString().trim();
        if (weight.isEmpty()) {
            mWeightEditText.setError("กรอกน้ำหนักกุ้งที่ขายได้");
            valid = false;
        }
        return valid;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FormulaSurvivalRateFragmentListener) {
            mListener = (FormulaSurvivalRateFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FormulaSurvivalRateFragmentListener");
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

    public interface FormulaSurvivalRateFragmentListener {
        void setTitle(String title);

        void setupRefreshButton(boolean visible, View.OnClickListener listener);

        void onClickBackButton();
    }
}
