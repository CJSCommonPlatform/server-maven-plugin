package uk.gov.justice.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;

@Mojo(name = "start", defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES, requiresOnline = false,
                requiresProject = true, threadSafe = false)
public class StartServerMojo extends AbstractMojo {

    /**
     * The port used to stop the spawned process
     */
    @Parameter(property = "port", required = true)
    protected int port;
    /**
     * The class to run in a process
     */
    @Parameter(property = "serverClass", required = true)
    protected String serverClass;
    
    @Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
    private ArtifactRepository local;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${mojoExecution}", readonly = true)
    private MojoExecution mojo;

    @Parameter(defaultValue = "${plugin}", readonly = true)
    private PluginDescriptor plugin;

    @Parameter(defaultValue = "${settings}", readonly = true)
    private Settings settings;

    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File basedir;

    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private File target;

    @Parameter(defaultValue = "${project.build.outputDirectory}", readonly = true)
    private File targetOutputDirectory;

    @Parameter(defaultValue = "${project.build.testOutputDirectory}", readonly = true)
    private File targetTestOutputDirectory;

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServerClass() {
        return serverClass;
    }

    public void setServerClass(String serverClass) {
        this.serverClass = serverClass;
    }

    private Thread waitThread;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            final File java = new File(new File(System.getProperty("java.home"), "bin"), "java");
            
            final List<String> args = new ArrayList<String>();
            args.add(java.getAbsolutePath());
            args.add("-cp");
            args.add(buildClasspath());
            args.add(getServerClass());

            final Process p = new ProcessBuilder(args).start();
            dumpStream(p.getInputStream(), System.out);
            dumpStream(p.getErrorStream(), System.err);
            addShutdownHook(p);
            waitOnStopCommand(p);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String buildClasspath() throws DependencyResolutionRequiredException {

        final String separator = System.getProperty("path.separator");

        final List<String> runtimeLocs = project.getRuntimeClasspathElements();

        runtimeLocs.add(targetOutputDirectory.getAbsolutePath());

        runtimeLocs.add(targetTestOutputDirectory.getAbsolutePath());

        final List<String> paths = new ArrayList<>();

        final Map<String, Artifact> cdependencies = plugin.getArtifactMap();

        for (final Artifact artifact : cdependencies.values()) {
            // Find the artifact in the local repository.
            final Artifact art = local.find(artifact);

            if (Objects.isNull(art) || Objects.isNull(art.getFile()) || (!art.getFile().exists())) {
                throw new DependencyResolutionRequiredException(artifact);
            }

            paths.add(art.getFile().getAbsolutePath());
        }

        runtimeLocs.addAll(paths);

        return String.join(separator, runtimeLocs);
    }

    public void waitOnStopCommand(final Process process) throws IOException {
        // !!! cannot write lambdas in plugins it fails
        // java.lang.ArrayIndexOutOfBoundsException: 5377
        waitThread = new Thread(new Runnable() {

            public void run() {
                try (final ServerSocket ssocket = new ServerSocket(getPort())) {
                    boolean flag = false;
                    while (!flag) {
                        ssocket.accept();
                        flag = true;
                    }
                    destroyIfAlive(process);
                } catch (IOException e) {
                    destroyForciblyIfAlive(process);
                }
            }
        });

        waitThread.start();
    }

    private void addShutdownHook(final Process process) {

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    destroyIfAlive(process);
                } catch (Exception e) {
                    if (!Objects.isNull(process) && process.isAlive()) {
                        process.destroyForcibly();
                    }
                }
            }
        }));
    }

    private void destroyIfAlive(final Process process) {
        if (!Objects.isNull(process) && process.isAlive()) {
            process.destroy();
        }
    }

    private void destroyForciblyIfAlive(final Process process) {
        if (!Objects.isNull(process) && process.isAlive()) {
            process.destroyForcibly();
        }
    }

    private void dumpStream(final InputStream src, final PrintStream dest) {
        new Thread(new Runnable() {
            public void run() {
                try (final Scanner sc = new Scanner(src)) {
                    while (sc.hasNextLine()) {
                        dest.println(sc.nextLine());
                    }
                }
            }
        }).start();
    }
}
