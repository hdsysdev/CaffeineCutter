package com.customcode420.caffeinecutter;


import java.sql.Date;

import io.realm.RealmObject;

public class History  extends RealmObject{
    private String name;
    private Float cafContent;
    private String time;

    public String getName(){return name;}
    public void setName(String name){this.name = name;}
    public Float getCafContent(){return cafContent;}
    public void setCafContent(Float cafContent){this.cafContent = cafContent;}

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
