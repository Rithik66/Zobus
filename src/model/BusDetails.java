package model;

public class BusDetails {
    private int id;
    private String name;
    private String owner;
    private int price;
    private int totalseats;
    private int type;

    public BusDetails(){}

    public BusDetails(int id, String name, String owner, int price, int totalseats,int type) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.price = price;
        this.totalseats = totalseats;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getTotalseats() {
        return totalseats;
    }

    public void setTotalseats(int totalseats) {
        this.totalseats = totalseats;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

