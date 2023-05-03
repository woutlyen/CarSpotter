package com.example.carspotter.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

public class Event implements Parcelable {

    private int id;
    private String name;
    private String description;
    private String date;
    private String start_hour;
    private String end_hour;
    private String type;
    private int fee;
    private String image;
    private Bitmap decodedImage;

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public Event(JSONObject o) throws JSONException {
        try{
            id = o.getInt("id");
            name = o.getString("name");
            description = o.getString("description");
            date = o.getString("date");
            start_hour = o.getString("start_hour");
            end_hour = o.getString("end_hour");
            type = o.getString("type");
            fee = o.getInt("fee");
            image = o.getString("image");

            byte[] byteImage = Base64.decode(image, Base64.DEFAULT);
            decodedImage = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);

        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public Event(Parcel in){

        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.date = in.readString();
        this.start_hour = in.readString();
        this.end_hour = in.readString();
        this.type = in.readString();
        this.fee = in.readInt();
        this.image = in.readString();

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getStart_hour() {
        return start_hour;
    }

    public String getEnd_hour() {
        return end_hour;
    }

    public String getType() {
        return type;
    }

    public int getFee() {
        return fee;
    }

    public String getImage() {
        return image;
    }

    public Bitmap getDecodedImage() {
        return decodedImage;
    }

    @Override
    public String toString() {
        return id+name+description+date+start_hour+end_hour+type+fee+image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(date);
        parcel.writeString(start_hour);
        parcel.writeString(end_hour);
        parcel.writeString(type);
        parcel.writeInt(fee);
    }
}
