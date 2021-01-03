package org.whitehat.httpworkshop;

public class Apprentice {
    private String name;
    private String[] guests;

    public Apprentice() {
    }

    public Apprentice(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setGuests(String[] guests) {
        this.guests = guests;
    }

    public String[] getGuests() {
        return this.guests;
    }
}
