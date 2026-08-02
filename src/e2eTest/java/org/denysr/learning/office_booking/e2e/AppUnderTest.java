package org.denysr.learning.office_booking.e2e;

import java.nio.file.Path;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

/**
 * The application under test, running in a container that is started once per test JVM and shared
 * by all end-to-end test classes.
 * <p>
 * By default the image is built from the project {@code Dockerfile}, so a plain
 * {@code ./gradlew e2eTest} works with nothing prepared up front. Pass {@code -Pe2eImage=<image>}
 * to test an image that was built beforehand - that is what CI does.
 */
final class AppUnderTest {
    private static final Logger LOG = LoggerFactory.getLogger(AppUnderTest.class);

    private static final int APP_PORT = 8080;
    /** Cheapest endpoint that only answers once the whole context, including JPA, is up. */
    private static final String READINESS_PATH = "/users/users";

    private static final GenericContainer<?> CONTAINER = createContainer();

    static {
        CONTAINER.start();
        LOG.info("Application under test is listening on {}", baseUrl());
    }

    private AppUnderTest() {
    }

    /**
     * Base URL of the running application. The container port is published to a free port picked by
     * the container runtime, so several instances can run side by side without colliding.
     */
    static String baseUrl() {
        return "http://" + CONTAINER.getHost() + ":" + CONTAINER.getMappedPort(APP_PORT);
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
