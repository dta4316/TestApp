package dta4316.testapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import dta4316.testapp.Common.Authentication;

public class AuthenticationBaseActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        if(!Authentication.GetIsSignedIn()) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(intent, 0);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }
}
