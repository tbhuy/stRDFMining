/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulr.l3i.strdfmining;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import weka.associations.Apriori;
import weka.core.Instances;
import weka.core.Instance;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.associations.AprioriItemSet;
import java.text.DecimalFormat;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.JSONArray;
import java.util.*; 

/**
 *
 * @author Tran
 */
public class AprioriWeb{

    static Instances dataOld; // before filtering
    static Instances data; // after filtering
    static Apriori apriori;
    static String filename;
    static int size;

    public static void RunApriori(String fn, String variable, float supp, float conf) throws IOException, Exception {
        CSVLoader loader = new CSVLoader();
        loader.setFile(new File(fn));
        dataOld = loader.getDataSet();
        Remove remove = new Remove();
        variable = variable.replaceAll("-", ",");
        remove.setAttributeIndices(variable);
        remove.setInvertSelection(true);
        remove.setInputFormat(dataOld);
        data = Filter.useFilter(dataOld, remove);
        size = data.numAttributes();
        apriori = new Apriori();
        for (int i = 0; i < size; i++) {
            data.renameAttribute(data.attribute(i), "?" + data.attribute(i).name());
        }
        apriori.setLowerBoundMinSupport(supp);
        apriori.setMinMetric(conf);
        apriori.setNumRules(400);
        apriori.buildAssociations(data);
        filename = fn;
    }

    public static String listRules() throws UnsupportedEncodingException {
        String s = "<b>Association rules found</b><br>";
        int n = 0;
        for (int i = 0; i < apriori.getAllTheRules()[0].size(); i++) {
            AprioriItemSet condition = ((AprioriItemSet) apriori.getAllTheRules()[0].elementAt(i));
            AprioriItemSet consequence = ((AprioriItemSet) apriori.getAllTheRules()[1].elementAt(i));
            double confidence = ((Double) apriori.getAllTheRules()[2].elementAt(i)).doubleValue();
            DecimalFormat df = new DecimalFormat("#.##");
            // if(confidence<1)
            // {
       
            String cond = new String(condition.toString(data).getBytes("ISO-8859-1"), "UTF-8");
            String cons = new String(consequence.toString(data).getBytes("ISO-8859-1"), "UTF-8");
            if (cond.split("=").length == (size)) {
                n++;
                String p = new String(cond.substring(0, cond.lastIndexOf(" ")).replaceAll(" \\?", " && \\?").getBytes(), "UTF-8");
                String c = new String(cons.substring(0, cons.lastIndexOf(" ")).getBytes(), "UTF-8");
                // s+="<a href='javascript:show(\"filter("+ p+" && "+c+")\")'>"+n+". "+p+" => "+c+ " ("+df.format(confidence)+") </a><br>";
                s += "<a href='javascript:showrule(" + i + ")'>" + n + ". " + p + " => " + c + " (" + df.format(confidence) + ") </a><br>";
            }

        }
        s += "<br><b> " + n + " rules listed/ " + apriori.getAllTheRules()[0].size() + " rules found </b>";
        return s;
    }

    public static String getRuleInstances(int n) throws UnsupportedEncodingException {
        int point = 0;
        int poly = 0;
        int c = 0;
        String s = "<table id='list' class='list'><th>N0</th>";
        String ss = "";
        //print Header
        for (int j = 0; j < dataOld.numAttributes(); j++) {
            s += "<th>" + dataOld.attribute(j).name() + "</th>";
        }

        //print Instances
        AprioriItemSet condition = ((AprioriItemSet) apriori.getAllTheRules()[0].elementAt(n));
        AprioriItemSet consequence = ((AprioriItemSet) apriori.getAllTheRules()[1].elementAt(n));
        Instance ins, ins2;
        int m = 1, k = 1;
        for (int i = 0; i < dataOld.numInstances(); i++) {
            ins = data.instance(i);
            ins2 = dataOld.instance(i);
            if (condition.containedBy(ins) && consequence.containedBy(ins)) {
                point = 0;
                poly = 0;
                s += "<tr><td>" + m + "</td>";
                m++;
                for (int j = 0; j < ins2.numAttributes(); j++) {
                    String data = ins2.toString(j).replace("'", "");
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
            } else if (condition.containedBy(ins) && !consequence.containedBy(ins)) {
                point = 0;
                poly = 0;
                ss += "<tr><td>" + k + "</td>";
                k++;
                for (int j = 0; j < ins2.numAttributes(); j++) {
                    String data = ins2.toString(j).replace("'", "");
                    if (data.contains("POLY")) {
                        ss += "<td class='geomvalue'><a id='poly" + poly + i + "' href='javascript:visual(\"poly" + poly + "\")'>" + StringEscapeUtils.escapeHtml(data) + "</a></td>";
                        poly = 1;
                    } else if (data.contains("POINT")) {
                        ss += "<td class='geomvalue' ><a id='point" + point + i + "' href='javascript:visual(\"point" + point + "\")'>" + StringEscapeUtils.escapeHtml(data) + "</a></td>";
                        point = 1;
                    } else {
                        ss += "<td><a href='#'>" + new String(data.getBytes(), "UTF-8") + "</a></td>";
                    }
                }
                ss += "</tr>\n";
            }

        }
        s += "<tr><td colspan=></td></tr>" + ss + "</table>";
        return s;
    }

    public static String getRuleInstancesJSON(int n) throws UnsupportedEncodingException {
        JSONArray json = new JSONArray();
		ArrayList<org.json.simple.JSONObject> notRule=new ArrayList<org.json.simple.JSONObject>();
        AprioriItemSet condition = ((AprioriItemSet) apriori.getAllTheRules()[0].elementAt(n));
        AprioriItemSet consequence = ((AprioriItemSet) apriori.getAllTheRules()[1].elementAt(n));
        Instance ins, ins2;

        for (int i = 0; i < dataOld.numInstances(); i++) {
            ins = data.instance(i);
            ins2 = dataOld.instance(i);

            org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
            //if contain instance
            if (condition.containedBy(ins) && consequence.containedBy(ins)) {
                for (int j = 0; j < ins2.numAttributes(); j++) {
                    obj.put(dataOld.attribute(j).name(), ins2.toString(j));

                }

                json.add(obj);
            }

        
			
			// instance that does not satisfy the rule
			if (condition.containedBy(ins) && !consequence.containedBy(ins)) {
                for (int j = 0; j < ins2.numAttributes(); j++) {
                    obj.put(dataOld.attribute(j).name(), ins2.toString(j));

                }

                notRule.add(obj);
            }
		

        }	
		// add instances that do not satisfy the rule in the end of list
		for(org.json.simple.JSONObject obj:notRule)
			json.add(obj);
		
        return json.toJSONString();
    }
}
