package com.github.shrimadbhagwatam;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.folioreader.Config;
import com.folioreader.FolioReader;
import com.folioreader.model.HighLight;
import com.folioreader.model.locators.ReadLocator;
import com.folioreader.ui.base.OnSaveHighlight;
import com.folioreader.util.AppUtil;
import com.folioreader.util.OnHighlightListener;
import com.folioreader.util.ReadLocatorListener;
import com.github.shrimadbhagwatam.menu.DrawerAdapter;
import com.github.shrimadbhagwatam.menu.DrawerItem;
import com.github.shrimadbhagwatam.menu.SimpleItem;
import com.github.shrimadbhagwatam.menu.SpaceItem;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener,View.OnClickListener, OnHighlightListener, ReadLocatorListener, FolioReader.OnClosedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private FolioReader folioReader;
    private static final int POS_DASHBOARD = 0;
    private static final int POS_ACCOUNT = 1;
    private static final int POS_MESSAGES = 2;
    private static final int POS_CART = 3;
    private static final int POS_LOGOUT = 5;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        folioReader = FolioReader.get()
                .setOnHighlightListener(this)
                .setReadLocatorListener(this)
                .setOnClosedListener(this);

        getHighlightsAndSave();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_DASHBOARD).setChecked(true),
                createItemFor(POS_ACCOUNT),
                createItemFor(POS_MESSAGES),
                createItemFor(POS_CART),
                new SpaceItem(48),
                createItemFor(POS_LOGOUT)));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_DASHBOARD);

        findViewById(R.id.one).setOnClickListener(this);
        findViewById(R.id.two).setOnClickListener(this);
        findViewById(R.id.three).setOnClickListener(this);
        findViewById(R.id.four).setOnClickListener(this);
        findViewById(R.id.five).setOnClickListener(this);
        findViewById(R.id.six).setOnClickListener(this);
        findViewById(R.id.seven).setOnClickListener(this);
        findViewById(R.id.eight).setOnClickListener(this);
        findViewById(R.id.nine).setOnClickListener(this);
        findViewById(R.id.tena).setOnClickListener(this);
        findViewById(R.id.tenb).setOnClickListener(this);
        findViewById(R.id.eleventh).setOnClickListener(this);
        findViewById(R.id.twelfth).setOnClickListener(this);

    }

    @Override
    public void onItemSelected(int position) {
        if (position == POS_LOGOUT) {
            finish();
        }
        else if (position == POS_ACCOUNT) {
            Intent intent = new Intent(this, Indexing.class);
            startActivity(intent);
        }
        else if (position == POS_MESSAGES) {
            Intent intent = new Intent(this, Preface.class);
            startActivity(intent);
        }
        else if (position == POS_CART) {
            Intent intent = new Intent(this, Aboutme.class);
            startActivity(intent);
        }
        slidingRootNav.closeMenu();

    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.one)
        {
                    ReadLocator readLocator = getLastReadLocator();

                    Config config = AppUtil.getSavedConfig(getApplicationContext());
                    if (config == null)
                        config = new Config();
                    config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

                    folioReader.setReadLocator(readLocator);
                    folioReader.setConfig(config, true)
                            .openBook("file:///android_asset/canto1.epub");

        }
        else if(view.getId() == R.id.two)
        {
            ReadLocator readLocator = getLastReadLocator();

            Config config = AppUtil.getSavedConfig(getApplicationContext());
            if (config == null)
                config = new Config();
            config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

            folioReader.setReadLocator(readLocator);
            folioReader.setConfig(config, true)
                    .openBook("file:///android_asset/canto2.epub");
        }
        else if(view.getId() == R.id.three)
        {
            ReadLocator readLocator = getLastReadLocator();

            Config config = AppUtil.getSavedConfig(getApplicationContext());
            if (config == null)
                config = new Config();
            config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

            folioReader.setReadLocator(readLocator);
            folioReader.setConfig(config, true)
                    .openBook("file:///android_asset/canto3.epub");
        }
        else if(view.getId() == R.id.four)
        {
            ReadLocator readLocator = getLastReadLocator();

            Config config = AppUtil.getSavedConfig(getApplicationContext());
            if (config == null)
                config = new Config();
            config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

            folioReader.setReadLocator(readLocator);
            folioReader.setConfig(config, true)
                    .openBook("file:///android_asset/4canto.epub");
        }
        else if(view.getId() == R.id.five)
        {
            ReadLocator readLocator = getLastReadLocator();

            Config config = AppUtil.getSavedConfig(getApplicationContext());
            if (config == null)
                config = new Config();
            config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

            folioReader.setReadLocator(readLocator);
            folioReader.setConfig(config, true)
                    .openBook("file:///android_asset/canto5.epub");
        }
        else if(view.getId() == R.id.six)
        {
            ReadLocator readLocator = getLastReadLocator();

            Config config = AppUtil.getSavedConfig(getApplicationContext());
            if (config == null)
                config = new Config();
            config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

            folioReader.setReadLocator(readLocator);
            folioReader.setConfig(config, true)
                    .openBook("file:///android_asset/canto6.epub");
        }
        else if(view.getId() == R.id.seven)
        {
            ReadLocator readLocator = getLastReadLocator();

            Config config = AppUtil.getSavedConfig(getApplicationContext());
            if (config == null)
                config = new Config();
            config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

            folioReader.setReadLocator(readLocator);
            folioReader.setConfig(config, true)
                    .openBook("file:///android_asset/canto7.epub");
        }
        else if(view.getId() == R.id.eight)
        {
            ReadLocator readLocator = getLastReadLocator();

            Config config = AppUtil.getSavedConfig(getApplicationContext());
            if (config == null)
                config = new Config();
            config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

            folioReader.setReadLocator(readLocator);
            folioReader.setConfig(config, true)
                    .openBook("file:///android_asset/canto8.epub");
        }
        else if(view.getId() == R.id.nine)
        {
            ReadLocator readLocator = getLastReadLocator();

            Config config = AppUtil.getSavedConfig(getApplicationContext());
            if (config == null)
                config = new Config();
            config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

            folioReader.setReadLocator(readLocator);
            folioReader.setConfig(config, true)
                    .openBook("file:///android_asset/canto9.epub");
        }
        else if(view.getId() == R.id.tena)
        {
            ReadLocator readLocator = getLastReadLocator();

            Config config = AppUtil.getSavedConfig(getApplicationContext());
            if (config == null)
                config = new Config();
            config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

            folioReader.setReadLocator(readLocator);
            folioReader.setConfig(config, true)
                    .openBook("file:///android_asset/canto10a.epub");
        }
        else if(view.getId() == R.id.tenb)
        {
            ReadLocator readLocator = getLastReadLocator();

            Config config = AppUtil.getSavedConfig(getApplicationContext());
            if (config == null)
                config = new Config();
            config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

            folioReader.setReadLocator(readLocator);
            folioReader.setConfig(config, true)
                    .openBook("file:///android_asset/canto10b.epub");
        }
        else if(view.getId() == R.id.eleventh)
        {
            ReadLocator readLocator = getLastReadLocator();

            Config config = AppUtil.getSavedConfig(getApplicationContext());
            if (config == null)
                config = new Config();
            config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

            folioReader.setReadLocator(readLocator);
            folioReader.setConfig(config, true)
                    .openBook("file:///android_asset/canto11.epub");
        }
        else if(view.getId() == R.id.twelfth)
        {
            ReadLocator readLocator = getLastReadLocator();

            Config config = AppUtil.getSavedConfig(getApplicationContext());
            if (config == null)
                config = new Config();
            config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

            folioReader.setReadLocator(readLocator);
            folioReader.setConfig(config, true)
                    .openBook("file:///android_asset/canto12.epub");
        }
    }

    private ReadLocator getLastReadLocator() {

        String jsonString = loadAssetTextAsString("Locators/LastReadLocators/last_read_locator_1.json");
        return ReadLocator.fromJson(jsonString);
    }

    @Override
    public void saveReadLocator(ReadLocator readLocator) {
        Log.i(LOG_TAG, "-> saveReadLocator -> " + readLocator.toJson());
    }

    /*
     * For testing purpose, we are getting dummy highlights from asset. But you can get highlights from your server
     * On success, you can save highlights to FolioReader DB.
     */
    private void getHighlightsAndSave() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<HighlightData> highlightList = null;
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    highlightList = objectMapper.readValue(
                            loadAssetTextAsString("highlights/highlights_data.json"),
                            new TypeReference<List<HighlightData>>() {
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (highlightList == null) {
                    folioReader.saveReceivedHighLights(null, new OnSaveHighlight() {
                        @Override
                        public void onFinished() {
                            //You can do anything on successful saving highlight list
                        }
                    });
                }
            }
        }).start();
    }

    private String loadAssetTextAsString(String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e("HomeActivity", "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("HomeActivity", "Error closing asset " + name);
                }
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FolioReader.clear();
    }

    @Override
    public void onHighlight(HighLight highlight, HighLight.HighLightAction type) {
        Toast.makeText(this,
                "highlight id = " + highlight.getUUID() + " type = " + type,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFolioReaderClosed() {
        Log.v(LOG_TAG, "-> onFolioReaderClosed");
    }
}
