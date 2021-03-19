import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class MainHandler extends ChannelInboundHandlerAdapter {
    private final String userFolder;
    private final String root;

    public MainHandler(String userFolder) {
        this.userFolder = userFolder;
        //this.root = new StringBuilder("Server").append(File.separator).append("ServerStorage").toString();
        this.root = "Server/ServerStorage/";
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg == null) {
                return;
            }
            if (msg instanceof FileRequest) { //Скачиваем с сервака
                FileRequest fr = (FileRequest) msg;
                if (Files.exists(Paths.get(root + fr.getFileName()))){
                    FileMessage fm = new FileMessage(Paths.get(root + fr.getFileName()));
                    ctx.writeAndFlush(fm);
                }
                return;
            }
            if (msg instanceof FileMessage) { //Загружаем файл на сервер
                Files.write(Paths.get(root + ((FileMessage) msg).getFileName()), ((FileMessage) msg).getData(), StandardOpenOption.CREATE);
                System.out.println("Файл получен");
                sendFileList(ctx);
                return;
            }
            if (msg instanceof FileListRequest) {
                sendFileList(ctx);
                return;
            }
            if (msg instanceof FileDeleteRequest){
                FileDeleteRequest deleteRequest = (FileDeleteRequest) msg;
                Files.delete(Paths.get(root + deleteRequest.getFileName()));
                sendFileList(ctx);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void sendFileList(ChannelHandlerContext ctx) throws IOException {
        FileListRequest listRequest = new FileListRequest();
        Files.list(Paths.get(root)).map(p -> p.getFileName().toString()).forEach(listRequest::addFile);
        ctx.writeAndFlush(listRequest);
        System.out.println(listRequest.getRemoteFiles());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public String getUserFolder() {
        return userFolder;
    }
}