package com.example.sqlrecyclerview;

public class RowItemList {
    private int id;
    private String name;
    private String url;
    public RowItemList(String name,String url,int id){
        this.name = name;
        this.url = url;
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
