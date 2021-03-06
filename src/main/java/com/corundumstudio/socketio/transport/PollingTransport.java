/**
 * Copyright (c) 2012-2019 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.corundumstudio.socketio.transport;

import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.handler.AuthorizeHandler;
import com.corundumstudio.socketio.handler.ClientHead;
import com.corundumstudio.socketio.handler.ClientsBox;
import com.corundumstudio.socketio.handler.EncoderHandler;
import com.corundumstudio.socketio.messages.HttpMessage;
import com.corundumstudio.socketio.messages.OutPacketMessage;
import com.corundumstudio.socketio.messages.PacketsMessage;
import com.corundumstudio.socketio.messages.XHROptionsMessage;
import com.corundumstudio.socketio.messages.XHRPostMessage;
import com.corundumstudio.socketio.protocol.PacketDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Sharable
public class PollingTransport extends ChannelInboundHandlerAdapter {

    public static final String NAME = "polling";

    private static final Logger log = LoggerFactory.getLogger(PollingTransport.class);

    private final PacketDecoder decoder;
    private final ClientsBox clientsBox;
    private final AuthorizeHandler authorizeHandler;

    public PollingTransport(PacketDecoder decoder, AuthorizeHandler authorizeHandler, ClientsBox clientsBox) {
        this.decoder = decoder;
        this.authorizeHandler = authorizeHandler;
        this.clientsBox = clientsBox;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;
            QueryStringDecoder queryDecoder = new QueryStringDecoder(req.uri());
            Map<String, List<String>> parameters = queryDecoder.parameters();
          
            List<String> transport = parameters.get("transport");

            if (isPolling(transport)) {
                List<String> sid = parameters.get("sid");
                List<String> j = parameters.get("j");
                List<String> b64 = parameters.get("b64");

                String origin = req.headers().get(HttpHeaderNames.ORIGIN);
                ctx.channel().attr(HttpMessage.ORIGIN).set(origin);

                String userAgent = req.headers().get(HttpHeaderNames.USER_AGENT);
                ctx.channel().attr(HttpMessage.USER_AGENT).set(userAgent);

                if (isNotNull(j)) {
                    Integer index = Integer.valueOf(j.get(0));
                    ctx.channel().attr(OutPacketMessage.JSONP_INDEX).set(index);
                }
                if (isNotNull(b64)) {
                    String flag = b64.get(0);
                    if ("true".equals(flag)) {
                        flag = "1";
                    } else if ("false".equals(flag)) {
                        flag = "0";
                    }
                    Integer enable = Integer.valueOf(flag);
                    ctx.channel().attr(OutPacketMessage.B64).set(enable == 1);
                }

                try {
                    if (isNotNull(sid)) {
                        final UUID sessionId = UUID.fromString(sid.get(0));
                        handleMessage(req, sessionId, queryDecoder, ctx);
                    } else {
                        // first connection
                        ClientHead client = ctx.channel().attr(ClientHead.CLIENT).get();
                        handleMessage(req, client.getSessionId(), queryDecoder, ctx);
                    }
                } finally {
                    req.release();
                }
                return;
            }
        }
        ctx.fireChannelRead(msg);
    }

    private void handleMessage(FullHttpRequest req, UUID sessionId, QueryStringDecoder queryDecoder, ChannelHandlerContext ctx)
                                                                                throws IOException {
            String origin = req.headers().get(HttpHeaderNames.ORIGIN);
            if (queryDecoder.parameters().containsKey("disconnect")) {
                disconnectChannel(sessionId, origin, ctx);
            } else {
                handleHttpMethod(req.method(), req.content(), sessionId, ctx, origin);
            } 
    }

    private void disconnectChannel(UUID sId, String origin, ChannelHandlerContext ctx){
            ClientHead client = clientsBox.get(sId);
            client.onChannelDisconnect();
            ctx.channel().writeAndFlush(new XHRPostMessage(origin, sId));
    }
    
    private void handleHttpMethod(HttpMethod method, ByteBuf contents, UUID sessionId, ChannelHandlerContext ctx, String origin) throws IOException{
    	if (HttpMethod.POST.equals(method)) {
            onPost(sessionId, ctx, origin, contents);
        } else if (HttpMethod.GET.equals(method)) {
            onGet(sessionId, ctx, origin);
        } else if (HttpMethod.OPTIONS.equals(method)) {
            onOptions(sessionId, ctx, origin);
        } else {
            log.error("Wrong {} method invocation for {}", method, sessionId);
            sendError(ctx);
        }
        return;
    }

    private void onOptions(UUID sessionId, ChannelHandlerContext ctx, String origin) {
        ClientHead client = clientsBox.get(sessionId);
        if (client == null) {
            log.error("{} is not registered. Closing connection", sessionId);
            sendError(ctx);
            return;
        }

        ctx.channel().writeAndFlush(new XHROptionsMessage(origin, sessionId));
    }

     private void onPost(UUID sessionId, ChannelHandlerContext ctx, String origin, ByteBuf content)
                                                                                 throws IOException {
         ClientHead client = clientsBox.get(sessionId);
         if (client == null) {
             log.error("{} is not registered. Closing connection", sessionId);
             sendError(ctx);
             return;
         }


         // release POST response before message processing
         ctx.channel().writeAndFlush(new XHRPostMessage(origin, sessionId));

         Boolean b64 = ctx.channel().attr(OutPacketMessage.B64).get();
         if (b64 != null && b64) {
             Integer jsonIndex = ctx.channel().attr(OutPacketMessage.JSONP_INDEX).get();
             content = decoder.preprocessJson(jsonIndex, content);
         }

         ctx.pipeline().fireChannelRead(new PacketsMessage(client, content, Transport.POLLING));
     }

     protected void onGet(UUID sessionId, ChannelHandlerContext ctx, String origin) {
         ClientHead client = clientsBox.get(sessionId);
         if (client == null) {
             log.error("{} is not registered. Closing connection", sessionId);
             sendError(ctx);
             return;
         }

         client.bindChannel(ctx.channel(), Transport.POLLING);

         authorizeHandler.connect(client);
     }

     private void sendError(ChannelHandlerContext ctx) {
         HttpResponse res = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
         ctx.channel().writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
     }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final Channel channel = ctx.channel();
        ClientHead client = clientsBox.get(channel);
        if (client != null && client.isTransportChannel(ctx.channel(), Transport.POLLING)) {
            log.debug("channel inactive {}", client.getSessionId());
            client.releasePollingChannel(channel);
        }
        super.channelInactive(ctx);
    }

    private Boolean isPolling(List<String> transport){
        return transport != null && NAME.equals(transport.get(0));
    }

    private Boolean isNotNull(List<String> param){
        return param != null && param.get(0) != null;
    }

}
