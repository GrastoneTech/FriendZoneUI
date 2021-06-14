var peer = null;

function init(pInitiator){

    console.log("Init Called Inside"+pInitiator);
    peer = new SimplePeer({ initiator: pInitiator,trickle: false});

    peer.on('stream', stream => {
        var video = document.querySelector('#remote')
        if ('srcObject' in video) {
            video.srcObject = stream
        } else {
            video.src = window.URL.createObjectURL(stream) 
        }
        video.play();

        console.log("Streamed started....")
    })

    peer.on('signal', data => {
        console.log("Peer 1 send", data);
        console.log(JSON.parse(JSON.stringify(data)))
        //peer2.signal(data)
        sendSDP(data);
     })

      peer.on('connect', () => {
            console.log("Connected");
             peer.send('whatever' + Math.random());
              navigator.mediaDevices.getUserMedia({
                  video: true,
                  audio: true
              }).then(addMedia).catch(() => {})
      })

      peer.on('data', data => {
       console.log("data"+data);
      })
    
}


function sendSDP(data){
    console.log(JSON.stringify(data));
    Android.sendSDP(btoa(JSON.stringify(data)));
}


function recieveSDP(data){
    var d = data.trim();
    peer.signal(JSON.parse(atob(d)));
}


function addMedia(stream) {
    peer.addStream(stream);
}

 navigator.mediaDevices.getUserMedia({
     video: true,
     audio: true
 }).then(addMedia).catch(() => {})



