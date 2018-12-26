package dta4316.testapp.Database;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import dta4316.testapp.Common.Constants;
import dta4316.testapp.Common.ThisApplication;

public class DatabaseAuthentication {
    private static DatabaseAuthentication m_Instance = null;
    private DynamoDBMapper m_DynamoDBMapper;
    private AmazonDynamoDBClient m_DynamoDBClient;

    private DatabaseAuthentication() {Init();}

    private void Init() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(ThisApplication.GetContext(), Constants.IDENTITY_POOL_ID, Constants.IDENTITY_REGION);
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();

        m_DynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);
        m_DynamoDBMapper = DynamoDBMapper.builder().dynamoDBClient(m_DynamoDBClient).awsConfiguration(configuration).build();
    }

    public static DatabaseAuthentication GetInstance() {
        if(m_Instance == null) {
            m_Instance = new DatabaseAuthentication();
        }
        return m_Instance;
    }

    public DynamoDBMapper GetDynamoDBMapper(){
        return m_DynamoDBMapper;
    }

    public AmazonDynamoDBClient GetAmazonDynamoDBClient(){
        return m_DynamoDBClient;
    }
}
