import org.junit.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void testRegisterService_shouldRegisterChatRoomService() {
        Server server = new Server();
        ChatRoom chatRoom = new ChatRoom();

        server.registerService("chatroom", chatRoom);

        assertNotNull(server.services.get("chatroom"));
    }

    @Test
    public void testHandleRequest_shouldAddMessageToChatRoom() {
        Server server = new Server();
        ChatRoom chatRoom = new ChatRoom();

        server.registerService("chatroom", chatRoom);

        String payload = "[{ \"method\": \"chatroom.new_message\", \"params\": { \"message\": \"Hello\" } }]";

        server.handle(payload);

        assertEquals(1, chatRoom.getMessageCount());
    }

    @Test
    public void testHandleRequest_shouldAddRandomMessageAndMember() {
        // Generate random input data
        String randomMessage = UUID.randomUUID().toString();
        int randomAge = new Random().nextInt(99);

        Server server = new Server();
        ChatRoom chatRoom = new ChatRoom();
        MemberSystem memberSystem = new MemberSystem();

        server.registerService("chatroom", chatRoom);
        server.registerService("member", memberSystem);

        String payload = "[{ \"method\": \"chatroom.new_message\", \"params\": { \"message\": \"" + randomMessage +"\" } }, { \"method\": \"member.new_member\", \"params\": { \"email\": \"test@example.com\", \"age\": "+ randomAge +" } }]";

        server.handle(payload);

        assertEquals(1, chatRoom.getMessageCount());
        assertEquals(randomAge, memberSystem.getAgeByEmail("test@example.com"));
    }

    @Test
    public void testHandleRequest_shouldAddMultipleMessagesToChatRoom() {
        Server server = new Server();
        ChatRoom chatRoom = new ChatRoom();
        MemberSystem memberSystem = new MemberSystem();

        server.registerService("chatroom", chatRoom);
        server.registerService("member", memberSystem);

        String jsonPayload = "[{\"method\": \"chatroom.new_message\", \"params\": { \"message\": \"Hello\" }}, {\"method\": \"chatroom.new_message\", \"params\": { \"message\": \"World\" }}]";

        server.handle(jsonPayload);

        assertEquals(2, chatRoom.getMessageCount());
    }

    @Test
    public void testHandleRequest_shouldAddMessageAndRegisterMember() {
        Server server = new Server();
        ChatRoom chatRoom = new ChatRoom();
        MemberSystem memberSystem = new MemberSystem();

        server.registerService("chatroom", chatRoom);
        server.registerService("member", memberSystem);

        String jsonPayload = "[{\"method\": \"chatroom.new_message\", \"params\": { \"message\": \"Test\" }}, {\"method\": \"member.new_member\", \"params\": { \"email\": \"test@example.com\", \"age\": 18 }}]";

        server.handle(jsonPayload);

        assertEquals(1, chatRoom.getMessageCount());
        assertEquals(18, memberSystem.getAgeByEmail("test@example.com"));
    }

    @Test
    public void testHandleRequest_shouldHandleEmptyPayload() {
        Server server = new Server();
        ChatRoom chatRoom = new ChatRoom();
        MemberSystem memberSystem = new MemberSystem();

        server.registerService("chatroom", chatRoom);
        server.registerService("member", memberSystem);

        String jsonPayload = "[]";

        server.handle(jsonPayload);

        assertEquals(0, chatRoom.getMessageCount());
    }
}