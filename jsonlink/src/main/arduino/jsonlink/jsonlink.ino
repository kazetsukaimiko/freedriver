#include <Arduino.h>
#include <EEPROM.h>
#include <ArduinoJson.h>

/*
 * JSONLink for Arduino Mega2560
 * This sketch speaks a protocol I call "jsonlink" over serial. It allows you
 * to set pin modes, read and write from digital pins, and read analog pin values
 * via JSON commands.
 *
 * The idea here is that because the microcontroller requires flashing, it is
 * impossible to reconfigure it through user input. By boiling its usefulness down
 * to its I/O and leaving more complex decision logic to a general purpose computer,
 * you can reconfigure behavior based on user settings rather than hard-coded values,
 * or relying on state within the microcontroller itself.
 *
 */

/*
 * CONSTANTS - general
 */
// A convenience newline character.
static char NEWLINE = '\n';
// Where to find the UUID of the board in eeprom.
static char UUID_ADDRESS = 0;
// How long the UUID is (String)
static int UUID_LENGTH = 36;
// Version of JSONLink
int JSONLINK_VERSION[] = {1,0,0};
// Banned digital pins - setting these will mess things up.
int BANNED_DIGITALS[] = {PIN_SPI_SS, PIN_SPI_SCK, PIN_WIRE_SDA, PIN_WIRE_SCL};


/*
 * CONSTANTS - jsonlink properties
 */

// Json property to read the version from.
static String VERSION = "version";

// Json property to set current time.
static String CURRENT_TIME = "currentTime";

// Json property to request debug information with. Set to true if you want debugging output.
// Will return an array of the same name containing strings of debugging information.
static String DEBUG = "debug";

// Json property containing errors.
static String ERROR = "error";

// Json property containing errors.
static String INFO = "info";

// Json property containing the Request ID. This allows you to pool responses- critically important in
// multithreaded environments and asynchronous operations. Whatever you send as the request id is returned
// in the corresponding response.
static String REQUEST_ID = "requestId";

// Json property containing the Board ID. This allows you to differentiate between multiple boards
// connected to the same machine, regardless of connection order or other factors.
static String UUID = "uuid";

// Json property requesting the state of digital/analog pins.
static String READ = "read";

// Json property to write digital pin states to.
static String WRITE = "write";

// Json property to set a pins mode (read/write).
static String MODE = "mode";

// A Json property under READ: Array of pinNum; WRITE: Array of { pinNum : boolean }
static String DIGITAL = "digital";

// A Json property under READ: Array of { pinNum : resistance }; WRITE: todo
static String ANALOG = "analog";

// Shortcut Json properties to set pin states for digital pins set to write mode.
static String TURN_ON = "turn_on";
static String TURN_OFF = "turn_off";

// A response property containing the raw value of analog read.
static String RAW = "raw";

// A response property specifying a pin number.
static String PIN = "pin";

// A response property specifying the voltage for an analog read.
static String VOLTAGE = "voltage";

// A response property specifying the resistance.
static String RESISTANCE = "resistance";

// A response property with the last time updated.
static String LAST_UPDATED = "lastUpdated";

// A response property containing board info.
static String BOARD_INFO = "boardInfo";

// A response property in boardInfo containing
static String DIGITALS = "digitals";

// A response property in boardInfo containing
static String ANALOGS = "analogs";


/*
 * Critical Constants for Serial
 */

// The speed of the connection.
const long BAUD = 115200;
// The size of the StaticJsonDocuments to allocate.
const int JSONSIZE = 2048;
// The timeout value for serial- at maximum size, how long to wait for serial data.
// This is so that the controller is as responsive as possible.
static long TIMEOUT = (JSONSIZE*2)/(BAUD/1000);

// The document received by serial.
StaticJsonDocument<JSONSIZE> inputDocument;
// The document this sketch populates to write to serial.
StaticJsonDocument<JSONSIZE> outputDocument;

