package com.hengxuan.eht.massager2;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class MainActivity extends MyAciontBarActivity implements Tab1.OnFragmentInteractionListener, Tab2.OnFragmentInteractionListener,Tab3.OnFragmentInteractionListener, Tab4.OnFragmentInteractionListener{
    private Tab1 mTab1;
    private Tab2 mTab2;
    private Tab3 mTab3;
    private Tab4 mTab4;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTab();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, Tab3.newInstance("mode", "time"))
                    .commit();
            ((RadioButton)findViewById(R.id.tab3)).setChecked(true);
        }
        RadioGroup menu = (RadioGroup) findViewById(R.id.bottom_menu);
        menu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id){
                    case R.id.tab1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, mTab1).commit();
                        break;
                    case R.id.tab2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, mTab2).commit();
                        break;
                    case R.id.tab3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, mTab3).commit();
                        break;
                    case R.id.tab4:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, mTab4).commit();
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void initTab() {
        mTab1 = Tab1.newInstance(2,new String[]{"asx", "tool"}, new String[]{getString(R.string.asx),getString(R.string.music_massager)});
        mTab2 = Tab2.newInstance();
        mTab3 = Tab3.newInstance();
        mTab4 = Tab4.newInstance();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
