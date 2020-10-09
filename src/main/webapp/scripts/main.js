var editor;
editor = ace.edit("text");
editor.session.setMode("ace/mode/sparql");

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
var raster = new ol.layer.Tile({
    source: new ol.source.OSM()
});
var format = new ol.format.WKT();
var features = [];
var vectorSource = new ol.source.Vector({
    features: features //add an array of features
    //,style: iconStyle     //to set the style for all your features...
});
var vectorLayer = new ol.layer.Vector({
    source: vectorSource
});
var rq = "";
var oldColumn = [];
var algo = "none";
var param = 0;
var viewmode = 3;

function listVariables(q) {
    var list;
    v = q.replace(/\./g, '');
    var va = v.split("\n");
    //alert(q);
    for (i = 0; i < va.length; i++)
        if (va[i].toLowerCase().includes("select")) {
            // If select all, find all variables
            if (va[i].includes("*")) {
                var re = /\?\w+\s*\.*/g;
                var variable = v.match(re);
                for (j = 0; j < variable.length; j++)
                    variable[j] = variable[j].trim().replace("?", "");
                var unique = variable.filter(function (item, i, ar) {
                    return ar.indexOf(item) === i;
                });
                // alert(variable);
                return unique;
            }
            // Find selected variables otherwise
            else {
                list = [];
                va[i] = va[i].replace("select", "").trim();
                va2 = va[i].split("?");
                for (j = 1; j < va2.length; j++)
                    list.push(va2[j]);
                return list;
            }
        }
}

function query(jsonlink) {
    //     var list = [];
    //   $("#status").html("Receiving data..");

    $.get(jsonlink, function (data) {
        //       $("#status").html("");
        dat = JSON.parse(data);

        if (viewmode !== 1)
            makeTable(dat);
        if (viewmode !== 2) {
            var isGeom = false;
            $.each(dat[0], function (name, da) {

                if (name.includes("geom"))
                    isGeom = true;
            });
            if (isGeom)
                loadMap(dat);
            else
            if (editor.getValue().includes("group by"))
                loadChart(dat);
        }
    });
    //    $("#status").html("");
}

function makeTable(data) {
    //   $("#status").html("Rendering list..");
    //alert("data");
    $("#div_list").tabulator("destroy");
    $("#div_list").tabulator({
        width: "98%",
        layout: "fitColumns",
        rowClick: function (e, row) { //trigger an alert message when the row is clicked

            //  row.getData().MMSI_Number;
            features = vectorSource.getFeatures();
            $.each(features, function (index, feature) {
                // alert(feature.get("id"));
                if (feature.get("id") * 1.0 === row.getData().id * 1.0) {
                    //  alert("Row " + feature.get("id")+ " Clicked!!!!");
                    content.innerHTML = feature.get("name");
                    //  alert(feature.getGeometry().getCoordinates()[0][0]);
                    overlay.setPosition(feature.getGeometry().getCoordinates()[0]);
                    return;
                }
            });
        }

    });

    $.each(dat[0], function (name, da) {
        $("#div_list").tabulator("addColumn", {
            title: name,
            field: name,
            cellDblClick: function (e, cell) {
                window.open("RDFD.jsp?query=" + escape(cell.getValue()), '', 'height=800,width=1500');
            }
        });
    });
    $("#div_list").tabulator("setData", dat);
    $("#div_list").tabulator("hideColumn", "id");
    //   $("#status").html("");
}

