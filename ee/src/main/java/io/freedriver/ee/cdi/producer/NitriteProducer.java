package io.freedriver.ee.cdi.producer;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.freedriver.ee.cdi.qualifier.NitriteDatabase;
import io.freedriver.ee.cdi.qualifier.NitriteDatabaseLiteral;
import io.freedriver.ee.convention.AppData;
import io.freedriver.ee.prop.DeploymentProperties;
import org.dizitart.no2.Nitrite;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.logging.Logger;

@ApplicationScoped
public class NitriteProducer {
    private static final Logger LOGGER = Logger.getLogger(NitriteProducer.class.getName());

    private final Map<NitriteDatabase, Nitrite> cache = new ConcurrentHashMap<>(new HashMap<>());

    @Produces
    @NitriteDatabase
    public Nitrite getNitrite(InjectionPoint injectionPoint) {
        NitriteDatabase nitriteDatabase = injectionPoint.getQualifiers()
                .stream()
                .filter(NitriteDatabase.class::isInstance)
                .map(NitriteDatabase.class::cast)
                .findFirst()
                .orElseGet(NitriteDatabaseLiteral::new);
        return getOrBuild(nitriteDatabase, () -> Nitrite.builder()
                .registerModule(new JavaTimeModule())
                .compressed()
                .filePath(inConfigDirectory(nitriteDatabase).toFile())
                .openOrCreate("nitrite", "nitrite")); // TODO
    }

    public static Path inConfigDirectory(NitriteDatabase nitriteDatabase) {
        return inConfigDirectory(DeploymentProperties.NAME.fetch().orElse(nitriteDatabase.deployment()), nitriteDatabase.database());
    }

    public static Path inConfigDirectory(String deployment, Class<?> entityClass) {
        Path deployPath = AppData.get(deployment);
        LOGGER.warning("INConfig: " + deployPath.toString());
        if (!Files.isDirectory(deployPath)) {
            try {
                Files.createDirectories(deployPath);
            } catch (IOException e) {
                throw new RuntimeException(e); // TODO
            }
        }
        return AppData.get(deployment, getEntityDatabase(entityClass));
    }

    public static String getEntityDatabase(Class<?> klazz) {
        return ((klazz == null || klazz == Object.class) ?
                "Default" : klazz.getSimpleName())
                + ".db";
    }

    public synchronized Nitrite getOrBuild(NitriteDatabase nitriteDatabase, Supplier<Nitrite> supplier) {
        Nitrite resolved =  Optional.of(nitriteDatabase)
                .filter(cache::containsKey)
                .map(cache::get)
                .orElseGet(supplier);
        if (!cache.containsKey(nitriteDatabase)) {
            cache.put(nitriteDatabase, resolved);
        }
        return resolved;
    }

}
