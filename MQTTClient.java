import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTClient implements Runnable, MqttCallback {

    private Engine engine;
    private IMqttClient client;
    private String prefix;

    public static final String chargeSolarOnly = "40630001000A05040506139900031CE4";
    public static final String chargeSolarUtility = "48E30001000A0504050613990002DD24";
    public static final String loadUtility = "490A0001000A05040506139A0000ACE5";
    public static final String loadSBU = "490D0001000A05040506139A00022D24";

    public MQTTClient(Engine engine) throws Exception {
        this.engine = engine;
        this.prefix = engine.mqttTopic;

        try {
            String publisherId = UUID.randomUUID().toString();
            MemoryPersistence persistence = new MemoryPersistence();
            this.client = new MqttClient(
                    "tcp://" + engine.mqttServer + ":" + engine.mqttPort,
                    publisherId, persistence);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            client.connect(options);
            while (!client.isConnected())
                Thread.currentThread().sleep(100);
            String time = new Timestamp(System.currentTimeMillis()).toString();
            System.out.println(time + " - MQTT connected");
            client.setCallback(this);
            client.subscribe("paxyhome/Inverter/Set/#");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {

    }

    public void sendMsg(String topic, String msg) {
        if (!client.isConnected()) {
            return;
        }
        try {
            MqttMessage mmsg = new MqttMessage(msg.getBytes());
            client.publish(this.prefix + topic, mmsg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String topic, int val) {
        sendMsg(topic, String.valueOf(val));
    }

    public void sendMsg(String topic, double val) {
        sendMsg(topic, String.valueOf(val));
    }

    @Override
    public void connectionLost(Throwable arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {
        // TODO Auto-generated method stub

    }

    public void messageArrived(String topic, MqttMessage message)
            throws Exception {
        String msg = new String(message.getPayload());

        if (topic.contains("chargeState")) {
            if (msg.equals("3")) {
                engine.nsrv.sendData(
                        engine.hexStringToByteArray(this.chargeSolarOnly));
                String time = new Timestamp(System.currentTimeMillis())
                        .toString();
                System.out.println(time + " - Server: " + chargeSolarOnly);
            } else {
                engine.nsrv.sendData(
                        engine.hexStringToByteArray(this.chargeSolarUtility));
                String time = new Timestamp(System.currentTimeMillis())
                        .toString();
                System.out.println(time + " - Server: " + chargeSolarUtility);
            }
        } else if (topic.contains("loadState")) {
            if (msg.equals("2")) {
                engine.nsrv.sendData(engine.hexStringToByteArray(this.loadSBU));
                String time = new Timestamp(System.currentTimeMillis())
                        .toString();
                System.out.println(time + " - Server: " + loadSBU);
            } else {
                engine.nsrv.sendData(
                        engine.hexStringToByteArray(this.loadUtility));
                String time = new Timestamp(System.currentTimeMillis())
                        .toString();
                System.out.println(time + " - Server: " + loadUtility);
            }
        } else {
            String time = new Timestamp(System.currentTimeMillis()).toString();
            System.out.println("\nReceived a Message!" + "\n\tTime:    " + time
                    + "\n\tTopic:   " + topic + "\n\tMessage: "
                    + new String(message.getPayload()) + "\n\tQoS:     "
                    + message.getQos() + "\n");
        }

    }

}