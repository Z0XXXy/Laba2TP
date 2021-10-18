import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{
    private ServerSocket serverSocket;
    private BufferedReader in;
    private BufferedWriter out;
    private Socket client;
    private Jsonb jsonb;
    private static final String startMessage="Server started\n";
    private static final String connectMessage="Client connected\n";
    private static final String welcomeMessage="Welcome!\n";
    private static final String enterMessage="Enter a virazeniye:\n";

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Server(int port) throws IOException {
        this.serverSocket=new ServerSocket(port,2);
        this.jsonb= JsonbBuilder.create(new JsonbConfig());
    }
    public void sendMessage(String message){
        try {
            out.write(jsonb.toJson(message));
            out.newLine();
            out.flush();
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
    public String getMessage(){
        try{
            String json=in.readLine();
            String message=jsonb.fromJson(json,String.class);
            return message;}
        catch (IOException e){
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
            while(true) {
                sendMessage(enterMessage);
                Integer res=Parser.eval(getMessage());
                sendMessage(res.toString());
            }
        }
        catch (IOException e){
            System.out.println("IOException");
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(4004);
            server.run();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
}

