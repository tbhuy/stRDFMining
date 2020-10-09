var type = '3';
var link = location.href.replace("chart.jsp?", "list.jsp?f=json&param=0&algo=none&");
var header = [];
$.ajaxSetup({
    async: false
});
// If to viz less than 2 columns (combo char is not possible)

//   if ($('#list', window.opener.document).find("tr:first th").length <= 3)
type = prompt("Chart type \n1. Column chart \n2. Line chart \n3. Combo chart \n4. Pie chart \n5. Scatter chart",
    "1");
google.charts.load('current', {
    'packages': ['corechart', 'table']
});
// Set a callback to run when the Google Visualization API is loaded.
if (type === '3')
    google.charts.setOnLoadCallback(drawChart); // combo chart
else
    google.charts.setOnLoadCallback(drawChart2); //the others

// draw combo chart 
function drawChart() {
    var values = []; // to hold our values for data table
    // get our values
    var u0 = [];
    var u1 = [];

    var values = [];
    var j = 0;
    //alert( $('#list',window.opener.document).html());
    $.getJSON(link, function (result) {
        values[0] = [];
        $.each(result, function (i, v) {
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
        seriesType: 'bars',
    };
    var data = new google.visualization.arrayToDataTable(matrix);
    var table = new google.visualization.Table(document.getElementById('table_div'));
    var chart = new google.visualization.ComboChart(document.getElementById('chart_div'));
    table.draw(data);
    chart.draw(data, options);
}
// draw the others
function drawChart2() {
    var values = [];
    var j = 0;
    //alert( $('#list',window.opener.document).html());
    $.getJSON(link, function (result) {
        values[0] = [];
        $.each(result, function (i, v) {
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
        'height': 600
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
    table.draw(data);
    chart.draw(data, options);
}