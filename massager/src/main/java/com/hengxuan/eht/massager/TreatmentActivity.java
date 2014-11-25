package com.hengxuan.eht.massager;

import java.util.ArrayList;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hengxuan.eht.logger.Log;


public class TreatmentActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    ArrayList<RadioButton> menus = new ArrayList<RadioButton>();
    int pageIndex = 0;
    public static final String DEFAULT_PAGE_INDEX = "index";

    public static final String DISEASE_ID = "disease_id";
    //不同的疾病id对应不同的内容
    private int diseaseId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment);
        pageIndex = getIntent().getIntExtra(DEFAULT_PAGE_INDEX, 0);
        diseaseId = getIntent().getIntExtra(DISEASE_ID, 0);

        RadioButton menu1 = (RadioButton)findViewById(R.id.menu1);
        menus.add(menu1);
        RadioButton menu2 = (RadioButton)findViewById(R.id.menu2);
        menus.add(menu2);
        RadioButton menu3 = (RadioButton)findViewById(R.id.menu3);
        menus.add(menu3);
        RadioButton menu4 = (RadioButton)findViewById(R.id.menu4);
        menus.add(menu4);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                pageIndex = i;
                menus.get(i).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mViewPager.setCurrentItem(pageIndex);



        menus.get(pageIndex).setChecked(true);
        ((RadioGroup)menus.get(pageIndex).getParent()).setOnCheckedChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.treatment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        switch (id){
            case R.id.menu1:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.menu2:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.menu3:
                //弹出alertDialogs
//                VerifySerialNumber();
                mViewPager.setCurrentItem(2);
                break;
            case R.id.menu4:
                mViewPager.setCurrentItem(3);
                break;
            default:
                break;
        }

    }

    private void VerifySerialNumber() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View v = layoutInflater.inflate(R.layout.edit_text, null);
        new AlertDialog.Builder(this).setTitle(R.string.input_product_number).setView(v).setNeutralButton(R.string.verify, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create();
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new TreatmentToolsFragment();
                    break;
                case 1:
                    fragment = new PointLocationFragment();
                    Bundle args = new Bundle();
                    args.putInt(TreatmentActivity.DISEASE_ID, diseaseId);
                    fragment.setArguments(args);
                    break;
                case 2:
                    fragment = new TreatmentFragment();
                    break;
                case 3:
                    fragment = new KnowledgeFragment();
                    Bundle args2 = new Bundle();
                    args2.putInt(TreatmentActivity.DISEASE_ID, diseaseId);
                    fragment.setArguments(args2);
                    break;
                default:
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_treatment, container, false);
            return rootView;
        }
    }


}
