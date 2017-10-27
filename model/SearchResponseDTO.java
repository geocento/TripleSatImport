package com.geocento.earthimages.triplesatimport.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponseDTO
{

    @SerializedName("status")
    public String status;
    @SerializedName("data")
    public Data data;
    @SerializedName("msg")
    public String msg;
    @SerializedName("row")
    public List<Field> row;

    public static class RollAngle
    {
        @SerializedName("min")
        public String min;
        @SerializedName("max")
        public String max;
    }

    public static class CloudCover
    {
        @SerializedName("min")
        public String min;
        @SerializedName("max")
        public String max;
    }

    public static class CenterTime
    {
        @SerializedName("min")
        public String min;
        @SerializedName("max")
        public String max;
    }

    public static class Condition
    {
        @SerializedName("satellite")
        public List<String> satellite;
        @SerializedName("rollangle")
        public RollAngle rollangle;
        @SerializedName("cloudcover")
        public CloudCover cloudcover;
        @SerializedName("centertime")
        public CenterTime centertime;
    }

    public static class Root
    {
        @SerializedName("id")
        public String id;
        @SerializedName("fieldType")
        public String fieldType;
        @SerializedName("value")
        public String value;
        @SerializedName("showType")
        public String showType;
    }

    public static class Param
    {
        @SerializedName("cql_filter")
        public String cql_filter;
        @SerializedName("bbox")
        public String bbox;
        @SerializedName("layers")
        public String layers;
        @SerializedName("format")
        public String format;
    }

    public static class GeomCover
    {
        @SerializedName("param")
        public Param param;
        @SerializedName("type")
        public String type;
        @SerializedName("url")
        public String url;
    }

    public static class Data
    {
        @SerializedName("ndateTwo")
        public String ndateTwo;
        @SerializedName("ndateOne")
        public String ndateOne;
        @SerializedName("count")
        public int count;
        @SerializedName("condition")
        public Condition condition;
        @SerializedName("root")
        public List<Root> root;
        @SerializedName("pageNo")
        public int pageNo;
        @SerializedName("geomCover")
        public GeomCover geomCover;
        @SerializedName("pageSize")
        public int pageSize;
        @SerializedName("exportWKT")
        public String exportWKT;
        @SerializedName("rows")
        public List<List<Field>> rows;
    }

    public static class Field
    {
        @SerializedName("id")
        public String id;
        @SerializedName("title")
        public String title;
        @SerializedName("fieldType")
        public String fieldType;
        @SerializedName("value")
        public String value;
        @SerializedName("showType")
        public String showType;
    }
}
