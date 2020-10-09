<%-- 
    Document   : index
    Created on : Nov 25, 2015, 5:18:02 PM
    Author     : huy
--%>


<%@page import="java.io.BufferedWriter"%>
<%@page import="java.io.FileWriter"%>
<%@page import="java.io.File"%>
<%@page import="ulr.l3i.strdfmining.AprioriWeb"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="ulr.l3i.strdfmining.QueryProcessor"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    String req = "";
    String supp = "";
    String conf = "";
    String variable = "";
    req = StringEscapeUtils.unescapeJavaScript(request.getParameter("q"));;
    if (!req.equals("")) {
        supp = request.getParameter("supp");
        conf = request.getParameter("conf");
        variable = request.getParameter("var");
        //out.print(req);

        String result = QueryProcessor.getMD5(req + "csv");
        String fn = getServletContext().getRealPath("") + "/KB/Cache/" + result;
        File file = new File(fn);

        if (!file.exists()) {
            String resp = "";

            resp = QueryProcessor.queryCSVQ(req);

            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(resp);
            bw.close();
        }

        AprioriWeb.RunApriori(fn, variable, Float.parseFloat(supp), Float.parseFloat(conf));
        out.print(AprioriWeb.listRules());
    } else {
        out.print(AprioriWeb.getRuleInstancesJSON(Integer.parseInt(request.getParameter("r"))));
    }


%>