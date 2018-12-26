package dta4316.testapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import dta4316.testapp.Common.Authentication;
import dta4316.testapp.Common.Constants;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private ProgressDialog m_ProgressDialog;

    @BindView(R.id.txtEmail) EditText txtEmail;
    @BindView(R.id.txtPassword) EditText txtPassword;
    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.linkSignup) TextView linkSignup;
    @BindView(R.id.linkForgotPassword) TextView linkForgotPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        m_ProgressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        linkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        linkForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivityForResult(intent, 0);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        btnLogin.setEnabled(false);
        m_ProgressDialog.setIndeterminate(true);
        m_ProgressDialog.setMessage("Authenticating...");
        m_ProgressDialog.show();

        String userName = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        // Callback handler for the sign-in process
        AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                LoginSucceed();
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                // The API needs user sign-in credentials to continue
                AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, password, null);

                // Pass the user sign-in credentials to the continuation
                authenticationContinuation.setAuthenticationDetails(authenticationDetails);

                // Allow the sign-in to continue
                authenticationContinuation.continueTask();
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
                // Multi-factor authentication is required; get the verification code from user
                //multiFactorAuthenticationContinuation.setMfaCode(mfaVerificationCode);
                // Allow the sign-in process to continue
                //multiFactorAuthenticationContinuation.continueTask();
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
            }

            @Override
            public void onFailure(Exception exception) {
                if(exception instanceof AmazonServiceException && ((AmazonServiceException)exception).getErrorCode().equals(Constants.COOGNITO_USER_NOT_CONFIRMED)){
                    VerifyAccount();
                }
                else {
                    LoginFailed(exception);
                }
            }
        };

        Authentication.GetUser(userName).getSessionInBackground(authenticationHandler);
    }

    private void LoginFailed(Exception exception){
        Authentication.SignOut();
        m_ProgressDialog.dismiss();
        String errorMessage = exception instanceof AmazonServiceException ? ((AmazonServiceException)exception).getErrorMessage() : exception.getMessage();
        Toast.makeText(getBaseContext(), "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
        btnLogin.setEnabled(true);
    }

    private void LoginSucceed(){
        btnLogin.setEnabled(true);
        m_ProgressDialog.dismiss();
        Authentication.SignIn();
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void VerifyAccount() {
        String userName = txtEmail.getText().toString();
        Intent intent = new Intent(getBaseContext(), SignupVerificationActivity.class);
        intent.putExtra("SIGNUP_VERIFICATION_USER_ID", userName);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void ConfirmUserLogin(){
        GetDetailsHandler GetDetailsCallback = new GetDetailsHandler(){
            @Override
            public void onSuccess(CognitoUserDetails cognitoUserDetails) {
                CognitoUserAttributes cognitoUserAttributes = cognitoUserDetails.getAttributes();

                Map<String, String> attributes = cognitoUserAttributes.getAttributes();
                Boolean emailVerified = attributes.get("email_verified").toLowerCase().equals("true")? true: false;
                if(emailVerified) {
                    LoginSucceed();
                }
                else {
                    VerifyAccount();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                LoginFailed(exception);
            }
        };
        Authentication.GetCurrentUser().getDetailsInBackground(GetDetailsCallback);
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginFailed() {
        Authentication.SignOut();
        Toast.makeText(getBaseContext(), "Login failed.", Toast.LENGTH_LONG).show();
        btnLogin.setEnabled(true);
    }
}

