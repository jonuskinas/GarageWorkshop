package com.project.garageworkshop;

public class Repairs {
    String userId;
    String carNumb;
    String repaired;
    double cost;

    public Repairs(String userId, String carNumb, String repaired, double cost) {
        this.userId = userId;
        this.carNumb = carNumb;
        this.repaired = repaired;
        this.cost = cost;
    }

    public Repairs() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCarNumb() {
        return carNumb;
    }

    public void setCarNumb(String carNumb) {
        this.carNumb = carNumb;
    }

    public String getRepaired() {
        return repaired;
    }

    public void setRepaired(String repaired) {
        this.repaired = repaired;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
