package dta4316.testapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import dta4316.testapp.Common.Authentication;

public class SignupVerificationActivity extends AuthenticationBaseActivity {
    private static final String TAG = "SignupVerificationActivity";
    private CognitoUser m_CognitoUser;
    @BindView(R.id.txtVerificationCode) EditText txtVerificationCode;
    @BindView(R.id.btnVerifyAccount) Button btnVerifyAccount;
    @BindView(R.id.linkResendVerification) TextView linkResendVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_verification);
        ButterKnife.bind(this);

        String userId = getIntent().getStringExtra("SIGNUP_VERIFICATION_USER_ID");
        m_CognitoUser = Authentication.GetUser(userId);

        btnVerifyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Verify();
            }
        });

        linkResendVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResendVerification();
            }
        });
    }

    private void Verify() {
        btnVerifyAccount.setEnabled(false);
        linkResendVerification.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupVerificationActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Verifying Account...");
        progressDialog.show();

        String verificationCode = txtVerificationCode.getText().toString();

        GenericHandler confirmationCallback = new GenericHandler() {
            @Override
            public void onSuccess() {
                btnVerifyAccount.setEnabled(true);
                linkResendVerification.setEnabled(true);
                progressDialog.dismiss();
                finish();
            }

            @Override
            public void onFailure(Exception exception) {
                progressDialog.dismiss();
                btnVerifyAccount.setEnabled(true);
                linkResendVerification.setEnabled(true);
                Toast.makeText(getBaseContext(), "Account verification failed: " + ((AmazonServiceException)exception).getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        };

        // This will cause confirmation to fail if the user attribute has been verified for another user in the same pool
        boolean forcedAliasCreation = false;
        m_CognitoUser.confirmSignUpInBackground(verificationCode, forcedAliasCreation, confirmationCallback);
    }

    private void DisplayMessage(String message){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
    private void ResendVerification() {
        new ResendVerificationTask(this).execute();
    }

    private static class ResendVerificationTask extends AsyncTask<String, Void, Boolean> {
        private Exception m_Exception;
        private WeakReference<SignupVerificationActivity> m_ActivityReference;

        ResendVerificationTask(SignupVerificationActivity context) {
            m_ActivityReference = new WeakReference<>(context);
        }

        VerificationHandler resendVerificationCallback = new VerificationHandler() {
            @Override
            public void onSuccess(CognitoUserCodeDeliveryDetails verificationCodeDeliveryMedium) {
                SignupVerificationActivity activity = m_ActivityReference.get();
                if (activity == null || activity.isFinishing()) return;
                activity.DisplayMessage("Verification code sent. Please check your email for the new verification code.");
            }

            @Override
            public void onFailure(Exception exception) {
                SignupVerificationActivity activity = m_ActivityReference.get();
                if (activity == null || activity.isFinishing()) return;

                String errorMessage = exception instanceof AmazonServiceException ? ((AmazonServiceException)exception).getErrorMessage() : exception.getMessage();
                activity.DisplayMessage("Verification code sending failed: " + errorMessage);
            }
        };

        protected Boolean doInBackground(String... urls) {
            try {
                SignupVerificationActivity activity = m_ActivityReference.get();
                if (activity == null || activity.isFinishing()) return false;
                activity.m_CognitoUser.resendConfirmationCode(resendVerificationCallback);
            } catch (Exception e) {
                this.m_Exception = e;
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
        }
    }
}
