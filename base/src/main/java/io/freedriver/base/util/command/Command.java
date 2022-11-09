package io.freedriver.base.util.command;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Command {
    private final String invocationName;
    private final Path command;

    public Command(String invocationName) {
        this.invocationName = invocationName;
        this.command = getCommandPath(invocationName).orElse(null);
    }

    public Command(Path command) {
        this.invocationName = command.getFileName().toString();
        this.command = command;
    }

    public String getInvocationName() {
        return invocationName;
    }

    public Path getCommand() {
        return command;
    }

    public boolean exists() {
        return command != null;
    }

    public static Stream<Path> getCommandPaths() {
        return Optional.of("PATH")
                .map(System::getenv)
                .map(path -> path.split(File.pathSeparator))
                .stream()
                .flatMap(Stream::of)
                .map(Path::of);
    }

    public static Stream<Command> getAllKnownCommands() {
        return getCommandPaths().flatMap(Command::getAllKnownCommandsIn);
    }
    public static Stream<Command> getAllKnownCommandsIn(Path pathDir) {
        if (pathDir != null && Files.isDirectory(pathDir)) {
            try (Stream<Path> list = Files.list(pathDir)) {
                return list.filter(Command::isCommand).map(Command::new).collect(Collectors.toList()).stream();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return Stream.empty();
    }

    public static boolean isCommand(Path path) {
        return path != null && Files.exists(path) && Files.isRegularFile(path) && Files.isExecutable(path);
    }
    public static Optional<Path> getCommandPath(String command) {
        return getCommandPaths()
                .filter(Files::isDirectory)
                .map(pathDir -> pathDir.resolve(command))
                .filter(Command::isCommand)
                .findFirst();
    }
}
