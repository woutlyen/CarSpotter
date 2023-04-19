package be.kuleuven.gt.javabean.model;
import android.os.Parcel;
import android.os.Parcelable;

public class CoffeeOrder implements Parcelable {
    private String name;
    private String coffee;
    private boolean sugar;
    private boolean whipCream;
    private int quantity;
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
}