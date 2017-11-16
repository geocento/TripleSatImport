package com.geocento.earthimages.triplesatimport;

import com.geocento.earthimages.triplesatimport.model.SearchResponseDTO;
import com.metaaps.webapps.libraries.client.map.EOLatLng;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripleSatShpSaver
{
    SimpleFeatureTypeBuilder builder;
    SimpleFeatureBuilder featureBuilder;
    ShapefileDataStore newDataStore;
    SimpleFeatureType featureType;
    String file;
    int index = 0;
    DefaultFeatureCollection features;

    public TripleSatShpSaver(String file) throws IOException
    {
        this.file = file;

        builder = new SimpleFeatureTypeBuilder();
        builder.setName("TripleSat");
        builder.setNamespaceURI("http://earthimages.geocento.com");
        try
        {
            //CoordinateReferenceSystem crs = CRS.decode("EPSG:4326", true);
            String wkt = "GEOGCS[" + "\"WGS 84\"," + "  DATUM[" + "    \"WGS_1984\","
                    + "    SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],"
                    + "    TOWGS84[0,0,0,0,0,0,0]," + "    AUTHORITY[\"EPSG\",\"6326\"]],"
                    + "  PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],"
                    + "  UNIT[\"DMSH\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9108\"]],"
                    + "  AXIS[\"Lat\",NORTH]," + "  AXIS[\"Long\",EAST],"
                    + "  AUTHORITY[\"EPSG\",\"4326\"]]";

            CoordinateReferenceSystem crs = CRS.parseWKT(wkt);
            builder.setCRS(crs);
        } catch (Exception e)
        {
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


        featureType = builder.buildFeatureType();
        featureBuilder = new SimpleFeatureBuilder(featureType);
        features = new DefaultFeatureCollection(null, featureType);
    }

    public void addFeatures(SearchResponseDTO.Data data) throws Exception
    {

        GeometryFactory gf = new GeometryFactory();

            for (List<SearchResponseDTO.Field> row : data.rows)
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
                        String coordinates = field.value.toUpperCase().replace("POLYGON", "").replace("((", "").replace("))", "");
                        LinearRing contour = gf.createLinearRing(transformToCoordinates(EOLatLng.parseWKT(coordinates)));
                        shape = gf.createPolygon(contour, null);
                    }
                    if (field.id.equals("id")) id = field.value;
                    if (field.id.equals("transformimg")) transform = field.value;
                    if (field.id.equals("satellite")) satellite = field.value;
                    if (field.id.equals("thumbimg")) thumbimg = field.value;
                    if (field.id.equals("rollangle")) rollangle = field.value;
                    if (field.id.equals("cloudcover")) cloudcover = field.value;
                    if (field.id.equals("fgeometry")) wkt = field.value;
                    if (field.id.equals("browserimg")) browserimg = field.value;
                    //if (field.id.equals("resolution"))
                    resolution = "1.4";//field.value;
                    if (field.id.equals("centertime")) centertime = field.value;
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
    }


    public void saveFeatures() throws IOException
    {

        File newFile = new File(file);

        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("url", newFile.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);
        newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
        newDataStore.createSchema(features.getSchema());

        Transaction transaction = new DefaultTransaction("create");

        String typeName = newDataStore.getTypeNames()[0];
        SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);

        if (featureSource instanceof SimpleFeatureStore)
        {
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

            featureStore.setTransaction(transaction);
            try
            {
                featureStore.addFeatures(features);
                transaction.commit();

            } catch (Exception problem)
            {
                problem.printStackTrace();
                transaction.rollback();
                throw new IOException("issue with saving selected features");
            } finally
            {
                transaction.close();
            }
        } else
        {
            throw new IOException(typeName + " does not support read/write access");
        }
    }


    private Coordinate[] transformToCoordinates(EOLatLng[] points)
    {
        Coordinate[] coordinates = new Coordinate[points.length];
        for (int index = 0; index < points.length; index++)
        {
            coordinates[index] = new Coordinate(points[index].getLng(), points[index].getLat());
        }
        return coordinates;
    }
}