// Buffer for reading from Serial.
String buffer = "";


// Reads an analog pin using resistance given :
// The (int) pin number,
// The (float) voltage input,
// The (float) ohms value of the known resistor connected.
void readAnalogPin(int analogPinRead, float voltageIn, float knownResistance) {
  int raw = analogRead(analogPinRead);
  if (raw) {
    float buffer = raw * voltageIn;
    float voltageOut = (buffer)/1024.0;
    buffer = (voltageIn/voltageOut) - 1;
    float unknownResistance = knownResistance * buffer;

    if (!outputDocument.containsKey(ANALOG)) {
      outputDocument.createNestedArray(ANALOG);
    }
    JsonObject response = outputDocument[ANALOG].createNestedObject();
    response[PIN] = analogPinRead;
    response[RAW] = raw;
    response[VOLTAGE] = voltageOut;
    response[RESISTANCE] = unknownResistance;
  }
}

// This writes non-json output straight to the Serial port.
// The java implementation of JSONLink writes non-json responses directly to the logger,
// so this is a great way to get good debug integrations.
void debugOutput(String debug) {
  for (int i=0; i< debug.length(); i++) {
    Serial.write(debug.charAt(i));
  }
  Serial.write(NEWLINE);
}

// Function to write a string to EEPROM.
// add - the address to write to.
// data - the String to write.
// Used primarily to write the board id on first setup.
void writeToEEPROM(char add,String data) {
  int _size = data.length();
  int i;
  for(i=0;i<_size;i++) {
    EEPROM.update(add+i,data[i]);
  }
  // Add termination null character for String Data
  EEPROM.update(add+_size,'\0');
}

// Function to read from EEPROM.
// add - the address to read from.
// Used primarily to read the board id.
String readFromEEPROM(char add) {
  int i;
  char data[UUID_LENGTH]; //Max 64 Bytes
  int len=0;
  unsigned char k;
  k=EEPROM.read(add);
  while(k != '\0' && len<UUID_LENGTH) {   //Read until null character
    k=EEPROM.read(add+len);
    data[len]=k;
    len++;
  }
  data[len]='\0';
  return String(data);
}


// Function to read the board id from EEPROM and write it to the outputDocument.
void setupVersion() {
    if (!outputDocument.containsKey(UUID)) {
        outputDocument.createNestedArray(VERSION);
        int size = sizeof(JSONLINK_VERSION) / sizeof(int);
        for (int i=0; i < size; i++) {
            outputDocument[VERSION].add(JSONLINK_VERSION[i]);
        }
    }
}

// Function to read the board id from EEPROM and write it to the outputDocument.
void readUUID() {
    String uuid = readFromEEPROM(UUID_ADDRESS);
    if (uuid.length() == UUID_LENGTH) {
        outputDocument[UUID] = uuid;
    }
}

// Function to setup the board id. Disallows multiple writes to preserve EEPROM.
void setupUUID() {
    if (inputDocument.containsKey(UUID)) {
        if (outputDocument.containsKey(UUID)) {
          appendError("UUID Already set.");
        } else {
          String newUUID = inputDocument[UUID];
          writeToEEPROM(UUID_ADDRESS, newUUID);
        }
    }
    readUUID();
}

// Function to pass the request Id through for multi-threaded and asynchronous environments.
void setupRequestId() {
    if (inputDocument.containsKey(REQUEST_ID)) {
        outputDocument[REQUEST_ID] = inputDocument[REQUEST_ID];
    }
}

