package com.example.carspotter.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

public class Spot implements Parcelable {

        private int spot_id;
        private int car_id;
        private String location;
        private String date;
        private String image;
        private Bitmap decodedImage;

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
            try{
                spot_id = o.getInt("spot_id");
                car_id = o.getInt("car_id");
                date = o.getString("date");
                location = o.getString("location");
                image = o.getString("image");

                byte[] byteImage = Base64.decode(image, Base64.DEFAULT);
                decodedImage = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);

            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        public Spot(Parcel in){

            this.spot_id = in.readInt();
            this.car_id = in.readInt();
            this.date = in.readString();
            this.location = in.readString();

        }

        public int getSpot_id() {
            return spot_id;
        }

        public int getCar_id() {
            return car_id;
        }

        public String getLocation(){ return location;}

        public String getDate(){ return date; }

        public String getImage() { return image; }

        public Bitmap getDecodedImage() { return decodedImage; }

        @Override
        public String toString() {
            return spot_id+car_id+date+location+image;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(spot_id);
            parcel.writeInt(car_id);
            parcel.writeString(date);
            parcel.writeString(location);
        }
    }
