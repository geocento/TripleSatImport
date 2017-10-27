package com.geocento.earthimages.triplesatimport;

import com.geocento.earthimages.triplesatimport.model.SearchRequestDTO;
import com.geocento.earthimages.triplesatimport.model.SearchResponseDTO;
import com.google.gson.Gson;
import com.metaaps.webapps.libraries.client.map.EOLatLng;
import com.metaaps.webapps.libraries.server.ServerUtil;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.ParseException;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.WKTReader2;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        Gson gson = new Gson();

        SearchRequestDTO searchRequestDTO = new SearchRequestDTO();
        searchRequestDTO.condition = new ArrayList<SearchRequestDTO.Condition>();

        SearchRequestDTO.Condition startDateCondition = new SearchRequestDTO.Condition();
        startDateCondition.id = "centertime";
        startDateCondition.name = "startDate";
        startDateCondition.val = "2016-01-13";
        startDateCondition.datatype = "date";
        startDateCondition.relation = "greater_equal";
        searchRequestDTO.condition.add(startDateCondition);

        SearchRequestDTO.Condition endDateCondition = new SearchRequestDTO.Condition();
        endDateCondition.id = "centertime";
        endDateCondition.name = "endDate";
        endDateCondition.val = "2016-10-13";
        endDateCondition.datatype = "date";
        endDateCondition.relation = "less_equal";
        searchRequestDTO.condition.add(endDateCondition);

        searchRequestDTO.orderby = new ArrayList<SearchRequestDTO.Orderby>();

        SearchRequestDTO.Orderby orderbyTime = new SearchRequestDTO.Orderby();
        orderbyTime.id = "centertime";
        orderbyTime.by = "desc";
        searchRequestDTO.orderby.add(orderbyTime);

        searchRequestDTO.page = new SearchRequestDTO.Page();
        searchRequestDTO.page.pageNo = "1";
        searchRequestDTO.page.pageSize = "100";

        String url = "http://192.168.2.112:8008/dataSync";
        String parameters = "Version=1.2" +
                "&Action=search_v_1_1" +
                "&Method=search" +
                "&AppKey=ZWY5ODA4OWItYTRkZS00Mzk3LWIwYjAtMmNmNWE3Y2JhMTA5" +
                "&Req=" + gson.toJson(searchRequestDTO) +
                "&TimeStamp=10000" +
                "&Signature=geocento"+
                "Language=en";


        String response = null;

        try
        {
            response = ServerUtil.postUrlData(url, parameters);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        SearchResponseDTO searchResponseDTO = gson.fromJson(response, SearchResponseDTO.class);

        try
        {
            /*
         * We use the DataUtilities class to create a FeatureType that will describe the data in our
         * shapefile.
         *
         * See also the createFeatureType method below for another, more flexible approach.
         */
         /*   final SimpleFeatureType TYPE = DataUtilities.createType("Location",
                    "shape:Polygon:srid=4326," + // <- the geometry attribute: Polygon type
                            "id:String," +
                            "transform:String," +
                            "satellite:String," +
                            "thumbimg:String," +
                            "rollangle:String," +
                            "cloudcover:Double," +
                            "wkt:String," +
                            "browserimg:String," +
                            "resolution:String," +
                            "centertime:String"
            );*/

             /*
         * A list to collect features as we create them.
         */
          //  List<SimpleFeature> features = new ArrayList<>();
        /*
         * GeometryFactory will be used to create the geometry attribute of each feature (a Point
         * object for the location)
         */
            //GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);

          //  SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);

            SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
            builder.setName("TripleSat");
            builder.setNamespaceURI("http://earthimages.geocento.com");
            try {
                CoordinateReferenceSystem crs = CRS.decode("EPSG:4326", true);
                builder.setCRS(crs);
            } catch (Exception e) {
                builder.setSRS("EPSG:4326");
            }
            builder.add("id", String.class);
            builder.add("transform", String.class);
            builder.add("satellite", String.class);
            builder.add("geometry", Polygon.class);
            builder.add("thumbimg", String.class);
            builder.add("rollangle", String.class);
            builder.add("cloudcover", Double.class);
            builder.add("browserimg", String.class);
            builder.add("resolution", String.class);
            builder.add("centertime", String.class);
            builder.add("wkt", String.class);


            SimpleFeatureType featureType = builder.buildFeatureType();
            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
            GeometryFactory gf = new GeometryFactory();
            DefaultFeatureCollection features = new DefaultFeatureCollection(null, featureType);

            int index=0;

            for (List<SearchResponseDTO.Field> row : searchResponseDTO.data.rows)
            {
                Polygon shape = null;
                String id = null;
                String transform = null;
                String satellite = null;
                String thumbimg = null;
                String rollangle = null;
                String cloudcover = null;
                String wkt = null;
                String browserimg = null;
                String resolution = null;
                String centertime = null;


                for (SearchResponseDTO.Field field : row)
                {
                    if (field.id.equals("fgeometry"))
                    {
                        //shape = (Polygon) new WKTReader2().read(field.value);
                        String coordinates = field.value.toUpperCase().replace("POLYGON","").replace("((", "")
                                .replace("))","");
                        LinearRing contour = gf.createLinearRing(transformToCoordinates(EOLatLng.parseWKT(coordinates)));
                        shape = gf.createPolygon(contour, null);
                    }
                    if (field.id.equals("id"))
                        id = field.value;
                    if (field.id.equals("transformimg"))
                        transform = field.value;
                    if (field.id.equals("satellite"))
                        satellite = field.value;
                    if (field.id.equals("thumbimg"))
                        thumbimg = field.value;
                    if (field.id.equals("rollangle"))
                        rollangle = field.value;
                    if (field.id.equals("cloudcover"))
                        cloudcover = field.value;
                    if (field.id.equals("fgeometry"))
                        wkt = field.value;
                    if (field.id.equals("browserimg"))
                        browserimg = field.value;
                    //if (field.id.equals("resolution"))
                        resolution = "1.4";//field.value;
                    if (field.id.equals("centertime"))
                        centertime = field.value;
                }

                featureBuilder.set("geometry", shape);
                featureBuilder.set("id", id);
                featureBuilder.set("transform", transform);
                featureBuilder.set("satellite", satellite);
                featureBuilder.set("thumbimg", thumbimg);
                featureBuilder.set("rollangle", rollangle);
                featureBuilder.set("cloudcover", cloudcover);
                featureBuilder.set("wkt", wkt);
                featureBuilder.set("browserimg", browserimg);
                featureBuilder.set("resolution", resolution);
                featureBuilder.set("centertime", centertime);

                SimpleFeature feature = featureBuilder.buildFeature(index + "");
                index++;
                features.add(feature);
            }

              /*
         * Get an output file name and create the new shapefile
         */
            File newFile = new File("/home/rmg/IdeaProjects/TripleSatImport/shapefile.shp");

            /*ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
            Map<String, Serializable> shapeParams = new HashMap<>();
            shapeParams.put("url", newFile.toURI().toURL());
            shapeParams.put("create spatial index", Boolean.TRUE);
            ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(shapeParams);
            newDataStore.createSchema(featureType);
            Transaction transaction = new DefaultTransaction("create");
            String typeName = newDataStore.getTypeNames()[0];
            SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);
            SimpleFeatureType SHAPE_TYPE = featureSource.getSchema();*/
        /*
         * The Shapefile format has a couple limitations:
         * - "the_geom" is always first, and used for the geometry attribute name
         * - "the_geom" must be of type Point, MultiPoint, MuiltiLineString, MultiPolygon
         * - Attribute names are limited in length
         * - Not all data types are supported (example Timestamp represented as Date)
         *
         * Each data store has different limitations so check the resulting SimpleFeatureType.
         */
            /*System.out.println("SHAPE:"+SHAPE_TYPE);

            if (featureSource instanceof SimpleFeatureStore) {
                SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;*/
            /*
             * SimpleFeatureStore has a method to add features from a
             * SimpleFeatureCollection object, so we use the ListFeatureCollection
             * class to wrap our list of features.
             */
                //SimpleFeatureCollection collection = new ListFeatureCollection(TYPE, features);
            /*    featureStore.setTransaction(transaction);
                try {
                    featureStore.addFeatures(features);
                    transaction.commit();
                } catch (Exception problem) {
                    problem.printStackTrace();
                    transaction.rollback();
                } finally {
                    transaction.close();
                }
                System.exit(0); // success!
            } else {
                System.out.println(typeName + " does not support read/write access");
                System.exit(1);
            }*/

            ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put("url", newFile.toURI().toURL());
            params.put("create spatial index", Boolean.TRUE);

            ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
            newDataStore.createSchema(features.getSchema());

        /*
         * Write the features to the shapefile
         */
            Transaction transaction = new DefaultTransaction("create");

            String typeName = newDataStore.getTypeNames()[0];
            SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);

            if (featureSource instanceof SimpleFeatureStore) {
                SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

                featureStore.setTransaction(transaction);
                try {
                    featureStore.addFeatures(features);
                    transaction.commit();

                } catch (Exception problem) {
                    problem.printStackTrace();
                    transaction.rollback();
                    throw new Exception("issue with saving selected features");
                } finally {
                    transaction.close();
                }
            } else {
                throw new Exception(typeName + " does not support read/write access");
            }

        }
         catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private static Coordinate[] transformToCoordinates(EOLatLng[] points) {
        Coordinate[] coordinates = new Coordinate[points.length];
        for(int index = 0; index < points.length; index++) {
            coordinates[index] = new Coordinate(points[index].getLng(), points[index].getLat());
        }
        return coordinates;
    }
}
