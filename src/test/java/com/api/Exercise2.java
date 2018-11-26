package com.api;

import com.sun.javafx.binding.SelectBinding;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.when;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class Exercise2 {

    /**
     * Test to verify that sums of generation mix is 100
     */
    @Test
    public void testPercSumIs100(){
        String baseURL = "https://api.carbonintensity.org.uk/regional/regionid/{regionid}";
        String parametrizedParameter = "{regionid}";
        Float sumValues=0.F;
        //Because in API I checked that total regions
        for (int i=1; i<18; i++) {
            String updatedBaseURL = baseURL.replace(parametrizedParameter, Integer.toString(i));
            System.out.println(baseURL);
            String response = get(updatedBaseURL).asString();

            ArrayList listPerc = from(response).get("data.data.generationmix.perc");
            for (int j = 0; j < listPerc.size(); j++) {
                ArrayList allPerc = (ArrayList) listPerc.get(j);
                Object[] perc = allPerc.toArray();
                ArrayList e = (ArrayList) perc[0];
                for (int q = 0; q < e.size(); q++) {
                    Object valuesList = e.get(q);
                    if (valuesList instanceof Integer) {
                        int value = (Integer) valuesList;
                        Float intValue = Float.valueOf(value);
                        sumValues += intValue;
                    } else if (valuesList instanceof Float) {
                        Float value = (Float) valuesList;
                        sumValues += value;
                    }
                }
            }
            System.out.println(sumValues);
            int roundedValue = Math.round(sumValues);
            MatcherAssert.assertThat(roundedValue, is(100));
            sumValues = 0.0F;
        }
    }
}