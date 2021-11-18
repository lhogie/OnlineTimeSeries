$.getJSON("http://localhost:8084/api/c0/ots.TimeSeriesDB/getNbPoints/testMetric", function (json) {
    json['results'][0].forEach((graph) => {
        var ul = document.getElementById("metricList");
        //ul.style.paddingLeft = "4em";
        var list = document.getElementById("list");
        list.style.paddingLeft = "1em";
        list.style.paddingTop = ".8em";
        list.style.fontSize = "22px";
        var li = document.createElement("h4");
        var element = document.createElement('a');
        element.text = graph
        //element.href = "graph.html?gid=" + graph
        element.style.fontSize = "22px";
        h4.appendChild( element );
        ul.appendChild(h4);
    });
})
/*

        $.ajax({
            url: 'http://localhost:8084/api/c0/ots.TimeSeriesDB/getNbPoints/testMetric',
            dataType: 'json',
            jsonpCallback: 'testMetric', // specify the callback name if you're hard-coding it
            success: function(json){
                json['results'][0].forEach((graph) => {
                    var ul = document.getElementById("metricList");
                    //ul.style.paddingLeft = "4em";
                    var list = document.getElementById("list");
                    list.style.paddingLeft = "1em";
                    list.style.paddingTop = ".8em";
                    list.style.fontSize = "22px";
                    var li = document.createElement("h4");
                    var element = document.createElement('a');
                    element.text = graph
                    //element.href = "graph.html?gid=" + graph
                    element.style.fontSize = "22px";
                    h4.appendChild( element );
                    ul.appendChild(h4);
                });
            }
            }
          );



          /*

          http = cockpit.http({
            "address": "localhost",

            "port": 8084,
            "tls": {
                "validate": true,
                }

        });

          //http = api.http(8084);
request = http.get("/c0/ots.TimeSeriesDB/getNbPoints/testMetric");

request.done(function(data) {
    console.log(data);
});

request.fail(function(exception) {
    console.log(exception);
});
*/