// Show the result in the bottom
function show(s) {
    s = s.replace(/=/g, "=\"");
    s = s.replace(/ &&/g, "\" &&");
    s = s.replace(")", "\")");
    if (s.includes("http")) {
        s = s.replace(/"h/g, "<h");
        s = s.replace(/"/g, ">");
    }
    rq2 = rq.replace("}", s + " }");
    linklist = 'Query?f=json&q=' + escape(rq2.replace(/\n/g, " "));
    // $("#div_list").load(linklist);
}


function showrule(r) {
    $("#status").html("Loading..");
    $.get("apriori.jsp?q=&r=" + r, function (data) {
        //       $("#status").html("");
        dat = JSON.parse(data);
        makeTable(dat);
        loadMap(dat);
    });
    $("#status").html("");
}



function showJRip(n) {
    $("#status").html("Loading..");
    $.get("jrip.jsp?q=&n=" + n, function (data) {
        //       $("#status").html("");
        dat = JSON.parse(data);
        makeTable(dat);
        loadMap(dat);
    });
    $("#status").html("");
}


function showcluster(c) {

    $("#status").html("Loading..");
    $.get("kmeans.jsp?q=&c=" + c, function (data) {
        dat = JSON.parse(data);
        makeTable(dat);
        loadMap(dat);
    });
    $("#status").html("");
}



//Show the chart page
function chart() {
    window.open('chart.jsp', '', 'width=1600, height=1000');
}
//Prepare geometry data and call the visualization page
$(document).ready(function () {

    window.name = "myMainWindow";
    //Prepare the dialog window
    $(function () {
        $("#dialog").dialog({
            autoOpen: false,
            modal: true,
            height: 400,
            width: 350,
            buttons: {
                "OK": function () {
                    $("#dialog").dialog("close");
                    mining();
                },
                Cancel: function () {
                    $("#dialog").dialog("close");
                }
            }
        });
        $("#config").dialog({
            autoOpen: false,
            modal: true,
            height: 550,
            width: 700,
        });
        $("#mining").dialog({
            autoOpen: false,
            modal: false,
            height: 550,
            width: 650,
            open: function (event, ui) {
                // Reset Dialog Position
                $(this).dialog('widget').position({
                    my: "left bottom",
                    at: "left bottom",
                    of: window
                });
            },
            buttons: {
                "OK": function () {
                    $("#mining").dialog("close");
                }

            }
        });
    });
    //$("#kmean").hide();

    // Open the browser page

    // Show options when changing algo 

    //Show a dialog for feature selection   
    function ShowVariables() {
        list = listVariables(editor.getValue());
        s = "<select id='listvariable' multiple='multiple' size='10'>";
        for (j = 0; j < list.length; j++)
            // if(va2[j])
            s += "<option value='" + (j + 1) + "' selected='selected'>" + list[j] + "</option>";
        s += "</select>";
        // Fill the dialog and display it
        $("#dialog").html(s);
        $("#dialog").dialog("open");
    }

    var table = 0;

    function mining() {

        variable = "";
        $('#listvariable :selected').each(function (i, selected) {
            variable += ($(selected).val()) + "-";
        });
        rq = editor.getValue();
        rq = escape(rq.replace(/\n/g, " ").replace(/  /g, " ")).trim();
        // apriori
        switch ($("#algo").val() * 1.0) {
            case 1:
                link = 'apriori.jsp?q=' + rq + '&supp=' + $("#supp").val() + "&conf=" + $("#conf").val() + "&var=" + variable;
                break;
            case 2:
                link = 'part.jsp?q=' + rq + '&num=' + $("#numPart").val() + '&conf=' + $("#confPart").val() + "&var=" + variable;
                break;
            case 3:
                link = 'kmeans.jsp?q=' + rq + '&num=' + $("#num").val() + "&var=" + variable;
                break;
            case 4:
                link = 'jrip.jsp?q=' + rq + '&num=' + $("#numjrip").val() + "&var=" + variable;
                break;
            case 0:
                link = 'Query?algo=none&param=0&f=json&q=' + rq + "&var=" + variable;
                break;
        }
        // alert(link);
        $.get(link,
            function (data, status) {

                $("#mining").html(data);
                $("#mining").dialog("open");
            });
        //  $('#map').load(link);
    }

    // Start button click handle
    $("#start").click(function (event) {
        if ($("#algo").val() * 1.0 > 0)
            ShowVariables();
        //load data and show Tabulator
        else {
            rq = editor.getValue();
            rq = escape(rq.replace(/\n/g, " ").replace(/  /g, " ")).trim();
            link = 'Query?algo=none&param=0&f=json&q=' + rq;
            query(link);
        }

        // $("body").css("cursor", "default");       
    });
});
var url = location.href; // or window.location.href for current url
//  var captured = /myParam=([^&]+)/.exec(url)[1]; // Value is in [1] ('384' in our case)
var q = location.search.split('q')[1];
var s = " PREFIX strdf: <http://strdf.di.uoa.gr/ontology#>" +
    "\n PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
    "\n PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
    "\n PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
    "\n PREFIX time: <http://www.w3.org/2006/time#>" +
    "\n PREFIX gem: <http://za-geminat.cnrs.fr/Assolement.owl#>" +
    "\n SELECT ?name1 ?name2 ?dt1 ?geom1" +
    "\n WHERE " +
    "\n {" +
    "\n  ?ts1 rdf:type gem:TimeSlice." +
    "\n  ?ts1 gem:hasFiliation ?ts2." +
    "\n  ?ts1 gem:hasLandUse ?lu1." +
    "\n  ?ts2 gem:hasLandUse ?lu2." +
    "\n  ?lu1 gem:lname ?name1." +
    "\n  ?lu2 gem:lname ?name2." +
    "\n  ?ts1 gem:pgeometry ?geom1." +
    "\n  ?ts1 gem:hasTime ?t1." +
    "\n  ?t1 time:hasBeginning ?bg1." +
    "\n  ?bg1 time:inXSDDateTime ?dt1." +
    "\n }" +
    "\n LIMIT 1000";
if (q === undefined) {
    editor.setValue(s);
} else {
    var q1 = unescape(q);
    q1 = q1.replace(/\+/g, ' ');
    q1 = q1.substr(1);
    editor.setValue(q1);
}


$("#div_list").tabulator({
    fitColumns: true,
    placeholder: "No Data Set",
});
$("#download-csv").click(function () {
    $("#div_list").tabulator("download", "csv", "data.csv");
});
//trigger download of data.json file
$("#download-json").click(function () {

    $("#div_list").tabulator("download", "json", "data.json");
});
$("#btn_map").click(function () {

    window.open('map.jsp?algo=' + algo + '&param=' + param + '&q=' + rq, '', 'width=1600, height=1000');
});
$("#btn_chart").click(function () {
    window.open('chart.jsp?q=' + rq, '', 'width=1600, height=1000');
});
$("#dump").click(function () {
    window.open('dump.jsp?q=' + rq, '', 'width=1900, height=800');
});

$("#KBdump").click(function () {

    var popout = window.open('KBDump', '', 'width=10,height=10');
    window.setTimeout(function () {
        popout.close();
    }, 5000);
});
$("#browse").click(function () {
    window.open("Browser.jsp?dir=" + $("#querypath").val(), 'browser', 'width=800,height=900,toolbar=0,menubar=0,location=0');
});
//Open the store page
$("#store").click(function () {
    window.open("store.jsp", 'browser', 'width=1000,height=600,toolbar=0,menubar=0,location=0');
});
// Send data to the GEMINAT store to update
$("#update").click(function () {
    $.post("StoreManager", {
            query: editor.getValue(),
            type: "update"
        },
        function (data, status) {
            alert(data);
        });
});
// Send query to the SaveQuery page to save it
$("#save").click(function () {
    var fname = prompt("File name:", "");
    $.post("SaveQuery.jsp", {
            fn: fname,
            text: editor.getValue()
        },
        function (data, status) {
            alert(data);
        });
});
// To adapt to the menu representation 
function Open() {
    window.open("Browser.jsp?dir=" + $("#querypath").val(), 'browser', 'width=800,height=900,toolbar=0,menubar=0,location=0');
}

function OpenCache() {
    window.open("Browser.jsp?dir=" + $("#cachepath").val(), 'browser', 'width=800,height=900,toolbar=0,menubar=0,location=0');
}


function Store() {
    window.open("store.jsp", 'browser', 'width=1000,height=600,toolbar=0,menubar=0,location=0');
}

function SPARQLUpdate() {
    $.post("StoreManager", {
            query: editor.getValue(s),
            type: "update"
        },
        function (data, status) {
            alert(data);
        });
}

function Save() {
    var fname = prompt("File name:", "");
    $.post("SaveQuery.jsp", {
            fn: fname,
            text: editor.getValue()
        },
        function (data, status) {
            alert(data);
        });
}

function KBDump() {
    var popout = window.open('KBDump', '', 'width=10,height=10');

}

function RDBDump() {
    window.open('dump.jsp?q=' + rq, '', 'width=1900, height=800');
}

function Delete(fn) {
    $.post("CacheDelete", {

            fn: fn.substring(8)
        },
        function (data, status) {
            alert(data);
        });
}


function DownloadCSV() {
    $("#div_list").tabulator("download", "csv", "data.csv");
}
//trigger download of data.json file
function DownloadJSON() {

    $("#div_list").tabulator("download", "json", "data.json");
}
$.get("Config", {
        job: "down"
    },
    function (data) {

        var param = data.split('\n');
        $("#host").val(param[0]);
        $("#port").val(param[1]);
        $("#storename").val(param[2]);
        $("#supp").val(param[3]);
        $("#conf").val(param[4]);
        $("#num").val(param[5]);
        $("#min").val(param[6]);
    });

function OpenConfig() {



    $("#config").dialog("open");
}


function view(v) {
    viewmode = v;
    //Map only
    if (v == 1) {
        $("#viz").height(840);
        map.updateSize();
        $("#div_list").height(0);
    } else
        // List only
        if (v == 2) {


            $("#viz").height(0);
            $("#div_list").height(840);
        } else
    //Both
    {


        $("#viz").height(530);
        $("#div_list").height(310);
        map.updateSize();
    }


}

var items = [{
        "href": "#",
        "text": "Query",
        "children": [{
            "href": "javascript:Open();",
            "text": "Open"
        }, {
            "href": "javascript:OpenCache();",
            "text": "Open Cache"
        }, {
            "href": "javascript:Save();",
            "text": "Save"
        }]
    },
    {
        "href": "#",
        "text": "Knowledge base",
        "children": [{
            "href": "javascript:SPARQLUpdate();",
            "text": "SPARQL Update"
        }, {
            "href": "javascript:RDBDump();",
            "text": "Dump RDF"
        }, {
            "href": "javascript:Store();",
            "text": "Load RDF"
        }, {
            "href": "javascript:KBDump();",
            "text": "Dump KB"
        }]
    },
    {
        "href": "#",
        "text": "List",
        "children": [{
            "href": "javascript:DownloadCSV();",
            "text": "Download CSV"
        }, {
            "href": "javascript:DownloadJSON();",
            "text": "Download JSON"
        }]
    },
    {
        "href": "#",
        "text": "View",
        "children": [{
            "href": "javascript:view('1');",
            "text": "Map/Chart only"
        }, {
            "href": "javascript:view('2');",
            "text": "List only"
        }, {
            "href": "javascript:view('3');",
            "text": "Map/Chart and List"
        }]
    },
    {
        "href": "javascript:OpenConfig();",
        "text": "Configuration"
    },
    //  {"href": "#", "text": "Mining"},
];
var $menu = $('#myMenu').menuList({
    data: items
});
$menu.smartmenus();
$.get("CacheCheck", {
        query: editor.getValue(),
    },
    function (data, status) {

        $("#md5").html(data + "<button onclick=\"Delete('" + data + "')\"> Delete </button>");
    });

function getParameterByName(name) {
    url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results)
        return null;
    if (!results[2])
        return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}



var color = ["#0e2431", "#415b90", "#c4aff0", "#65799b"];

function pointStyle(index) {

    var style = new ol.style.Style({
        image: new ol.style.Circle({
            radius: 4,
            fill: new ol.style.Fill({
                color: color[index]
            })

        })

    });
    return [style];
}

function polygonStyle(index) {
    var style = new ol.style.Style({

        stroke: new ol.style.Stroke({
            color: color[3 - index],
            width: 1

        })


    });
    return [style];
}


function loadMap(data)

{

    //$("#status").html("Rendering map..");

    $("#map").show();
    vectorSource.clear();
    var list = listVariables(editor.getValue());
    $.each(data, function (index, item) {
        var poly = 0;
        var point = 0;
        var name = "";
        for (i = 0; i < list.length; i++) {

            if (list[i].includes("geom")) {
                var geom = eval("item." + list[i]);
                geom = geom.replace(/'/g, '');
                if (geom.includes("MULTIPOLYGON")) {
                    geom = geom.replace("MULTIPOLYGON", "MULTILINESTRING");
                    geom = geom.replace("(((", "((");
                    geom = geom.replace(")))", "))");
                    poly++;
                } else
                if (geom.includes("POLYGON")) {
                    geom = geom.replace("POLYGON", "LINESTRING");
                    geom = geom.replace("((", "(");
                    geom = geom.replace("))", ")");
                    poly++;
                }
                if (geom.includes("POINT")) {
                    point++;
                }

                var f = format.readFeature(geom, {
                    dataProjection: 'EPSG:4326',
                    featureProjection: 'EPSG:3857'
                });
                // alert(index);
                f.set("name", name);
                f.set("id", item.id);
                if (point > 1 || poly > 1)
                    if (geom.includes("POINT"))
                        f.setStyle(pointStyle(point - 1));
                    else
                        f.setStyle(polygonStyle(poly - 1));
                vectorSource.addFeature(f);
                name = "";
            } else {
                name = name + "<b>" + list[i] + ":</b> " + eval("item." + list[i]) + "<br>";
            }

        }

        //set map center



    });
    var mousePositionControl = new ol.control.MousePosition({
        coordinateFormat: ol.coordinate.createStringXY(4),
        projection: 'EPSG:4326',
        className: 'custom-mouse-position',
        target: document.getElementById('mouse-position'),
        undefinedHTML: ''
    });
    /*  map = new ol.Map({
     controls: ol.control.defaults({
     attributionOptions: ({
     collapsible: false
     })
     }).extend([mousePositionControl]),
     target: 'map',
     layers: [
     new ol.layer.Tile({
     source: new ol.source.OSM()
     }),
     vectorLayer
     ],
     view: new ol.View({
     center: ol.proj.transform([-0.33, 46.3], 'EPSG:4326', 'EPSG:3857'),
     
     zoom: 13
     }),
     });
     */
    //   $("#map").html("");

    map.getView().fit(vectorSource.getExtent(), {
        duration: 1000
    });
    //map.getView().fitExtent(vectorsource.getExtent(), map.getSize() { duration: 1000 });); 
    //  map.render();

    //$("#status").html("");
}

// Afficher les coordonnées en function de la position du curseur
var mousePositionControl = new ol.control.MousePosition({
    coordinateFormat: ol.coordinate.createStringXY(4),
    projection: 'EPSG:4326',
    className: 'custom-mouse-position',
    target: document.getElementById('mouse-position'),
    undefinedHTML: ''
});
var hiddenStyle = new ol.style.Style({
    image: new ol.style.RegularShape({}) //a shape with no points is invisible
});
map = new ol.Map({
    controls: ol.control.defaults({
        attributionOptions: /** @type {olx.control.AttributionOptions} */ ({
            collapsible: false
        })
    }).extend([mousePositionControl]),
    target: 'map',
    layers: [
        new ol.layer.Tile({
            source: new ol.source.OSM()
        })
    ],
    view: new ol.View({

        center: ol.proj.transform([-0.33, 46.3], 'EPSG:4326', 'EPSG:3857'),
        zoom: 13
    }),
});
map.addLayer(vectorLayer);
// Chagement du style du curseur quand il tombe sur un navire
var cursorHoverStyle = "pointer";
var target = map.getTarget();
var jTarget = typeof target === "string" ? $("#" + target) : $(target);
map.on("pointermove", function (evt) {
    //  var mouseCoordInMapPixels = [event.originalEvent.offsetX, event.originalEvent.offsetY];

    //detect feature at mouse coords
    var hit = map.forEachFeatureAtPixel(evt.pixel, function (feature, layer) {
        // alert(feature.get('name'));
        return true;
    });
    if (hit) {
        jTarget.css("cursor", cursorHoverStyle);
    } else {
        jTarget.css("cursor", "");
    }
});
// Affichage des infos d'un navire 
var container = document.getElementById('popup');
var content = document.getElementById('popup-content');
var closer = document.getElementById('popup-closer');
var overlay = new ol.Overlay( /** @type {olx.OverlayOptions} */ ({
    element: container,
    autoPan: true,
    autoPanAnimation: {
        duration: 250
    }
}));
map.addOverlay(overlay);
/**
 * Add a click handler to hide the popup.
 * @return {boolean} Don't follow the href.
 */
closer.onclick = function () {
    overlay.setPosition(undefined);
    closer.blur();
    return false;
};
map.on('singleclick', function (evt) {
    // alert("ok");

    map.forEachFeatureAtPixel(evt.pixel, function (feature, layer) {

        content.innerHTML = feature.get("name");
        overlay.setPosition(evt.coordinate);
    });
    // var coordinate = evt.coordinate;

});

function loadChart(data) {
    $("#map").hide();
    var type = '3';
    var header = [];
    // If to viz less than 2 columns (combo char is not possible)

    //   if ($('#list', window.opener.document).find("tr:first th").length <= 3)
    type = prompt("Chart type \n1. Column chart \n2. Line chart \n3. Combo chart \n4. Pie chart \n5. Scatter chart", "1");
    google.charts.load('current', {
        'packages': ['corechart', 'table']
    });
    // Set a callback to run when the Google Visualization API is loaded.
    if (type === '3')
        google.charts.setOnLoadCallback(drawChart(data)); // combo chart
    else
        google.charts.setOnLoadCallback(drawChart2(data)); //the others
}

// draw combo chart 
function drawChart(data) {

    $("#mouse-position").html("Rendering chart..");
    var values = []; // to hold our values for data table
    // get our values
    var u0 = [];
    var u1 = [];
    var values = [];
    var j = 0;
    //alert( $('#list',window.opener.document).html());
    delete data.id;
    values[0] = [];
    $.each(data, function (i, v) {
        //  alert(i);
        values[i + 1] = [];
        j = 0;
        // select either th or td, doesn't matter
        $.each(v, function (ii, vv) {

            if (i === 0)
                values[0][j] = ii;
            if (isNaN(vv))
                values[i + 1][j] = vv;
            else
                values[i + 1][j] = vv * 1.0;
            if (j === 1)
                if (u0.indexOf(vv) < 0)
                    u0.push(vv);
            //unique values of the second column
            if (j === 2)
                if (u1.indexOf(vv) < 0)
                    u1.push(vv);
            j++;
        });
    });
    //Create row header 
    var matrix = [];
    for (i = 0; i < u0.length + 1; i++) {
        matrix[i] = new Array(u1.length + 1);
        if (i > 0)
            matrix[i][0] = u0[i - 1];
    }

    //Create column header
    for (i = 0; i < u1.length; i++) {
        matrix[0][i + 1] = u1[i];
    }
    matrix[0][0] = "*";
    // convert 2d array to dataTable
    for (i = 1; i < values.length; i++) {

        matrix[u0.indexOf(values[i][1]) + 1][u1.indexOf(values[i][2]) + 1] = values[i][0];
        //    alert(values[i][0]+" "+values[i][1]+" "+values[i][2]);
        //    alert(u0.indexOf(values[i][0]))
    }

    ti = prompt("Graph's title", "Visualization");
    var options = {
        'title': ti,
        'height': 600,
        seriesType: 'bars'
    };
    var data = new google.visualization.arrayToDataTable(matrix);
    var table = new google.visualization.Table(document.getElementById('table_div'));
    var chart = new google.visualization.ComboChart(document.getElementById('chart_div'));
    // table.draw(data);
    chart.draw(data, options);
}


// draw the others
function drawChart2(data) {
    var values = [];
    var j = 0;
    //alert( $('#list',window.opener.document).html());
    delete data.id;
    values[0] = [];
    $.each(data, function (i, v) {
        //  alert(i);
        values[i + 1] = [];
        j = 0;
        // select either th or td, doesn't matter
        $.each(v, function (ii, vv) {

            if (i === 0)
                values[0][j] = ii;
            if (isNaN(vv))
                values[i + 1][j] = vv;
            else
                values[i + 1][j] = vv * 1.0;
            // alert(values[i][ii]);
            j++;
        });
    });
    var temp;
    for (i = 0; i < values.length; i++) {
        temp = values[i][0];
        values[i][0] = values[i][values[i].length - 1];
        values[i][values[i].length - 1] = temp;
    }





    // convert 2d array to dataTable and draw
    ti = prompt("Please enter the graph's title", "Visualization");
    var options = {
        'title': ti,
        'height': 500
    };
    var data = new google.visualization.arrayToDataTable(values);
    var table = new google.visualization.Table(document.getElementById('table_div'));
    var chart;
    if (type == '1')
        chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
    else
    if (type == '2')
        chart = new google.visualization.LineChart(document.getElementById('chart_div'));
    else
    if (type == '4')
        chart = new google.visualization.PieChart(document.getElementById('chart_div'));
    else
        chart = new google.visualization.ScatterChart(document.getElementById('chart_div'));
    //  table.draw(data);
    chart.draw(data, options);
}

$("#algo").selectmenu();
$("#hide").click(function () {


    if ($(".left").width() >= 80) {
        $(".left").width(0);
        $(".right").width("100%");
        map.updateSize();
        $("#hide").text("Query");
    } else // sinon, on l'affiche
    {
        $(".left").width("25%");
        $(".right").width("74%");
        map.updateSize();
        $("#hide").text("✖ Query");
    }
});
var options = {

    complete: function (response) {
        $("#config").dialog("close");
    }
};
$("#ConfigForm").ajaxForm(options);
editor.setValue(s);