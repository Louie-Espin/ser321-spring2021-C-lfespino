import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;

import org.json.*;

public class ServerTask extends Thread {
	private BufferedReader bufferedReader;
	private Peer peer = null;
	private PrintWriter out = null;
	private Socket socket = null;

	public ServerTask(Socket socket, Peer peer) throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		this.peer = peer;
		this.socket = socket;
	}

	public void run() {
		while (true) {
			try {
				JSONObject json = new JSONObject(bufferedReader.readLine());

				switch (json.getString("type")) {
					case "join":
						System.out.println(json.getString("username") + " wants to join the network");
						peer.updateListenToPeers(json.getString("ip") + "-" + json.getInt("port") + "-" + json.getString("username") + "-" + json.getBoolean("leader"));
						out.println(("{'type': 'join', 'list': '" + peer.getPeers() + "'}"));

						if (peer.isLeader()) {
							peer.pushMessage(json.toString());
						}

						break;
					case "remove":
						System.out.println("Updating the list!");
						peer.removePeer(json.getInt("port"));
						out.println(json);
						
						if (peer.isLeader()) {
							peer.pushMessage(json.toString());
						}

						break;
					case "joke":
						System.out.println(json.getString("username") + " has submitted a joke");
						out.println(("{'type': 'joke', 'message': '"+json.getString("message")+"'}"));

						if (peer.isLeader()) {
							if (peer.getTempJoke() == null) {
								peer.setTempJoke(json.getString("message"));
							}
							peer.pushMessage("{'type': 'jokeQuery', 'message': '"+peer.getTempJoke()+"'}");
						}

						break;
					case "jokeQuery":
						System.out.println("Please vote on the following joke: '"+json.getString("message")+"'");

						break;
					case "yes":
						System.out.println("'Yes' vote: "+json.getString("username")+"'");
						out.println(json);

						if (peer.isLeader() && peer.getTempJoke() != null) {

							peer.pushMessage("{'type': 'message', 'username': 'LEADER', 'message': '"+peer.getTempJoke()+" was voted YES'}");

							peer.addVote();

							if (peer.getVotes() == peer.getPeerCount()) {
								peer.addJoke(peer.getTempJoke());
								peer.pushMessage("{'type': 'addJoke', 'message': '"+peer.getJokes()+"'}");
								peer.setTempJoke(null);
								peer.setVotes(0);
							}


						}

						break;
					case "no":
						System.out.println("'No' vote: '"+json.getString("username")+"'");
						out.println(json);

						if (peer.isLeader() && peer.getTempJoke() != null) {
							peer.pushMessage("{'type': 'message', 'username': 'LEADER', 'message': '"+peer.getTempJoke()+" was voted NO, joke discarded'}");
							peer.setTempJoke(null);
							peer.setVotes(0);
						}

						break;
					case "addJoke":
						System.out.println("Joke list has been updated...");

						peer.addJoke(json.getString("message"));

						out.println(json);

						break;
					default:
						System.out.println("[" + json.getString("username")+"]: " + json.getString("message"));
						break;
				}

			}
			catch (Exception e) {
				interrupt();
				break;
			}
		}
	}

}
