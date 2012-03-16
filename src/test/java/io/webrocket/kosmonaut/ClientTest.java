package io.webrocket.kosmonaut;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.HashMap;
import java.util.UUID;

public class ClientTest extends TestCase{
    private Client client;
    
    public ClientTest(String testName){
        super(testName);
        client = new Client("wr://51143c719c576f4018eef1a4f3c505a490ecf4ec@127.0.0.1:8081/test");
    }

    public static Test suite(){
        return new TestSuite(ClientTest.class);
    }
    
    public void testOpenChannel(){
        String response = client.openChannel("chat");
        assertEquals("0", response);
    }
    
    public void testBroadcast(){
        HashMap<String, String> data = new HashMap<String,String>();
        data.put("content", "Hello WebRocket!");
        String response = client.broadcast("chat", "message", data);
        assertEquals("0", response);
    }
    
    public void testRequestToken(){
    	String token = client.returnSingleAccessToken(UUID.randomUUID().toString(), ".*");
    	assertNotNull(token);
    }
    
    public void testCloseChannel(){
    	String response = client.closeChannel("chat");
    	assertEquals("0", response);
    }
    
    public void testCloseWrongChannel(){
    	String response = client.closeChannel("test");
    	assertEquals("454 - " + Error.getErrorString(454), response);
    }
    
}