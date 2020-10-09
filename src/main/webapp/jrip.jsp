<%-- 
    Document   : index
    Created on : Nov 25, 2015, 5:18:02 PM
    Author     : huy
--%><%@page import="java.io.BufferedWriter"%>
<%@page import="java.io.FileWriter"%>
<%@page import="ulr.l3i.strdfmining.JRipWeb"%>
<%@page import="weka.filters.unsupervised.attribute.Remove"%>
<%@page import="java.io.File"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="ulr.l3i.strdfmining.QueryProcessor"%>
<%@page import="java.util.Random"%>
<%@page import="weka.classifiers.Evaluation"%>
<%@page import="weka.core.Instance"%>
<%@page import="weka.clusterers.SimpleKMeans"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="weka.classifiers.rules.PART"%>
<%@page import="weka.associations.Apriori"%>
<%@page import="weka.core.Instances"%>
<%@page import="weka.core.converters.CSVLoader"%>
<%@page import="java.util.regex.Matcher"%>
<%@page import="java.util.regex.Pattern"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="weka.filters.Filter"%>
<%
String num = "";
    String variable = "";
    String req = "";
    req = StringEscapeUtils.unescapeJavaScript(request.getParameter("q"));
    if (!req.equals("")) {
        num = request.getParameter("num");
        variable = request.getParameter("var");

        String result = QueryProcessor.getMD5(req + "csv");
        String fn =  getServletContext().getRealPath("") + "/KB/Cache/" + result;
        File file = new File(fn);

        if (!file.exists()) {
            String resp = "";

            resp = QueryProcessor.queryCSVQ(req);

            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(resp);
            bw.close();
        }
      JRipWeb.RunJRip(fn, variable, 5);
      out.print(JRipWeb.listRules());
  
    } else {
       
        int c = Integer.parseInt(StringEscapeUtils.unescapeJavaScript(request.getParameter("n")));
        out.print(JRipWeb.getInstancesRuleJSON(c));

    }
%>

