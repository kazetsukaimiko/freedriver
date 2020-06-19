# electrodacus-java
## Electrodacus SBMS0 Java Library
Tries to be good at finding, opening, and decoding streams to the SBMS0. Requires my other projects

Serial communication
https://github.com/kazetsukaimiko/kaze-serial

SI Units, Measurements, etc.
https://github.com/kazetsukaimiko/kaze-math

## Reading Events from SBMS0
To read 10 entries from the first SBMS0 found:
```java
        SBMS0Finder.findFirstSBMS0()
                .limit(10)
                .forEach(System.out::println);
```

You'll get entries like:
```
SBMSMessage{
  timestamp=2020-06-08T15:56:06.164623Z, 
  soc=99.0, 
  cellOne=3.993 V, 
  cellTwo=3.992 V, 
  cellThree=3.992 V, 
  cellFour=0 V, 
  cellFive=0 V, 
  cellSix=3.993 V, 
  cellSeven=3.983 V, 
  cellEight=3.994 V, 
  internalTemperature=27.7 C, 
  externalTemperature=-45 C, 
  charging=false, 
  discharging=true, 
  batteryCurrent=0.006 A, 
  pvCurrent1=0 A, 
  pvCurrent2=null, 
  extCurrent=0 A, 
  errorCodes=[]
}
```
## Assumptions
Your SBMS0 should have UART logging enabled and baud rate to 115.2Kbps for the above to work.

## Finding SBMS0 devices to open
You can get a Stream of paths that SBMS0Finder thinks are SBMS0 units.
```java
Stream<Path> devices = SBMS0Finder.findSBMS0Units();
```
## Discovery process
Discovery is done Linux platform-dependent via /dev/serial/by-id. I am researching reliable ways to discover serial devices and filter them by type. My application requires more than one SBMS0 in addition to other serial devices being connected, I need to be able to discover them without opening the SBMS thinking its an Arduino and/or vice-versa.

### Known limitations
/dev/serial/by-id MIGHT NOT be unique- I found by default multiple SBMS0 units overlapped here and had to change udev rules to ensure they were unique.

## Doing it manually
If your baud rate is different or /dev/serial/by-id doesn't work for you, you can create connections manually.
```java
Stream<SBMSMessage> stream = SBMSFinder.open("/dev/ttyUSB10", 256000);
```
Where "/dev/ttyUSB10" is the path to your device, 256000 is an integer representing the serial connection rate.

## Serial Connection Lifecycle
When reading events, a Java 8 Stream is created. These Streams control the lifecycle of the Serial Connection. As long as the Stream is open, the Serial port will be open. Once you discard or end the Stream, the serial port closes. This is an important feature, keep this in mind especially in multithreaded environments. Merely finding devices does not open them, so that is safe to do in a threaded environment.

## Units
Temperatures, voltages and current values are handled using my kaze.math library, which while leaves a lot to be desired, properly represents these values. There's JPA conversion classes to store these in RDBMS fields which I'll factor out into a kaze-math subproject at some point.

## Notes on working with the SBMS0
I found decoding SBMS data was not very straightforward. It is "compression"- numeric values are smaller represented over serial or in memory/storage this way. But it isn't the same "base-91" encoding as the sourceforge project basE91. See the SBMSField enum for how this works:
https://github.com/kazetsukaimiko/electrodacus-java/blob/master/src/main/java/com/electrodacus/bms/SBMSField.java
