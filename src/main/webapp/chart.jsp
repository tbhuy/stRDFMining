<%-- 
    Document   : visual
    Created on : Nov 29, 2015, 8:50:03 PM
    Author     : huy
--%>

<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
    <title>GEMINAT Spatio-temproal RDF Data Mining</title>
    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <link href="css/RDFMining.css" rel="stylesheet">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <script type="text/javascript" src="scripts/draw.js">  </script>
</head>

<body>
    <div id="container">
        <!-- End Zone pour la barre en haut -->
        <header>
            <div id="title"> Chart </div>
        </header>
        <article>
            <div id="chart_div"></div>
            <div id="table_div"></div>
        </article>
        <footer>

            Laboratoire L3i
            <div id="number"></div>
        </footer>
        <!-- Zone pour la barre en bas-->
</body>
</html>