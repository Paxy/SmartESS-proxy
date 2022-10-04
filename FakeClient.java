import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;

public class FakeClient extends ModbusClient implements Runnable {

    public FakeClient(Engine engine) throws Exception {
        super(engine);
        this.engine = engine;
    }

    private Engine engine;
    private Socket srv;

    private static String cfg = "3D0A0001000EFF020102030405080C0E191A2041";
    private static String ping = "3D0B0001000AFF011609190F00350023";
    private static String getData = "3D0C00010003001100";

    public void run() {
        while (true) {
            try {
                sendMsgToClient(cfg);
                int cnt = 0;
                while (true) {
                    int res = sendMsgToClient(getData);
                    if (res == -1)
                        break;
                    Thread.currentThread()
                            .sleep(engine.fakeClientUpdateFrequency * 1000);

                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public int sendData(byte[] data) throws InterruptedException {
        return 0;
    }
    private int sendMsgToClient(String msg) throws InterruptedException {
        while (engine.nsrv == null || engine.nsrv.node == null)
            Thread.currentThread().sleep(100);
        byte[] data = engine.hexStringToByteArray(msg);
        int res = engine.nsrv.sendData(data);
        String time = new Timestamp(System.currentTimeMillis()).toString();
        System.out.println(time + " - Server: " + msg);
        return res;
    }

}
