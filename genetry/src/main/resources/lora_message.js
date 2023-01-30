// STEP 1:
// First we send a message, with a unique id (UUID)
{
  "source" : "some string uniquely identifying the source",
  "target" : "some string representing subscribers to a message type"
  "messageId" : "29a566d6-0b9e-4834-9b89-458d6b2667d7",
  "type" : "DATA",
  "data" : {
    // Stuff for the application to use goes here
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

// The sender can send the message several times to ensure someone has received the message at least once until
// the required number of recipients reply, without worrying about reprocessing.
