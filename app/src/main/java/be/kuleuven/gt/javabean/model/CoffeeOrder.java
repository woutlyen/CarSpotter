package be.kuleuven.gt.javabean.model;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CoffeeOrder implements Parcelable {
    private String name;
    private String coffee;
    private boolean sugar;
    private boolean whipCream;
    private int quantity;
    private String dateDue;
    public static final Parcelable.Creator<CoffeeOrder> CREATOR = new Creator<CoffeeOrder>() {
        @Override
        public CoffeeOrder createFromParcel(Parcel in) {
            return new CoffeeOrder(in);
        }

        @Override
        public CoffeeOrder[] newArray(int size) {
            return new CoffeeOrder[size];
        }
    };

    public CoffeeOrder(String name, String coffee, boolean sugar,
                       boolean whipCream, int quantity) {
        this.name = name;
        this.coffee = coffee;
        this.sugar = sugar;
        this.whipCream = whipCream;
        this.quantity = quantity;
    }

    public CoffeeOrder(JSONObject o) {
        try {
            name = o.getString("customer");
            coffee = o.getString("coffee");
            String toppings = o.getString("toppings");
            sugar = toppings.contains("sugar");
            whipCream = toppings.contains("cream");
            quantity = o.getInt("quantity");
            dateDue = o.getString("date_due"); // yyyy-mm-dd hh:mm
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public CoffeeOrder(Parcel in) {
        this(
                in.readString(),
                in.readString(),
                Boolean.parseBoolean(in.readString()),
                in.readByte() == 1,
                in.readInt()
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(coffee);
// workaround 1 for writeBoolean
        parcel.writeString(Boolean.valueOf(sugar).toString());
// workaround 2 for writeBoolean
        parcel.writeByte(whipCream ? (byte) 1 : (byte) 0);
        parcel.writeInt(quantity);
    }
    @Override
    public String toString(){
        return "CoffeeOrder{name="+getName()+", coffee="+getCoffee()+", sugar="+isSugar()+
                ", whipCream="+isWhipCream()+", quantity="+getQuantity()+"}";
    }

    public String getName() {
        return name;
    }

    public String getCoffee() {
        return coffee;
    }

    public boolean isSugar() {
        return sugar;
    }

    public boolean isWhipCream() {
        return whipCream;
    }

    public int getQuantity() {
        return quantity;
    }

    public Map<String, String> getPostParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("customer", name);
        params.put("coffee", coffee);
        params.put("toppings", getToppingsURL());
        params.put("quantity", String.valueOf(quantity));
        return params;
    }
    private String getToppingsURL() {
        if (sugar || whipCream) {
            return (sugar ? "+sugar" : "") + (whipCream ? "+cream" : "");
        } else {
            return "-";
        }
    }
}