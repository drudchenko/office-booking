package org.denysr.learning.office_booking.e2e;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.StoreScope;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

/**
 * Runs the application in a container and injects API clients pointed at it into the annotated test
 * classes. Covering another part of the API means writing its client and adding one {@link #CLIENTS}
 * entry; nothing else here needs to change.
 * <p>
 * The container lives in the launcher session store, so it is started once for the whole test run
 * however many classes ask for it, and JUnit closes it when the session ends.
 * <p>
 * The image is built from the project {@code Dockerfile} by default, so a plain
 * {@code ./gradlew e2eTest} works with nothing prepared up front. Pass {@code -Pe2eImage=<image>}
 * to test an image that was built beforehand - that is what CI does.
 */
final class ApplicationUnderTest implements BeforeAllCallback, ParameterResolver {
    private static final Namespace NAMESPACE = Namespace.create(ApplicationUnderTest.class);

    /** The injectable API clients, each built from the base URL of the running application. */
    private static final Map<Class<?>, Function<String, Object>> CLIENTS =
            Map.<Class<?>, Function<String, Object>>of(
                    UserApiClient.class, UserApiClient::new
            );

    /** Fail on startup problems here rather than on the first test that touches the API. */
    @Override
    public void beforeAll(ExtensionContext context) {
        runningApplication(context);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameter, ExtensionContext context) {
        return CLIENTS.containsKey(parameter.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameter, ExtensionContext context) {
        return runningApplication(context).client(parameter.getParameter().getType());
    }

    private static RunningApplication runningApplication(ExtensionContext context) {
        return context.getStore(StoreScope.LAUNCHER_SESSION, NAMESPACE)
                .computeIfAbsent(
                        RunningApplication.class,
                        key -> new RunningApplication(),
                        RunningApplication.class
                );
    }

    /** The started container, closed by JUnit at the end of the launcher session. */
    private static final class RunningApplication implements AutoCloseable {
        private static final Logger LOG = LoggerFactory.getLogger(ApplicationUnderTest.class);

        private static final int APP_PORT = 8080;
        /** Cheapest endpoint that only answers once the whole context, including JPA, is up. */
        private static final String READINESS_PATH = "/users/users";

        private final GenericContainer<?> container;
        private final String baseUrl;
        private final Map<Class<?>, Object> clients = new ConcurrentHashMap<>();

        private RunningApplication() {
            container = createContainer();
            container.start();
            // The container port is published to a free port picked by the container runtime,
            // so several instances can run side by side without colliding.
            baseUrl = "http://" + container.getHost() + ":" + container.getMappedPort(APP_PORT);
            LOG.info("Application under test is listening on {}", baseUrl);
        }

        /** One client per type, created on first use and shared by every test that asks for it. */
        private Object client(Class<?> type) {
            return clients.computeIfAbsent(type, key -> CLIENTS.get(key).apply(baseUrl));
        }

        @Override
        public void close() {
            LOG.info("Stopping the application under test");
            container.stop();
        }

        private static GenericContainer<?> createContainer() {
            final String prebuiltImage = System.getProperty("e2e.image", "").trim();
            final GenericContainer<?> container;
            if (prebuiltImage.isEmpty()) {
                LOG.info("No -Pe2eImage given, building the image from the project Dockerfile");
                container = new GenericContainer<>(imageBuiltFromDockerfile());
            } else {
                LOG.info("Testing prebuilt image {}", prebuiltImage);
                container = new GenericContainer<>(DockerImageName.parse(prebuiltImage));
            }
            container.withExposedPorts(APP_PORT);
            container.waitingFor(
                    Wait.forHttp(READINESS_PATH)
                            .forPort(APP_PORT)
                            .forStatusCode(200)
                            .withStartupTimeout(Duration.ofMinutes(5))
            );
            container.withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("office-booking-app")));
            return container;
        }

        private static ImageFromDockerfile imageBuiltFromDockerfile() {
            final Path projectRoot = Path.of(System.getProperty("e2e.projectRoot", "."));
            // Keeping the image around makes repeated local runs cheap; the Dockerfile itself is layer cached.
            return new ImageFromDockerfile("office-booking-e2e:latest", false)
                    .withDockerfile(projectRoot.resolve("Dockerfile"));
        }
    }
}
