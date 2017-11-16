package com.geocento.earthimages.triplesatimport;

public class Main {

    public static void main(String[] args) {

        if(args.length < 3)
        {
            System.out.println("Three arguments required (start date, end date, output file)");
            System.out.println("Url optional ()");
            System.out.println("Example: 2017-01-01 2017-11-01 /root/TripleSatImport/shapefile.shp");
            System.exit(1);
        }

        String startDate = args[0];
        String endDate = args[1];
        String filePath = args[2];

        String url = null;
        if(args.length == 4)
            url = args[4];

        TripleSatImporter tripleSatImporter = new TripleSatImporter(startDate, endDate, url);
        tripleSatImporter.searchAndSave(filePath);

        System.out.println("Shapefile " + filePath + " successfully created.");
        System.exit(0);
    }

}
