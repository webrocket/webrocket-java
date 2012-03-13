package io.webrocket.kosmonaut;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public abstract class Worker extends WRSocket {
	private boolean isAlive;
	private float heartbitAt; 
	private int heartbitInterval;
	
	/**
	 *  Number of milliseconds after which client should retry to reconnect
     *  to the backend endpoint. 
     */
	private int RECONNECT_DELAY = 1000;
	
    /**
     *  Number of milliseconds between next heartbeat message.
     */
    private int HEARTBEAT_INTERVAL = 500;


    /**
     * Public: The Worker constructor. Pre-configures the worker instance. See
     * also the Kosmonaut::Socket#initialize to get more information.
     * 
     * url - The WebRocket backend endpoint URL to connect to.
     */
	public Worker(String uri) {
		super(uri);
		isAlive = false;
		heartbitAt = (float) 0;
		heartbitInterval = HEARTBEAT_INTERVAL;
		
	}
	
	/**
	 * Public: Starts a listener's loop for the worker. Listener implements
	 * the Majordomo pattern to manage connection with the backend.
	 * 
	 * Raises Kosmonaut::UnauthorizedError if worker's credentials are invalid.
	 */
	public boolean run(){
		if (isAlive){
			return false;
		}
		isAlive = true;
		
		//Signal.trap("INT") { @alive = false }
		Signal.handle(new Signal("INT"), new SignalHandler() {
			  public void handle(Signal signal) {
			    isAlive = false;
			  }
			});
		reconnect(false);
		
		while (true){
			if (isAlive == false){
				disconnect();
				break;
			}
			while (socket == null){
				reconnect(true);
			}
			
			//TODO receive_and_process or (disconnect and next)
			receiveAndProcess();
			disconnect();
			
			heartbeatIfTime();
			break;
		}
		
		
		return false;
	}
	
	/**
	 * Internal: Sets up new connection with the backend endpoint and sends
     * information that it's ready to work. Also initializes heartbeat
     * scheduling.
     *
     * wait - If true, then it will wait before the reconnect try
	 */
	public void reconnect(boolean wait){
		if (wait == false){
			try {
				Thread.sleep(new Long(RECONNECT_DELAY));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		socket = connect(new Float((HEARTBEAT_INTERVAL * 2) / 1000.0) + 1);
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add("RD");
		send(socket, cmd, true);
		this.heartbitAt = new Float(new Date().getTime()) + (this.HEARTBEAT_INTERVAL / 1000);
	  	//TODO rescue Errno::ECONNREFUSED
	}
	
	/**
	 * Internal: Sends heartbeat message to the server and updates
     * heartbeat schedule.
	 */
	public void heartbeatIfTime(){
		if (new Float(new Date().getTime()) > this.heartbitAt){
			ArrayList<String> payload = new ArrayList<String>();
			payload.add("HB");
			send(socket, payload);
			this.heartbitAt = new Float( new Date().getTime() ) + (heartbitInterval / 1000); 
		}
	}
	
	/**
	 *  Internal: Packs given payload and writes it to the specified socket.
	 *  
	 *  sock - The socket to write to.
	 *  payload - The payload to be packed and sent.
	 *  withIdentity - Whether identity should be prepend to the packet.
    */
	public void send(Socket socket, ArrayList<String> payload, boolean withIdentity){
		String packet = pack(payload, withIdentity);
		this.write(packet);
		// TODO
		//  Kosmonaut.log("Worker/SENT : #{packet.inspect}")
		// rescue Errno::EPIPE
	}
	
	public void send(Socket socket, ArrayList<String> payload){
		send(socket, payload, false);
	}

	@Override
	protected String getSocketType() {
		return "dlr";
	}
	
    /**
     *  Internal: Receives a message from the server and dispatches it.
     *
     *  Returns false if message couldn't be processed or socket has been closed.
     */
	public boolean receiveAndProcess(){
		try {
			ArrayList<String> message = recv(socket);
			//TODO log
			return (dispatch(message));
		} catch (Exception e) {
			//TODO log
			return false;
		}
	}
	
	/**
	 *  Internal: Dispatches the incoming message.
     *
     *  message - A message to be dispatched.
     *
     *  Returns false if server sent a quit message.
	 */
	public boolean dispatch(ArrayList<String> message){
        String cmd = message.get(0).toString();
        //TODO: Log
        if ( cmd.equals("HB") ){
            //nothing to do
        }
        if ( cmd.equals("QT") ){
            return false;
        }
        if ( cmd.equals("TR") ){
        	//messageHandler(message);
        }
        if (cmd.equals("ER")){
        	int errorCode = (Integer) (message.size() < 1 ? 597 : message.get(1));
        	errorHandler(errorCode);
        }
        return true;
    }
	
	/**
	 *  # Internal: Message handler routes received data to user defined
     *  'on_message' method (if exists) and handles it's exceptions.
     *
     *  data - The data to be handled.
     *
    def message_handler(data)
      if respond_to?(:on_message)
        payload = JSON.parse(data.to_s)
        message = Message.new(self.uri.to_s, *payload.first)
        on_message(message)
      end
    rescue => err
      exception_handler(err)
    end
	 */
	public void messageHandler(HashMap<String, String> data){
		try {
			JSONObject payload = new JSONObject(data.toString());
			Message message = new Message(this.uri.toString(), payload.toString());
			onMessage(message);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Internal: Disconnects and cleans up the socket if connected.
	 *
	 * Returns always true.
	 */
	public boolean disconnect(){
		if (this.socket != null){
			ArrayList<String> payload = new ArrayList<String>();
			payload.add("QT");
			send(socket, payload, true);
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return true;
			}
		}
		return true;
	}
	
	/**
	 *     # Internal: Handles given WebRocket error.
    #
    # errcode - A code of the received error.
    #
    # Raises Kosmonaut::UnauthorizerError if error 402 has been received.
    def error_handler(errcode)
      if respond_to?(:on_error)
        err = ERRORS[errcode.to_i]
        if err == UnauthorizedError
          raise err.new
        end
        begin
          on_error((err ? err : UnknownServerError).new)
        rescue => err
          exception_handler(err)
        end
      end
    end
	 */
	private void errorHandler(int errorCode){
		Error error = new Error(errorCode);
		// TODO Error handling
		System.err.println(error.toString());
	}
	

	public Float getHeartbitAt() {
		return heartbitAt;
	}

	public void setHeartbitAt(Float heartbitAt) {
		this.heartbitAt = heartbitAt;
	}
	
	/**
	 * Abstract methods which should be implemented
	 */
	public abstract void onMessage(Message message);
	public abstract void onError(Message message);
	public abstract void onException(Message message);
	

}
