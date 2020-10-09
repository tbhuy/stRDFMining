<%-- 
    Document   : index
    Created on : Nov 25, 2015, 5:18:02 PM
    Author     : huy
--%><%@page import="weka.filters.unsupervised.attribute.Remove"%>
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

<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>JSP Page</title>
</head>
<%
 String req="";
 String num="";
 String conf="";
String variable="";
req=StringEscapeUtils.unescapeJavaScript(request.getParameter("q"));;
num=request.getParameter("num");  
conf=request.getParameter("conf"); 
variable=request.getParameter("var"); 
//out.print(req);

 String fn_sem=getServletContext().getRealPath("/").replace('\\', '/')+"rs.csv";
int nitem=0;
req=StringEscapeUtils.unescapeJavaScript(request.getParameter("q"));
QueryProcessing.query(req,fn_sem);
    CSVLoader loader = new CSVLoader();
    loader.setFile(new File(fn_sem+"p"));
    Instances dataOld = loader.getDataSet();
    Remove remove = new Remove();
    variable=variable.replaceAll("-", ",");
    remove.setAttributeIndices(variable); 
       
    remove.setInvertSelection(true); 
    remove.setInputFormat(dataOld); 
    Instances data = Filter.useFilter(dataOld, remove);
   
    PART part = new PART();
       
    part.setUnpruned(false);
    part.setMinNumObj(Integer.parseInt(num));
    part.setReducedErrorPruning(true);
    part.setNumFolds(10);
    part.setConfidenceFactor(Float.parseFloat(conf));
    //part.setBinarySplits(true);
    data.setClassIndex(data.numAttributes() - 1);
    part.buildClassifier(data);
  
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(part, data, 10, new Random(1));
        
        double percentageCorrect = eval.pctCorrect();
        String classifierRules = part.toString();
        classifierRules=classifierRules.replaceAll("\n", "<br>");
        out.println("Percentage of correctly classified instances for PART classifier: "+eval.pctCorrect() +"<br>");
        out.println(new String(classifierRules.getBytes(),"UTF-8"));
             


            %>

</html>