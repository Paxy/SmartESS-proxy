import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;

public class ModbusClient implements Runnable {

    private Engine engine;
    private Socket srv;

    public ModbusClient(Engine engine) throws IOException {
        this.engine = engine;
    }

    public void run() {
        while (true) {
            try {
                srv=new Socket(engine.realModbusServer,502);
                String time = new Timestamp(System.currentTimeMillis()).toString();
                System.out.println(time + " - Connected to server "
                        + srv.getInetAddress().getHostAddress());
                InputStream in=srv.getInputStream();
                while (true) {
                    while (in.available() == 0) {
                        Thread.currentThread().sleep(100);
                    }
                    byte[] data=in.readNBytes(in.available());
                    String hex=Engine.bytesToHex(data);
                    time = new Timestamp(System.currentTimeMillis()).toString();
                    System.out.println(time+" - Server: "+hex);
                    int ret=engine.nsrv.sendData(data);
                    if(ret==-1) break;
                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public int sendData(byte[] data) throws InterruptedException {
        if(this.srv==null) {
            String time = new Timestamp(System.currentTimeMillis()).toString();
            System.out.println(time + " - Waiting for server ...");
            while(srv==null) Thread.currentThread().sleep(100);
        }
        try {
            if(this.srv.getOutputStream()==null) return -1;

        OutputStream out=this.srv.getOutputStream();
        out.write(data);
        out.flush();

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

}