void setupBoardInfo() {
    if (inputDocument.containsKey(BOARD_INFO) && (bool)inputDocument[BOARD_INFO]) {
        outputDocument.createNestedObject(BOARD_INFO);
        outputDocument[BOARD_INFO].createNestedArray(DIGITALS);
        for (int digitalPin = 0; digitalPin < (NUM_DIGITAL_PINS-NUM_ANALOG_INPUTS); digitalPin++) {
            if (!bannedDigitalPin(digitalPin)) {
                outputDocument[BOARD_INFO][DIGITALS].add(digitalPin);
            }
        }
        outputDocument[BOARD_INFO].createNestedArray(ANALOGS);
        for (int analogPin = A0; analogPin < (A0+NUM_ANALOG_INPUTS); analogPin++) {
            outputDocument[BOARD_INFO][ANALOGS].add(analogPin);
        }
    }
}

boolean bannedDigitalPin(int pinNum) {
    for (int bannedPin : BANNED_DIGITALS) {
        if (bannedPin == pinNum) {
            return true;
        }
    }
    return false;
}

// Is the pinNum a valid digital pin?
boolean validDigitalPin(int pinNum) {
    return ((pinNum >= 0) && (pinNum < NUM_DIGITAL_PINS) && !bannedDigitalPin(pinNum));
}

// Is the pinNum a valid analog pin?
boolean validAnalogPin(int pinNum) {
    return ((pinNum >= A0) && (pinNum < (A0+NUM_ANALOG_INPUTS)));
}

// Is the pinNum a valid pin?
boolean validPin(int pinNum) {
  return validDigitalPin(pinNum) || validAnalogPin(pinNum);
}

int getPinMode(uint8_t pin) {
  if (!validPin(pin)) return (-1);

  uint8_t bit = digitalPinToBitMask(pin);
  uint8_t port = digitalPinToPort(pin);
  volatile uint8_t *reg = portModeRegister(port);
  if (*reg & bit) return (OUTPUT);

  volatile uint8_t *out = portOutputRegister(port);
  return ((*out & bit) ? INPUT_PULLUP : INPUT);
}

// Reads modeset commands from the JSON inputDocument, and acts upon them.
// Writes any errors to outputDocument.
void modePins() {
  if (inputDocument.containsKey(MODE)) {
    JsonObject pinsToMode = inputDocument[MODE];
    for( JsonPair pinToMode : pinsToMode ) {
      modeSetPin(atoi(pinToMode.key().c_str()), pinToMode.value(), true);
    }
  }
}

void modeSetPin(int pinToModeNumber, bool mode, bool setup) {
    if (mode) {
        if (!validDigitalPin(pinToModeNumber)) {
            appendError(String("Cannot set OUTPUT, Invalid Digital Pin: ") + String(pinToModeNumber));
        } else {
            pinMode(pinToModeNumber, mode ? OUTPUT : INPUT);
            if (setup) {
                digitalWrite(pinToModeNumber, HIGH);
            }
        }
    } else {
        if (!validAnalogPin(pinToModeNumber)) {
            appendError(String("Cannot set INPUT, Invalid Analog Pin: ") + String(pinToModeNumber));
        }
    }
}

void writeDigitalPin(int digitalPinNumber, bool value) {
    if (validDigitalPin(digitalPinNumber)) {
        if (getPinMode(digitalPinNumber) != OUTPUT) {
            modeSetPin(digitalPinNumber, OUTPUT, false);
        }
        digitalWrite(digitalPinNumber, value ? LOW : HIGH);
        outputDocument[DIGITAL][String(digitalPinNumber)] = value;
        appendDebug("Set:" + String(digitalPinNumber) + ":" + value ? "LOW" : "HIGH");
    } else {
        appendError("Invalid DigitalPin: " + String(digitalPinNumber));
    }
}

