Kosmonaut - Java client for WebRocket
=========================================

Kosmonaut is a Java backend client for WebRocket. The idea of Kosmonaut is to keep it simple, straightforward and easy to maintain, 
although to allow to build more sophisticated libraries on top of it.

Installation
------------

    TODO

Usage
-----
Kosmonaut has two components: Client and Worker. Client is used to manage a WebRocket's vhost and broadcast messages, for example:

    Client client = new Client("wr://token@127.0.0.1:8081/vhost");
    client.openChannel("world");
    
    HashMap<String, String> data = new HashMap<String, String>();
    data.put("who", "Chris");
    client.broadcast("test", "hello", data);
        
Worker is used to listen for incoming messages and handle it in user's desired way, example:

    TODO

Hacking
-------

    TODO
    
Sponsors
--------
All the work on the project is sponsored and supported by Cubox - an awesome dev shop from Uruguay <http://cuboxlabs.com>.
   
Copyright
---------
Copyright (C) 2012 Krzysztof Kowalik <chris@nu7hat.ch> and folks at Cubox

Released under the MIT license. See COPYING for details.