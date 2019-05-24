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
import th.ac.dusit.dbizcom.smartshrimp.fragment.FeedingRecordPagerFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.FormulaAdgFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.FormulaFcrFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.FormulaMainFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.FormulaSizeFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.FormulaSurvivalRateFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.HatcheryFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.HatcheryPagerFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.PondInfoFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.ReportFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.ReportPagerFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.SummaryFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.SummaryPagerFragment;
import th.ac.dusit.dbizcom.smartshrimp.fragment.WaterQualityFragment;
import th.ac.dusit.dbizcom.smartshrimp.model.Feeding;
import th.ac.dusit.dbizcom.smartshrimp.model.Pond;

public class MainActivity extends AppCompatActivity implements
        FarmInfoFragment.FarmInfoFragmentListener,
        PondInfoFragment.PondInfoFragmentListener,
        FeedingRecordFragment.FeedingRecordFragmentListener,
        FeedingRecordPagerFragment.FeedingRecordPagerFragmentListener,
        AddFeedingRecordFragment.AddFeedingRecordFragmentListener,
        FormulaMainFragment.FormulaMainFragmentListener,
        FormulaFcrFragment.FormulaFcrFragmentListener,
        FormulaSizeFragment.FormulaSizeFragmentListener,
        FormulaAdgFragment.FormulaAdgFragmentListener,
        FormulaSurvivalRateFragment.FormulaSurvivalRateFragmentListener,
        SummaryFragment.SummaryFragmentListener,
        SummaryPagerFragment.SummaryPagerFragmentListener,
        ReportFragment.ReportFragmentListener,
        ReportPagerFragment.ReportPagerFragmentListener,
        WaterQualityFragment.WaterQualityFragmentListener,
        HatcheryFragment.HatcheryFragmentListener,
        HatcheryPagerFragment.HatcheryPagerFragmentListener {

    public static final String KEY_FRAGMENT = "fragment";
    public static final String TAG_FRAGMENT_FARM_INFO = "farm_info_fragment";
    private static final String TAG_FRAGMENT_POND_INFO = "pond_info_fragment";
    public static final String TAG_FRAGMENT_FEEDING_RECORD = "feeding_record_fragment";
    public static final String TAG_FRAGMENT_FEEDING_RECORD_PAGER = "feeding_record_pager_fragment";
    private static final String TAG_FRAGMENT_ADD_FEEDING_RECORD = "add_feeding_record_fragment";
    public static final String TAG_FRAGMENT_WATER_QUALITY = "water_quality_fragment";
    public static final String TAG_FRAGMENT_HATCHERY = "hathery_fragment";
    public static final String TAG_FRAGMENT_HATCHERY_PAGER = "hathery_pager_fragment";
    public static final String TAG_FRAGMENT_FORMULA_MAIN = "formula_main_fragment";
    public static final String TAG_FRAGMENT_FORMULA_FCR = "formula_fcr_fragment";
    public static final String TAG_FRAGMENT_FORMULA_SIZE = "formula_size_fragment";
    public static final String TAG_FRAGMENT_FORMULA_ADG = "formula_adg_fragment";
    public static final String TAG_FRAGMENT_FORMULA_SURVIVAL_RATE = "formula_survival_rate_fragment";
    public static final String TAG_FRAGMENT_SUMMARY = "summary_fragment";
    public static final String TAG_FRAGMENT_SUMMARY_PAGER = "summary_pager_fragment";
    public static final String TAG_FRAGMENT_REPORT = "report_fragment";
    public static final String TAG_FRAGMENT_REPORT_PAGER = "report_pager_fragment";

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
            case TAG_FRAGMENT_FEEDING_RECORD_PAGER:
                fragment = new FeedingRecordPagerFragment();
                break;
            case TAG_FRAGMENT_WATER_QUALITY:
                fragment = new WaterQualityFragment();
                break;
            case TAG_FRAGMENT_HATCHERY_PAGER:
                fragment = new HatcheryPagerFragment();
                break;
            case TAG_FRAGMENT_FORMULA_MAIN:
                fragment = new FormulaMainFragment();
                break;
            case TAG_FRAGMENT_SUMMARY_PAGER:
                fragment = new SummaryPagerFragment();
                break;
            case TAG_FRAGMENT_REPORT_PAGER:
                fragment = new ReportPagerFragment();
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
    public void setupRefreshButton(View.OnClickListener listener) {
        ImageView refreshImageView = findViewById(R.id.refresh_image_view);
        refreshImageView.setVisibility(listener != null ? View.VISIBLE : View.GONE);
        refreshImageView.setOnClickListener(listener);
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
    public void onClickAddFeedingButton(Pond pond) {
        loadFragment(
                AddFeedingRecordFragment.newInstance(pond, null),
                TAG_FRAGMENT_ADD_FEEDING_RECORD,
                true,
                FragmentTransitionType.SLIDE
        );
    }

    @Override
    public void onEditFeeding(Pond pond, Feeding feeding) {
        loadFragment(
                AddFeedingRecordFragment.newInstance(pond, feeding),
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
                fragment = new FormulaAdgFragment();
                break;
            case 3:
                fragment = new FormulaSurvivalRateFragment();
                break;
        }

        if (fragment != null) {
            loadFragment(
                    fragment,
                    TAG_FRAGMENT_FORMULA_FCR,
                    true,
                    FragmentTransitionType.SLIDE
            );
        }
    }

    @Override
    public void onClickBackButton() {
        popBackStack();
    }
}
