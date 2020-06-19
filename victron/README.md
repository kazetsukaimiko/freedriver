# victron-java
A pure Java Victron Connectivity library using JSSC.

This library allows for simple Java 8+ style reading of events from VE.Direct devices. Currently, only the Victron VE Direct USB cable is supported, but other serial-based connection options should be easy enough to add.

To address a potentially confusing aspect for some users up front, note that this library differentiates between Victron "Devices" (USB Cable) and Victron "Products" (what is attached to it).

## Finding devices
Device discovery is curently handled by looking for recognized entries under /dev/serial/by-id. This is a Linux-specific solution, we should be instead looking at USB vendor/product ids and connecting that way. For a list of currently recognized VEDirectDevices, see the VEDirectDEviceType enum.

To get a Stream of the connected VEDirectDevices:
```java
Stream<VEDirectDevice> allDevices = VEDirectDevice.allVEDirectDevices();
```

If you have one, it is typically safe to do:
```java
Optional<VEDirectDevice> myDevice = VEDirectDevice.allVEDirectDevices()
      .findFirst();
```

Note that Optional is used, this is important as the code cannot be sure a device is connected. If you're positive, in your code you can just use orElse(null);
```java
VEDirectDevice myDevice = VEDirectDevice.allVEDirectDevices()
      .findFirst()
      .ifPresent(device -> {
            // Do stuff with device.
      });
```

## Serial Connection Lifecycle
To read events, a Java 8 Stream is created. These Streams control the lifecycle of the Serial Connection. As long as the Stream is open, the Serial port will be open. Once you discard or end the Stream, the serial port closes. This is an important feature, keep this in mind especially in multithreaded environments. Merely finding devices does not open them, so that is safe to do in a threaded environment.

## Finding Victron Products

To print out all Victron Products (Charge controllers, inverters, etc) connected via VEDirect, some convenience methods are used. Once you go beyond finding a VEDirectDevice and try to get VictronProduct information, you are making serial connections. Keep this in mind.

```java
VEDirectDevice.allVEDirectDevices() // Finds all Devices. For each Device:
      .map(VEDirectDevice::attemptToGetProduct) // Opens a Serial Connection, reads an event, fetches the VictronProduct. May return Optional.empty(). Closes Serial Connection.
      .map(VictronProduct::getProductName) // Gets the product name of the VictronProduct information.
      .forEach(System.out::println); // Prints to stdout. You can use your logger as well.
```

You can do this for just one as well:
```java
VEDirectDevice.allVEDirectDevices()
      .findFirst()
      .map(VEDirectDevice::attemptToGetProduct) // Opens a Serial Connection, reads an event, fetches the VictronProduct. May return Optional.empty(). Closes Serial Connection.
      .map(VictronProduct::getProductName) // Gets the product name of the VictronProduct information.
      .forEach(System.out::println); // Prints to stdout. You can use your logger as well.
```

Note again the serial connection lifecycle- for each VEDirectDevice actually used, the Serial port was opened, a VEDirectMessage was read, the product name printed if available, and then the Serial port closed at the end of the call.

## Reading Events
Events can be read once you have a device handle using the Java 8 Stream API once you have a VEDirectDevice handle.

```java
VEDirectDevice.allVEDirectDevices() // Of all devices
        .findFirst() // Find the first
        .flatMap(VEDirectDevice::readOneMessage) // Opens a Serial connection, reads one message, and closes the connection.
        .ifPresent(System.out::println); // Print the message.
```

The result is something like:
```
VEDirectMessage{product=SMARTSOLAR_MPPT_150_100_REV2, relayState=OFF, firmwareVersion='146', serialNumber='HQXYZABC433S', mainVoltage=24.480 V, mainCurrent=0.000 A, panelVoltage=30 mV, panelPower=0 W, stateOfOperation=OFF, trackerOperation=OFF, loadOutputState=ON, errorCode=NO_ERROR, offReason=NO_INPUT_POWER, resettableYield=279.73 kWh, yieldToday=0.00 Wh, maxPowerToday=0 W, yieldYesterday=30.00 Wh, maxPowerYesterday=21 W}
```

# Recommendations
All in all, if you are in a single threaded environment, you're going to want to use .findFirst() and have short-lived serial connections. If you are in a threaded environment, spawn the Stream in its own thread and use it long-lived to update a field on a thread-safe object, feed messages onto a message bus, or save them to a database for retrieval in other threads.

There's some pretty nice SIUnit conversion code in the kaze.math package that helps with the Watts, Amps, and Volts. 

Feel free to submit feature requests via the Issue tracker.
