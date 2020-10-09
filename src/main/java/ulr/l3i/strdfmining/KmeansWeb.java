/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulr.l3i.strdfmining;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.JSONArray;
import weka.clusterers.SimpleKMeans;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 *
 * @author Huy Tran
 */
public class KmeansWeb {

    static Instances dataOld;
    static SimpleKMeans kMeans;
    static String filename;

    public static void RunKmean(String fn, String variable, int num) throws IOException, Exception {
        CSVLoader loader = new CSVLoader();
        loader.setFile(new File(fn));
        dataOld = loader.getDataSet();
        Remove remove = new Remove();
        variable = variable.replaceAll("-", ",");
        remove.setAttributeIndices(variable);
        remove.setInvertSelection(true);
        remove.setInputFormat(dataOld);
        Instances data = Filter.useFilter(dataOld, remove);
        kMeans = new SimpleKMeans();
        kMeans.setNumClusters(num);
        kMeans.setPreserveInstancesOrder(true);
        kMeans.buildClusterer(data);
        filename = fn;
    }

    public static String getCentroids() throws UnsupportedEncodingException {
        String s = "";
        Instances centroids = kMeans.getClusterCentroids();
        for (int i = 0; i < centroids.numInstances(); i++) {
            s += "<a href='javascript:showcluster(" + i + ")'> Centroid " + i + ": " + new String(centroids.instance(i).toString().getBytes(), "UTF-8") + " [" + kMeans.getClusterSizes()[i] + "]</a><br>";
        }

        return s;
    }

    public static String getAssignments(int cluster) throws Exception {
        int point = 0;
        int poly = 0;
        int c = 0;
        String s = "<table id='list' class='list'><th>N0</th>";
        //print Header
        for (int j = 0; j < dataOld.numAttributes(); j++) {
            s += "<th>" + dataOld.attribute(j).name() + "</th>";
        }

        //print Instances
        int[] assignments = kMeans.getAssignments();
        for (int i = 0; i < assignments.length; i++) {
            if (assignments[i] == cluster) {
                point = 0;
                poly = 0;
                s += "<tr><td>" + i + "</td>";
                for (int j = 0; j < dataOld.instance(i).numAttributes(); j++) {
                    String data = dataOld.instance(i).toString(j).replace("'", "");
                    if (data.contains("POLY")) {
                        s += "<td class='geomvalue'><a id='poly" + poly + i + "' href='javascript:visual(\"poly" + poly + "\")'>" + StringEscapeUtils.escapeHtml(data) + "</a></td>";
                        poly = 1;
                    } else if (data.contains("POINT")) {
                        s += "<td class='geomvalue' ><a id='point" + point + i + "' href='javascript:visual(\"point" + point + "\")'>" + StringEscapeUtils.escapeHtml(data) + "</a></td>";
                        point = 1;
                    } else {
                        s += "<td><a href='#'>" + new String(data.getBytes(), "UTF-8") + "</a></td>";
                    }
                }
                s += "</tr>\n";
            }
        }

        s += "</table>";
        return s;
    }

    public static String getAssignmentsJSON(int cluster) throws Exception {
        JSONArray json = new JSONArray();
    
        int[] assignments = kMeans.getAssignments();
        for (int i = 0; i < assignments.length; i++) {
            if (assignments[i] == cluster) {
                org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
                for (int j = 0; j < dataOld.instance(i).numAttributes(); j++) {
                    //   String data = dataOld.instance(i).toString(j).replace("'", "");
                    obj.put(dataOld.attribute(j).name(), dataOld.instance(i).toString(j));

                }
                json.add(obj);
            }

        }
        return json.toJSONString();

    }
}
