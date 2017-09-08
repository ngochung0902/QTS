package com.peppa.app.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.peppa.app.Fragment.PeopleFollowFragment;
import com.peppa.app.Fragment.PeopleFollowersFragment;
import com.peppa.app.Fragment.RestaurantsFragment;
import com.peppa.app.R;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;

import java.util.ArrayList;
import java.util.List;

public class FollowActivity extends AppCompatActivity {

    private Context thisContext;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EditText etSearch;
    private ViewPagerAdapter adapter;
    private FloatingActionButton fab;
    private ImageView ivSearch;

    //Follow Function
    private boolean IsFollow_User_Following=false;
    TextView toolbarTital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        thisContext = this;


        //Mapping - Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Mapping - Textview
        toolbarTital = (TextView) findViewById(R.id.toolbartitle);
        Generalfunction.SetToolbartitalstyle(thisContext,toolbarTital,false);

        //Mapping - Edittext
        etSearch     = (EditText) findViewById(R.id.searchFieldFollowEt);

        //Mapping - Viewpager
        viewPager    = (ViewPager) findViewById(R.id.viewpager);

        //Mapping - Tab Layout
        tabLayout    = (TabLayout) findViewById(R.id.tabs);

        //Mapping - Floating action button
        fab = (FloatingActionButton)findViewById(R.id.fab);

        //Mapping - Imageview
        ivSearch = (ImageView) findViewById(R.id.ivsearch);

        //Check - Previous Screen
        if (getIntent().getExtras() != null) {
            IsFollow_User_Following = getIntent().getExtras().getBoolean(Constant.IsFollow_User_Following);
        }

        //Set - Viewpager
        setupViewPager(viewPager);

        //Set - TabLayout
        tabLayout.setupWithViewPager(viewPager);

        //Keyboard - search action
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Generalfunction.hideKeyboard(FollowActivity.this);
                    DisplayUpdatedList();
                    return true;
                }
                return false;
            }
        });

        //Set - Viewpager page change listener event
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                //Set - Hint of edittext
                if(position==0){
                    etSearch.setHint("Search username or email");
                }
                else{
                    etSearch.setHint("Search restaurant");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.requestFocus();
                Generalfunction.ShowKeyboard(FollowActivity.this);
            }
        });

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Generalfunction.hideKeyboard(FollowActivity.this);
                ivSearch.requestFocus();
                DisplayUpdatedList();
            }
        });

    }


    //Set - ViewPager
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        GlobalVar.setMyBooleanPref(thisContext,Constant.IsFollow_User_Following,IsFollow_User_Following);

        if(IsFollow_User_Following){
            toolbarTital.setText(thisContext.getResources().getString(R.string.following));
            adapter.addFragment(new PeopleFollowFragment(), thisContext.getResources().getString(R.string.people));
            adapter.addFragment(new RestaurantsFragment(), thisContext.getResources().getString(R.string.restaurant));
        }
        else{

            String strName = getIntent().getStringExtra(Constant.Followers_Name);
            if(Generalfunction.isEmptyCheck(strName)){
                toolbarTital.setText(thisContext.getResources().getString(R.string.followers_tital));
            }
            else {
                toolbarTital.setText(thisContext.getResources().getString(R.string.following) + " " + strName + "");
            }

            adapter.addFragment(new PeopleFollowersFragment(), thisContext.getResources().getString(R.string.people));

            //Set - Toolbar tital style
            Generalfunction.SetToolbartitalstyle(thisContext,toolbarTital,false);
        }

        viewPager.setAdapter(adapter);
    }



    //Serch - Show List according to search values
    private void DisplayUpdatedList(){
        Fragment currentFragment=adapter.getItem(viewPager.getCurrentItem());

        //perform action according to current fragment
        if (currentFragment instanceof PeopleFollowFragment) {
            ((PeopleFollowFragment) currentFragment).SerachAndFilterPeoplelist(etSearch.getText().toString().toLowerCase().trim());
        }
        else if (currentFragment instanceof RestaurantsFragment) {
            ((RestaurantsFragment)currentFragment).SerachAndFilterRestaurant(etSearch.getText().toString().toLowerCase().trim());
        }
        else if (currentFragment instanceof PeopleFollowersFragment) {
            ((PeopleFollowersFragment) currentFragment).SerachAndFilterPeoplelist(etSearch.getText().toString().toLowerCase().trim());
        }
    }



    //Set - Viewpager Addapter
    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.d("position", position + "");
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            finish();
            Generalfunction.hideKeyboard(FollowActivity.this);

        }
        return super.onOptionsItemSelected(item);
    }


}

