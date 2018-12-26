package dta4316.testapp.Database;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.models.nosql.StoreDO;
import com.amazonaws.models.nosql.StoreInfoDO;

import java.util.HashMap;
import java.util.List;

import dta4316.testapp.Common.Common;

public class StoreService extends IntentService {
    private static final String TAG = StoreService.class.getSimpleName();
    private static List<StoreDO> m_StoreList = null;
    private static HashMap<String, StoreInfoDO> m_StoreInfoList = new HashMap<String, StoreInfoDO>();

    private static final String ACTION_STORE_LOADED = "STORE_LOADED";
    private static final String ACTION_ALL_STORE_LOADED = "ALL_STORE_LOADED";

    private final static String STORE_SERVICE_LOAD_STORE = "LOAD_STORE";
    private final static String STORE_SERVICE_LOAD_ALL_STORE = "LOAD_ALL_STORE";

    public StoreService() {
        super(TAG);
    }

    public static boolean GetIsLoaded() {
        return m_StoreList != null;
    }

    public static List<StoreDO> GetStoreList() {
        return m_StoreList;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        if (action.equals(STORE_SERVICE_LOAD_ALL_STORE)) {
            LoadAllStoreInternal(intent);
        }
        else if (action.equals(STORE_SERVICE_LOAD_STORE)) {
            LoadStoreInternal(intent);
        }
    }

    public static IntentFilter GetAllStoreLoadedIntent() {
        return new IntentFilter(StoreService.ACTION_ALL_STORE_LOADED);
    }

    public static void LoadAllStore(Context context) {
        Intent intent = new Intent(context, StoreService.class);
        intent.setAction(StoreService.STORE_SERVICE_LOAD_ALL_STORE);
        context.startService(intent);
    }

