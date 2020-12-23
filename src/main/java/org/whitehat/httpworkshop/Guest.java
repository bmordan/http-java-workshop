package org.whitehat.httpworkshop;

public class Guest {
    private String[] guests;

    public void setGuests(String guests) {
        this.guests = guests.split(",\s*");
    }

    public String[] getGuests() {
        return this.guests;
    }
}
