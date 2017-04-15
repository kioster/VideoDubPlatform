package com.kioster.videodubplatform;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.view.animation.AlphaAnimation;
import android.widget.*;

import com.daimajia.slider.library.*;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.OnClick;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener,
        BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener {

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private Toolbar toolbar;

    private SliderLayout mSliderLayout;

    private View mainPage;

    private View videoPage;

    private View listPage;

    private LinearLayout rateSubmitBar;

    private ImageButton uploadButton;

    MaterialRatingBar myRate;

    HashMap<String,Integer> recomendation_maps;

    HashMap<String, Integer> latest_map;

    private float rateValue;

    private UserData user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

        initToolbar();
        initMainViewPage();

        videoPage = null;
        listPage = null;
    }

    private void initData() {
        recomendation_maps = new HashMap<>();
        recomendation_maps.put("Hannibal", R.drawable.hannibal);
        recomendation_maps.put("Big Bang Theory", R.drawable.bigbang);
        recomendation_maps.put("House of Cards", R.drawable.house);
        recomendation_maps.put("Game of Thrones", R.drawable.game_of_thrones);


        latest_map = new HashMap<>();
        latest_map.put("love in tokyo", R.drawable.love_in_tokyo);
        latest_map.put("gurizaia no kajitu ", R.drawable.gurizaia_no_kajitu);
        latest_map.put("hanzawa naoki1", R.drawable.hanzawa_naoki);
        latest_map.put("hanzawa naoki2", R.drawable.hanzawa_naoki);
        latest_map.put("hanzawa naoki3", R.drawable.hanzawa_naoki);
        latest_map.put("hanzawa naoki4", R.drawable.hanzawa_naoki);

        rateValue = 4;

        user = new UserData();

        user.rate = rateValue;
    }

    private void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Set an OnMenuItemClickListener to handle menu item clicks
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the menu item
                return true;
            }
        });

        setSupportActionBar(toolbar);

        toolbar.setTitle(R.string.app_name);
        toolbar.setNavigationIcon(R.drawable.no_user);

        ActionBar bar = getSupportActionBar();

        if (bar == null) {
            return;
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    private void initRateFunction() {
        myRate = (MaterialRatingBar) videoPage.findViewById(R.id.my_rate);

        if (myRate == null) {
            return;
        }

        rateSubmitBar = (LinearLayout) videoPage.findViewById(R.id.rate_submit_bar);

        myRate.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                user.new_rate = rating;
            }
        });

        myRate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                rateSubmitBar.setVisibility(View.VISIBLE);
                return false;
            }
        });

        Button cancelRate = (Button) rateSubmitBar.getChildAt(0);
        Button submitRate = (Button) rateSubmitBar.getChildAt(1);

        cancelRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRate.setRating(user.rate);
                rateSubmitBar.setVisibility(View.INVISIBLE);
            }
        });

        submitRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRate.setRating(user.new_rate);
                rateSubmitBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initListViewPage() {
        LinearLayout container = (LinearLayout) findViewById(R.id.main_content);

        listPage = getLayoutInflater().inflate(R.layout.list_page_item, container, false);

        Button comeback_button = (Button) listPage.findViewById(R.id.comeback_button);

        if (comeback_button != null) {
            comeback_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.startAnimation(new AlphaAnimation(0.5f, 1f));
                    changeToMainPage();
                }
            });
        }

        ListView uploadList = (ListView) listPage.findViewById(R.id.item_list);

        if (uploadList == null) {
            return;
        }

        uploadList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initVideoViewPage() {
        LinearLayout container = (LinearLayout) findViewById(R.id.main_content);
        videoPage = getLayoutInflater().inflate(R.layout.video_page_item, container, false);

        Button comeback_button = (Button) videoPage.findViewById(R.id.comeback_button);

        if (comeback_button != null) {
            comeback_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.startAnimation(new AlphaAnimation(0.5f, 1f));
                    clearVideo();
                    changeToMainPage();
                }
            });
        }

        Vitamio.isInitialized(getApplicationContext());

        initRateFunction();
    }

    private void initMainViewPage(){
        LinearLayout container = (LinearLayout)findViewById(R.id.main_content);
        mainPage = getLayoutInflater().inflate(R.layout.main_page_item, container, false);

        if (mainPage == null) {
            return;
        }

        uploadButton = (ImageButton) mainPage.findViewById(R.id.upload_button);

        if (uploadButton == null) {
            return;
        }

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //upload function
            }
        });

        container.addView(mainPage);

        initSlider();
        initVideoList();
    }

    private String getRateStar(int rate) {
        rate = rate > 5 ? 5 : rate;
        rate = rate < 0 ? 0 :rate;

        StringBuilder starBuilder = new StringBuilder(5);
        starBuilder.replace(0, 5, getString(R.string.no_star));
        starBuilder.replace(0, rate, getString(R.string.have_star));

        return starBuilder.toString();
    }

    private void initSlider() {
        mSliderLayout = (SliderLayout)findViewById(R.id.slider);


        for (String image : recomendation_maps.keySet()) {
            TextSliderView view = new TextSliderView(this);

            view
                    .description(image)
                    .image(recomendation_maps.get(image))
                    .setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
                    .setOnSliderClickListener(this);
            view.bundle(new Bundle())
                    .getBundle().putString("extra", image);

            mSliderLayout.addSlider(view);
        }

        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSliderLayout.setCustomAnimation(new DescriptionAnimation());
        mSliderLayout.setDuration(5000);
        mSliderLayout.addOnPageChangeListener(this);
    }

    private void initUploadList() {
        ListView uploadList = (ListView) listPage.findViewById(R.id.item_list);

        if (uploadList == null) {
            return;
        }

        uploadList.setAdapter(new VideoListAdapter(this, "upload"));
    }

    private void initDownloadList() {
        ListView uploadList = (ListView) listPage.findViewById(R.id.item_list);

        if (uploadList == null) {
            return;
        }

        uploadList.setAdapter(new VideoListAdapter(this, "download"));
    }

    private void initRateList() {
        ListView uploadList = (ListView) listPage.findViewById(R.id.item_list);

        if (uploadList == null) {
            return;
        }

        uploadList.setAdapter(new VideoListAdapter(this, "rates"));
    }

    private void initVideoList() {
        ListView videoList = (ListView) mainPage.findViewById(R.id.video_list);

        if (videoList == null) {
            return;
        }

        videoList.setAdapter(new VideoListAdapter(this));

        videoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        videoList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int event = motionEvent.getAction();

                if (event == MotionEvent.ACTION_DOWN || event == MotionEvent.ACTION_MOVE) {
                    uploadButton.setAlpha(0.25f);
                } else {
                    uploadButton.setAlpha(1.0f);
                }

                return false;
            }
        });
    }

    private void initVideo() {
        String path = "/storage/sdcard0/DCIM/video/1456503758026.mp4";

        TextView title = (TextView) videoPage.findViewById(R.id.video_title);
        String tempTitle = getString(R.string.hello_world);
        title.setText(tempTitle);

        VideoView video = (VideoView) videoPage.findViewById(R.id.video_surface);
        ViewGroup videoContainer = (ViewGroup) videoPage.findViewById(R.id.video_container);


        if (!path.equals("")) {
            MediaController controller = new MediaController(this, videoContainer);
            controller.setAnchorView(videoContainer);
            controller.setFileName("");

            video.setMediaController(controller);

            video.requestFocus();

            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer player) {
                    player.setPlaybackSpeed(1.0f);
                }
            });

            video.setVideoPath(path);
            controller.setVisibility(View.GONE);
        }

        myRate.setRating(user.rate);
    }

    private void clearVideo() {
        VideoView video = (VideoView) videoPage.findViewById(R.id.video_surface);
        video.suspend();
    }

    private void changeToMainPage() {
        LinearLayout container = (LinearLayout) findViewById(R.id.main_content);

        if (container == null || mainPage == null) {
            return;
        }

        container.removeAllViews();
        container.addView(mainPage);
    }

    private void changeToPlaying(Intent intent) {
        LinearLayout container = (LinearLayout) findViewById(R.id.main_content);

        if (videoPage == null) {
            initVideoViewPage();
        }

        if (container == null || videoPage == null) {
            return;
        }

        container.removeAllViews();
        container.addView(videoPage);

        initVideo();
    }

    private void changeToDownload() {
        LinearLayout container = (LinearLayout) findViewById(R.id.main_content);

        if (container == null || listPage == null) {
            return;
        }

        initDownloadList();

        container.removeAllViews();
        container.addView(listPage);
    }

    private void changeToRate() {
        LinearLayout container = (LinearLayout) findViewById(R.id.main_content);

        if (container == null || listPage == null) {
            return;
        }

        initRateList();

        container.removeAllViews();
        container.addView(listPage);
    }

    private void changeToUpload() {
        LinearLayout container = (LinearLayout) findViewById(R.id.main_content);

        if (container == null || listPage == null) {
            return;
        }

        initUploadList();

        container.removeAllViews();
        container.addView(listPage);
    }

    @Override
    public void onStop() {
        super.onStop();
        mSliderLayout.stopAutoCycle();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        changeToPlaying(new Intent());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class UserData {
        float rate;

        float new_rate;
    }

    private class VideoListAdapter extends BaseAdapter {
        class ItemData {
            String title;
            String time;
            //TODO using local now, so using int
            int imageId;
            float rate;
        }

        private Context mContext;

        private String[] titles;

        private ArrayList<LinearLayout> items;

        private ArrayList<ItemData> itemsInf;

        private String time;

        VideoListAdapter(Context context, String type) {
            getItemsData(type);

            mContext = context;

            initItems();
        }

        VideoListAdapter(Context context) {
            this(context, "latest");
        }

        private void getItemsData(String type) {
            //TODO connect server side
            itemsInf = new ArrayList<>();


            time = "2016-04-09";
            titles = new String[latest_map.size()];
            latest_map.keySet().toArray(titles);

            for (String title : titles) {
                ItemData item = new ItemData();

                item.title = title;
                item.imageId = latest_map.get(title);
                item.time = time;
                item.rate = user.rate;

                itemsInf.add(item);
            }
        }

        private void initItems() {
            items = new ArrayList<>();

            for (ItemData data : itemsInf) {
                LinearLayout item = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.video_item, null);

                ImageView image = (ImageView) item.findViewById(R.id.snapshot);
                image.setImageResource(data.imageId);

//            image.setClipBounds(new Rect());

                TextView titleText = (TextView) item.findViewById(R.id.title);
                titleText.setText(data.title);

                TextView timeText = (TextView) item.findViewById(R.id.time);
                timeText.setText(data.time);

                TextView rateText = (TextView) item.findViewById(R.id.rate);
                rateText.setText(String.valueOf(data.rate));

                items.add(item);
            }
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Object getItem(int position) {

            return titles[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return items.get(position);
        }

    }

    private void setMenuIconEnable(Menu menu) {
        try {
            Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
            method.setAccessible(true);

            method.invoke(menu, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setMenuIconEnable(menu);

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_main, menu);


        menu.findItem(R.id.my_search).setIcon(R.drawable.searcher);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searcher = (SearchView) menu.findItem(R.id.my_search).getActionView();
        searcher.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                // About option clicked.
                return true;
            case R.id.action_exit:
                // Exit option clicked.
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);

        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (listPage == null) {
            initListViewPage();
        }

        Integer id = item.getItemId();

        if (id == R.id.my_upload) {
            changeToUpload();
        } else if (id == R.id.my_rate) {
            changeToRate();
        } else if (id == R.id.my_download) {
            changeToDownload();
        }

        return true;
    }
}
