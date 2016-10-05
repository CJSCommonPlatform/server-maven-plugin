package uk.gov.justice.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StopServerMojoTest {

    @InjectMocks
    StopServerMojo stopServerMojo;

    @Test
    public void shouldGetPort() {
        stopServerMojo.setPort(8000);
        assertEquals(8000, stopServerMojo.getPort());
    }

    @Test
    public void shouldSetPort() {
        stopServerMojo.setPort(8000);
        assertEquals(8000, stopServerMojo.getPort());
    }

    @Test
    public void shouldExecuteMojo() throws IOException {
        try (final ServerSocket socket = new ServerSocket(0)) {
            int port = socket.getLocalPort();
            stopServerMojo.setPort(port);
            stopServerMojo.execute();
        } catch (MojoExecutionException e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeException() throws MojoExecutionException, IOException {
        try (final ServerSocket socket = new ServerSocket(0)) {
            int port = socket.getLocalPort();
            stopServerMojo.setPort(port - 1);
            stopServerMojo.execute();
        }
    }

}
