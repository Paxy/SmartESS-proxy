# SmartESS-proxy
SmartESS (PowMr) to MQTT proxy

Aim of the project is to adopt PowMr WiFi Plug Pro to send data over MQTT to HASS beside SmartESS cloud.
It is achived by running SmartESS-proxy and poisoning DNS ess.eybond.com to point to the proxy.

Parameters are changed in Engine class:
```
    static boolean fakeClient = true;
    static String mqttServer = "172.16.2.1";
    static int mqttPort = 1883;
    static String mqttTopic = "paxyhome/Inverter/";
    static int fakeClientUpdateFrequency = 10; // 10 seconds
    static String realModbusServer="47.242.188.205";
 ```
 
 FakeClient is initiated by default to prevent data to be sent to SmartESS cloud. If you still want to use SmartESS cloud, set fakeClient to false.
 
 Project requires org.eclipse.paho.client.mqtt library.
