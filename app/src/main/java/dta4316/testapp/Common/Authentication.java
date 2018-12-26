package dta4316.testapp.Common;

import android.text.TextUtils;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;

public class Authentication {
    private static CognitoUserPool m_UserPool;
    private static Boolean m_IsSignedIn = false;

    private Authentication() {
    }

    public static void Init() {
        m_UserPool = new CognitoUserPool(ThisApplication.GetContext(), Constants.USER_POOL_ID, Constants.APP_CLIENT_ID, Constants.APP_CLIENT_SECRET, Regions.US_EAST_1);
    }

    public static CognitoUserPool GetUserPool() {
        return m_UserPool;
    }

    public static CognitoUser GetUser(String userId) {
        if (m_UserPool != null) {
            return m_UserPool.getUser(userId);
        }
        return null;
    }

    public static CognitoUser GetCurrentUser() {
        if (m_UserPool != null) {
            return m_UserPool.getCurrentUser();
        }
        return null;
    }

    public static Boolean GetHasCurrentUser() {
        CognitoUser currentUser = GetCurrentUser();
        return !(currentUser == null || currentUser.getUserId() == null || TextUtils.isEmpty(currentUser.getUserId()));
    }

    public static Boolean GetIsSignedIn() {
        return m_IsSignedIn;
    }

    public static void SignIn() {
        m_IsSignedIn = true;
    }

    public static void SignOut() {
        CognitoUser currentUser = GetCurrentUser();
        if (currentUser != null) {
            currentUser.signOut();
        }
        m_IsSignedIn = false;
    }
}
