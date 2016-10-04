package uk.gov.justice.plugin;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class StartServerMojoTest {

    @Mock
    MavenProject project;

    @Mock
    File targetOutputDirectory;

    @Mock
    File targetTestOutputDirectory;

    @Mock
    PluginDescriptor plugin;

    @InjectMocks
    StartServerMojo startServerMojo;

    @Test
    public void shouldExecuteMojo() throws Exception {

        when(project.getRuntimeClasspathElements()).thenReturn(new ArrayList<>());

        when(targetOutputDirectory.getAbsolutePath()).thenReturn("");

        when(targetTestOutputDirectory.getAbsolutePath()).thenReturn("");

        startServerMojo.setServerClass(
                        mock(StartServerMojoTest.class).getClass().getCanonicalName());

        when(plugin.getArtifactMap()).thenReturn(new HashMap<String, Artifact>());

        startServerMojo.execute();

    }

}
