package tester;
import java.nio.ByteBuffer;

public class testDataExtract {
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


    public testDataExtract() {
        String hex = "2B270925008205110000119511D10400CE08F301B90001007C00420000000000CE08F301100000000100010072B20000C1A200000100DC05DC05E60006007800E600F401060000000000F9231601D70F72006501020001000000020000003C00E6001E00740087007E007D0064008D003C0078001E0062ECE90E010000004A000000000000000000";
        byte[] data = hexStringToByteArray(hex);
        if (data[2] == 0x09 && data[3] == 0x25) {
            double batteryVoltage = getData(data, batteryVoltageIdx, 10);
            System.out.println("batteryVoltage: " + batteryVoltage);
            int batteryCharged = getData(data, batteryChargedIdx, 1, true);
            System.out.println("batteryCharged: " + batteryCharged);
            double batteryChargingCurr = getData(data,
                    batteryChargingCurrIdx, 10);
            System.out.println("batteryChargingCurr: " + batteryChargingCurr);
            double batteryDisChargingCurr = getData(data,
                    batteryDisChargingCurrIdx, 10);
            System.out.println(
                    "batteryDisChargingCurr: " + batteryDisChargingCurr);
            double outputVoltage = getData(data, outputVoltageIdx, 10);
            System.out.println("outputVoltage: " + outputVoltage);
            double outputFrequency = getData(data, outputFrequencyIdx, 10);
            System.out.println("outputFrequency: " + outputFrequency);
            int outputPower = getData(data, outputPowerIdx, 1, true);
            System.out.println("outputPower: " + outputPower);
            int outputLoad = getData(data, outputLoadIdx, 1, true);
            System.out.println("outputLoad: " + outputLoad);
            double acVoltage = getData(data, acVoltageIdx, 10);
            System.out.println("acVoltage: " + acVoltage);
            double acFrequency = getData(data, acFrequencyIdx, 10);
            System.out.println("acFrequency: " + acFrequency);
            double pvVoltage = getData(data, pvVoltageIdx, 10);
            System.out.println("pvVoltage: " + pvVoltage);
            int pvPower = getData(data, pvPowerIdx, 1, true);
            System.out.println("pvPower: " + pvPower);
            int mode = getData(data, modeIdx, 1, true);
            System.out.println("mode: " + mode);
            int chargeState = getData(data, chargeStateIdx, 1, true);
            System.out.println("chargeState: " + chargeState);
            int loadState = getData(data, loadStateIdx, 1, true);
            System.out.println("loadState"+loadState);
        }
    }

    private double getData(byte[] data, short idx,
            int denominator) {
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

    public static void main(String[] args) {
        new testDataExtract();
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
