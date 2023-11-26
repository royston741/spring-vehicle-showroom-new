package com.showroom.constants;

public enum FuelType {
    PETROL(20),
    CNG(10);
    private double discount;

    FuelType(double discount) {
        this.discount=discount;
    }

    public double getDiscountedPrice(double price){
        return price * (this.discount / 100);
    }

    public double getDiscount() {
        return discount;
    }

}
