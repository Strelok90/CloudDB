import java.io.IOException;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileMessage extends AbstractMessage {
    @Serial
    private static final long serialVersionUID = Long.MAX_VALUE;

    private final String fileName;
    private final byte[] data;
    private final double size;
    private final String sha1;

    public double getSize() {
        return size;
    }

    public String getSha1() {
        return sha1;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }

    public FileMessage(Path path) throws IOException, NoSuchAlgorithmException {
        fileName = path.getFileName().toString();
        data = Files.readAllBytes(path);
        sha1 = getCheckSum();
        size = Files.size(path);
    }

    private String getCheckSum() throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA1");
        sha.update(data);
        byte[] hashByte = sha.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashByte) sb.append(String.format("%08X", b));
        return sb.toString();
    }
}