package th.ac.dusit.dbizcom.smartshrimp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import th.ac.dusit.dbizcom.smartshrimp.etc.Utils;
import th.ac.dusit.dbizcom.smartshrimp.fragment.AddFeedingRecordFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.FarmInfoFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.FeedingRecordFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.FormulaFcrFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.FormulaMainFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.FormulaSizeFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.PondInfoFragment;
import th.ac.dusit.dbizcom.smartshrimp.model.Feeding;

public class MainActivity extends AppCompatActivity implements
        FarmInfoFragment.FarmInfoFragmentListener,
        PondInfoFragment.PondInfoFragmentListener,
        FeedingRecordFragment.FeedingRecordFragmentListener,
        AddFeedingRecordFragment.AddFeedingRecordFragmentListener,
        FormulaMainFragment.FormulaMainFragmentListener,
        FormulaFcrFragment.FormulaFcrFragmentListener,
        FormulaSizeFragment.FormulaSizeFragmentListener {

    static final String KEY_FRAGMENT = "fragment";
    static final String TAG_FRAGMENT_FARM_INFO = "farm_info_fragment";
    private static final String TAG_FRAGMENT_POND_INFO = "pond_info_fragment";
    static final String TAG_FRAGMENT_FEEDING_RECORD = "feeding_record_fragment";
    private static final String TAG_FRAGMENT_ADD_FEEDING_RECORD = "add_feeding_record_fragment";
    static final String TAG_FRAGMENT_WATER_QUALITY = "water_quality_fragment";
    static final String TAG_FRAGMENT_BREED_SOURCE = "breed_source_fragment";
    static final String TAG_FRAGMENT_FORMULA_MAIN = "formula_main_fragment";
    static final String TAG_FRAGMENT_FORMULA_FCR = "formula_fcr_fragment";
    static final String TAG_FRAGMENT_FORMULA_SIZE = "formula_size_fragment";
    static final String TAG_FRAGMENT_FORMULA_ADG = "formula_adg_fragment";
    static final String TAG_FRAGMENT_FORMULA_SURVIVAL_RATE = "formula_survival_rate_fragment";
    static final String TAG_FRAGMENT_SUMMARY = "summary_fragment";
    static final String TAG_FRAGMENT_REPORT = "report_fragment";

    protected enum FragmentTransitionType {
        NONE,
        SLIDE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.home_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView titleTextView = findViewById(R.id.title_text_view);

        String fragmentTag = getIntent().getStringExtra(KEY_FRAGMENT);
        Fragment fragment = null;
        switch (fragmentTag) {
            case TAG_FRAGMENT_FARM_INFO:
                fragment = new FarmInfoFragment();
                break;
            case TAG_FRAGMENT_FEEDING_RECORD:
                fragment = new FeedingRecordFragment();
                break;
            case TAG_FRAGMENT_WATER_QUALITY:
                //fragment = new WaterQualityFragment();
                //titleTextView.setText("คุณภาพน้ำในบ่อเลี้ยง");
                break;
            case TAG_FRAGMENT_BREED_SOURCE:
                //fragment = new BreedSourceFragment();
                //titleTextView.setText("แหล่งพันธุ์ลูกกุ้ง");
                break;
            case TAG_FRAGMENT_FORMULA_MAIN:
                fragment = new FormulaMainFragment();
                break;
            case TAG_FRAGMENT_SUMMARY:
                //fragment = new SummaryFragment();
                //titleTextView.setText("สรุปผลการเลี้ยง");
                break;
            case TAG_FRAGMENT_REPORT:
                //fragment = new ReportFragment();
                //titleTextView.setText("รายงานข้อมูล");
                break;
        }

        if (fragment != null) {
            /*getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();*/
            loadFragment(fragment, fragmentTag, false, FragmentTransitionType.NONE);
        }
    }

    protected void loadFragment(Fragment fragment, String tag, boolean addToBackStack,
                                FragmentTransitionType transitionType) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (transitionType == FragmentTransitionType.SLIDE) {
            transaction.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
            );
        }
        transaction.replace(
                R.id.fragment_container,
                fragment,
                tag
        );
        if (addToBackStack) {
            transaction.addToBackStack(null).commit();
        } else {
            transaction.commit();
        }
    }

    protected void popAllBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    protected void popBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }

    @Override
    public void setTitle(String title) {
        TextView titleTextView = findViewById(R.id.title_text_view);
        titleTextView.setText(title);
    }

    @Override
    public void setupRefreshButton(boolean visible, View.OnClickListener listener) {
        ImageView refreshImageView = findViewById(R.id.refresh_image_view);
        refreshImageView.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (visible) {
            refreshImageView.setOnClickListener(listener);
        }
    }

    @Override
    public void onSaveFeedingRecordSuccess() {
        Utils.hideKeyboard(this);
        popBackStack();
        FeedingRecordFragment fragment = (FeedingRecordFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_FEEDING_RECORD);
        if (fragment != null) {
            fragment.doGetFeeding();
        }
    }

    @Override
    public void onClickPondInfoButton() {
        loadFragment(
                new PondInfoFragment(),
                TAG_FRAGMENT_POND_INFO,
                true,
                FragmentTransitionType.SLIDE
        );
    }

    @Override
    public void onClickAddFeedingButton(int pondId) {
        loadFragment(
                AddFeedingRecordFragment.newInstance(pondId, null),
                TAG_FRAGMENT_ADD_FEEDING_RECORD,
                true,
                FragmentTransitionType.SLIDE
        );
    }

    @Override
    public void onEditFeeding(Feeding feeding) {
        loadFragment(
                AddFeedingRecordFragment.newInstance(-1, feeding),
                TAG_FRAGMENT_ADD_FEEDING_RECORD,
                true,
                FragmentTransitionType.SLIDE
        );
    }

    @Override
    public void onClickFormulaButton(int which) {
        Fragment fragment = null;
        switch (which) {
            case 0:
                fragment = new FormulaFcrFragment();
                break;
            case 1:
                fragment = new FormulaSizeFragment();
                break;
            case 2:
                break;
            case 3:
                break;
        }
        loadFragment(
                fragment,
                TAG_FRAGMENT_FORMULA_FCR,
                true,
                FragmentTransitionType.SLIDE
        );
    }

    @Override
    public void onClickBackButton() {
        popBackStack();
    }
}
