package io.freedriver.serial.api;

import java.util.Random;

public abstract class SerialApiTest <SR extends SerialResource> {
    public static final Random RANDOM = new Random();
    public abstract SR construct();
}
