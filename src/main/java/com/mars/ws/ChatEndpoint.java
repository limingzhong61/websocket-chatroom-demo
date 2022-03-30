package com.mars.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mars.pojo.Message;
import com.mars.util.MessageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**

 * Date Time:      2021/5/24 22:40
 */


@ServerEndpoint(value = "/chat", configurator = GetHttpSessionConfigurator.class)
@Component
public class ChatEndpoint {

    private final static Logger LOGGER = LogManager.getLogger(ChatEndpoint.class);

    /**
     * 用来储存在线用户的容器
     * 用来存储每一个客户端对象对应的ChatEndpoint对象
     */

    public static Map<String, ChatEndpoint> onlineUsers = new ConcurrentHashMap<>();

    /**
     * 用来给客户端发送消息
     * /声明session对象，通过该对象可以发送消息给指定的用户
     */
    private Session session;

    /**
     * 用来获取在登录成功后，放在httpsession域中存放的username
     * 声明一个HttpSession对象，我们之前在HttpSession对象中存储了用户名
     */
    private HttpSession httpSession;

    /**
     *
     *   连接建立时调用
     * @param session
     * @param endpointConfig
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        LOGGER.info("wbsocket建立连接onOpen！");
        //将局部的session对象赋值给成员session
        this.session = session;
        //获取httpsession对象
        HttpSession httpSession = (HttpSession) endpointConfig.getUserProperties().get(HttpSession.class.getName());
        this.httpSession = httpSession;
        //获取httpsession域中存放的username对应的值
        String username = (String) httpSession.getAttribute("username");
        //存放到onlineUsers中保存
        onlineUsers.put(username, this);

        //系统消息推送所有在线用户给客户端
        //封装系统推送消息,前端onmessage接收的数据
        //将当前在线用户的用户名推送给所有的客户端
        // 1,获取消息
        String message = MessageUtils.getMessage(true, null, getAllOnlineUsername());
        //2,调用方法进行系统消息的推送
        broadcastMsgToAllOnlineUsers(message);
    }

    /**
     * .
     * @param message 给所有客户端发送消息
     * @return void
     */
    private void broadcastMsgToAllOnlineUsers(String message) {
        //所有登录用户名称
        Set<String> names = onlineUsers.keySet();
        for (String name : names) {
            ChatEndpoint chatEndpoint = onlineUsers.get(name);
            //获取推送对象
            RemoteEndpoint.Basic basicRemote = chatEndpoint.session.getBasicRemote();
            try {
                basicRemote.sendText(message);
            } catch (IOException e) {
                LOGGER.error("广播发送系统消息失败！{}", e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取发送给客户端的消息，消息数据示例值：{“systemMsgFlag”: true, "fromName": null, "message": ["Name1", "Name2"]}.
     * @return java.lang.Object
     */
    private Object getAllOnlineUsername() {
        return ChatEndpoint.onlineUsers.keySet();
    }



    /**
     * 接收到客户端发送的数据时调用.
     * @param message 客户端发送的数据
     * @param session session对象
     * @return void
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Message msg = objectMapper.readValue(message, Message.class);
            //获取接收信息的用户
            String toName = msg.getToName();
            //获取发送的消息
            String msgData = msg.getMessage();
            //获取当前登录的用户
            String username = (String) httpSession.getAttribute("username");
            //封装发送的消息
            String sendMsg = MessageUtils.getMessage(false, username, msgData);
            //发送消息
            onlineUsers.get(toName).session.getBasicRemote().sendText(sendMsg);
        } catch (JsonProcessingException e) {
            LOGGER.error("接收客户端的消息，转换出错了！");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 连接关闭时调用
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        String username = (String) httpSession.getAttribute("username");
        //
        ChatEndpoint remove = onlineUsers.remove(username);
        //广播
        String message = MessageUtils.getMessage(true, null, getAllOnlineUsername());
        broadcastMsgToAllOnlineUsers(message);
    }

}
