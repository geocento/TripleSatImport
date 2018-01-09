package com.geocento.earthimages.triplesatimport;

import com.geocento.earthimages.triplesatimport.model.SearchRequestDTO;
import com.geocento.earthimages.triplesatimport.model.SearchResponseDTO;
import com.google.gson.Gson;
import com.metaaps.webapps.libraries.server.ServerUtil;

import java.io.IOException;
import java.util.ArrayList;

public class TripleSatImporter
{
    SearchRequestDTO searchRequestDTO;
    String url;

    public TripleSatImporter(String startDate, String endDate, String url)
    {

        int pageSize = 1000;
        this.url = url;
        searchRequestDTO = new SearchRequestDTO();

        searchRequestDTO.condition = new ArrayList<SearchRequestDTO.Condition>();

        SearchRequestDTO.Condition startDateCondition = new SearchRequestDTO.Condition();
        startDateCondition.id = "centertime";
        startDateCondition.name = "startDate";
        startDateCondition.val = startDate;
        startDateCondition.datatype = "date";
        startDateCondition.relation = "greater_equal";
        searchRequestDTO.condition.add(startDateCondition);

        SearchRequestDTO.Condition endDateCondition = new SearchRequestDTO.Condition();
        endDateCondition.id = "centertime";
        endDateCondition.name = "endDate";
        endDateCondition.val = endDate;
        endDateCondition.datatype = "date";
        endDateCondition.relation = "less_equal";
        searchRequestDTO.condition.add(endDateCondition);

        searchRequestDTO.orderby = new ArrayList<SearchRequestDTO.Orderby>();

        SearchRequestDTO.Orderby orderbyTime = new SearchRequestDTO.Orderby();
        orderbyTime.id = "centertime";
        orderbyTime.by = "desc";
        searchRequestDTO.orderby.add(orderbyTime);

        searchRequestDTO.page = new SearchRequestDTO.Page();
        searchRequestDTO.page.pageSize = pageSize + "";

    }

    public void searchAndSave(String file)
    {
        TripleSatShpSaver tripleSatShpSaver = null;
        Gson gson = new Gson();

        try
        {
            tripleSatShpSaver = new TripleSatShpSaver(file);
        }
        catch (IOException e)
        {
            System.err.println("Error creating shapefile: " + e.getMessage());
            System.exit(1);
        }

        //String url = "http://192.168.2.112:8008/dataSync";
        if(url==null)
            url = "https://discover.21at.net/ATCenter/Service/dataSync";

        String response = null;

        try
        {
            int pageNumber = 0;
            int total = 0;
            do
            {
                pageNumber++;
                searchRequestDTO.page.pageNo = pageNumber + "";
                response = ServerUtil.postUrlData(url, createParameters());
                SearchResponseDTO searchResponseDTO = gson.fromJson(response, SearchResponseDTO.class);
                if(searchResponseDTO.data != null)
                {
                    total = searchResponseDTO.data.count;
                    System.out.print("Sending request " + pageNumber);
                    System.out.println(" of " + (int) Math.floor(total / Integer.valueOf(searchRequestDTO.page.pageSize)));
                    tripleSatShpSaver.addFeatures(searchResponseDTO.data);
                }
                else
                {
                    System.out.println("No data response, possible reason: " + searchResponseDTO.msg);
                }

            }while ((pageNumber * Integer.valueOf(searchRequestDTO.page.pageSize)) < total);

            System.out.println("Saving features to file...");
            tripleSatShpSaver.saveFeatures();
        }
        catch (Exception e)
        {
            System.err.println("Not very expected error: " + e.getMessage());
            System.exit(1);
        }

    }

    private String createParameters()
    {
        Gson gson = new Gson();
        return "Version=1.2" +
                "&Action=search_v_1_1" +
                "&Method=search" +
                "&AppKey=ZWY5ODA4OWItYTRkZS00Mzk3LWIwYjAtMmNmNWE3Y2JhMTA5" +
                "&Req=" + gson.toJson(searchRequestDTO) +
                "&TimeStamp=10000" +
                "&Signature=geocento"+
                "Language=en";
    }
}
