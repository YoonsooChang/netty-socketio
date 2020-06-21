package com.corundumstudio.socketio.handler;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.messages.HttpErrorMessage;
import com.corundumstudio.socketio.messages.HttpMessage;
import com.corundumstudio.socketio.messages.OutPacketMessage;
import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.corundumstudio.socketio.protocol.PacketEncoder;
import com.corundumstudio.socketio.messages.OutPacketMessage;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class EncoderHandlerTest {
    EncoderHandler encoderHandler;
    Configuration configuration;

    @BeforeClass
    public void oneTimeSetUp() throws IOException {
        configuration = new Configuration();
        encoderHandler = new EncoderHandler(configuration, new PacketEncoder(configuration, new JacksonJsonSupport()));
    }

    /**
     * Purpose: Test if-else Condition
     * Input: new Object
     * Expected:
     *          Return Exception
     *          Object instance != HttpMessage
     */
    @Test(expected = Exception.class)
    public void getHttpMessage() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = encoderHandler.getClass().getDeclaredMethod("getHttpMessage", Object.class);
        method.setAccessible(true);
        method.invoke(encoderHandler, new Object());
    }

    /**
     * Purpose: Test if-else Condition
     * Input: new HttpErrorMessage object
     * Expected:
     *          Return Success
     *          object = HttpErrorMessage class
     */
    @Test
    public void getHttpMessage1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = encoderHandler.getClass().getDeclaredMethod("getHttpMessage", Object.class);
        method.setAccessible(true);

        Object object = method.invoke(encoderHandler, new HttpErrorMessage(new HashMap<String, Object>()));
        assertTrue(object instanceof HttpErrorMessage);
    }
}