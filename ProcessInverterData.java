import java.nio.ByteBuffer;

public class ProcessInverterData implements Runnable {

    private Engine engine;
    private short modeIdx = 14;
    private short acVoltageIdx = 16;
    private short acFrequencyIdx = 18;
    private short pvVoltageIdx = 20;
    private short pvPowerIdx = 22;
    private short batteryVoltageIdx = 24;
    private short batteryChargedIdx = 26;
    private short batteryChargingCurrIdx = 28;
    private short batteryDisChargingCurrIdx = 30;
    private short outputVoltageIdx = 32;
    private short outputFrequencyIdx = 34;
    private short outputPowerIdx = 38;
    private short outputLoadIdx = 40;
    private short chargeStateIdx=84;
    private short loadStateIdx=86;


    public ProcessInverterData(Engine engine) {
        this.engine = engine;
    }

    public void run() {
        while (true)
            try {
                while (engine.lastData == null || engine.lastData.length == 0)
                    Thread.currentThread().sleep(100);
                byte[] data = engine.lastData;
                String hex = Engine.bytesToHex(data);
                if (data[2] == 0x09 && data[3] == 0x25) {
                    double batteryVoltage = getData(data, batteryVoltageIdx,
                            10);
                    engine.mqtt.sendMsg("batteryVoltage", batteryVoltage);
                    int batteryCharged = getData(data, batteryChargedIdx, 1,
                            true);
                    engine.mqtt.sendMsg("batteryCharged", batteryCharged);
                    double batteryChargingCurr = getData(data,
                            batteryChargingCurrIdx, 10);
                    engine.mqtt.sendMsg("batteryChargingCurr",
                            batteryChargingCurr);
                    double batteryDisChargingCurr = getData(data,
                            batteryDisChargingCurrIdx, 10);
                    engine.mqtt.sendMsg("batteryDisChargingCurr",
                            batteryDisChargingCurr);
                    double outputVoltage = getData(data, outputVoltageIdx, 10);
                    engine.mqtt.sendMsg("outputVoltage", outputVoltage);
                    double outputFrequency = getData(data, outputFrequencyIdx,
                            10);
                    engine.mqtt.sendMsg("outputFrequency", outputFrequency);
                    int outputPower = getData(data, outputPowerIdx, 1, true);
                    engine.mqtt.sendMsg("outputPower", outputPower);
                    int outputLoad = getData(data, outputLoadIdx, 1, true);
                    engine.mqtt.sendMsg("outputLoad", outputLoad);
                    double acVoltage = getData(data, acVoltageIdx, 10);
                    engine.mqtt.sendMsg("acVoltage", acVoltage);
                    double acFrequency = getData(data, acFrequencyIdx, 10);
                    engine.mqtt.sendMsg("acFrequency", acFrequency);
                    double pvVoltage = getData(data, pvVoltageIdx, 10);
                    engine.mqtt.sendMsg("pvVoltage", pvVoltage);
                    int pvPower = getData(data, pvPowerIdx, 1, true);
                    engine.mqtt.sendMsg("pvPower", pvPower);
                    int mode = getData(data, modeIdx, 1, true);
                    engine.mqtt.sendMsg("mode", mode);
                    int chargeState = getData(data, chargeStateIdx, 1, true);
                    engine.mqtt.sendMsg("chargeState", chargeState);
                    int loadState = getData(data, loadStateIdx, 1, true);
                    engine.mqtt.sendMsg("loadState", loadState);
                }if (data[2] == 0x00 && data[3] == 0x01) {
                    int chargeState=-1;
                    int loadState=-1;
                    if(hex.equals(MQTTClient.chargeSolarOnly)) chargeState=3;
                    else if(hex.equals(MQTTClient.chargeSolarUtility)) chargeState=2;
                    else if(hex.equals(MQTTClient.loadSBU)) loadState=2;
                    else if(hex.equals(MQTTClient.loadUtility)) loadState=0;
                    if(chargeState!=-1) engine.mqtt.sendMsg("chargeState", chargeState);
                    if(loadState!=-1) engine.mqtt.sendMsg("loadState", loadState);

                }
                engine.lastData = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private double getData(byte[] data, short idx, int denominator) {
        int b = 0;
        for (int i = 1; i >= 0; i--) {
            b = (b << 8) + (data[idx + i] & 0xFF);
        }
        return b * 1.0 / denominator;
    }

    private int getData(byte[] data, short idx, int denominator,
            boolean toInt) {
        int b = 0;
        for (int i = 1; i >= 0; i--) {
            b = (b << 8) + (data[idx + i] & 0xFF);
        }
        return b / denominator;
    }

}
