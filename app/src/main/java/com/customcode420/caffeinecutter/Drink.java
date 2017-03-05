package com.customcode420.caffeinecutter;


import java.io.Serializable;

public class Drink implements Serializable{

    private String drinkId;
    private String drinkName;
    private Integer cafContent;
    private int id;

    public Drink() {
        super();
    }

    public Drink(String drinkId, String drinkName) {
        this.drinkId = drinkId;
        this.drinkName = drinkName;
    }

    public Drink(int id, String drinkId, String drinkName, Integer cafContent) {
        this.drinkId = drinkId;
        this.drinkName = drinkName;
        this.cafContent = cafContent;
        this.id = id;
    }

    public String getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(String drinkId) {
        this.drinkId = drinkId;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public Integer getCafContent() {
        return cafContent;
    }

    public void setCafContent(Integer cafContent) {
        this.cafContent = cafContent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Drink other = (Drink) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
