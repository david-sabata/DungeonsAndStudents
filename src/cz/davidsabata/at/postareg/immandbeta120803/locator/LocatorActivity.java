package cz.davidsabata.at.postareg.immandbeta120803.locator;

import cz.davidsabata.at.postareg.immandbeta120803.R;
import cz.davidsabata.at.postareg.immandbeta120803.R.layout;
import cz.davidsabata.at.postareg.immandbeta120803.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class LocatorActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_locator, menu);
        return true;
    }
}
