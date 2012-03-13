package io.webrocket.kosmonaut;

import java.util.HashMap;

/**
 * Public: Message is an unified wrapper for the incoming events.
 * 
 * Example:
 *
 * class ChatBackend
 * def save_to_history_and_broadcast(msg)
 * room = Room.find(msg.room)
 * room.history.append(msg)
 * msg.broadcast_copy("presence-#{room[:name]}")
 * end
 * end
 *
 */
public class Message {
	/**
	 * Public: The name of the event.
	 */
	private String event;
	private Client client;
	private HashMap<String, String> data;
	
	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}
	
	/**
	 * Internal: Constructor, creates new message.
     *
     * url - The String url of the backend endpoint.
     * event - The String event name.
     * data - The Hash message payload.
     *
	 */
	public Message(String url, String event, HashMap<String, String> data) {
		this.event = event;
		this.client = new Client(url);
		this.data = data;
	}
	
	public Message(String url, String event){
		this.event = event;
		this.client = new Client(url);
	}
	
	/**
	 *  Public: Broadcasts reply to the message on the specified channel.
	 *
	 * channel - The String channel name to broadcast to.
	 * event - The String event name to be broadcasted.
	 * data - The Hash payload.
	 *
	 * Example:
	 *
	 * TODO
	 * def hello(msg)
	 *  msg.broadcast_reply("private-#{msg[:author]}", "hello", {
	 *   :greeting => "Hi, how are you #{msg[:author_full_name]}"
	 *  })
	 * end
	 *
	 */
	public String broadcastReply(String channel, String event, HashMap<String, String> data){
		return client.broadcast(channel, event, data);
	}
	
	/**
	 * Public: Broadcasts copy of the message on the specified channel.
     *
     * channel - The String channel name to broadcast to.
     * event - The String event name to be broadcasted (default: original
     * event name).
     * Example:
     * 
     * TODO
     * 
     * def save_to_history_and_broadcast(msg)
     * 	room = Room.find(msg.room)
     * 	room.history.append(msg)
     * 	msg.broadcast_copy("presence-#{room[:name]}")
     * end
	 */
	public String broadcastCopy(String channel, String event){
		return broadcastReply(channel, event, data);
	}
	
	public String broadcastCopy(String channel){
		return broadcastCopy(channel, this.event);
	}
	
	/**
	 * 
     * Public: Sends direct reply to the message sender.
     *
     * event - The String event name to be broadcasted.
     * data - The Hash payload.
     *
	 */	
	public String directReply(String event, String data) throws Exception{
		// TODO This method is not implemented yet.
		throw new UnsupportedOperationException();
	}
}
