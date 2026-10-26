/**
 * Application entry point for Brarner.M.Alete.
 *
 * <p>Delegates to {@link BaseServer}, which reads {@code source-code/config.xml}
 * and starts a listening thread for every active instance across all enabled
 * modules.</p>
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        BaseServer.main(args);
    }
}
