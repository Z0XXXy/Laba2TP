import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.io.*;
import java.net.Socket;
import javax.json.bind.JsonbException;

public class Client implements Runnable {

  private Socket socket;
  private BufferedReader reader;
  private BufferedReader in;
  private BufferedWriter out;
  private String message;
  private Jsonb jsonb;

  public Client(int port) throws IOException {
    socket = new Socket("localhost", port);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    reader = new BufferedReader(new InputStreamReader(System.in));
    jsonb = JsonbBuilder.create(new JsonbConfig());
  }

  public String getMessage() throws JsonbException{
    try {
      String json = in.readLine();
      String message = jsonb.fromJson(json, String.class);
      return message;
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    return "error";
  }

  public void sendMessage(String message) throws JsonbException{
    try {
      out.write(jsonb.toJson(message));
      out.newLine();
      out.flush();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  @Override
  public void run() {
    try {
      System.out.println(getMessage());
      while (true) {
        System.out.println(getMessage());
        sendMessage(reader.readLine());
        System.out.println(getMessage());
      }
    } catch (IOException| JsonbException|NumberFormatException e) {
      System.err.println(e.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      Client player = new Client(4004);
      player.run();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}
