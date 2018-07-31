package eu.wonderfulme.triptracker.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import eu.wonderfulme.triptracker.R;


public class DetailActivity extends AppCompatActivity {

    static final String ACTION_ROUTE_REMOVED = "ACTION_ROUTE_REMOVED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //TODO After remove a route, setresult and finish must be called.
//        setResult(Activity.RESULT_OK, resultIntent)
//        finish()

    }
}
