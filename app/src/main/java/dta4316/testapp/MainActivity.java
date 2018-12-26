package dta4316.testapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;

import java.lang.ref.WeakReference;

import dta4316.testapp.Common.Authentication;
import dta4316.testapp.ui.Fragments.AboutMeFragment;
import dta4316.testapp.ui.Fragments.SearchStoreFragment;
import dta4316.testapp.ui.Fragments.test1Fragment;

public class MainActivity extends AppCompatActivity implements AboutMeFragment.OnAboutMeFragmentSignedOutListener {
    private BottomNavigationView m_BottomNavigationView;
    private Fragment m_SearchFragment = null;
    private Fragment m_SearchFragment1 = null;
    private Fragment m_SearchFragment2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_SearchFragment = new SearchStoreFragment();
        if (savedInstanceState == null) {
            SetFragment(m_SearchFragment, SearchStoreFragment.class.getSimpleName());
        }

        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.d("MainActivity", "AWSMobileClient is instantiated and you are connected to AWS!");
            }
        }).execute();

        //AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
//        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(getApplicationContext(), "us-east-1:7e84972b-2b7f-410b-8b48-d177d3879b6d", Regions.US_EAST_1);
//        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();
//        DatabaseAuthentication.Init(credentialsProvider, configuration);

        m_BottomNavigationView = findViewById(R.id.navigation);
        m_BottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(!Authentication.GetHasCurrentUser()) {
            Login();
        }
        else {
            new CheckIsSignedInTask(this).execute();
        }
    }

    private void Login(){
        Authentication.SignOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.container);
            Fragment selectedFragment = null;
            String selectedFragmentClassName = "";
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    if(m_SearchFragment == null) {
                        m_SearchFragment = new SearchStoreFragment();
                    }
                    selectedFragmentClassName = SearchStoreFragment.class.getSimpleName();
                    selectedFragment = m_SearchFragment;
                    break;
                case R.id.navigation_about_me:
                    if(m_SearchFragment1 == null) {
                        m_SearchFragment1 = new AboutMeFragment();
                    }
                    selectedFragmentClassName = AboutMeFragment.class.getSimpleName();
                    selectedFragment = m_SearchFragment1;
                    break;
                case R.id.navigation_notifications:
                    if(m_SearchFragment2 == null) {
                        m_SearchFragment2 = new test1Fragment();
                    }
                    selectedFragmentClassName = test1Fragment.class.getSimpleName();
                    selectedFragment = m_SearchFragment2;
                    break;
            }

            SetFragment(selectedFragment, selectedFragmentClassName);
            return true;
        }
    };

    public void SetFragment(Fragment fragment, String tagFragmentName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment currentFragment = fragmentManager.getPrimaryNavigationFragment();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }

        Fragment fragmentTemp = fragmentManager.findFragmentByTag(tagFragmentName);
        if (fragmentTemp == null) {
            fragmentTemp = fragment;
            fragmentTransaction.add(R.id.container, fragmentTemp, tagFragmentName);
        } else {
            fragmentTransaction.show(fragmentTemp);
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNowAllowingStateLoss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        return true;
    }

    @Override
    public void OnAboutMeFragmentSignedOut() {
        m_BottomNavigationView.setSelectedItemId(R.id.navigation_search);
        Login();
    }

    private static class CheckIsSignedInTask extends AsyncTask<String, Void, Boolean> {
        private Exception m_Exception;
        private WeakReference<MainActivity> m_ActivityReference;

        CheckIsSignedInTask(MainActivity context) {
            m_ActivityReference = new WeakReference<>(context);
        }

        AuthenticationHandler authenticationCallback = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice cognitoDevice) {

                MainActivity activity = m_ActivityReference.get();
                if (activity == null || activity.isFinishing()) return;

                String JWTToken = cognitoUserSession.getIdToken().getJWTToken();

                if(TextUtils.isEmpty(JWTToken)) {
                    activity.Login();
                }
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String s) {
                authenticationContinuation.continueTask();
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation challengeContinuation) {
            }

            @Override
            public void onFailure(Exception e) {
                MainActivity activity = m_ActivityReference.get();
                if (activity == null || activity.isFinishing()) return;
                activity.Login();
            }
        };

        protected Boolean doInBackground(String... urls) {
            try {
                Authentication.GetCurrentUser().getSession(authenticationCallback);
            } catch (Exception e) {
                this.m_Exception = e;
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
}

