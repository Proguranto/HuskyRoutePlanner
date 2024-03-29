/*
 * Copyright (C) 2022 Kevin Zatloukal.  All rights reserved.  Permission is
 * hereby granted to students registered for University of Washington
 * CSE 331 for use solely during Spring Quarter 2022 for purposes of
 * the course.  No other use, copying, distribution, or modification
 * is permitted without prior written consent. Copyrights for
 * third-party components of this work must be honored.  Instructors
 * interested in reusing these course materials should contact the
 * author.
 */

package campuspaths;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;

import campuspaths.utils.CORSFilter;
import pathfinder.CampusMap;
import pathfinder.datastructures.Path;
import pathfinder.datastructures.Point;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class SparkServer {

    public static void main(String[] args) {
        CORSFilter corsFilter = new CORSFilter();
        corsFilter.apply();
        // The above two lines help set up some settings that allow the
        // React application to make requests to the Spark server, even though it
        // comes from a different server.
        // You should leave these two lines at the very beginning of main().


        CampusMap map = new CampusMap();
        
        Spark.get("/locations", new Route() {
        @Override
        public Object handle(spark.Request request, spark.Response response) throws Exception {
            // Convert Map buildings to a list of names.
            List<String> mapNames = new LinkedList<>();
            for (String shortName : map.buildingNames().keySet()) {
                String name = shortName + ", " + map.buildingNames().get(shortName);
                mapNames.add(name);
            }


            Gson gson = new Gson();
            String jsonResponse = gson.toJson(mapNames);

            return jsonResponse;
        }
        });

        Spark.get("/check", new Route() {

            @Override
            public Object handle(Request request, Response response) throws Exception {
                String start = request.queryParams("start");
                String end = request.queryParams("end");
                return map.shortNameExists(start) && map.shortNameExists(end);
            }
            
        });

        Spark.get("/path", new Route() {

            @Override
            public Object handle(Request request, Response response) throws Exception {
                String start = request.queryParams("start");
                String end = request.queryParams("end");
                Path<Point> path = map.findShortestPath(start, end);
                if (path == null) {
                    return "none";
                }
                return path.toString();
            }
            
        });
    }

}
