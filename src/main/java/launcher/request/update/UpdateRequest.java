package launcher.request.update;

import launcher.Launcher.Config;
import launcher.LauncherAPI;
import launcher.hasher.FileNameMatcher;
import launcher.hasher.HashedDir;
import launcher.helper.IOHelper;
import launcher.request.Request;
import launcher.serialize.HInput;
import launcher.serialize.HOutput;
import launcher.serialize.signed.SignedObjectHolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SignatureException;
import java.util.Objects;

public final class UpdateRequest extends Request<SignedObjectHolder<HashedDir>>
{
    @LauncherAPI
    public static final int MAX_QUEUE_SIZE = 128;

    // Instance
    private final String dirName;
    private final Path dir;

    @LauncherAPI
    public UpdateRequest(Config config, String dirName, Path dir, FileNameMatcher matcher, boolean digest) {
        super(config);
        this.dirName = IOHelper.verifyFileName(dirName);
        this.dir = Objects.requireNonNull(dir, "dir");
    }

    @LauncherAPI
    public UpdateRequest(String dirName, Path dir, FileNameMatcher matcher, boolean digest) {
        this(null, dirName, dir, matcher, digest);
    }

    @LauncherAPI
    public void setStateCallback(Object callback) {}

    @Override
    public Type getType()
    {
        return Type.UPDATE;
    }

    @Override
    public SignedObjectHolder<HashedDir> request() throws Throwable {
        Files.createDirectories(dir);
        return super.request();
    }

    @Override
    protected SignedObjectHolder<HashedDir> requestDo(HInput input, HOutput output) throws IOException, SignatureException {
        output.writeString(this.dirName, 255);
        output.flush();
        this.readError(input);
        final SignedObjectHolder<HashedDir> remoteHDirHolder = new SignedObjectHolder<HashedDir>(input, this.config.publicKey, HashedDir::new);
        return remoteHDirHolder;
    }
}
