package dta4316.testapp.Common;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.text.NumberFormat;

public class Common {
    private Common() {
    }

    public static Double CalculateDistanceInMeters(double lat1, double lon1, double lat2, double lon2) {
//        Location loc1 = new Location("");
//        loc1.setLatitude(lat1);
//        loc1.setLongitude(lon1);
//
//        Location loc2 = new Location("");
//        loc2.setLatitude(lat2);
//        loc2.setLongitude(lon2);
//
//        float distanceInMiles = loc1.distanceTo(loc2) * 0.00062137f;
//        return (distanceInMiles);

        LatLng location1 = new LatLng(lat1, lon1);
        LatLng location2 = new LatLng(lat2, lon2);
        return SphericalUtil.computeDistanceBetween(location1, location2);
    }

    public static String GetDistanceText(Double distanceInMeters) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        String result;
        UnitLocale locale = UnitLocale.GetDefault();
        if (locale == UnitLocale.Imperial) {
            numberFormat.setMaximumFractionDigits(1);
            result = numberFormat.format((distanceInMeters * 0.00062137)) + Constants.DISTANCE_MILE_POSTFIX;
        } else {
            if (distanceInMeters >= 1000) {
                numberFormat.setMaximumFractionDigits(1);
                result = numberFormat.format(distanceInMeters / 1000) + Constants.DISTANCE_KILOMETER_POSTFIX;
            } else {
                result = numberFormat.format(distanceInMeters) + Constants.DISTANCE_METER_POSTFIX;
            }
        }
        return result;
    }

    public static String GetHeaderImageURL(String imageFileName) {
        return Constants.STORE_HEADER_URL + imageFileName;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static void SlideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public static void SlideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }
}