package me.urkkiz.util;

import me.urkkiz.Physics.PhysicsLoop;
import me.urkkiz.Shapes.PolygonHolder;

import java.awt.*;
import java.util.ArrayList;

public class Transform {
    public static double[][] AccurateCumulativeDoubleOverFlows = new double[3][2];
    public static ArrayList<int[][]> DeltaPositions=new ArrayList<>();
    //index 0 : position | index 1 : rotation | index 2 : scale (for rounding errors)
    public static void MovePolygon(int PolygonIndex, float x, float y){
        Polygon polygon=PolygonHolder.shapes.get(PolygonIndex);
        DeltaPositions.set(PolygonIndex, new int[][]{polygon.xpoints, polygon.ypoints});
        // \/- obsolete - JFrame has a translate() method for moving objects. (works nonetheless, but this might be faster so leaving it here)
        /*
        for(int PolygonIndex = 0; PolygonIndex < polygon.npoints; PolygonIndex++) {
            coordinates[0][PolygonIndex] += (int) ((coordinates[0][PolygonIndex]+x)-coordinates[0][PolygonIndex]);
            coordinates[1][PolygonIndex] += (int) ((coordinates[1][PolygonIndex]+y)-coordinates[1][PolygonIndex]);
            //coordinates[1][PolygonIndex]= coordinates[1][PolygonIndex]+Math.round((coordinates[1][PolygonIndex]+y)-coordinates[1][PolygonIndex]);
        }
        */
        polygon.translate((int) ((PhysicsLoop.CalculatePseudoCenter(polygon)[0] +x)-PhysicsLoop.CalculatePseudoCenter(polygon)[0]), (int) ((int) (PhysicsLoop.CalculatePseudoCenter(polygon)[1]+y)-PhysicsLoop.CalculatePseudoCenter(polygon)[1]));
    }
    public static void RotatePolygon(int PolygonIndex, float Angles, float[] pivot) {
        /*rounding errors must be accounted for, as java awts custom polygons can not be instantiated with double/float accuracy.
          however, over a time period long enough (very long time) the doubles 64-bit accuracy will not be sufficient for accurately rotating polygons, but
          this avoids unnecessary operations of copying the polygon from PolygonHolder.BasePolygons and translating it to CustomPolygon.shapes with accounting for transformations.
        */
        double[] xy = new double[2];
        double[] xyCumulativeOverFlow = new double[2]; //done with cpu optimization in mind; rounding errors can also be accounted for with a one-liner
        for (int i = 0; i < PolygonHolder.shapes.get(PolygonIndex).npoints; i++) {
            xy[0] = ((((PolygonHolder.BasePolygons.get(PolygonIndex).xpoints[i] - pivot[0]) * Math.cos(Angles)) - (((PolygonHolder.BasePolygons.get(PolygonIndex).ypoints[i] - pivot[1]) * Math.sin(Angles))))) + pivot[0];
            xy[1] = (((((PolygonHolder.BasePolygons.get(PolygonIndex).ypoints[i] - pivot[1]) * Math.cos(Angles)) + (((PolygonHolder.BasePolygons.get(PolygonIndex).xpoints[i] - pivot[0]) * Math.sin(Angles)))))) + pivot[1];
            if(Math.abs(Math.round(AccurateCumulativeDoubleOverFlows[1][0])) > 0){
                xyCumulativeOverFlow[0]=Math.round(AccurateCumulativeDoubleOverFlows[1][0]);
                AccurateCumulativeDoubleOverFlows[1][0] = (AccurateCumulativeDoubleOverFlows[1][0]-xyCumulativeOverFlow[0]);
            }
            else AccurateCumulativeDoubleOverFlows[1][0] += xy[0] - (int) (Math.round(xy[0])); //POSSIBLE ERROR HERE!!!1! FIX LATER PLZ!!!!!!!!
            if(Math.abs(Math.round(AccurateCumulativeDoubleOverFlows[1][1])) > 0){
                xyCumulativeOverFlow[1] = Math.round(AccurateCumulativeDoubleOverFlows[1][1]);
                AccurateCumulativeDoubleOverFlows[1][1] = (AccurateCumulativeDoubleOverFlows[1][1]-xyCumulativeOverFlow[1]);
            }
            else AccurateCumulativeDoubleOverFlows[1][1] += xy[1] - (Math.round(xy[1])); //POSSIBLE ERROR HERE!!!1! FIX LATER PLZ!!!!!!!!
            PolygonHolder.shapes.get(PolygonIndex).xpoints[i] = (int) (Math.round(xy[0])) + (int)xyCumulativeOverFlow[0];
            PolygonHolder.shapes.get(PolygonIndex).ypoints[i] = (int) (Math.round(xy[1])) + (int)xyCumulativeOverFlow[1];
        }
    }
}