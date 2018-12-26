package dta4316.testapp.Common;

import java.util.Locale;

class UnitLocale {
    public static UnitLocale Imperial = new UnitLocale();
    public static UnitLocale Metric = new UnitLocale();

    public static UnitLocale GetDefault() {
        return getFrom(Locale.getDefault());
    }
    private static UnitLocale getFrom(Locale locale) {
        String countryCode = locale.getCountry().toUpperCase();
        switch(countryCode) {
            case "US":
                return Imperial;
            case "MY":
                return Metric;
            default:
                return Imperial;
        }
    }
}
