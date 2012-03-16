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

    /**
     * Public: Broadcasts a event with attached data on the specified channel.
     * The data attached to the event must be a hash!
     *
     * @param channel - A name of the channel to broadcast to.
     * @param event - A name of the event to be triggered.
     * @param data - The data attached to the event.
     * 
     * Examples
     * 
     * HashMap<String, String> data = new HashMap<String,String>();
     * data.put("message", "on the meeting");
     * String response = client.broadcast("room", "away", data);
     * 
     * Returns 0 if succeed.
     * TODO Raises one of the Kosmonaut::Error inherited exceptions.
     *
     */
    public String broadcast(String channel, String event, HashMap<String, String> data){
        JSONObject json = new JSONObject(data);
        ArrayList<String> payload = new ArrayList<String>();
        payload.add("BC");
        payload.add(channel);
        payload.add(json.toString());
        return performRequest(payload);
    }

    /**
     * Public: Opens specified channel. If channel already exists, then ok
     * response will be received anyway. If channel name is starts with the
     * `presence-` or `private-` prefix, then appropriate type of the channel
     * will be created.
     *
     * @param name - A name of the channel to be created.
     *
     * Examples
     * 
     * client.openChannel("room");
     * client.openChannel("presence-room");
     * client.openChannel("private-room");
     *
     * Returns 0 if succeed.
     * TODO: Raises one of the Kosmonaut::Error inherited exceptions.
     */
    public String openChannel(String name){
        ArrayList<String> payload = new ArrayList<String>();
        payload.add("OC");
        payload.add(name);
        return performRequest(payload);
    }

    /**
     * Public: Closes specified channel. If channel doesn't exist then an
     * error will be thrown.
     * 
     * @param name - A name of the channel to be deleted.
     * @return
     *
     * Examples
     *
     * client.closeChannel("test");
     * client.closeChannel("presence-room")
     *
     * Returns 0 if succeed.
     * 
     * TODO Raises one of the Kosmonaut::Error inherited exceptions.
     * 
     */
    public String closeChannel(String name){
        ArrayList<String> payload = new ArrayList<String>();
        payload.add("CC");
        payload.add(name);
        return performRequest(payload);
    }

    /**
     * Public: Sends a request to generate a single access token for given
     * user with specified permissions.
     *
     * @param uid An user defined unique ID.
     * @param permission - A permissions regexp to match against the channels.
     * 
     * Examples
     *
     * client.requestSingleAccessToken(user.getName(), ".*");
     * Returns generated access token string if succeed.
     * TODO: Raises one of the Kosmonaut::Error inherited exceptions.
     * 
     * @return token
     */
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
    
    
    /**
     * Performs request with specified payload and waits for the
     * response with it's result.
     * 
     * @param payload
     *
     * Returns response result if succeed.
     * TODO Raises one of the Kosmonaut::Error inherited exceptions.
     */
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
    
    /**
     *  Internal: Parses given response and discovers it's result according
     *  to the WebRocket Backend Protocol specification.
     *
     *  @param response
     *
     *  Response format
     *
     * 0x01 | command \n |
     * 0x02 | payload... \n | *
     * 0x.. | ... \n | *
     * | \r\n\r\n |
     *
     * Returns response result if succeed.
     * Raises one of the Kosmonaut::Error inherited exceptions.
     * @return
     */
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
            return error.toString();
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