// Reads digital write commands from the JSON inputDocument, and acts upon them.
// Writes the state of any pins set back to the outputDocument.
void writePins() {
  if (inputDocument.containsKey(WRITE)) {
    if (inputDocument[WRITE].containsKey(DIGITAL)) {
      JsonObject digitalPins = inputDocument[WRITE][DIGITAL];
      for( JsonPair digitalPin : digitalPins ) {
        writeDigitalPin(atoi(digitalPin.key().c_str()), digitalPin.value());
      }
    }
  }

  if (inputDocument.containsKey(TURN_ON)) {
    JsonArray pinsToTurnOn = inputDocument[TURN_ON];
    for (int pinToTurnOn : pinsToTurnOn) {
      writeDigitalPin(pinToTurnOn, LOW);
    }
  }

  if (inputDocument.containsKey(TURN_OFF)) {
    JsonArray pinsToTurnOff = inputDocument[TURN_OFF];
    for (int pinToTurnOff : pinsToTurnOff) {
      writeDigitalPin(pinToTurnOff, HIGH);
    }
  }
}


// Reads digital and analog read commands from the JSON inputDocument, and acts upon them.
// Writes the state of any requested pins back to the outputDocument.
void readPins() {
  if (inputDocument.containsKey(READ)) {
    if (inputDocument[READ].containsKey(DIGITAL)) {
      JsonArray digitalPins = inputDocument[READ][DIGITAL];
      for( const int& digitalPin : digitalPins ) {
        outputDocument[DIGITAL][String(digitalPin)] = (digitalRead(digitalPin) == LOW);
      }
    }
    if (inputDocument[READ].containsKey(ANALOG)) {
      JsonArray analogPins = inputDocument[READ][ANALOG];
      for (JsonObject analogPin : analogPins) {
        if (analogPin.containsKey(PIN) && analogPin.containsKey(VOLTAGE) && analogPin.containsKey(RESISTANCE)) {
          int analogPinNumber = analogPin[PIN];
          float voltageIn = analogPin[VOLTAGE];
          float knownResistance = analogPin[RESISTANCE];
          readAnalogPin(analogPinNumber, voltageIn, knownResistance);
        } else {
          appendError(String("Requested Analog Read but missing keys."));
        }
      }
    }
  }
}

void appendInfo(String message) {
    appendLog(message, INFO);
}

void appendError(String message) {
    appendLog(message, ERROR);
}

void appendDebug(String message) {
    if (inputDocument.containsKey(DEBUG)) {
        appendLog(message, DEBUG);
    }
}


void appendLog(String message, String key) {
    if (!outputDocument.containsKey(key)) {
        outputDocument.createNestedArray(key);
    }
    outputDocument[key].add(message);
}


// The general JSON processing loop.
void processJson() {
  // Set Version
  setupVersion();
  // Set Board Id
  setupUUID();
  // Set Request Id
  setupRequestId();
  // Set Board Info
  setupBoardInfo();
  // Enact any pin modesets.
  modePins();
  // Write any digital pins set
  writePins();
  // Read any pins requested
  readPins();
}

// Arduino Loop.
void loop() {
  // Reset the output
  deserializeJson(outputDocument, "{}");

  // Read the buffer and process it.
  buffer = Serial.readStringUntil(NEWLINE);
  processBuffer();
}

// Processing the JSON buffer.
void processBuffer() {
    // We don't have a JSON request if the payload doesn't begin with { and end with }.
    if (buffer.startsWith("{") && buffer.endsWith("}")) {
      // Attempt to deserialize the json buffer. If it is malformed, keep the error.
      DeserializationError error = deserializeJson(inputDocument, buffer);
      // If no error, process the buffer.
      if (!error) {
        processJson();
      } else {
        // If an error occurred processing the JSON, set the board id and respond with the error.
        readUUID();
        appendError(error.c_str());
      }
      // Write the outputDocument JSON to serial, with a newline to hint we're done.
      serializeJson(outputDocument, Serial);
      Serial.write(NEWLINE);
    } else if (buffer.length() > 0) {
      // Log any bad input.
      debugOutput("Bad Input.");
      debugOutput(buffer);
    }
    
    buffer = "";
}

// Arduino setup.
void setup() {
    // Initialize Serial port
    Serial.begin(BAUD);
    Serial.setTimeout(TIMEOUT);

    // Clear the buffer.
    buffer = "";
}
