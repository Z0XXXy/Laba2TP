import java.io.BufferedReader;import java.io.BufferedWriter;import java.io.IOException;import java.io.InputStreamReader;import java.io.OutputStreamWriter;import java.net.ServerSocket;import javax.json.bind.Jsonb;import javax.json.bind.JsonbBuilder;import javax.json.bind.JsonbConfig;import java.net.Socket;import javax.json.bind.JsonbException;


public class Server implements Runnable {

  private ServerSocket serverSocket;
  private BufferedReader in;
  private BufferedWriter out;
  private Socket client;
  private Jsonb jsonb;
  private static final String startMessage = "Server started";
  private static final String connectMessage = "Client connected";
  private static final String welcomeMessage = "Welcome!";
  private static final String enterMessage = "Enter a virazeniye:";

  public ServerSocket getServerSocket() {

    return serverSocket;
  }

  public void setServerSocket(ServerSocket serverSocket) {

    this.serverSocket = serverSocket;
  }

  public Server(int port) throws IOException {
    this.serverSocket = new ServerSocket(port, 2);
    this.jsonb = JsonbBuilder.create(new JsonbConfig());
  }

  public void sendMessage(String message) throws JsonbException {
    try {
      out.write(jsonb.toJson(message));
      out.newLine();
      out.flush();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  public String getMessage() throws JsonbException {
    try {
      String json = in.readLine();
      String message = jsonb.fromJson(json, String.class);
      return message;
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    return "error";
  }

  @Override
  public void run() {
    try {
      System.out.println(startMessage);
      this.client = serverSocket.accept();
      System.out.println(connectMessage);
      in = new BufferedReader(new InputStreamReader(client.getInputStream()));
      out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
      sendMessage(welcomeMessage);
      while (true) {
        sendMessage(enterMessage);
        Integer res = Parser.eval(getMessage());
        sendMessage(res.toString());
      }
    } catch (IOException| JsonbException|NumberFormatException e) {
      System.out.println("IOException");
    }
  }

  public static void main(String[] args) {
    try {
      Server server = new Server(4004);
      server.run();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}

