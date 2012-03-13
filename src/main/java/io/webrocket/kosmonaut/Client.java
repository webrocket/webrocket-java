package io.webrocket.kosmonaut;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * 
 * Public: Client is an implementation REQ-REP type socket which handles
 * communication between backend application and WebRocket backend endpoint.
 * 
 * Client is used to synchronously request operations from the server.
 * Synchronous operations are used to provide consistency for the backed
 * generated events.
 * 
 * Examples
 * 
 * Client c = new Client("wr://51143c719c576f4018eef1a4f3c505a490ecf4ec@127.0.0.1:8081/test");
 * c.openChannel("test");
 * 
 * HashMap<String, String> data = new HashMap<String,String>();
 * data.put("content", "Hello WebRocket!");
 * c.broadcast("comments", "comment_added", data)
 *
 */
public class Client extends WRSocket{
    private float REQUEST_TIMEOUT = 5;

    public Client(String uri){
        super(uri);
    }

    public String broadcast(String channel, String event, HashMap<String, String> data){
        JSONObject json = new JSONObject(data);
        ArrayList<String> payload = new ArrayList<String>();
        payload.add("BC");
        payload.add(channel);
        payload.add(json.toString());
        return performRequest(payload);
    }

    public String openChannel(String name){
        ArrayList<String> payload = new ArrayList<String>();
        payload.add("OC");
        payload.add(name);
        return performRequest(payload);
    }

    public String closeChannel(String name){
        ArrayList<String> payload = new ArrayList<String>();
        payload.add("CC");
        payload.add(name);
        return performRequest(payload);
    }

    public String returnSingleAccessToken(String uid, String permission){
        ArrayList<String> payload = new ArrayList<String>();
        payload.add("AT");
        payload.add(uid);
        payload.add(permission);
        return performRequest(payload);
    }

    public String getSocketType(){
        return "req";
    }

    public synchronized String performRequest(ArrayList<String> payload){
        ArrayList<String> response;
        this.connect(REQUEST_TIMEOUT);
        String packet = pack(payload, true);
        //TODO: Log
        this.write(packet);
        response = recv(this.socket);
        closeSocket();
        return parseResponse(response);
    }
    
    public String parseResponse(ArrayList<String> response){
        String cmd = response.get(0).toString();
        //TODO: Log
        //Java: Y U NO switch over Strings?
        if ( cmd.equals("OK") ){
            return "0";
        }
        if ( cmd.equals("ER") ){
            int errCode = Integer.parseInt(response.get(1));
            Error error = new Error(errCode);
            System.out.println("ERROR: " + error.toString());
            //TODO: Log
        }
        if ( cmd.equals("AT") ){
            String token = response.get(1).toString();
            if (token.length() == 128){
                return token;
            }
        }
        return null;
    }
}