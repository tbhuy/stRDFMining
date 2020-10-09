<%-- 
    Document   : dump
    Created on : Sep 2, 2017, 9:56:43 AM
    Author     : bhtran
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <style>
        #container {
            height: 800px;
        }

        #div_right {
            margin-left: 20px;


        }

        #div_left {
            float: left;

        }

        #result {
            height: 700px;
            width: 1050px;
            overflow: scroll;
            font-size: larger;
        }

        #mapping {
            height: 700px;
            width: 750px;
            overflow: scroll;
            font-size: larger;
        }

        #fn {
            float: left;
        }
    </style>
    <script>
        $(document).ajaxStart(function () {
            $(document.body).css({
                'cursor': 'wait'
            });
        }).ajaxStop(function () {
            $(document.body).css({
                'cursor': 'default'
            });
        });
    </script>
</head>

<body>
    <div id='container'>
        <div id='div_left'>
            <textarea rows="40" id="mapping" name='mapping'>
            @prefix map: <#> .
            @prefix db: <> .
            @prefix vocab: <vocab/> .
            @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
            @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix d2rq: <http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#> .
            @prefix jdbc: <http://d2rq.org/terms/jdbc/> .
            @prefix time: <http://www.w3.org/2006/time#>.
            @prefix strdf: <http://strdf.di.uoa.gr/ontology#>.
            @prefix gem: <http://za-geminat.cnrs.fr/Assolement.owl#>.
            @prefix d2r: <http://sites.wiwiss.fu-berlin.de/suhl/bizer/d2r-server/config.rdf#> .
            <> a d2r:Server;
            d2r:sparqlTimeout 3000;
            .
            map:Assolement a d2rq:Database;
            d2rq:jdbcDriver "org.postgresql.Driver";
            d2rq:jdbcDSN "jdbc:postgresql://localhost/lu";
            d2rq:username "postgres";
            d2rq:password "123123";
            .

            # Table Culture
            map:Culture a d2rq:ClassMap;
            d2rq:dataStorage map:Assolement;
            d2rq:uriPattern "http://za-geminat.cnrs.fr/Landuse/@@culture.culture_id@@";
            d2rq:class gem:Culture;
            .
            map:libelle a d2rq:PropertyBridge;
            d2rq:belongsToClassMap map:Culture;
            d2rq:property gem:name;
            d2rq:column "culture.libelle";
            .
            </textarea><br>
            <input type='text' id='fn' name='fn' placeholder="Dump file name" size="40">
            <input type="checkbox" id="view"> View <button class="ui-button ui-widget ui-corner-all" id='dump'> Dump
            </button>
        </div>
        <div id='div_right'>
            <textarea rows="40" id="result">   </textarea><br>
        </div>
    </div>
    <script>
        $('#div_right').hide();
        $("#dump").click(function () {

            $.get("RDFDump", {
                    fn: $("#fn").val(),
                    mapping: $("#mapping").val(),
                    view: $('#view').is(':checked')
                },
                function (data, status) {
                    if ($('#view').is(':checked')) {

                        $('#result').html(data);
                        $("#div_right").show(2000);
                    } else {
                        if (data.trim() === "OK") {
                            alert("RDF Dump OK");
                            $('#div_right').hide(2000);
                        }
                    }
                });
        });
    </script>

</html>