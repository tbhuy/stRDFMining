<%-- 
    Document   : store
    Created on : Nov 12, 2016, 11:18:46 AM
    Author     : huy
--%>

<%@page import="ulr.l3i.strdfmining.Config"%>
<%@page import="java.io.File"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>RDF Storing</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
    <script src="http://malsup.github.com/jquery.form.js"></script>
    <script>
        // Set cursor
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
    <%
            File dir = new File(getServletContext().getRealPath("/").replace('\\', '/') + "KB/Store");

            File[] list = dir.listFiles();
            String s = "";
            for (int c = 0; c < list.length; c++) {
                if (list[c].isFile()) {
                    if (list[c].getName().indexOf("jsp") < 0) {
                        s += "<option value='" + list[c].getAbsolutePath() + "'>" + list[c].getName() + "</option>";
                    }
                }
            }

          //  out.write("<input type='hidden' id='endpoint' value='" + Config.endpoint + "'>");
        %>
    <FORM method="POST" name="form" enctype="UTF-8" accept-charset="UTF-8" enctype="multipart/form-data">

        <fieldset>
            <legend>Login information</legend>
            <div class="controlgroup">
                Username <input type="text" name="user" id="user" value="endpoint">
                Password <input type="password" name="pass" id="pass" value="3ndpo1nt">
            </div>
        </fieldset>
        <fieldset>
            <legend>RDF Format:</legend>
            <div class="controlgroup">

                <SELECT name="format" title="select one of the following RDF graph format types">

                    <OPTION value="RDF/XML">RDF/XML</OPTION>

                    <OPTION value="N-Triples">N-Triples</OPTION>

                    <OPTION value="Turtle">Turtle</OPTION>

                    <OPTION value="N3">N3</OPTION>

                    <OPTION value="TriX">TriX</OPTION>

                    <OPTION value="TriG">TriG</OPTION>

                    <OPTION value="BinaryRDF">BinaryRDF</OPTION>

                </SELECT>

            </div>
        </fieldset>
        <fieldset>
            <legend>Direct Input</legend>
            <div class="controlgroup">

                <textarea id="data" name="data" rows="15" cols="100"></textarea></td>
                <br />
                <input type="button" id="direct" value="Store Input" name="dsubmit" style="width: 350px" />
            </div>
        </fieldset>

        <fieldset>
            <legend>File Input</legend>
            <div class="controlgroup">
                Server files:
                <input type='hidden' id='url' name="url">
                <input type='hidden' id='type' name="type" value="Store">
                <select id='file'>
                    <%= s%>
                </select>

                <br />
                <INPUT id="uri" type="button" value="Store from file" name="fromurl" style="width: 350px" />

            </div>
        </fieldset>
    </form>
    <fieldset>
        <legend>Upload File</legend>
        <form id="UploadForm" action="FileUpload" method="post" enctype="multipart/form-data">
            <input type="file" size="60" id="myfile" name="myfile"> <br />
            <input type="submit" value="Upload" style="width: 350px">

            <div id="progressbox">
                <div id="progressbar"></div>
                <div id="percent"></div>
            </div>

        </form>
        </div>
    </fieldset>


    <script src="scripts/store.js"> </script>
</body>

</html>