
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
var request = new XMLHttpRequest();

console.log(requestURL);
var requestURL1 = requestURL + "getMetricInfo";

request.open('GET', requestURL1);
// remplissage du tableau et representation des informations 

var tr = document.getElementById("numero");
var tar = document.getElementById("tar");
var table = document.getElementById("inforRepresentation");

request.responseType = 'json';
  
request.send();

request.onload = function() {

var answer = request.response;

for (i = 0; i<answer['results'][0][0].length;  i++){
  var td = document.createElement('td');
  var tdnum = document.createElement('td');

  var metricname = document.createElement('p');
  var metricidentifiant = document.createElement('p');

  metricidentifiant.textContent = i+1;
  metricname.textContent = answer['results'][0][0][i]['name'];

  console.log(answer); 

  td.setAttribute('id',i);
// recuperation des identifiants des metrics . 
td.setAttribute('onclick',"metricinformation()")
tdnum.appendChild(metricidentifiant);
td.appendChild(metricname);

tr.appendChild(tdnum);
tar.appendChild(td);

}
table.appendChild(tr)
table.appendChild(tar);


}
var chart = document.getElementById("chart");

var refreshIntervalId;
function drawGrapheOfOne(){
  refreshIntervalId = setInterval(function(){
  
    chart.style.display="block";

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
   // affichage de notre plan de travail
  if(window.first){
  Plotly.newPlot('chart', data, layout);
  window.first = false ;
  }

    Plotly.extendTraces('chart',{ x: [[answer['results'][0][0][0]['lastX']]], y: [[answer['results'][0][0][0]['lastY']]]}, [0]);

  }
  
},1000);
  
}


function myFunction() {
  
  if (chart.style.display === "none") {
    chart.style.display = "block";
  } else {
    chart.style.display = "none";
  }
}


function drawGrapheOfTwo(){
   refreshIntervalId = setInterval(function(){
  
    
    chart.style.display="block";

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
  let trace1 = {

    x: [answer['results'][0][0][1]['lastX']],
  
    y: [answer['results'][0][0][1]['lastY']],
  
    mode: 'line'
  
  };
    
  
   data = [ trace2 , trace1 ];
   // affichage de notre plan de travail
  if(window.first){
  Plotly.newPlot('chart', data , layout);
  window.first = false ;
  }
  //Plotly.extendTraces('chart',{y: [[[answer['results'][0][0][0]['lastX']]], [[answer['results'][0][0][1]['lastY']]]]}, [0,1]);
  //Plotly.extendTraces('chart',{x: [[[answer['results'][0][0][0]['lastX']]],[[answer['results'][0][0][1]['lastX']]]],y: [[[answer['results'][0][0][0]['lastY']]],[[answer['results'][0][0][1]['lastY']]]]},[0,1]);

    Plotly.extendTraces('chart',{x: [[answer['results'][0][0][1]['lastX']]],y: [[answer['results'][0][0][1]['lastY']]]},[1]);
    Plotly.extendTraces('chart',{x: [[answer['results'][0][0][0]['lastX']]],y: [[answer['results'][0][0][0]['lastY']]]},[0]);
  }
  
},1000);
  
}

function drawGrapheOfMultiple(){
  var compteur =1 ;
  setInterval(function(){
  
    
    chart.style.display="block";

    var requestURL = "http://localhost:8084/api/c0/ots.TimeSeriesDB/getMetricInfo";
  
    var request = new XMLHttpRequest();
    
    request.open('GET', requestURL);
    
    request.responseType = 'json';
    //request.Origin = "try.html";
    //console.log("data connection");
    
    request.send();
    
    request.onload = function() {
        var answer = request.response;
        var idmetric = document.getElementById('idm').value - 1;
        if(compteur <=1){
        data.push({x:[answer['results'][0][0][idmetric]['lastX']],y: [answer['results'][0][0][idmetric]['lastX']],mode :"line"});
          compteur=compteur+1;
      }
   // affichage de notre plan de travail
  if(window.first){
  
  Plotly.newPlot('chart', data , layout);
  window.first = false ;
  }
  
    Plotly.extendTraces('chart',{x: [[answer['results'][0][0][idmetric]['lastX']]],y: [[answer['results'][0][0][idmetric]['lastY']]]},[idmetric]);
   
  }
  
},1000);
  
}

function refresh(){
  document.location.reload(true);
  /*Plotly.deleteTraces(chart,0);
  clearInterval(refreshIntervalId);*/
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


