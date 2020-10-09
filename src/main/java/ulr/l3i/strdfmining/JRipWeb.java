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
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.JRip.RipperRule;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 *
 * @author Tran
 */
public class JRipWeb {

    static String filename;
    static JRip jrip;
    static Instances data, dataOld;

    public static void RunJRip(String fn, String variable, int num) throws IOException, Exception {
        CSVLoader loader = new CSVLoader();
        loader.setFile(new File(fn));
        dataOld = loader.getDataSet();
        Remove remove = new Remove();
        variable = variable.replaceAll("-", ",");
        remove.setAttributeIndices(variable);
        remove.setInvertSelection(true);
        remove.setInputFormat(dataOld);
        data = Filter.useFilter(dataOld, remove);
        dataOld.setClassIndex(dataOld.numAttributes() - 1);
        data.setClassIndex(data.numAttributes() - 1);
        jrip = new JRip();
        jrip.buildClassifier(data);
        filename = fn;
    }

    public static String listRules() throws Exception {
        String s = "";
        /* bugging
    ClassOrder m_Filter = new ClassOrder();
   // ((ClassOrder)m_Filter).setSeed(m_Random.nextInt());	
    ((ClassOrder)m_Filter).setClassOrder(ClassOrder.FREQ_ASCEND);
    m_Filter.setInputFormat(data);
    data = Filter.useFilter(data, m_Filter);
    m_Filter.setInputFormat(dataOld);
    dataOld= Filter.useFilter(dataOld, m_Filter);
    
  
    FastVector ruleset = jrip.getRuleset();
    for (int j = 0; j < ruleset.size(); j++) 
     {
       JRip.RipperRule rule = (JRip.RipperRule) ruleset.elementAt(j);
       s+="<a href='javascript:showJRip("+j+")'>"+(j+1)+":"+ rule.toString(data.classAttribute()) + " (c="+rule.getConsequent()+") <br>";
       }
    s+="<b> Total "+ruleset.size()+" rules found.</b><br>";
         */
        String ss[] = jrip.toString().trim().split("\n");
        for (int i = 3; i < ss.length - 2; i++) {
            s += "<a href='javascript:showJRip(" + (i - 3) + ")'>" + (i - 2) + ": " + new String(ss[i].getBytes(), "UTF-8") + "<br>";
        }
        return s;
    }

    public static String getInstancesRule(int n) throws UnsupportedEncodingException, Exception {

        int point, poly;
        RipperRule r = (JRip.RipperRule) jrip.getRuleset().elementAt(n);
        String s = "<table id='list' class='list'><th>N0</th>";
        //print Header
        for (int j = 0; j < dataOld.numAttributes(); j++) {
            s += "<th>" + dataOld.attribute(j).name() + "</th>";
        }
        int k = 1;
        for (int i = 0; i < dataOld.numInstances(); i++) {
            if (r.covers(data.instance(i))) {

                point = 0;
                poly = 0;
                s += "<tr><td>" + k + "</td>";
                k++;
                for (int j = 0; j < dataOld.instance(i).numAttributes(); j++) {
                    String dat = dataOld.instance(i).toString(j).replace("'", "");
                    if (dat.contains("POLY")) {
                        s += "<td class='geomvalue'><a id='poly" + poly + i + "' href='javascript:visual(\"poly" + poly + "\")'>" + StringEscapeUtils.escapeHtml(dat) + "</a></td>";
                        poly = 1;
                    } else if (dat.contains("POINT")) {
                        s += "<td class='geomvalue' ><a id='point" + point + i + "' href='javascript:visual(\"point" + point + "\")'>" + StringEscapeUtils.escapeHtml(dat) + "</a></td>";
                        point = 1;
                    } else {
                        s += "<td><a href='#'>" + new String(dat.getBytes(), "UTF-8") + "</a></td>";
                    }
                }
                s += "</tr>\n";
            }
        }

        s += "</table>";
        return s;
    }

    public static String getInstancesRuleJSON(int n) throws UnsupportedEncodingException, Exception {
        JSONArray json = new JSONArray();

        RipperRule r = (JRip.RipperRule) jrip.getRuleset().elementAt(n);

        //print Header
        //  for(int j=0;j<dataOld.numAttributes();j++)
        //   s+="<th>"+dataOld.attribute(j).name()+"</th>";
        int k = 1;
        for (int i = 0; i < dataOld.numInstances(); i++) {
            if (r.covers(data.instance(i))) {
                org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
             
                for (int j = 0; j < dataOld.instance(i).numAttributes(); j++) {
                       obj.put(dataOld.attribute(j).name(), dataOld.instance(i).toString(j));
                  
                } 
                json.add(obj);
            }
        }
         
        return json.toJSONString();
    }

}
