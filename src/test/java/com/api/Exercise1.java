package com.api;

import static io.restassured.path.json.JsonPath.from;
import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.assertTrue;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import io.restassured.path.json.JsonPath;
import javafx.scene.control.Tab;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Unit test for simple App.
 */
public class Exercise1 {
    String baseURL = "https://api.carbonintensity.org.uk/regional/regionid/{regionid}";
    String parametrizedParameter = "{regionid}";
    //Value extracted from: https://carbon-intensity.github.io/api-definitions/?javascript#region-list
    Integer regionsIds = 17;

    /**
     * Test to verify intensity for regions
     */
    @Test
    public void testFindRegionsIntensity(){
        for (int i=1; i<=regionsIds; i++){
            String updatedBaseURL = baseURL.replace(parametrizedParameter,Integer.toString(i));
            System.out.println(baseURL);
            // Get the response body as a string
            String response = get(updatedBaseURL).asString();
            //Intensities retrieved
            ArrayList intensities = (ArrayList) from(response).getList("data.data.intensity");
            Map map0 = (Map)((ArrayList)intensities.get(0)).get(0);
            MatcherAssert.assertThat(map0.size(), is(2));
        }
    }

    /**
     * Test to verify that is retrieved forecast for the regions
     */
     @Test
     public void testFindRegionsForecast(){
        Map<Map,Map> intensityIndexAndForecast;
        List forecastListed = new ArrayList();
        for (int i=1; i<=regionsIds; i++){
            String updatedBaseURL = baseURL.replace(parametrizedParameter,Integer.toString(i));
            System.out.println(baseURL);
            // Get the response body as a string
            String response = get(updatedBaseURL).asString();
            ArrayList intensities = (ArrayList) from(response).getList("data.data.intensity");
            Map map0 = (Map)((ArrayList)intensities.get(0)).get(0);
            int forecast = (int) map0.get("forecast");
            forecastListed.add(forecast);
        }
        System.out.print(forecastListed);
        MatcherAssert.assertThat(forecastListed.size(), is(17));
     }

    /**
     * Test to sort regions index
     */
     @Test
     public void testSortRegionsIndex(){
        List<String> region = new ArrayList<String>();
        List<String> index = new ArrayList<String>();
        Map<String, String> mapRegionIndex = new HashMap<String, String>();
        for (int i=1; i<=regionsIds; i++){
            String updatedBaseURL = baseURL.replace(parametrizedParameter,Integer.toString(i));
            System.out.println(baseURL);
            // Get the response body as a string
            String response = get(updatedBaseURL).asString();
            // Region and Index
            region.add(from(response).get("data.dnoregion").toString());
            index.add(from(response).get("data.data.intensity.index").toString());
            int j = 0;
            while (j < region.size() && j < index.size() && j<regionsIds) {
                region.add(region.get(j));
                index.add(index.get(j));
                ++j;
            }
        }
        for (int z = 0; z < region.size(); z++) {
            mapRegionIndex.put(region.get(z), index.get(z));
        }
        System.out.print(mapRegionIndex);
        //Sorting
        Map<String, String> sortedMap =
            mapRegionIndex.entrySet().stream()
                .sorted(Map.Entry.comparingByValue((e1, e2) -> compareIndexValue.compare(e1, e2)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new));
        System.out.print(sortedMap);
     }

    @Test
    public void testSortForecastDisplayingSortName(){
        List<String> regionShortName = new ArrayList<String>();
        List<Integer> forecast = new ArrayList<>();
        Map<Integer, String> mapRegionIndex = new TreeMap<Integer, String>();
        for (int i=1; i<=regionsIds; i++){
            String updatedBaseURL = baseURL.replace(parametrizedParameter,Integer.toString(i));
            System.out.println(baseURL);
            // Get the response body as a string
            String response = get(updatedBaseURL).asString();
            // RegionShortName and forecast values
            regionShortName.add(from(response).get("data.shortname").toString());
            forecast = from(response).get("data.data.intensity.forecast");
            int j = 0;
            while (j < forecast.size() && j < regionShortName.size() && j<regionsIds) {
                forecast.add(forecast.get(j));
                regionShortName.add(regionShortName.get(j).toString());
                ++j;
            }
        }
        for (int z = 0; z < regionsIds; z++) {
            Object[] forecastElements = forecast.toArray();
            ArrayList forecastListed = (ArrayList) forecastElements[0];
            for (Object intValue: forecastListed.toArray()) {
                mapRegionIndex.put((Integer) intValue, regionShortName.get(z).toString());
            }
            System.out.print(mapRegionIndex);
        }
    }

    private static Comparator<String> compareIndexValue = new Comparator<String>() {
        private int mapToInt(String str) {
            switch (str) {
                case "very high":
                    return 0;
                case "high":
                    return 1;
                case "medium":
                    return 2;
                case "low":
                    return 3;
                case "very low":
                    return 4;
                default:
                    return 5;
            }
        }

        //@Override
        public int compare(String o1, String o2) {
            return mapToInt(o1) - mapToInt(o2);
        }
    };
}
