import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.net.Socket;
import javax.json.bind.JsonbException;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Client implements Runnable {

  private Socket socket;
  private BufferedReader reader;
  private BufferedReader in;
  private BufferedWriter out;
  private BufferedWriter fileWriter;
  private String message;
  private Jsonb jsonb;
  private ObjectMapper objectMapper;
  private Schema schema;
  private Lip lip;

  public BufferedReader getReader() {
    return reader;
  }

  public void setReader(BufferedReader reader) {
    this.reader = reader;
  }

  public BufferedReader getIn() {
    return in;
  }

  public void setIn(BufferedReader in) {
    this.in = in;
  }

  public BufferedWriter getOut() {
    return out;
  }

  public void setOut(BufferedWriter out) {
    this.out = out;
  }

  public BufferedWriter getFileWriter() {
    return fileWriter;
  }

  public void setFileWriter(BufferedWriter fileWriter) {
    this.fileWriter = fileWriter;
  }

  public Client(int port) throws IOException {
    this.socket = new Socket("localhost", port);
    this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    this.reader = new BufferedReader(new InputStreamReader(System.in));
    this.jsonb = JsonbBuilder.create(new JsonbConfig());
    this.objectMapper=new ObjectMapper();
    this.fileWriter=new BufferedWriter(new FileWriter("exp.json"));
    FileInputStream inputStream = new FileInputStream("schema.json");
    JSONObject jsonSchema = new JSONObject(new JSONTokener(inputStream));
    this.schema = SchemaLoader.load(jsonSchema);
    System.out.println("Client Started. Enter the message.");
  }

  public Lip getMessage() throws JsonbException{
    try {
      String json = in.readLine();
      Lip lip=objectMapper.readValue(json,Lip.class);
      return lip;
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    return null;
  }
  public void saveLip() throws IOException{
    fileWriter.write(jsonb.toJson(lip));
    fileWriter.flush();
  }
  public void sendMessage(String message) throws JsonbException{
    try {
      Msg msg=new Msg();
      msg.setExpression(message);
      String json=objectMapper.writeValueAsString(msg);
      schema.validate(new JSONObject(json));
      out.write(jsonb.toJson(message));
      out.newLine();
      out.flush();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    catch (ValidationException e) {
      System.err.println(e.getMessage());
      System.exit(0);
    }
  }

  @Override
  public void run() {
    try {
      while (true) {
        String sendingMessage=reader.readLine();
        if (sendingMessage.equals("save")){
          this.saveLip();
          System.out.println("Json was saved.");
        }
        else {
          sendMessage(sendingMessage);
          lip = getMessage();
          System.out.println(lip);
        }

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
