package foadkhezri.github.com.earthquakereport;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.util.Objects;

public class About extends AppCompatActivity {
    TextView description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        forceRTLIfSupported();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        description = findViewById(R.id.about_me);
        Typeface ty1 = Typeface.createFromAsset(getApplication().getAssets(), "fonts/nasimbd.ttf");
        description.setTypeface(ty1);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
