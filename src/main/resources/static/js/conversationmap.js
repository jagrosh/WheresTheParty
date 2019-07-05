function start() {
  var nodes = new vis.DataSet([]);
  var edges = new vis.DataSet([]);
  var brokenImage = 'https://discordapp.com/assets/6debd47ed13483642cf09e832ed0bc1b.png';
  
  // Connect to the websocket
  connect = function() {
    var socket = new WebSocket(window.location.protocol.replace('http','ws') + '//' + window.location.hostname + ':' + window.location.port + '/ws');
    socket.onopen = function(e) {
      console.log('Successfully connected to server!');
    };
    socket.onmessage = function(e) {
      json = JSON.parse(e.data);
      if(json.stats) {
          if(json.stats.sessions)
              document.getElementById('sessions').innerHTML = json.stats.sessions;
          if(json.stats.servers)
              document.getElementById('servers').innerHTML = json.stats.servers;
      } else if(json.user) {
        let data;
        let now = new Date();

        data = {id:json.user.id, label:json.user.name, image:json.user.avatar, shape:'circularImage', date:now};
        if(!nodes.get(json.user.id))
          nodes.add(data);
        else
          nodes.update(data);

        data = {id:json.channel.id, label:'#'+json.channel.name, group:json.guild.id, shape:'box', font: { color:'#2C2F33' }, date:now};
        if(!nodes.get(json.channel.id))
          nodes.add(data);
        else
          nodes.update(data);

        data = {id:json.guild.id, label:json.guild.name, group:json.guild.id, image:(json.guild.icon ? json.guild.icon : brokenImage), shape:'image', date:now};
        if(!nodes.get(json.guild.id))
          nodes.add(data);
        else
          nodes.update(data);

        data = {id:json.user.id+"-"+json.channel.id, from:json.user.id, to:json.channel.id, date:now};
        if(!edges.get(json.user.id+"-"+json.channel.id))
          edges.add(data);
        else
          edges.update(data);

        data = {id:json.channel.id+"-"+json.guild.id, from:json.channel.id, to:json.guild.id, date:now};
        if(!edges.get(json.channel.id+"-"+json.guild.id))
          edges.add(data);
        else
          edges.update(data);
      }
    };
    socket.onclose = function(e) {
      console.log('Disconnected... Reconnecting in 30 seconds.');
      setTimeout(connect, 30000);
    };
  };
  connect();

  cleanup = function() {
    let now = new Date()

    filter = function (item) {
      return now - item.date > 600000;
    };

    nodes.forEach(function (item) {nodes.remove(item.id)}, {filter: filter});
    edges.forEach(function (item) {edges.remove(item.id)}, {filter: filter});

    setTimeout(cleanup, 1000);
  };
  cleanup();

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

function toggleInfobox() {
  var im = document.getElementById("infobox-main");
  var sb = document.getElementById("small-button");
  if (im.style.display === "none") {
    im.style.display = "block";
    sb.style.display = "none";
  } else {
    im.style.display = "none";
    sb.style.display = "block";
  }
}
