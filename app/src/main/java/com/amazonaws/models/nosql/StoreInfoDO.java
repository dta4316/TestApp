package com.amazonaws.models.nosql;

import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@DynamoDBTable(tableName = "StoreInfo")

public class StoreInfoDO {
    private String _StoreUUID;
    private String _StoreContactPerson;
    private String _StoreAddressLine1;
    private String _StoreAddressLine2;
    private String _StoreAddressLine3;
    private String _StoreAddressCity;
    private String _StoreAddressState;
    private String _StoreAddressZip;
    private String _StoreAddressCountry;
    private String _StorePhone;
    private String _StoreEmail;
    private String _StoreDescription;
    private String _StoreServices;
    private List<Service> _ServiceList;

    @DynamoDBHashKey(attributeName = "StoreUUID")
    @DynamoDBAttribute(attributeName = "StoreUUID")
    public String getStoreUUID() {
        return _StoreUUID;
    }

    public void setStoreUUID(final String _StoreUUID) {
        this._StoreUUID = _StoreUUID;
    }

    @DynamoDBAttribute(attributeName = "StoreContactPerson")
    public String getStoreContactPerson() {
        return _StoreContactPerson;
    }

    public void setStoreContactPerson(final String _StoreContactPerson) {
        this._StoreContactPerson = _StoreContactPerson;
    }

    @DynamoDBAttribute(attributeName = "StoreAddressLine1")
    public String getStoreAddressLine1() {
        return _StoreAddressLine1;
    }

    public void setStoreAddressLine1(final String _StoreAddressLine1) {
        this._StoreAddressLine1 = _StoreAddressLine1;
    }

    @DynamoDBAttribute(attributeName = "StoreAddressLine2")
    public String getStoreAddressLine2() {
        return _StoreAddressLine2;
    }

    public void setStoreAddressLine2(final String _StoreAddressLine2) {
        this._StoreAddressLine2 = _StoreAddressLine2;
    }

    @DynamoDBAttribute(attributeName = "_StoreAddressLine3")
    public String getStoreAddressLine3() {
        return _StoreAddressLine3;
    }

    public void setStoreAddressLine3(final String _StoreAddressLine3) {
        this._StoreAddressLine3 = _StoreAddressLine3;
    }

    @DynamoDBAttribute(attributeName = "StoreAddressCity")
    public String getStoreAddressCity() {
        return _StoreAddressCity;
    }

    public void setStoreAddressCity(final String _StoreAddressCity) {
        this._StoreAddressCity = _StoreAddressCity;
    }

    @DynamoDBAttribute(attributeName = "StoreAddressState")
    public String getStoreAddressState() {
        return _StoreAddressState;
    }

    public void setStoreAddressState(final String _StoreAddressState) {
        this._StoreAddressState = _StoreAddressState;
    }

    @DynamoDBAttribute(attributeName = "StoreAddressZip")
    public String getStoreAddressZip() {
        return _StoreAddressZip;
    }

    public void setStoreAddressZip(final String _StoreAddressZip) {
        this._StoreAddressZip = _StoreAddressZip;
    }

    @DynamoDBAttribute(attributeName = "StoreAddressCountry")
    public String getStoreAddressCountry() {
        return _StoreAddressCountry;
    }

    public void setStoreAddressCountry(final String _StoreAddressCountry) {
        this._StoreAddressCountry = _StoreAddressCountry;
    }

    @DynamoDBAttribute(attributeName = "StorePhone")
    public String getStorePhone() {
        return _StorePhone;
    }

    public void setStorePhone(final String _StorePhone) {
        this._StorePhone = _StorePhone;
    }

    @DynamoDBAttribute(attributeName = "StoreEmail")
    public String getStoreEmail() {
        return _StoreEmail;
    }

    public void setStoreEmail(final String _StoreEmail) {
        this._StoreEmail = _StoreEmail;
    }

