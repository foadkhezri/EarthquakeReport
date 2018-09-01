package foadkhezri.github.com.earthquakereport;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarthquakeAdapter extends ArrayAdapter {

    private static final String LOCATION_SEPARATOR = " of ";
    private static String locationOffset = "";
    public EarthquakeAdapter(EarthquakeActivity context, ArrayList<Earthquake> earthQuake) {
        super(context, 0, earthQuake);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Typeface ty1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/bzar.ttf");

        Earthquake currentEarthquake = (Earthquake) getItem(position);

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        TextView magnitude = listItemView.findViewById(R.id.magnitude);
        magnitude.setTypeface(ty1);

        assert currentEarthquake != null;
        DecimalFormat formatter = new DecimalFormat("0.0");
        String output = formatter.format(currentEarthquake.getMagnitude());
        magnitude.setText(output);
        if (currentEarthquake.getMagnitude() >= 0.0 && currentEarthquake.getMagnitude() <= 3.9)
            magnitude.setTextColor(listItemView.getResources().getColor((R.color.green)));
        else if (currentEarthquake.getMagnitude() >= 4.0 && currentEarthquake.getMagnitude() <= 4.9)
            magnitude.setTextColor(listItemView.getResources().getColor((R.color.lime)));
        else if (currentEarthquake.getMagnitude() >= 5.0 && currentEarthquake.getMagnitude() <= 5.9)
            magnitude.setTextColor(listItemView.getResources().getColor((R.color.yellow)));
        else if (currentEarthquake.getMagnitude() >= 6.0 && currentEarthquake.getMagnitude() <= 6.9)
            magnitude.setTextColor(listItemView.getResources().getColor((R.color.orange)));
        else
            magnitude.setTextColor(listItemView.getResources().getColor((R.color.red)));


        String originalLocation = currentEarthquake.getLocation();
        String primaryLocation;
        String finalPrimaryLocation = "";

        if (originalLocation.contains(LOCATION_SEPARATOR)) {
            // Split the string into different parts (as an array of Strings)
            // based on the " of " text. We expect an array of 2 Strings, where
            // the first String will be "5km N" and the second String will be "Cairo, Egypt".
            String[] partsA = originalLocation.split(LOCATION_SEPARATOR);
            // Location offset should be "5km N " + " of " --> "5km N of"
            locationOffset = partsA[0] + LOCATION_SEPARATOR;
            // Primary location should be "Cairo, Egypt"
            primaryLocation = partsA[1];
            if (primaryLocation.contains(", Iran")) {
                String[] partsB = primaryLocation.split(", Iran");
                finalPrimaryLocation = partsB[0];
            }
        } else {
            // Otherwise, there is no " of " text in the originalLocation string.
            // Hence, set the default location offset to say "Near the".
            // The primary location will be the full location string "Pacific-Antarctic Ridge".
            primaryLocation = originalLocation;
            if (primaryLocation.contains(", Iran")) {
                String[] partsB = primaryLocation.split(", Iran");
                finalPrimaryLocation = partsB[0];
            }
        }


        locationOffset = locationOffset.replace("km", " کیلومتری");
        locationOffset = locationOffset.replace("of", "");


        if (locationOffset.contains("NNE") || locationOffset.contains("NNW") || locationOffset.contains("WNW") || locationOffset.contains("WSW")
                || locationOffset.contains("SSW") || locationOffset.contains("SSE") || locationOffset.contains("ESE") || locationOffset.contains("ENE"))
            intermediateLocation(locationOffset);

        if (locationOffset.contains("NE") || locationOffset.contains("NW") || locationOffset.contains("SW") || locationOffset.contains("SE"))
            lowLocation(locationOffset);

        if (locationOffset.contains("N") || locationOffset.contains("W") || locationOffset.contains("S") || locationOffset.contains("E"))
            tinyLocation(locationOffset);


        TextView primaryView = listItemView.findViewById(R.id.location_primary);

        primaryView.setText(finalPrimaryLocation);

        TextView offsetView = listItemView.findViewById(R.id.location_offset);
        offsetView.setTypeface(ty1);
        offsetView.setText(locationOffset);

        TextView timeTextView = listItemView.findViewById(R.id.time);
        TextView dateTextView = listItemView.findViewById(R.id.date);
        Date dateObject = new Date(currentEarthquake.getDate());
        String formattedDate =  formatDate(dateObject);
        String formattedTime = formatTime(dateObject);


        int month;


        if (formattedDate.contains("Jan"))
            month = 1;
        else if (formattedDate.contains("Feb"))
            month = 2;
        else if (formattedDate.contains("March"))
            month = 3;
        else if (formattedDate.contains("Apr"))
            month = 4;
        else if (formattedDate.contains("May"))
            month = 5;
        else if (formattedDate.contains("Jun"))
            month = 6;
        else if (formattedDate.contains("Jul"))
            month = 7;
        else if (formattedDate.contains("Aug"))
            month = 8;
        else if (formattedDate.contains("Sep"))
            month = 9;
        else if (formattedDate.contains("Oct"))
            month = 10;
        else if (formattedDate.contains("Nov"))
            month = 11;
        else
            month = 12;


        int day = Integer.valueOf(formattedDate.substring(4, 6));

        int year = Integer.valueOf(formattedDate.substring(8,12));

        int [] result = gregorian_to_jalali(year, month, day);

        formattedDate = String.valueOf(result[0]) + " / " + String.valueOf(result[1]) + " / " + String.valueOf(result[2]);

        if (formattedTime.contains("AM"))
            formattedTime = formattedTime.replace("AM", "");
        else
            formattedTime = formattedTime.replace("PM", "");

        dateTextView.setTypeface(ty1);
        timeTextView.setTypeface(ty1);

        dateTextView.setText(formattedDate);
        timeTextView.setText(formattedTime);

        return listItemView;
    }

    private void tinyLocation(String loc) {
        if (loc.contains("N"))
            locationOffset = locationOffset.replace("N", " شمال ");
        else if (loc.contains("S"))
            locationOffset = locationOffset.replace("S", " جنوب ");
        else if (loc.contains("E"))
            locationOffset = locationOffset.replace("E", " شرق ");
        else if (loc.contains("W"))
            locationOffset = locationOffset.replace("W", " غرب ");
    }

    private void lowLocation(String loc) {
        if (loc.contains("NW"))
            locationOffset = locationOffset.replace("NW", " شمال غربی ");
        else if (loc.contains("SW"))
            locationOffset = locationOffset.replace("SW", " جنوب غربی ");
        else if (loc.contains("SE"))
            locationOffset = locationOffset.replace("SE", " جنوب شرقی ");
        else if (loc.contains("NE"))
            locationOffset = locationOffset.replace("NE", " شمال شرقی ");
    }

    private void intermediateLocation(String loc) {
        if (loc.contains("NNE"))
            locationOffset = locationOffset.replace("NNE", "شمال شرقی");
        else if (loc.contains("NNW"))
            locationOffset = locationOffset.replace("NNW", "شمال غربی");
        else if (loc.contains("WNW"))
            locationOffset = locationOffset.replace("WNW", "شمال غربی");
        else if (loc.contains("WSW"))
            locationOffset = locationOffset.replace("WSW", "جنوب غربی");
        else if (loc.contains("SSW"))
            locationOffset = locationOffset.replace("SSW", "جنوب غربی");
        else if (loc.contains("SSE"))
            locationOffset = locationOffset.replace("SSE", "جنوب شرقی");
        else if (loc.contains("ESE"))
            locationOffset = locationOffset.replace("ESE", "جنوب شرقی");
        else
            locationOffset = locationOffset.replace("ENE", "شمال شرقی");
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm a");
        return timeFormat.format(dateObject);
    }
    public static int[] gregorian_to_jalali(int gy, int gm, int gd) {
        int[] g_d_m = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
        int jy;
        if (gy > 1600) {
            jy = 979;
            gy -= 1600;
        } else {
            jy = 0;
            gy -= 621;
        }
        int gy2 = (gm > 2) ? (gy + 1) : gy;
        int days = (365 * gy) + ((int) ((gy2 + 3) / 4)) - ((int) ((gy2 + 99) / 100)) + ((int) ((gy2 + 399) / 400)) - 80 + gd + g_d_m[gm - 1];
        jy += 33 * ((int) (days / 12053));
        days %= 12053;
        jy += 4 * ((int) (days / 1461));
        days %= 1461;
        if (days > 365) {
            jy += (int) ((days - 1) / 365);
            days = (days - 1) % 365;
        }
        int jm = (days < 186) ? 1 + (int) (days / 31) : 7 + (int) ((days - 186) / 30);
        int jd = 1 + ((days < 186) ? (days % 31) : ((days - 186) % 30));
        int[] out = {jy, jm, jd};
        return out;
    }
}