package dta4316.testapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import dta4316.testapp.Common.Authentication;

public class ResetPasswordActivity extends AuthenticationBaseActivity {
    private static final String TAG = "ResetPasswordActivity";
    private CognitoUser m_CognitoUser;
    private ForgotPasswordContinuation m_Continuation;

    @BindView(R.id.txtEmailToResetPassword) EditText txtEmailToResetPassword;
    @BindView(R.id.txtPasswordToReset) EditText txtPasswordToReset;
    @BindView(R.id.txtPasswordToResetConfirm) EditText txtPasswordToResetConfirm;
    @BindView(R.id.txtResetPasswordVerificationCode) EditText txtResetPasswordVerificationCode;
    @BindView(R.id.btnResetPassword) Button btnResetPassword;
    @BindView(R.id.btnChangePassword) Button btnChangePassword;
    @BindView(R.id.layoutRestPassword) LinearLayout layoutRestPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);

        String userId = getIntent().getStringExtra("RESET_PASSWORD_USER_ID");
        m_CognitoUser = Authentication.GetUser(userId);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPassword();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePassword();
            }
        });
    }

    private void ChangePassword() {
        btnChangePassword.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(ResetPasswordActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Changing Password...");
        progressDialog.show();

        String passwordToReset = txtPasswordToReset.getText().toString();
        String passwordToResetConfirm = txtPasswordToResetConfirm.getText().toString();
        String verificationCode = txtResetPasswordVerificationCode.getText().toString();

        if(!passwordToReset.equals(passwordToResetConfirm)){
            progressDialog.dismiss();
            txtPasswordToResetConfirm.setError(null);
            btnChangePassword.setEnabled(true);
            return;
        }

        m_Continuation.setPassword(passwordToResetConfirm);
        m_Continuation.setVerificationCode(verificationCode);
        m_Continuation.continueTask();
    }

    private void ResetPassword(){
        btnResetPassword.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(ResetPasswordActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Resetting Password...");
        progressDialog.show();

        String emailToResetPassword = txtEmailToResetPassword.getText().toString();
        m_CognitoUser = Authentication.GetUser(emailToResetPassword);

        ForgotPasswordHandler forgotPasswordCallback = new ForgotPasswordHandler() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "Password changed.", Toast.LENGTH_LONG).show();
                onBackPressed();
            }

            @Override
            public void getResetCode(ForgotPasswordContinuation continuation) {
                m_Continuation = continuation;
                LoadResetPasswordView();
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "Verification code is sent to the registered email.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception exception) {
                progressDialog.dismiss();
                String errorMessage = exception.getMessage();
                if(exception instanceof AmazonServiceException) {
                    if(((AmazonServiceException)exception).getErrorCode().equalsIgnoreCase("InvalidParameterException")) {
                        errorMessage = "Invalid email.";
                    }
                    else{
                        errorMessage = ((AmazonServiceException)exception).getErrorMessage();
                    }
                }
                Toast.makeText(getBaseContext(), "Resetting password failed: " + errorMessage, Toast.LENGTH_LONG).show();
                btnResetPassword.setEnabled(true);
            }
        };

        m_CognitoUser.forgotPasswordInBackground(forgotPasswordCallback);
    }

    private void LoadResetPasswordView() {
        txtEmailToResetPassword.setEnabled(false);
        btnResetPassword.setVisibility(View.GONE);
        layoutRestPassword.setVisibility(View.VISIBLE);
        btnChangePassword.setVisibility(View.VISIBLE);
    }
}
