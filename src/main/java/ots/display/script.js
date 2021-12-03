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

var requestURL = "http://localhost:8084/api/c0/ots.TimeSeriesDB/getMetricInfo";

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
    setInterval(function(){
      var li = document.createElement("h3");
      var name= document.createElement('p');
      var lastX = document.createElement('p');
      var lastY = document.createElement('p');

      var ul = document.getElementById("metricList");
      name.textContent = 'result :' + answer['results'][0][0][0]['name'];
      lastX.textContent = 'result :' + answer['results'][0][0][0]['lastX'];
      lastY.textContent = 'result :' + answer['results'][0][0][0]['lastY'];

      li.appendChild(name);
      li.appendChild(lastX);
      li.appendChild(lastY);

      ul.appendChild(li)

    },1001); // toHeader sera executer a apres 1,001 seconde 
    //setTimeout(toHeader(answer), 20000); // toHeader(answer) sera exécutée au bout de 2 secondes
    //toHeader(answer);
    
  }

  function toHeader(jsonObj) {
    var li = document.createElement("h3");
    var name= document.createElement('p');
    var lastX = document.createElement('p');
    var lastY = document.createElement('p');

    var ul = document.getElementById("metricList");
    name.textContent = 'result :' + jsonObj['results'][0][0][0]['name'];
    lastX.textContent = 'result :' + jsonObj['results'][0][0][0]['lastX'];
    lastY.textContent = 'result :' + jsonObj['results'][0][0][0]['lastY'];

    li.appendChild(name);
    li.appendChild(lastX);
    li.appendChild(lastY);

    ul.appendChild(li)

  }


  $(function() {
    $('#aperçu').click(function() {
      var donnees = '[\'results\'][0][0][0]'
        $.getJSON(requestURL, function(donnees) {
        $('#r').html('<p><b>Nom</b> : ' + donnees.lastX + '</p>');
        $('#r').append('<p><b>Age</b> : ' + donnees.lastY + '</p>');
        $('#r').append('<p><b>Ville</b> : ' + donnees.name + '</p>');
        });
    });
});

 