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
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">  
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <link rel="stylesheet"
        href="https://cdnjs.cloudflare.com/ajax/libs/openlayers/4.6.5/ol.css" type="text/css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/openlayers/4.6.5/ol.js"
        type="text/javascript"></script>
    <title>RDF Mining</title>
   </head>

<body>

    <div class="container-fluid">
        <%
            // Show geom on the top
            String req = StringEscapeUtils.unescapeJavaScript(request.getParameter("query"));
            req = req.replaceAll("'", "");
            out.write("<input type='text' size='250' id='geom' value='" + req + "'><br>");

        %>
        <div class="row-fluid">
            <div class="span12">
                <div id="map" class="map"></div>
            </div>
        </div>

    </div>
    <script>


        var geom = $("#geom").val();
        geom = geom.substr(0, geom.lastIndexOf(")") + 1);

        var features = [];
        var raster = new ol.layer.Tile({
            source: new ol.source.OSM()
        });
        var vectorSource = new ol.source.Vector({
            features: features      //add an array of features
                    //,style: iconStyle     //to set the style for all your features...
        });

        var vectorLayer = new ol.layer.Vector({
            source: vectorSource
        });

        var format = new ol.format.WKT();

        var f = new ol.Feature({geometry: format.readFeature(geom).getGeometry().transform("EPSG:4326", "EPSG:900913")});
        vectorSource.addFeature(f);
        //  alert(f.getGeometry().getFirstCoordinate());
        var map = new ol.Map({

            layers: [raster],
            target: 'map',
            view: new ol.View({
                center: f.getGeometry().getFirstCoordinate(),
                zoom: 15
            })

        });
        var mousePosition = new ol.control.MousePosition({
            oordinateFormat: ol.coordinate.createStringXY(3),
            projection: 'EPSG:4326'

        });

        //read point geom


        map.addLayer(vectorLayer);

        map.addControl(mousePosition);


    </script>

    <div id="map" class="smallmap"></div>
</body>

</html>