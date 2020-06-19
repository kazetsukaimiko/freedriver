package io.freedriver.base.util.crypt;

import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;

public interface CryptUtils {
    int BUFFER_SIZE = 1024;

    static String hashFile(Path path, HashAlgorithm algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm.getName());
            messageDigest.reset();
            SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.READ);

            while (channel.position()<channel.size()) {
                long remaining = channel.size()-channel.position();
                ByteBuffer bb = ByteBuffer.allocate((BUFFER_SIZE<remaining)? BUFFER_SIZE : (int)remaining);
                int read = channel.read(bb);
                bb.rewind();
                messageDigest.update(bb);
            }
            return toHexString(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash file!", e);
        }
    }

    static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
