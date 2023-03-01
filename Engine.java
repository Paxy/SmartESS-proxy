import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Engine {

    static boolean fakeClient = true;
    static String mqttServer = "172.16.2.1";
    static boolean enableMqttAuth=false;
    static String mqttUser="";
    static String mqttPass="";
    static int mqttPort = 1883;
    static String mqttTopic = "paxyhome/Inverter/";
    static int fakeClientUpdateFrequency = 10; // 10 seconds
    static String realModbusServer="47.242.188.205";


    static Executor pool = Executors.newFixedThreadPool(4);
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF"
            .getBytes(StandardCharsets.US_ASCII);
    ModbusServer nsrv;
    ModbusClient ncli;
    MQTTClient mqtt;

    byte[] lastData;

    public Engine() throws Exception {

        Properties p = new Properties();
        p.load(new FileInputStream("conf.ini"));
        fakeClient = Boolean.parseBoolean(p.getProperty("fakeClient"));
        mqttServer = p.getProperty("mqttServer");
        mqttPort = Integer.parseInt(p.getProperty("mqttPort"));
        enableMqttAuth = Boolean.parseBoolean(p.getProperty("enableMqttAuth"));
        mqttUser = p.getProperty("mqttUser");
        mqttPass = p.getProperty("mqttPass");
        mqttTopic = p.getProperty("mqttTopic");
        fakeClientUpdateFrequency = Integer.parseInt(p.getProperty("updateFrequency"));


        nsrv = new ModbusServer(this);
        pool.execute(nsrv);
        if (!fakeClient)
            ncli = new ModbusClient(this);
        else
            ncli = new FakeClient(this);
        pool.execute(ncli);
        mqtt = new MQTTClient(this);
        pool.execute(mqtt);
        ProcessInverterData procesor = new ProcessInverterData(this);
        pool.execute(procesor);

        Thread.currentThread().sleep(5000);
        // test();
    }

    public static void main(String[] args) throws Exception {
        new Engine();

    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = (char) HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = (char) HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
