package pers.fq.hippo.transporter;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/7/14
 */
public class ServerConfig {
    public final RequestHandler requestHandler;

    public final String host;

    public final int port;

    public ServerConfig(RequestHandler requestHandler, String host, int port) {
        this.requestHandler = requestHandler;
        this.host = host;
        this.port = port;
    }
}
