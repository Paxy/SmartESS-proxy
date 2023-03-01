# SmartESS-proxy
SmartESS (PowMr) to MQTT proxy

Aim of the project is to adopt PowMr WiFi Plug Pro to send data over MQTT to HASS beside SmartESS cloud.
It is achived by running SmartESS-proxy and poisoning DNS ess.eybond.com to point to the proxy.

For use without compiling, place content of bin/ to the same folder.

Change parameters in conf.ini
```
fakeClient=true
mqttServer=172.16.2.1
mqttPort=1883
enableMqttAuth=false
mqttUser=
mqttPass=
mqttTopic=paxyhome/Inverter/
updateFrequency=10
 ```
 
 Run project with:
 ```
 java -jar SmartESS-proxy.jar
 ```
 
 FakeClient is initiated by default to prevent data to be sent to SmartESS cloud. If you still want to use SmartESS cloud, set fakeClient to false.
 
 Project requires org.eclipse.paho.client.mqtt library.
