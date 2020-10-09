<%-- 
    Document   : km
    Created on : Nov 16, 2016, 6:07:51 PM
    Author     : Huy Tran
--%>

<%@page import="java.io.File"%>
<%@page import="java.io.BufferedWriter"%>
<%@page import="java.io.FileWriter"%>
<%@page import="ulr.l3i.strdfmining.KmeansWeb"%>
<%@page import="ulr.l3i.strdfmining.QueryProcessor"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
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

        KmeansWeb.RunKmean(fn, variable, Integer.parseInt(num));
        out.print(KmeansWeb.getCentroids());
    } else {
       
        int c = Integer.parseInt(StringEscapeUtils.unescapeJavaScript(request.getParameter("c")));
        out.print(KmeansWeb.getAssignmentsJSON(c));

    }
%>