package fzdwx.netty.http2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;

/**
 * @author <a href="mailto:likelovec@gmail.com">fzdwx</a>
 * @date 2022/9/28 15:37
 */
class Utils {

    public static ApplicationProtocolNegotiationHandler getServerAPNHandler() {
        return new ApplicationProtocolNegotiationHandler(ApplicationProtocolNames.HTTP_2) {

            @Override
            protected void configurePipeline(ChannelHandlerContext ctx, String protocol) throws Exception {
                if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
                    ctx.pipeline().addLast(
                            Http2FrameCodecBuilder.forServer().build(), new Http2ServerResponseHandler());
                    return;
                }
                throw new IllegalStateException("Protocol: " + protocol + " not supported");
            }
        };
    }
}
