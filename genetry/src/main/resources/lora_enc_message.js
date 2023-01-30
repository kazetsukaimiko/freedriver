// STEP 0:
// Key propagation.
// This can happen over LoRa, but the initial key propagations will happen using direct serial for obvious reasons
{
  "source" : "some string uniquely identifying the source",
  "target" : "some string representing subscribers to a message type"
  "messageId" : "9c56bc2f-70c2-4433-8582-17813f1bfda5",
  "type" : "ENCRYPTION_KEY",
  // Data is the base64 encoded encryption key.
  "data" : "VXQgZW5pbSBhZCBtaW5pbSB2ZW5pYW0sIHF1aXMgbm9zdHJ1ZCBleGVyY2l0YXRpb24gdWxsYW1jbyBsYWJvcmlzIG5pc2kgdXQgYWxpcXVpcCBleCBlYSBjb21tb2RvIGNvbnNlcXVhdC4=",

  // What we want this key to be named and its algorithm
  "metadata" : {
    "keyId": "tn_key_01",
    "type" : "AES"
  }
}

// The recipient stores this key somewhere semi-permanently.

// STEP 1:
// First we send a message, with a unique id (UUID)
// Specify encryption
{
  "source" : "some string uniquely identifying the source",
  "target" : "some string representing subscribers to a message type"
  "messageId" : "29a566d6-0b9e-4834-9b89-458d6b2667d7",
  "type" : "DATA",
  // Data is base64 encoded, encrypted content. Everything else is clear.
  "data" : "TG9yZW0gaXBzdW0gZG9sb3Igc2l0IGFtZXQsIGNvbnNlY3RldHVyIGFkaXBpc2NpbmcgZWxpdCwgc2VkIGRvIGVpdXNtb2QgdGVtcG9yIGluY2lkaWR1bnQgdXQgbGFib3JlIGV0IGRvbG9yZSBtYWduYSBhbGlxdWEu",
  "encryption" : {
    "keyId" : "tn_key_01", // The recipient is assumed to have a key named after this string.
  }
}

// STEP 2:
// Any recipients then store the messageId temporarily in addition to processing data as normal.
// ...
//

// STEP 3:
// So that the sender knows who received it, the recipient replies with:
{
  "source" : "a recipient's unique identifier",
  "target" : "some string representing subscribers to a message type", // The target from the original message
  "messageId" : "29a566d6-0b9e-4834-9b89-458d6b2667d7", // The messageId from the original message
  "type" : "ACK"
}

// STEP 3 Alternative / failure:
// If configured to do so, a recipient may reply with a failure message for if they cannot process the message
// due to encryption problems, when they are the explicitly stated target.
{
  "source" : "a recipient's unique identifier",
  "target" : "some string representing subscribers to a message type", // The target from the original message
  "messageId" : "29a566d6-0b9e-4834-9b89-458d6b2667d7", // The messageId from the original message
  "type" : "ACK_FAIL",
  "data" : "No known key tn_key_01"
}


// The sender can send the message several times to ensure someone has received the message at least once until
// the required number of recipients reply, without worrying about reprocessing.

// Notes: Encrypted key propagations are also possible with this same pattern, so you can transfer new encryption keys
// to remote locations. 