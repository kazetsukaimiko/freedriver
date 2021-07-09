package io.freedriver.serial.stream;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

public class TestBean {
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private static final String LETTERS = "abcdefghijklmnop0123456789";

    private Integer anInt;
    private Long aLong;
    private Character aChar;
    private String aString;

    public TestBean(Integer anInt, Long aLong, Character aChar, String aString) {
        this.anInt = anInt;
        this.aLong = aLong;
        this.aChar = aChar;
        this.aString = aString;
    }

    public TestBean() {
        this(
                RANDOM.nextInt(),
                RANDOM.nextLong(),
                randomCharacter(),
                randomString()
        );
    }

    public static int randomInt() {
        return RANDOM.nextInt();
    }

    public static long randomLong() {
        return RANDOM.nextLong();
    }

    public static Character randomCharacter() {
        return (RANDOM.nextBoolean() ?
                LETTERS : LETTERS.toUpperCase())
                        .charAt(RANDOM.nextInt(LETTERS.length()));
    }

    public static String randomString() {
        StringBuilder builder = new StringBuilder();
        IntStream.range(10, 50)
                .mapToObj(i -> randomCharacter())
                .forEach(builder::append);
        return builder.toString();
    }

    public Integer getAnInt() {
        return anInt;
    }

    public void setAnInt(Integer anInt) {
        this.anInt = anInt;
    }

    public Long getaLong() {
        return aLong;
    }

    public void setaLong(Long aLong) {
        this.aLong = aLong;
    }

    public Character getaChar() {
        return aChar;
    }

    public void setaChar(Character aChar) {
        this.aChar = aChar;
    }

    public String getaString() {
        return aString;
    }

    public void setaString(String aString) {
        this.aString = aString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestBean testBean = (TestBean) o;
        return Objects.equals(anInt, testBean.anInt) && Objects.equals(aLong, testBean.aLong) && Objects.equals(aChar, testBean.aChar) && Objects.equals(aString, testBean.aString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anInt, aLong, aChar, aString);
    }
}
