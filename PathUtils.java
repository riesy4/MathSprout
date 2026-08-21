package com.example.mathsprout;

import android.graphics.Path;
import android.graphics.PathMeasure;
import java.util.ArrayList;
import java.util.List;

public class PathUtils {

    
    public static List<Float> convertPathToPoints(Path path) {
        List<Float> points = new ArrayList<>();
        if (path == null) return points;

        PathMeasure pm = new PathMeasure(path, false);
        float[] coords = new float[2];
        final float step = 8f; // Increase for speed, decrease for detail

        do {
            float length = pm.getLength();
            for (float distance = 0; distance < length; distance += step) {
                pm.getPosTan(distance, coords, null);
                points.add(coords[0]);
                points.add(coords[1]);
            }
         
            pm.getPosTan(length, coords, null);
            points.add(coords[0]);
            points.add(coords[1]);

        } while (pm.nextContour());

        return points;
    }


    public static Path convertPointsToPath(List<Float> points) {
        Path path = new Path();
        if (points == null || points.size() < 2) return path;

        path.moveTo(points.get(0), points.get(1));
        for (int i = 2; i < points.size(); i += 2) {
            if (i + 1 < points.size()) {
                path.lineTo(points.get(i), points.get(i + 1));
            }
        }
        return path;
    }
}
