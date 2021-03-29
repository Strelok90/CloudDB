

public class AuthRequest extends AbstractMessage {
    private final String folderName;

    public String getLogin() {
        return folderName;
    }

    public AuthRequest(String folderName) {
        this.folderName = folderName;
    }

}