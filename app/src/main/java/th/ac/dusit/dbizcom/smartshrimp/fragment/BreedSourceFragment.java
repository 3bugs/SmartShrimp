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

public class BreedSourceFragment extends Fragment {

    private static final String TITLE = "แหล่งพันธุ์ลูกกุ้ง";

    private BreedSourceFragmentListener mListener;

    private View mProgressView;

    public BreedSourceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_breed_source, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressView = view.findViewById(R.id.progress_view);

        if (mListener != null) {
            mListener.setupRefreshButton(null);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BreedSourceFragmentListener) {
            mListener = (BreedSourceFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BreedSourceFragmentListener");
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

    public interface BreedSourceFragmentListener {
        void setTitle(String title);

        void setupRefreshButton(View.OnClickListener listener);
    }
}
