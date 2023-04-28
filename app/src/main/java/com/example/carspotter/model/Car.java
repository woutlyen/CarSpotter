package com.example.carspotter.model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;


public class Car {

    private int id;
    private String brand;
    private String model;
    private String type;
    private String enginetype;
    private int msrp;
    private String start_build;
    private String end_build;
    private String seats;
    private String image;
    private Bitmap decodedImage;


    public Car(JSONObject o) throws JSONException {
        try{
            id = o.getInt("id");
            brand = o.getString("brand");
            model = o.getString("model");
            type = o.getString("type");
            enginetype = o.getString("enginetype");
            msrp = o.getInt("msrp");
            start_build = o.getString("start_build");
            end_build = o.getString("end_build");
            seats = o.getString("seats");
            image = o.getString("image");

            byte[] byteImage = Base64.decode(image, Base64.DEFAULT);
            decodedImage = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);

        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getType() {
        return type;
    }

    public String getEnginetype() {
        return enginetype;
    }

    public int getMsrp() {
        return msrp;
    }

    public String getStart_build() {
        return start_build;
    }

    public String getEnd_build() {
        return end_build;
    }

    public String getSeats() {
        return seats;
    }

    public String getImage() {
        return image;
    }

    public Bitmap getDecodedImage() {
        return decodedImage;
    }

    @Override
    public String toString() {
        return id+brand+model+type+enginetype+msrp+start_build+end_build+seats+image;
    }
}
