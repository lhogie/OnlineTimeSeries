
var trace1 = {

  x: [],

  y: [0],

  mode: 'line'

};

var data = [ trace1 ];

var layout = {

title:'Line and Scatter Plot',
yaxis: {
  type: 'log',
  autorange: true
},
  width: 1600,  // or any new width
  height: 500,  // " "
  //xaxis: {range: [-1, 1]},
  yaxis: {range: [-1, 1]}
};

Plotly.newPlot('chart', layout);

var first = true ;
var requestURL = "http://localhost:8084/api/c0/ots.TimeSeriesDB/";

console.log(requestURL);
var requestURL1 = requestURL + "getMetricInfo";

setInterval(function(){
  
  console.log(requestURL1);


  var request = new XMLHttpRequest();
  
  request.open('GET', requestURL1);
  
  request.responseType = 'json';
  
  request.send();
  
  request.onload = function() {
      
  var answer = request.response;

// remplissage du tableau et representation des informations 

//var table = document.getElementsById("metrics");

//var tr = document.getElementsById("names");
  
  var td = document.createElement('td');

  var name = document.createElement('p');


  name.textContent = answer['results'][0][0][0]['name'];
  //lastX.textContent =  answer['results'][0][0][0]['lastX'];
  //lastY.textContent =  answer['results'][0][0][0]['lastY'];
console.log(name.textContent);

  td.appendChild(name);

    
  // graphe introduction
  let trace2 = {

    x: [answer['results'][0][0][0]['lastX']],
  
    y: [answer['results'][0][0][0]['lastY']],
  
    mode: 'line'
  
  }; 
   /*data = [ trace2 ];

  if(window.first){
  Plotly.newPlot('chart', data, layout);
  window.first = false ;
  }

    Plotly.extendTraces('chart',{ x: [[answer['results'][0][0][0]['lastX']]], y: [[answer['results'][0][0][0]['lastY']]]}, [0]);
*/
  }
  
},1000);



function drawGraphe(){
  setInterval(function(){
  
    var x = document.getElementById("chart");
    x.style.display="block";

    var requestURL = "http://localhost:8084/api/c0/ots.TimeSeriesDB/getMetricInfo";
  
    var request = new XMLHttpRequest();
    
    request.open('GET', requestURL);
    
    request.responseType = 'json';
    //request.Origin = "try.html";
    //console.log("data connection");
    
    request.send();
    
    request.onload = function() {
        //console.log("data connection");
        var answer = request.response;
        //console.log(answer);

  // graphe introduction
  let trace2 = {

    x: [answer['results'][0][0][0]['lastX']],
  
    y: [answer['results'][0][0][0]['lastY']],
  
    mode: 'line'
  
  };
    
  
   data = [ trace2 ];

  if(window.first){
  Plotly.newPlot('chart', data, layout);
  window.first = false ;
  }

    Plotly.extendTraces('chart',{ x: [[answer['results'][0][0][0]['lastX']]], y: [[answer['results'][0][0][0]['lastY']]]}, [0]);

  }
  
},1000);
  
}
function myFunction() {
  var x = document.getElementById("chart");
  if (x.style.display === "none") {
    x.style.display = "block";
  } else {
    x.style.display = "none";
  }
}

/*var requestURL = "http://localhost:8084/api/c0/ots.TimeSeriesDB/getMetricInfo";

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

    },10001); // toHeader sera executer a apres 1,001 seconde 
    //setTimeout(toHeader(answer), 20000); // toHeader(answer) sera exécutée au bout de 2 secondes
    //toHeader(answer);
    

// remplissage du tableau et representation des informations 
setInterval(function(){
  //var li = document.createElement("h3");*
  var td1 = document.createElement('td');
  var td2 = document.createElement('td');
  //var name= document.createElement('p');
  var lastX = document.createElement('p');
  var lastY = document.createElement('p');



  var table = document.getElementById("inforRepresentation");
  //var tbody = document.getElementById("metricInformations")
  var tr = document.createElement('tr');
  //name.textContent = 'result :' + jsonObj['results'][0][0][0]['name'];
  lastX.textContent = 'result :' + answer['results'][0][0][0]['lastX'];
  lastY.textContent = 'result :' + answer['results'][0][0][0]['lastY'];

  //li.appendChild(name);
  td1.appendChild(lastX);
  td2.appendChild(lastY);

  tr.appendChild(td1);
  
  tr.appendChild(td2);
  //tbody.appendChild(tr);
  console.log(tr);
  table.appendChild(tr);

  console.log(table)
},1001); // toHeader sera executer a apres 1,001 seconde 
  }


  /*function toHeader(jsonObj) {
    //var li = document.createElement("h3");*
    var td1 = document.createElement('td');
    var td2 = document.createElement('td');
    //var name= document.createElement('p');
    var lastX = document.createElement('p');
    var lastY = document.createElement('p');

    var tr = document.createElement('tr');
    var tbody = document.getElementById("metricInformations")
    //name.textContent = 'result :' + jsonObj['results'][0][0][0]['name'];
    lastX.textContent = 'result :' + jsonObj['results'][0][0][0]['lastX'];
    lastY.textContent = 'result :' + jsonObj['results'][0][0][0]['lastY'];

    //li.appendChild(name);
    td1.appendChild(lastX);
    td2.appendChild(lastY);

    tr.appendChild(td1);
    tr.appendChild(td2)

    tbody.appendChild(tr)

  }*/


 

// Introduction du graphe 
/*
function getData() {
  return Math.random(10);
}


var trace1 = {

  x: answer['results'][0][0][0]['lastX'],

  y: answer['results'][0][0][0]['lastY'],

  mode: 'markers'

};


var trace2 = {

  // x: [2, 3, 4, 5],

  y: [getData()],

  mode: 'lines'

};


var trace3 = {

  //x: [1, 2, 3, 4],

  y: [getData()],

  mode: 'lines+markers'

};


//var data = [ trace1, trace2, trace3 ];
var data = [ trace1 ];


var layout = {

  title:'Line and Scatter Plot'

};

Plotly.newPlot('chart', data, layout);

setInterval(function(){
  //Plotly.extendTraces('chart',data, [0]);
  Plotly.extendTraces('chart',{ y:[[getData()]]}, [0]);
  //Plotly.extendTraces('chart',{ y:[[getData()]]}, [1]);
  //Plotly.extendTraces('chart',{ y:[[getData()]]}, [2]);
},1000);*/