    @DynamoDBAttribute(attributeName = "StoreDescription")
    public String getStoreDescription() {
        return _StoreDescription;
    }

    public void setStoreDescription(final String _StoreDescription) {
        this._StoreDescription = _StoreDescription;
    }

    @DynamoDBAttribute(attributeName = "StoreServices")
    public String getStoreServices() {
        return _StoreServices;
    }

    public void setStoreServices(final String _StoreServices) {
        this._StoreServices = _StoreServices;
    }

    public List<Service> GetStoreServiceList() {
        return _ServiceList;
    }

    public void ParseService() {
        if(_ServiceList != null)
        {
            _ServiceList.clear();
        }
        else{
            _ServiceList = new ArrayList<Service>();
        }

        if (_StoreServices == null || _StoreServices.isEmpty()) return;

        try {
            JSONObject jsonObject = new JSONObject(_StoreServices);
            JSONArray jsonArray = jsonObject.optJSONArray("Services");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject postObject = (JSONObject) jsonArray.get(i);

                String name = postObject.isNull("Name") ? "" : postObject.getString("Name");
                String description = postObject.isNull("Description") ? "" : postObject.getString("Description");
                Double price = postObject.isNull("Price") ? 0 : postObject.getDouble("Price");
                Double discountNumber = postObject.isNull("DiscountNumber") ? 0 : postObject.getDouble("DiscountNumber");
                String discountType = postObject.isNull("DiscountType") ? "" : postObject.getString("DiscountType");
                Boolean isEnabled = postObject.isNull("IsEnabled") ? false : postObject.getBoolean("IsEnabled");

                _ServiceList.add(new Service(name, description, price, discountNumber, discountType, isEnabled));
            }
        } catch (JSONException e) {
            _ServiceList.clear();
            e.printStackTrace();
            Log.e("error", String.valueOf(e));
        }
    }

    public String GetAddressText() {
        StringBuilder sb = new StringBuilder();

        if (_StoreAddressLine1 != null && !_StoreAddressLine1.trim().isEmpty()) {
            sb.append(_StoreAddressLine1.trim());
        }

        if (_StoreAddressLine2 != null && !_StoreAddressLine2.trim().isEmpty()) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(_StoreAddressLine2.trim());
        }

        if (_StoreAddressLine3 != null && !_StoreAddressLine3.trim().isEmpty()) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(_StoreAddressLine3.trim());
        }

        if ((_StoreAddressCity != null && !_StoreAddressCity.trim().isEmpty()) ||
                (_StoreAddressState != null && !_StoreAddressState.trim().isEmpty()) ||
                (_StoreAddressZip != null && !_StoreAddressZip.trim().isEmpty())) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(String.format("%s %s %s", _StoreAddressCity.trim(), _StoreAddressState.trim(), _StoreAddressZip.trim()));
        }
        return sb.toString();
    }

    public class Service {
        private String _Name;
        private String _Description;
        private Double _Price;
        private Double _DiscountNumber;
        private String _DiscountType;
        private Boolean _IsEnabled;
        private Boolean _IsSelected;

        public String GetName(){
            return _Name;
        }
        public String GetDescription(){
            return _Description;
        }
        public Double GetPrice(){
            return _Price;
        }
        public Double GetDiscountNumber(){
            return _DiscountNumber;
        }
        public String GetDiscountType(){
            return _DiscountType;
        }
        public Boolean GetIsEnabled(){
            return _IsEnabled;
        }
        public Boolean GetIsSelected(){
            return _IsSelected;
        }

        public void SetIsSelected(Boolean isSelected){
            _IsSelected = isSelected;
        }

        public Service(String name, String description, Double price, Double discountNumber, String discountType, Boolean isEnabled) {
            this._Name = name;
            this._Description = description;
            this._Price = price;
            this._DiscountNumber = discountNumber;
            this._DiscountType = discountType;
            this._IsEnabled = isEnabled;
            _IsSelected = false;
        }
    }
}
