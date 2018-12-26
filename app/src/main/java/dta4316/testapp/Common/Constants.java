package dta4316.testapp.Common;

import com.amazonaws.regions.Regions;

public class Constants {
    private Constants() {};

    final static public String DATABASE_STORE = "Store";
    final static public String DATABASE_STORE_SERVICES = "StoreServices";

    final static public String STORE_HEADER_URL = "https://s3.amazonaws.com/storeheader/";
    final static public String APP_CLIENT_ID = "6atmrc64r6pd1inkd6i0251mi8";
    final static public String APP_CLIENT_SECRET = "138srgpvd6g1i1md5detud19ma27e04cfctls7h486urhc8mfq9b";
    final static public String USER_POOL_ID = "us-east-1_Ud2jEnAMx";
    final static public String IDENTITY_POOL_ID = "us-east-1:7e84972b-2b7f-410b-8b48-d177d3879b6d";
    final static public Regions IDENTITY_REGION = Regions.US_EAST_1;

    final static public String DISTANCE_METER_POSTFIX = "km";
    final static public String DISTANCE_KILOMETER_POSTFIX = "km";
    final static public String DISTANCE_MILE_POSTFIX = "miles";


    final static public String COOGNITO_USER_NOT_CONFIRMED = "UserNotConfirmedException";








}