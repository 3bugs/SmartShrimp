package th.ac.dusit.dbizcom.smartshrimp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import th.ac.dusit.dbizcom.smartshrimp.R;

public class MenuDevelopersFragment extends Fragment {

    private MenuDevelopersFragmentListener mListener;

    public MenuDevelopersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_developers, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MenuDevelopersFragmentListener) {
            mListener = (MenuDevelopersFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MenuDevelopersFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface MenuDevelopersFragmentListener {
    }
}
