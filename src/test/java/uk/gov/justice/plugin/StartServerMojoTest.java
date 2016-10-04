package uk.gov.justice.plugin;

import static org.junit.Assert.*;
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

    @Test
    public void testGetProject() {
        startServerMojo.setProject(project);
        assertEquals(project, startServerMojo.getProject());
    }

    @Test
    public void testSetProject() {
        startServerMojo.setProject(project);
        assertEquals(project, startServerMojo.getProject());
    }

    @Test
    public void testGetPort() {
        startServerMojo.setPort(9000);
        assertEquals(9000, startServerMojo.getPort());
    }

    @Test
    public void testSetPort() {
        startServerMojo.setPort(9000);
        assertEquals(9000, startServerMojo.getPort());
    }

    @Test
    public void testGetServerClass() {
        startServerMojo.setServerClass(this.getClass().getName());
        assertEquals(this.getClass().getName(), startServerMojo.getServerClass());
    }

    @Test
    public void testSetServerClass() {
        startServerMojo.setServerClass(this.getClass().getName());
        assertEquals(this.getClass().getName(), startServerMojo.getServerClass());
    }

    
    @Test
    public void testGetJava() {
        File f = mock(File.class);
        startServerMojo.setJava(f);
        assertEquals(f, startServerMojo.getJava());
    }

    @Test
    public void testSetJava() {
        File f = mock(File.class);
        startServerMojo.setJava(f);
        assertEquals(f, startServerMojo.getJava());
    }

}
