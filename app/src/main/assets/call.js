var peer = null;

function init(pInitiator){

    var video = document.querySelector('#remote');
    try{

        ownFace();
        console.log("Init Called Inside"+pInitiator);
        peer = new SimplePeer({ initiator: pInitiator,trickle: false});
    }catch(e){
        console.log(e)
        Android.onException();
    }
    peer.on('stream', stream => {
    try{
           // var video = document.querySelector('#remote')
            if ('srcObject' in video) {
                video.srcObject = stream
            } else {
                video.src = window.URL.createObjectURL(stream)
            }
            video.play();
            console.log("Streamed started....");
            video.style.opacity=1;
         }catch(e){
            console.log(e)
            Android.onException();
        }
    })

    peer.on('signal', data => {
    try{
        console.log("Peer 1 send", data);
        console.log(JSON.parse(JSON.stringify(data)))
        //peer2.signal(data)
        sendSDP(data);
         }catch(e){
                    console.log(e)
                    Android.onException();
                }

     })

      peer.on('connect', () => {
      try{
            console.log("Connected");
             peer.send('whatever' + Math.random());
              navigator.mediaDevices.getUserMedia({
                  video: true,
                  audio: true
              }).then(addMedia).catch(() => {})
           }catch(e){
                      console.log(e)
                      Android.onException();
             }
      })

      peer.on('data', data => {
      try{
       console.log("data"+data);
        }catch(e){
                             console.log(e)
                             Android.onException();
                    }

      })

      peer.on('close', () => {

         try{
          close();
          Android.next();
          }catch(e){

              console.log(e)
                Android.onException();
          }
      })

     // peer.on('error', (err) => {close();})
    
}


function close(){
        peer.destroy();
        try{
//        Android.next();
        }catch(e){

            console.log(e)
            Android.onException();
        }
}


function sendSDP(data){
try{
    console.log(JSON.stringify(data));
    Android.sendSDP(btoa(JSON.stringify(data)));
    }catch(e){

          console.log(e)
    Android.onException();
    }
}


function recieveSDP(data){
    var d = data.trim();
    try{
        peer.signal(JSON.parse(atob(d)));
    }catch(e){
        //console.log("Unable to recieve signal");

        console.log(e)
        Android.onException();
    }

}


function addMedia(stream) {
    try{
     peer.addStream(stream);
    }catch(e){

                console.log(e)
            Android.onException();
    }

}

 navigator.mediaDevices.getUserMedia({
     video: true,
     audio: true
 }).then(addMedia).catch(() => {})



function ownFace(){
    var video = document.querySelector("#ownFace");

    if (navigator.mediaDevices.getUserMedia) {
      navigator.mediaDevices.getUserMedia({ video: true })
        .then(function (stream) {
          video.srcObject = stream;
        })
        .catch(function (err0r) {
          console.log("Something went wrong!");

           console.log(err0r);
           Android.onException();
        });
    }
}