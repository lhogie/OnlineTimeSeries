/*$.getJSON("http://localhost:8084/api/c0/ots.TimeSeriesDB/getNbPoints/testMetric", function (json) {
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
})*/

var requestURL = "http://localhost:8084/api/c0/ots.TimeSeriesDB/getNbPoints/testMetric";

var request = new XMLHttpRequest();

request.open('GET', requestURL);

request.responseType = 'json';
//request.Origin = "try.html";
console.log("data connection");

request.send();

request.onload = function() {
    console.log("data connection");
    var answer = request.response;
    console.log(answer);
    toHeader(answer);
    
  }

  function toHeader(jsonObj) {
    var myH1 = document.createElement('h1');
    myH1.textContent = 'result :' + jsonObj['result'][0];
    body.appendChild(myH1);

  }

 