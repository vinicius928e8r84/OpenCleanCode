import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Compila os .java de src/ contra o jar do CodeScene, gerando os .class que vao
 * para patch/cacsremoteservice/features/license/.
 *
 * Roda dentro da imagem do CodeScene (tem javac):
 *   docker run --rm -v "$PWD":/w -w /w --entrypoint java \
 *     codescene/codescene -cp /opt/codescene/codescene-enterprise-edition.standalone.jar \
 *     src/Build.java /w/src /w/patch
 */
public class Build {
    public static void main(String[] a) throws Exception {
        Path srcDir = Paths.get(a[0]);
        String outDir = a[1];
        String jar = "/opt/codescene/codescene-enterprise-edition.standalone.jar";
        JavaCompiler c = ToolProvider.getSystemJavaCompiler();
        if (c == null) {
            System.err.println("sem javac nesta JVM");
            System.exit(1);
        }
        try (Stream<Path> files = Files.list(srcDir)) {
            for (Path p : (Iterable<Path>) files.filter(f -> f.toString().endsWith(".java")
                    && !f.getFileName().toString().equals("Build.java"))::iterator) {
                int rc = c.run(null, null, null, "-classpath", jar, "-d", outDir, p.toString());
                System.out.println(p.getFileName() + " rc=" + rc);
                if (rc != 0) System.exit(rc);
            }
        }
    }
}
