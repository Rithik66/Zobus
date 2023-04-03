package model;

public class Bus {
    String bus_id;
    String bus_seat;
    int bus_seat_avail;

    public Bus(String bus_id, String bus_seat, int bus_seat_avail) {
        this.bus_id = bus_id;
        this.bus_seat = bus_seat;
        this.bus_seat_avail = bus_seat_avail;
    }

    public String getBus_id() {
        return bus_id;
    }

    public void setBus_id(String bus_id) {
        this.bus_id = bus_id;
    }

    public String getBus_seat() {
        return bus_seat;
    }

    public void setBus_seat(String bus_seat) {
        this.bus_seat = bus_seat;
    }

    public int getBus_seat_avail() {
        return bus_seat_avail;
    }

    public void setBus_seat_avail(int bus_seat_avail) {
        this.bus_seat_avail = bus_seat_avail;
    }
}
