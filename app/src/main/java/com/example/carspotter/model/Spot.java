package com.example.carspotter.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Spot implements Parcelable {

    private int spot_id;
    private int car_id;
    private String location;
    private String date;
    private String image;
    private Bitmap decodedImage;
    private String lat;
    private String lng;
    private String username;
    private String brand;
    private String model;
    private String edition;

    public static final Creator<Spot> CREATOR = new Creator<Spot>() {
        @Override
        public Spot createFromParcel(Parcel in) {
            return new Spot(in);
        }

        @Override
        public Spot[] newArray(int size) {
            return new Spot[size];
        }
    };

    public Spot(JSONObject o) throws JSONException {
        try {
            spot_id = o.getInt("spot_id");
            car_id = o.getInt("car_id");
            location = o.getString("location");
            date = o.getString("date");
            image = o.getString("image");
            lat = o.getString("lat");
            lng = o.getString("lng");
            username = o.getString("username");
            brand = o.getString("brand");
            model = o.getString("model");
            edition = o.getString("edition");

            String inputDateStr = date;
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            // SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd MMMM yyyy \n'at' HH:mm", Locale.getDefault());
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd MMMM yyyy 'at' HH:mm", Locale.getDefault());
            String outputDateStr = "";
            try {
                Date inputDate = inputDateFormat.parse(inputDateStr);
                date = outputDateFormat.format(inputDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            byte[] byteImage = Base64.decode(image, Base64.DEFAULT);
            decodedImage = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Spot(Parcel in) {

        this.spot_id = in.readInt();
        this.car_id = in.readInt();
        this.location = in.readString();
        this.date = in.readString();
        this.lat = in.readString();
        this.lng = in.readString();
        this.username = in.readString();
        this.brand = in.readString();
        this.model = in.readString();
        this.edition = in.readString();

    }

    public int getSpot_id() {
        return spot_id;
    }

    public int getCar_id() {
        return car_id;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public Bitmap getDecodedImage() {
        return decodedImage;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public LatLng getLatLng() {
        return new LatLng(Double.valueOf(lat), Double.valueOf(lng));
    }

    public String getUsername() {
        return username;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getEdition() {
        return edition;
    }

    @Override
    public String toString() {
        return spot_id + car_id + location + date + image + lat + lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(spot_id);
        parcel.writeInt(car_id);
        parcel.writeString(location);
        parcel.writeString(date);
        parcel.writeString(lat);
        parcel.writeString(lng);
        parcel.writeString(username);
        parcel.writeString(brand);
        parcel.writeString(model);
        parcel.writeString(edition);
    }
}