    private void LoadAllStoreInternal(Intent intent){
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<StoreDO> storeList = DatabaseAuthentication.GetInstance().GetDynamoDBMapper().scan(StoreDO.class, scanExpression);

        if (m_StoreList != null) {
            m_StoreList = null;
        }
        m_StoreList = storeList;

//        int i = 0;
//        for (StoreDO store : m_StoreList) {
//            i++;
//            String counter = " " + i;
//            String StoreUUID = store.getUUID();
//            String StoreName = "Store" + counter;
//            String StoreContactPerson = "Person" + counter;
//            String StoreAddressLine1 = "Street" + counter;
//            String StoreAddressLine2 = "Street" + counter;
//            String StoreAddressLine3 = " ";
//            String StoreAddressCity = "City" + counter;
//            String StoreAddressState = "State" + counter;
//            String StoreAddressZip = "Zip" + counter;
//            String StoreAddressCountry = "Country" + counter;
//            String StorePhone = "209-229-5232";
//            String StoreEmail = "Email" + counter + "@yahoo.com";
//
//            StoreService.AddStoreInfo(StoreUUID, StoreContactPerson, StoreAddressLine1, StoreAddressLine2, StoreAddressLine3,
//                    StoreAddressCity, StoreAddressState, StoreAddressZip, StoreAddressCountry, StorePhone, StoreEmail);
//        }

        Intent allStoreLoadedIntent = new Intent(this, StoreService.class);
        allStoreLoadedIntent.setAction(ACTION_ALL_STORE_LOADED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(allStoreLoadedIntent);
    }

    public static void LoadStore(Context context, String storeUUID) {
        Intent intent = new Intent(context, StoreService.class);
        intent.setAction(StoreService.STORE_SERVICE_LOAD_STORE);
        intent.putExtra("Store_UUID", storeUUID);
        context.startService(intent);
    }

    private void LoadStoreInternal(Intent intent) {
        String storeUUID = intent.getStringExtra("Store_UUID");
        final StoreInfoDO storeInfo = DatabaseAuthentication.GetInstance().GetDynamoDBMapper().load(StoreInfoDO.class, storeUUID);

        if (storeInfo != null) {
            m_StoreInfoList.put(storeUUID, storeInfo);
        }

        Intent broadcastIntent = new Intent("Load_" + storeUUID);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    public static StoreDO GetStore(String storeUUID){
        for (StoreDO store : m_StoreList) {
            if(store.getUUID().equals(storeUUID))
            {
                return store;
            }
        }
        return null;
    }

    public static StoreInfoDO GetStoreInfo(String storeUUID) {
        return m_StoreInfoList.containsKey(storeUUID) ? m_StoreInfoList.get(storeUUID) : null;
    }

    public static void AddStore(String StoreName, Double StoreLatitude, Double StoreLongtitude, String StoreHeaderImage) {

        final StoreDO newStore = new StoreDO();
        newStore.setStoreName(StoreName);
        newStore.setStoreLatitude(StoreLatitude);
        newStore.setStoreLongtitude(StoreLongtitude);
        newStore.setStoreHeaderImage(StoreHeaderImage);

        new Thread(() -> DatabaseAuthentication.GetInstance().GetDynamoDBMapper().save(newStore)).start();
    }

    public static void AddStoreInfo(String StoreUUID, String StoreContactPerson, String StoreAddressLine1, String StoreAddressLine2, String StoreAddressLine3, String StoreAddressCity, String StoreAddressState, String StoreAddressZip, String StoreAddressCountry, String StorePhone, String StoreEmail) {

        final StoreInfoDO newStoreInfo = new StoreInfoDO();
        newStoreInfo.setStoreUUID(StoreUUID);
        newStoreInfo.setStoreContactPerson(StoreContactPerson);
        newStoreInfo.setStoreAddressLine1(StoreAddressLine1);
        newStoreInfo.setStoreAddressLine2(StoreAddressLine2);
        newStoreInfo.setStoreAddressLine3(StoreAddressLine3);
        newStoreInfo.setStoreAddressCity(StoreAddressCity);
        newStoreInfo.setStoreAddressState(StoreAddressState);
        newStoreInfo.setStoreAddressZip(StoreAddressZip);
        newStoreInfo.setStoreAddressCountry(StoreAddressCountry);
        newStoreInfo.setStorePhone(StorePhone);
        newStoreInfo.setStoreEmail(StoreEmail);

        new Thread(() -> DatabaseAuthentication.GetInstance().GetDynamoDBMapper().save(newStoreInfo)).start();
    }

    public static void UpdateDistance(Location currentLocation) {
        if (currentLocation != null) {
            for (StoreDO store : m_StoreList) {
                store.setStoreDistance(Common.CalculateDistanceInMeters(store.getStoreLatitude(), store.getStoreLongtitude(), currentLocation.getLatitude(), currentLocation.getLongitude()));
            }
        }
    }
}

//    public void readLocation() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final String tableName = "testapp-mobilehub-1175351448-TestLocations";
//                Table dbTable = Table.loadTable(dynamoDBClient, tableName);
//
//                Map<String, AttributeValue> lastKeyEvaluated = null;
//                do {
//                    ScanRequest scanRequest = new ScanRequest()
//                            .withTableName(tableName)
//                            .withLimit(1)
//                            .withExclusiveStartKey(lastKeyEvaluated);
//
//                    ScanResult result = dynamoDBClient.scan(scanRequest);
////                    for (Map<String, AttributeValue> item : result.getItems()){
////                        //printItem(item);
////                    }
//                    lastKeyEvaluated = result.getLastEvaluatedKey();
//                } while (lastKeyEvaluated != null);
//
//
//                Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
//                expressionAttributeValues.put(":val", new AttributeValue().withN("1"));
//                expressionAttributeValues.put(":val2", new AttributeValue().withS("Test Location 4"));
//
//                ScanRequest scanRequest = new ScanRequest()
//                        .withTableName(tableName)
//                        .withFilterExpression("itemId = :val and category = :val2")
//                        //.withProjectionExpression("Id")
//                        .withExpressionAttributeValues(expressionAttributeValues);
//
//
//                ScanResult result1 = dynamoDBClient.scan(scanRequest);
//                StoreDO[] ary = (StoreDO[]) result1.getItems().toArray();
//
////                final StoreDO savedLocation = dynamoDBMapper.load(
////                        StoreDO.class,
////                        "User 1",
////                        1.0);
//
//
////                runOnUiThread(new Runnable() {
////
////                    @Override
////                    public void run() {
////                        TextView txt_display_name = (TextView)findViewById(R.id.txtResult);
////                        txt_display_name.setText(savedLocation.getName());
////                    }
////                });
//            }
//        }).start();
//    }