package com.geocento.earthimages.triplesatimport.model;

import java.util.List;

public class SearchRequestDTO
{

    @com.google.gson.annotations.SerializedName("condition")
    public List<Condition> condition;
    @com.google.gson.annotations.SerializedName("orderby")
    public List<Orderby> orderby;
    @com.google.gson.annotations.SerializedName("page")
    public Page page;

    public static class Condition
    {
        @com.google.gson.annotations.SerializedName("id")
        public String id;
        @com.google.gson.annotations.SerializedName("name")
        public String name;
        @com.google.gson.annotations.SerializedName("val")
        public String val;
        @com.google.gson.annotations.SerializedName("datatype")
        public String datatype;
        @com.google.gson.annotations.SerializedName("relation")
        public String relation;
    }

    public static class Orderby
    {
        @com.google.gson.annotations.SerializedName("id")
        public String id;
        @com.google.gson.annotations.SerializedName("by")
        public String by;
    }

    public static class Page
    {
        @com.google.gson.annotations.SerializedName("pageNo")
        public String pageNo;
        @com.google.gson.annotations.SerializedName("pageSize")
        public String pageSize;
    }
}
