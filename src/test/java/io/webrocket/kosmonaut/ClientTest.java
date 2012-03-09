package io.webrocket.kosmonaut;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.HashMap;

public class ClientTest extends TestCase{
    private Client client = new Client("wr://localhost:8081/test");;
    private String uid;
    
    public ClientTest(String testName){
        super(testName);
    }

    public static Test suite(){
        return new TestSuite(ClientTest.class);
    }
    
    public void testOpenChannel(){
        String response = client.openChannel("test");
    }

    public void testBroadcast(){
        HashMap<String, String> data = new HashMap<String,String>();
        data.put("content", "Hello World!");
        client.broadcast("test", "message", data);
    }
    
    
}