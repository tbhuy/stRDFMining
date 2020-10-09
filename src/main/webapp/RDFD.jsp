<%-- 
    Document   : RDFData
    Created on : Nov 7, 2016, 11:08:22 AM
    Author     : Huy Tran
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ulr.l3i.strdfmining.QueryProcessor"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>RDF Data</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <style>
        .list td,
        .list th {
            max-width: 500px;
            min-width: 30px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            border-bottom: 1px solid #ddd;
            border: 1px solid #ddd;
            padding: 8px;
            cursor: pointer;
            cursor: hand;
        }


        .list tr:nth-child(even) {
            background-color: #f2f2f2;
        }

        .list td:hover {
            background-color: #ddd;
        }

        .list th {
            padding-top: 12px;
            padding-bottom: 12px;
            text-align: left;
            background-color: #4CAF50;
            color: white;
        }

        div {
            display: inline-block;
        }

        .list {


            font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
            border-collapse: collapse;
            width: 100%;
        }
    </style>

</head>

<body>
    <%
           String req = StringEscapeUtils.unescapeJavaScript(request.getParameter("query"));
            //If it's a point or polygon, send to the viz page
            if (req.lastIndexOf("POLY") >= 0 || req.lastIndexOf("POINT") >= 0) {
                response.sendRedirect("visual.jsp?query=" + request.getParameter("query"));
            } //show data table orthewise
            else {
                String q1 = "";
                if (req.indexOf("http") >= 0) {
                    q1 = "select * where { ?subject ?predicate ?object . FILTER((?subject = <" + req + ">)  || (?object = <" + req + ">)) }";
                } else {
                    q1 = "select * where { ?subject ?predicate ?object . FILTER((?object = \"" + req + "\")) }";
                }
                out.write("<h3>" + req + "</h3>");
                out.write("<input type='hidden' id='body' value='" + req + "'>");
                String s[] = QueryProcessor.queryCSV(q1).split("\n");
                String agents[] = QueryProcessor.queryCSV("Select ?s where {?s a <http://xmlns.com/foaf/0.1/Person>.}").split("\n");
                String targets[] = QueryProcessor.queryCSV("Select ?s where {?s a <http://za-geminat.cnrs.fr/Assolement.owl#Suspicion>.}").split("\n");
                out.write("<b> Annotate this ? </b> <br/>");
                out.write("Agent <select id='agent'>" );
                for(int i = 1; i < agents.length; i++ )
                {
                    out.write("<option value='" + agents[i] + "'>" + agents[i] + "</option>");
                }
                out.write("</select>");
                out.write("Target <select id='target'>" );
                for(int i = 1; i < targets.length; i++ )
                {
                    out.write("<option value='" + targets[i] + "'>" + targets[i] + "</option>");
                }
                out.write("</select>");
                out.write("<button type='button' id='annotate'> OK </button> ");
                out.write("<table id='list' class='list'>");
                out.write("<tr><th>" + s[0].split(",")[0] + "</th><th>" + s[0].split(",")[1] + "</th><th>" + s[0].split(",")[2]+ "</th></tr>");

                for (int i = 1; i < s.length; i++) {
                    String ss[] = s[i].split(",");
                    out.write("<tr>");
                    if (ss.length > 3) {
                        for (int k = 3; k < ss.length; k++) {
                            ss[2] = ss[2] + ", " + ss[k];
                        }
                        ss[2] = ss[2].replaceAll("\"", "");
                    }
                    for (int j = 0; j < 3; j++) // if(ss[j].trim().equals(req.trim()) || (j==1))
                    {
                        out.write("<td>" + ss[j].trim() + "</td>");
                    }
                    // else
                    //  out.write("<td><a href=''>"+ss[j].trim()+"</a></td>");
                    out.write("</tr>");

                }
                out.write("</table>");
            }
        %>

    <script>
        // Handle data table click
        $('#list td').click(function (event) {
            var query = $(this).text();
            //  var query="select * where { ?subject ?predicate ?object . FILTER((?subject = "+e+") || (?predicate = "+e+")  || (?object = "+e+")) }";
            window.open("RDFD.jsp?query=" + escape(query), '', 'height=800,width=1500');
            event.stopPropagation();
            event.preventDefault();
        });
        let ts = Math.round(new Date().getTime() / 1000);
        let xsd = new Date().toISOString();
        $('#annotate').click(function (event) {
            $.post("InsertAnnotation", {
                    triple: '<http://za-geminat.cnrs.fr/Annotation/' + ts + '> a <http://www.w3.org/ns/oa#OA>.\n\
                        <http://za-geminat.cnrs.fr/Annotation/' + ts + '> <http://www.w3.org/ns/oa#annotatedBy> <' + $(
                            '#agent').val() + '>. \n\
                        <http://za-geminat.cnrs.fr/Annotation/' + ts + '> <http://www.w3.org/ns/oa#annotatedAt> "' +
                        xsd + '"^^<http://www.w3.org/2001/XMLSchema#dateTime>. \n\
                        <http://za-geminat.cnrs.fr/Annotation/' + ts + '> <http://www.w3.org/ns/oa#hasTarget> <' + $(
                            '#target').val() + '>. \n\n\
                        <http://za-geminat.cnrs.fr/Annotation/' + ts + '> <http://www.w3.org/ns/oa#hasBody> <' + $(
                            '#body').val() + '>.'
                },
                function (data) {
                    alert(data);
                });
        });
    </script>
</body>

</html>