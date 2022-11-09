package io.freedriver.base.command;

import io.freedriver.base.util.command.Command;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class CommandTest {

    @Test
    public void findBash() {
        Optional<Path> bashLocation = whereIsBash();
        assumeTrue(bashLocation.isPresent());

        Command bash = new Command("bash");
        assertTrue(bash.exists());
        assertEquals(bashLocation.get(), bash.getCommand());
    }


    @Test
    public void findBogusCommand() {
        Command whatAreTheChances = new Command("someBogusNonExistentCommand");
        assertFalse(whatAreTheChances.exists());
        assertNull(whatAreTheChances.getCommand());
    }

    public Optional<Path> whereIsBash() {
        return Stream.of("/usr/bin/bash", "/bin/bash")
                .map(Path::of)
                .filter(Files::exists)
                .filter(Files::isExecutable)
                .findFirst();
    }
}
