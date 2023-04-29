package com.example.carspotter.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

public class Spot implements Parcelable {

        private int id;
        private String location;
        private String date;
        private String image;
        private Bitmap decodedImage;

        public static final Creator<com.example.carspotter.model.Spot> CREATOR = new Creator<com.example.carspotter.model.Spot>() {
            @Override
            public com.example.carspotter.model.Spot createFromParcel(Parcel in) {
                return new com.example.carspotter.model.Spot(in);
            }

            @Override
            public com.example.carspotter.model.Spot[] newArray(int size) {
                return new com.example.carspotter.model.Spot[size];
            }
        };

        public Spot(JSONObject o) throws JSONException {
            try{
                id = o.getInt("id");
                location = o.getString("location");
                date = o.getString("date");
                image = o.getString("image");

                byte[] byteImage = Base64.decode(image, Base64.DEFAULT);
                decodedImage = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);

            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        public Spot(Parcel in){

            this.id = in.readInt();
            this.location = in.readString();
            this.date = in.readString();

        }

        public int getId() {
            return id;
        }

        public String getLocation(){ return location;}

        public String getDate(){ return date; }

        public String getImage() { return image; }

        public Bitmap getDecodedImage() { return decodedImage; }

        @Override
        public String toString() {
            return id+location+date+image;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(id);
            parcel.writeString(location);
            parcel.writeString(date);
        }
    }
