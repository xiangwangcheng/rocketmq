/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.mqtt;

import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import java.util.HashSet;
import org.apache.rocketmq.common.MqttConfig;
import org.apache.rocketmq.common.SnodeConfig;
import org.apache.rocketmq.common.client.ClientRole;
import org.apache.rocketmq.common.message.mqtt.WillMessage;
import org.apache.rocketmq.mqtt.client.IOTClientManagerImpl;
import org.apache.rocketmq.mqtt.client.MQTTSession;
import org.apache.rocketmq.mqtt.mqtthandler.impl.MqttDisconnectMessageHandler;
import org.apache.rocketmq.mqtt.processor.DefaultMqttMessageProcessor;
import org.apache.rocketmq.remoting.RemotingChannel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MqttDisconnectMessageHandlerTest {

    @Mock
    private RemotingChannel remotingChannel;

    @Mock
    private DefaultMqttMessageProcessor defaultMqttMessageProcessor;

    @Before
    public void before() {
        this.defaultMqttMessageProcessor = new DefaultMqttMessageProcessor(new MqttConfig(), new SnodeConfig(), null, null, null);
    }

    @Test
    public void testHandlerMessage() {

        MqttDisconnectMessageHandler mqttDisconnectMessageHandler = new MqttDisconnectMessageHandler(
            defaultMqttMessageProcessor);
        MQTTSession client = new MQTTSession("123456", ClientRole.IOTCLIENT, new HashSet<String>() {
            {
                add("IOT_GROUP");
            }
        }, true, true, remotingChannel, System.currentTimeMillis(), defaultMqttMessageProcessor);
        defaultMqttMessageProcessor.getIotClientManager().register(IOTClientManagerImpl.IOT_GROUP, client);
        defaultMqttMessageProcessor.getWillMessageService().saveWillMessage("123456", new WillMessage());
        MqttMessage mqttDisconnectMessage = new MqttMessage(new MqttFixedHeader(
            MqttMessageType.DISCONNECT, false, MqttQoS.AT_MOST_ONCE, false, 200));

        mqttDisconnectMessageHandler.handleMessage(mqttDisconnectMessage, remotingChannel);
    }

    @Test
    public void testHandlerMessage_fixHeaderIllegal() {

        this.defaultMqttMessageProcessor = new DefaultMqttMessageProcessor(new MqttConfig(), new SnodeConfig(), null, null, null);
        MqttDisconnectMessageHandler mqttDisconnectMessageHandler = new MqttDisconnectMessageHandler(
            defaultMqttMessageProcessor);
        MQTTSession client = new MQTTSession("123456", ClientRole.IOTCLIENT, new HashSet<String>() {
            {
                add("IOT_GROUP");
            }
        }, true, true, remotingChannel, System.currentTimeMillis(), defaultMqttMessageProcessor);
        defaultMqttMessageProcessor.getIotClientManager().register(IOTClientManagerImpl.IOT_GROUP, client);
        defaultMqttMessageProcessor.getWillMessageService().saveWillMessage("123456", new WillMessage());
        MqttMessage mqttDisconnectMessage = new MqttMessage(new MqttFixedHeader(
            MqttMessageType.DISCONNECT, false, MqttQoS.AT_LEAST_ONCE, false, 200));

        mqttDisconnectMessageHandler.handleMessage(mqttDisconnectMessage, remotingChannel);
    }
}
