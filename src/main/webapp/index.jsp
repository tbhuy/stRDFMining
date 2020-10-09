<%-- 
    Document   : index
    Created on : Nov 25, 2015, 5:18:02 PM
    Author     : huy
--%>


<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="ulr.l3i.strdfmining.Config"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>STRDF Mining</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <link rel="stylesheet" href="css/jquery-uib.css">
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
    <link rel="stylesheet"
        href="https://cdn.jsdelivr.net/gh/openlayers/openlayers.github.io@master/en/v6.4.3/css/ol.css" type="text/css">
    <script src="https://cdn.jsdelivr.net/gh/openlayers/openlayers.github.io@master/en/v6.4.3/build/ol.js"
        type="text/javascript"></script>
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tabulator/3.5.3/js/tabulator.min.js">
    </script>
    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <link href="css/tabulator.css" rel="stylesheet">
    <link href="css/RDFMining.css" rel="stylesheet">
    <link rel="stylesheet" href="css/jspanel.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.smartmenus/1.1.0/jquery.smartmenus.min.js"
        type="text/javascript"></script>
    <script src="scripts/renderMenu.js"></script>
    <!-- SmartMenus core CSS (required) -->
    <link href='https://cdnjs.cloudflare.com/ajax/libs/jquery.smartmenus/1.1.0/css/sm-core-css.css' rel='stylesheet'
        type='text/css' />
    <!-- "sm-blue" menu theme (optional, you can use your own CSS, too) -->
    <link href='https://cdnjs.cloudflare.com/ajax/libs/jquery.smartmenus/1.1.0/css/sm-blue/sm-blue.min.css'
        rel='stylesheet' type='text/css' />
    <script src="http://malsup.github.com/jquery.form.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/ace/1.4.12/ace.js">   </script>
</head>
<body>
    <%
            out.write("<input type='hidden' id='querypath' value='" + getServletContext().getRealPath("/KB/Query") + "'>");
            out.write("<input type='hidden' id='cachepath' value='" + getServletContext().getRealPath("/KB/Cache") + "'>");
        %>

    <!-- End Zone pour la barre en haut -->
    <div class="container">
        <div class="header">
            <ul id="myMenu" class="sm sm-blue"></ul>
        </div>
        <div id="content">
            <div id="mouse-position"></div>
            <!-- Dialog for variable selection -->
            <div id="dialog" title="Attribute selection">
                <select id='listvariable' multiple='multiple' size='10'>
                </select>
            </div>
            <!-- Dialog for variable selection -->

            <!-- Dialog for variable selection -->
            <div id="mining" title="Mining result">
            </div>
            <!-- Dialog for variable selection -->
            <!-- Dialog for configration -->
            <div id="config" title="Configuration">
                <form id="ConfigForm" action="Config" method="post">
                    <input type="hidden" name='job' value='up'>
                    <fieldset>
                        <legend>Knowledge base</legend>
                        <div class="controlgroup">
                            Host <input type="text" name="host" id="host" value="http://localhost">
                            Port <input type="text" name="port" id="port" value="8888">
                            Store <input type="text" name="storename" id="storename" value="strabon">
                            <br />
                            Username <input type="text" name="user" id="user" value="endpoint">
                            Password <input type="password" name="pass" id="pass" value="3ndpo1nt">
                        </div>
                    </fieldset>
                    <fieldset>
                        <legend>Mining parameters</legend>
                        <fieldset>
                            <legend>Associator - Apriori</legend>
                            <div id="apriori">
                                Supp: <input type="text" name='supp' id='supp' value='0.01' width="15px">
                                Conf: <input type='text' name='conf' id='conf' value='0.5'>
                            </div>
                        </fieldset>
                        <fieldset>
                            <legend>Clusterer - Kmeans</legend>
                            <div id="kmean">
                                Num_clusters: <input type="text" name='num' id='num' value='10' width="15px">
                            </div>
                        </fieldset>
                        <fieldset>
                            <legend>Classifier - PART</legend>
                            <div id="part">
                                Conf: <input type="text" id='confPart' value='0.25' width="15px">
                                Min_Objs: <input type='text' id='numPart' value='2'>
                            </div>
                        </fieldset>
                        <fieldset>
                            <legend>Classifier - JRip</legend>
                            <div id="jrip">
                                Min_Objs: <input type='text' name='min' id='min' value='2'>
                            </div>
                        </fieldset>
                    </fieldset>
                    <input type="submit" value="OK" style="width: 350px">
                </form>
            </div>
            <!-- Dialog for configration -->
            <!-- Partie gauche -->
            <div class="left">
                <div id="al">
                    <select id="algo">
                        <option value='0' selected='selected'> Query </option>
                        <option value='1'> Associator - Apriori</option>
                        <option value='2'> Classifier - PART</option>
                        <option value='4'> Classifier - JRip</option>
                        <option value='3'> Clusterer - Kmeans</option>
                    </select>
                    <button class="ui-button ui-widget ui-corner-all" id="start"> Run </button>
                </div>
                <div id="text"> </div>
            </div>
            <!-- Partie gauche -->
            <!-- Partie droite -->
            <div class="right">
                <div id="status"></div>
                <div id="mouse-position"></div>
                <!-- Zone pour la viz-->
                <div class="viz" id="viz">
                    <div id="map" class="map"></div>
                    <div id="chart_div"></div>
                    <div id="table_div"></div>
                    <div id="popup" class="ol-popup">
                        <a href="#" id="popup-closer" class="ol-popup-closer"></a>
                        <div id="popup-content"></div>
                    </div>
                </div>
                <!-- End Zone pour la viz -->
                <!-- Zone pour la liste en bas -->
                <div id="div_list"> </div>
            </div>
            <!-- Partie droite -->
        </div>
        <!-- Partie Footer -->
        <div class="footer">
            <button id="hide"> ✖ Query</button>
            <div id="md5"></div>
            <div id="number"></div>
        </div>
        <!-- Zone pour la barre en bas-->
    </div>
    <script src="scripts/main.js"> </script>
</body>
</html>