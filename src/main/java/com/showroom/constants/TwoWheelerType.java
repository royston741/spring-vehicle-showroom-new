package com.showroom.constants;

public enum TwoWheelerType {
    SPORTS(5),
    SCOOTY(10);

    private double discount;

    TwoWheelerType(double discount) {
        this.discount = discount;
    }

    public double getDiscountedPrice(double price) {
        return price * (this.discount / 100);
    }
    public double getDiscount() {
        return discount;
    }

}
