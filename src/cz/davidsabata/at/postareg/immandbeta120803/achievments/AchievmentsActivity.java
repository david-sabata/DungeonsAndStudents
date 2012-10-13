package cz.davidsabata.at.postareg.immandbeta120803.achievments;

import cz.davidsabata.at.postareg.immandbeta120803.R;
import cz.davidsabata.at.postareg.immandbeta120803.R.layout;
import cz.davidsabata.at.postareg.immandbeta120803.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class AchievmentsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievments);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_achievments, menu);
        return true;
    }
}
