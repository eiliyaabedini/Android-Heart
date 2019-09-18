package de.lizsoft.app;


import androidx.multidex.MultiDex;

import com.facebook.buck.android.support.exopackage.ExopackageApplication;


/**
 * When running debug builds (using buck) exopackage application would load dex files from /data/local/tmp and then
 * construct and start our App class manually.
 * When running beta/release builds, default exopackage behaviour is disabled, and we use multi-dex instead.
 * NOTE: this class has to be written in java for buck to work properly.
 */
public class AppShell extends ExopackageApplication {

    private static final String APP_CLASS = "de.lizsoft.app.App";
    private static final int SECONDARY_DEX = 1;
    private static final int NO_EXOPACKAGE = 0;

    public AppShell() {
        super(APP_CLASS, isExopackageMode() ? SECONDARY_DEX : NO_EXOPACKAGE);
    }

    private static boolean isExopackageMode() {
        return BuildConfig.BUILD_TYPE.equals("debug");
    }

    @Override
    protected void onBaseContextAttached() {
        super.onBaseContextAttached();
        if (!isExopackageMode()) {
            MultiDex.install(this);
        }
    }
}
