package com.showroom.constants;

public enum Color {
    PINK(9),
    BLACK(8),
    RED(7);
    private double charges;
    Color(double charges) {
        this.charges = charges;
    }
    public double getAdditionalCharges(double price){
        return price * (this.charges / 100);
    }
    public double getCharges() {
        return charges;
    }

}
