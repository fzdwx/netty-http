package fzdwx.netty.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.DefaultHttp2DataFrame;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.DefaultHttp2HeadersFrame;
import io.netty.handler.codec.http2.DefaultHttp2PingFrame;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2PingFrame;
import io.netty.handler.codec.http2.Http2SettingsAckFrame;
import io.netty.handler.codec.http2.Http2SettingsFrame;
import io.netty.util.CharsetUtil;

/**
 * @author <a href="mailto:likelovec@gmail.com">fzdwx</a>
 * @date 2022/9/28 15:38
 */
public class Http2ServerResponseHandler extends SimpleChannelInboundHandler {

    static final ByteBuf RESPONSE_BYTES = Unpooled.unreleasableBuffer(
            Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8));

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof Http2HeadersFrame f) {
            if (f.isEndStream()) {
                ByteBuf content = ctx.alloc().buffer();
                content.writeBytes(RESPONSE_BYTES.duplicate());

                Http2Headers headers = new DefaultHttp2Headers().status(HttpResponseStatus.OK.codeAsText());
                ctx.write(new DefaultHttp2HeadersFrame(headers).stream(f.stream()));
                ctx.write(new DefaultHttp2DataFrame(content, true).stream(f.stream()));
            }
        } else if (msg instanceof Http2PingFrame f) {
            ctx.write(new DefaultHttp2PingFrame(f.content(), true));
        } else if (msg instanceof Http2SettingsFrame) {
            ctx.write(Http2SettingsAckFrame.INSTANCE);
        } else if (msg instanceof Http2SettingsAckFrame) {
            
        } else {
            System.out.println("Unsupport frame " + msg.getClass());
            super.channelRead(ctx, msg);
        }
    }
}
