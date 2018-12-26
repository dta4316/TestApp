//package dta4316.testapp.Database;
//
//import android.location.Location;
//import android.os.AsyncTask;
//
//import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
//import com.amazonaws.models.nosql.StoreDO;
//import com.amazonaws.models.nosql.StoreInfoDO;
//
//import java.util.List;
//
//import dta4316.testapp.Command.Command;
//import dta4316.testapp.Common.Common;
//import dta4316.testapp.StoreViewAdapter;
//
//public class Store {
//    private static Store m_Instance = null;
//    private static Command.CommandFactory m_MainCommandFactory;
//    private static DatabaseAuthentication m_DatabaseAuthentication = null;
//    private static boolean m_IsLoaded = false;
//    private static List<StoreDO> m_StoreList = null;
//    private StoreViewAdapter m_StoreViewAdapter;
//
//    private Store() {
//    }
//
//    public static Store GetInstance() {
//        if (m_Instance == null) {
//            m_Instance = new Store();
//        }
//        return m_Instance;
//    }
//
//    public void Init(DatabaseAuthentication databaseAuthentication, Command.CommandFactory mainCommandFactory) {
//        m_DatabaseAuthentication = databaseAuthentication;
//        m_MainCommandFactory = mainCommandFactory;
//    }
//
//    public boolean GetIsLoaded() {
//        return m_IsLoaded;
//    }
//
//    public List<StoreDO> GetStore() {
//        return m_StoreList;
//    }
//
//    public void Load() {
//        TaskLoadAllStore taskLoadAllStore = new TaskLoadAllStore();
//        taskLoadAllStore.execute();
//    }
//
//    public void LoadStoreServices(String storeUUID) {
//        TaskLoadStoreServices taskLoadStoreServices = new TaskLoadStoreServices();
//        taskLoadStoreServices.execute(storeUUID);
//    }
//
//    private static class TaskLoadAllStore extends AsyncTask<String, Void, List<StoreDO>> {
//        @Override
//        protected List<StoreDO> doInBackground(String... params) {
//            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
//
//            //ScanRequest scanRequest = new ScanRequest().withTableName(tableName);
//            //ScanResult result = dynamoDBClient.scan(scanRequest);
//            return m_DatabaseAuthentication.GetDynamoDBMapper().scan(StoreDO.class, scanExpression);
//        }
//
//        @Override
//        protected void onPostExecute(List<StoreDO> storeList) {
//            if (m_StoreList != null) {
//                m_StoreList = null;
//            }
//            m_StoreList = storeList;
//            m_IsLoaded = m_StoreList != null;
//
//            if (m_MainCommandFactory != null) {
//                m_MainCommandFactory.ExecuteCommand("StoreLoaded");
//            }
//        }
//
//        @Override
//        protected void onPreExecute() {
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//        }
//    }
//
//    private static class TaskLoadStoreServices extends AsyncTask<String, Void, StoreInfoDO[]> {
//        @Override
//        protected StoreInfoDO[] doInBackground(String... params) {
//            final StoreInfoDO service = DatabaseAuthentication.GetInstance().GetDynamoDBMapper().load(
//                    StoreInfoDO.class,
//                    params[0]);
//
//            service.ParseService();
//
////            final String tableName = Constants.DATABASE_STORE_SERVICES;
////
////            Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
////            expressionAttributeValues.put(":val", new AttributeValue().withS(params[0]));
////
////            ScanRequest scanRequest = new ScanRequest()
////                    .withTableName(tableName)
////                    .withFilterExpression("Store_UUID = :val")
////                    .withExpressionAttributeValues(expressionAttributeValues);
////
////            ScanResult result = DatabaseAuthentication.GetInstance().GetAmazonDynamoDBClient().scan(scanRequest);
////            StoreInfoDO[] storeServicesArray = (StoreInfoDO[]) result.getItems().toArray();
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(StoreInfoDO[] storeServiceArray) {
//
//            if (m_MainCommandFactory != null) {
//                m_MainCommandFactory.ExecuteCommand("StoreServicesLoaded");
//            }
//        }
//
//        @Override
//        protected void onPreExecute() {
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//        }
//    }
//
//    @SuppressWarnings("unused")
//    public void AddStore(String StoreName, String StoreContactPerson, String StoreAddressLine1, String StoreAddressLine2, String StoreAddressLine3, String StoreAddressCity, String StoreAddressState, String StoreAddressZip, String StoreAddressCountry, String StorePhone, String StoreEmail, Double StoreLatitude, Double StoreLongtitude, String StoreHeaderImage) {
//
//        final StoreDO newStore = new StoreDO();
//        //newStore.setUUID(UUID);
//        newStore.setStoreName(StoreName);
//        newStore.setStoreContactPerson(StoreContactPerson);
//        newStore.setStoreAddressLine1(StoreAddressLine1);
//        newStore.setStoreAddressLine2(StoreAddressLine2);
//        newStore.setStoreAddressLine3(StoreAddressLine3);
//        newStore.setStoreAddressCity(StoreAddressCity);
//        newStore.setStoreAddressState(StoreAddressState);
//        newStore.setStoreAddressZip(StoreAddressZip);
//        newStore.setStoreAddressCountry(StoreAddressCountry);
//        newStore.setStorePhone(StorePhone);
//        newStore.setStoreEmail(StoreEmail);
//        newStore.setStoreLatitude(StoreLatitude);
//        newStore.setStoreLongtitude(StoreLongtitude);
//        newStore.setStoreHeaderImage(StoreHeaderImage);
//
//        new Thread(() -> m_DatabaseAuthentication.GetDynamoDBMapper().save(newStore)).start();
//    }
//
//    public void UpdateDistance(Location currentLocation) {
//        if (currentLocation != null) {
//            for (StoreDO store : m_StoreList) {
//                store.setStoreDistance(Common.CalculateDistanceInMeters(store.getStoreLatitude(), store.getStoreLongtitude(), currentLocation.getLatitude(), currentLocation.getLongitude()));
//            }
//        }
//    }
//
//    public StoreViewAdapter GetStoreViewAdapter() {
//        return m_StoreViewAdapter;
//    }
//
//    public void SetStoreViewAdapter(StoreViewAdapter storeViewAdapter) {
//        m_StoreViewAdapter = null;
//        m_StoreViewAdapter = storeViewAdapter;
//    }
//
//}
//
////    public void readLocation() {
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
////                final String tableName = "testapp-mobilehub-1175351448-TestLocations";
////                Table dbTable = Table.loadTable(dynamoDBClient, tableName);
////
////                Map<String, AttributeValue> lastKeyEvaluated = null;
////                do {
////                    ScanRequest scanRequest = new ScanRequest()
////                            .withTableName(tableName)
////                            .withLimit(1)
////                            .withExclusiveStartKey(lastKeyEvaluated);
////
////                    ScanResult result = dynamoDBClient.scan(scanRequest);
//////                    for (Map<String, AttributeValue> item : result.getItems()){
//////                        //printItem(item);
//////                    }
////                    lastKeyEvaluated = result.getLastEvaluatedKey();
////                } while (lastKeyEvaluated != null);
////
////
////                Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
////                expressionAttributeValues.put(":val", new AttributeValue().withN("1"));
////                expressionAttributeValues.put(":val2", new AttributeValue().withS("Test Location 4"));
////
////                ScanRequest scanRequest = new ScanRequest()
////                        .withTableName(tableName)
////                        .withFilterExpression("itemId = :val and category = :val2")
////                        //.withProjectionExpression("Id")
////                        .withExpressionAttributeValues(expressionAttributeValues);
////
////
////                ScanResult result1 = dynamoDBClient.scan(scanRequest);
////                StoreDO[] ary = (StoreDO[]) result1.getItems().toArray();
////
//////                final StoreDO savedLocation = dynamoDBMapper.load(
//////                        StoreDO.class,
//////                        "User 1",
//////                        1.0);
////
////
//////                runOnUiThread(new Runnable() {
//////
//////                    @Override
//////                    public void run() {
//////                        TextView txt_display_name = (TextView)findViewById(R.id.txtResult);
//////                        txt_display_name.setText(savedLocation.getName());
//////                    }
//////                });
////            }
////        }).start();
////    }