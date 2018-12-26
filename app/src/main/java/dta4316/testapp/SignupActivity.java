package dta4316.testapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import dta4316.testapp.Common.Authentication;

public class SignupActivity extends AuthenticationBaseActivity {
    private static final String TAG = "SignupActivity";

    @BindView(R.id.txtName) EditText txtName;
    @BindView(R.id.txtAddress) EditText txtAddress;
    @BindView(R.id.txtEmail) EditText txtEmail;
    @BindView(R.id.txtMobile) EditText txtMobile;
    @BindView(R.id.txtPassword) EditText txtPassword;
    @BindView(R.id.txtReenterPassword) EditText txtReenterPassword;
    @BindView(R.id.btnSignup) Button btnSignup;
    @BindView(R.id.linkLogin) TextView linkLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        btnSignup.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = "Meow Tan";//txtName.getText().toString();
        String address = "254 Nadine ST";//txtAddress.getText().toString();
        String email = "dta4316@gmail.com";//txtEmail.getText().toString();
        String mobile = "+12092295232";//txtMobile.getText().toString();
        String password = "11111111";//txtPassword.getText().toString();
        String reEnterPassword = "11111111";//txtReenterPassword.getText().toString();

        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
        userAttributes.addAttribute("given_name", name);
        userAttributes.addAttribute("phone_number", mobile);
        userAttributes.addAttribute("email", email);

        SignUpHandler signupCallback = new SignUpHandler() {

            @Override
            public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
                progressDialog.dismiss();
                if(!userConfirmed) {
                    if(cognitoUserCodeDeliveryDetails.getDeliveryMedium().equals("EMAIL")) {
                        Intent intent = new Intent(getBaseContext(), SignupVerificationActivity.class);
                        intent.putExtra("SIGNUP_VERIFICATION_USER_ID", cognitoUser.getUserId());
                        startActivityForResult(intent, 0);
                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                }
                else {
                    btnSignup.setEnabled(false);
                    setResult(RESULT_OK, null);
                    finish();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "Signup failed: " + ((AmazonServiceException)exception).getErrorMessage(), Toast.LENGTH_LONG).show();
                btnSignup.setEnabled(true);
            }
        };
        Authentication.GetUserPool().signUpInBackground(email, password, userAttributes, null, signupCallback);
    }

    public boolean validate() {
        boolean valid = true;
        return true;
//        String name = txtName.getText().toString();
//        String address = txtAddress.getText().toString();
//        String email = txtEmail.getText().toString();
//        String mobile = txtMobile.getText().toString();
//        String password = txtPassword.getText().toString();
//        String reEnterPassword = txtReenterPassword.getText().toString();
//
//        if (name.isEmpty() || name.length() < 3) {
//            txtName.setError("at least 3 characters");
//            valid = false;
//        } else {
//            txtName.setError(null);
//        }
//
//        if (address.isEmpty()) {
//            txtAddress.setError("Enter Valid Address");
//            valid = false;
//        } else {
//            txtAddress.setError(null);
//        }
//
//
//        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            txtEmail.setError("enter a valid email address");
//            valid = false;
//        } else {
//            txtEmail.setError(null);
//        }
//
//        if (mobile.isEmpty() || mobile.length()!=10) {
//            txtMobile.setError("Enter Valid Mobile Number");
//            valid = false;
//        } else {
//            txtMobile.setError(null);
//        }
//
//        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
//            txtPassword.setError("between 4 and 10 alphanumeric characters");
//            valid = false;
//        } else {
//            txtPassword.setError(null);
//        }
//
//        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
//            txtReenterPassword.setError("Password Do not match");
//            valid = false;
//        } else {
//            txtReenterPassword.setError(null);
//        }
//
//        return valid;
    }

    public void onSignupFailed()
    {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();
        btnSignup.setEnabled(true);
    }
}