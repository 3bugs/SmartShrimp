package th.ac.dusit.dbizcom.smartshrimp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import th.ac.dusit.dbizcom.smartshrimp.App;
import th.ac.dusit.dbizcom.smartshrimp.R;
import th.ac.dusit.dbizcom.smartshrimp.etc.Utils;
import th.ac.dusit.dbizcom.smartshrimp.model.Pond;

public class PondInfoFragment extends Fragment {

    private static final String TITLE = "ข้อมูลบ่อเลี้ยง";

    private PondInfoFragmentListener mListener;

    private View mProgressView;
    private RecyclerView mPondRecyclerView;

    public PondInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pond_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressView = view.findViewById(R.id.progress_view);
        mPondRecyclerView = view.findViewById(R.id.pond_recycler_view);

        if (mListener != null) {
            mListener.setupRefreshButton(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doGetPond();
                }
            });
        }

        doGetPond();
    }

    private void doGetPond() {
        mProgressView.setVisibility(View.VISIBLE);

        if (getActivity() != null) {
            App app = (App) getActivity().getApplication();
            app.getPondList(new App.PondListListener() {
                @Override
                public void onPondListReady(List<Pond> pondList) {
                    mProgressView.setVisibility(View.GONE);
                    PondListAdapter adapter = new PondListAdapter(
                            getContext(),
                            pondList
                    );
                    mPondRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    mPondRecyclerView.setAdapter(adapter);
                }

                @Override
                public void onError(String errorMessage) {
                    mProgressView.setVisibility(View.GONE);
                    Utils.showOkDialog(getActivity(), "ผิดพลาด", errorMessage);
                }
            });
        }

        /*Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        Call<GetPondResponse> call = services.getPond();
        call.enqueue(new MyRetrofitCallback<>(
                getActivity(),
                null,
                mProgressView,
                new MyRetrofitCallback.MyRetrofitCallbackListener<GetPondResponse>() {
                    @Override
                    public void onSuccess(GetPondResponse responseBody) {
                        List<Pond> pondList = responseBody.pondList;
                        PondListAdapter adapter = new PondListAdapter(
                                getContext(),
                                pondList
                        );
                        mPondRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        mPondRecyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Utils.showOkDialog(getActivity(), "ผิดพลาด", errorMessage);
                    }
                }
        ));*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PondInfoFragmentListener) {
            mListener = (PondInfoFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PondInfoFragmentListener");
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

    public interface PondInfoFragmentListener {
        void setTitle(String title);

        void setupRefreshButton(View.OnClickListener listener);
    }

    private static class PondListAdapter extends RecyclerView.Adapter<PondListAdapter.PondViewHolder> {

        private final Context mContext;
        private final List<Pond> mPondList;

        PondListAdapter(Context context, List<Pond> pondList) {
            mContext = context;
            mPondList = pondList;
        }

        @NonNull
        @Override
        public PondViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_pond, parent, false
            );
            return new PondViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PondViewHolder holder, int position) {
            final Pond pond = mPondList.get(position);
            holder.mPondNumberTextView.setText(String.valueOf(pond.number));
            holder.mPondAreaTextView.setText(String.valueOf(pond.area));
        }

        @Override
        public int getItemCount() {
            return mPondList.size();
        }

        class PondViewHolder extends RecyclerView.ViewHolder {

            private final TextView mPondNumberTextView;
            private final TextView mPondAreaTextView;

            PondViewHolder(View itemView) {
                super(itemView);

                mPondNumberTextView = itemView.findViewById(R.id.pond_number_text_view);
                mPondAreaTextView = itemView.findViewById(R.id.pond_area_text_view);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String msg = String.format(
                                Locale.getDefault(),
                                "บ่อที่ %s, พื้นที่ %s ไร่",
                                mPondNumberTextView.getText().toString(),
                                mPondAreaTextView.getText().toString()
                        );
                        Utils.showOkDialog(mContext, "ข้อมูลบ่อเลี้ยง", msg);
                    }
                });
            }
        }
    }
}
