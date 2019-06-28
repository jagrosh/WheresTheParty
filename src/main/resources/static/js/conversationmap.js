function start() {
  var nodes = new vis.DataSet([]);
  var edges = new vis.DataSet([]);
  
  // Connect to the websocket
  var socket = new WebSocket(window.location.protocol.replace('http','ws') + '//' + window.location.hostname + '/ws');
  socket.onopen = function(e) {
    console.log('Successfully connected to server!');
  };
  socket.onmessage = function(e) {
    json = JSON.parse(e.data);
    if(json.total) {
        document.getElementById('total').innerHTML = json.total;
    } else {
      if(!nodes.get(json.user.id))
          nodes.add({id:json.user.id, label:json.user.name, image:json.user.avatar, shape:'circularImage'});
      if(!nodes.get(json.channel.id))
          nodes.add({id:json.channel.id, label:'#'+json.channel.name, group:json.guild.id, shape:'box', font: { color:'#2C2F33' }});
      if(!nodes.get(json.guild.id))
          nodes.add({id:json.guild.id, label:json.guild.name, group:json.guild.id, image:json.guild.icon, shape:'image'});
      if(!edges.get(json.user.id+"-"+json.channel.id))
          edges.add({id:json.user.id+"-"+json.channel.id, from:json.user.id, to:json.channel.id});
      if(!edges.get(json.channel.id+"-"+json.guild.id))
          edges.add({id:json.channel.id+"-"+json.guild.id, from:json.channel.id, to:json.guild.id});
    }
  };
  
  // Create the veiwable network
  var container = document.getElementById('network');
  var data = {
    nodes: nodes,
    edges: edges
  };
  var options = {
    nodes: {
      borderWidth:4,
      size:30,
      color: {
        highlight: { 
          border: '#FFFFFF', 
          background: '#2C2F33'
        },
        border: '#99AAB5',
        background: '#2C2F33'
      },
      font: { color:'#99AAB5' }
    },
    edges: { color: '#99AAB5' }
  };
  var network = new vis.Network(container, data, options);
}