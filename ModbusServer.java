import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;

public class ModbusServer implements Runnable {

    private Engine engine;
    public Socket node;
    private boolean close = false;

    public ModbusServer(Engine engine) throws IOException {
        this.engine = engine;
    }

    public void run() {
        ServerSocket ss;
        try {
            ss = new ServerSocket(502);
            while (true) {
                node = ss.accept();
                String time = new Timestamp(System.currentTimeMillis())
                        .toString();
                System.out.println(time + " - Connected node "
                        + node.getInetAddress().getHostAddress());
                InputStream in = node.getInputStream();
                this.close=false;
                while (true) {
                    while (in.available() == 0) {
                        Thread.currentThread().sleep(100);
                        if (close)
                            break;
                    }
                    Thread.currentThread().sleep(100);
                    if (close)
                        break;
                    byte[] data = in.readNBytes(in.available());
                    String hex = Engine.bytesToHex(data);
                    time = new Timestamp(System.currentTimeMillis()).toString();
                    System.out.println(time + " - Node: " + hex);
                    int res=engine.ncli.sendData(data);
                    // engine.mqtt.sendMsg("data", hex);
                    engine.lastData = data;
                    if(res==-1) break;
                }

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int sendData(byte[] data) throws InterruptedException {
        if (this.node == null) {
            String time = new Timestamp(System.currentTimeMillis()).toString();
            System.out.println(time + " - Waiting for node ...");
            while (node == null)
                Thread.currentThread().sleep(100);
        }
        try {
            if (this.node.getOutputStream() == null) {
                this.node=null;
                this.close=true;
                return -1;
            }
            if (!node.isConnected()) {
                this.node=null;
                this.close=true;
                return -1;
            }

            OutputStream out = this.node.getOutputStream();
            out.write(data);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
            try {
                this.node.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            this.node=null;
            this.close=true;
            return -1;
        }
        return 0;
    }

}
