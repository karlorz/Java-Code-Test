import org.junit.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void testRegisterService() {
        Server server = new Server();
        ChatRoom chatRoom = new ChatRoom();

        server.registerService("chatroom", chatRoom);

        assertNotNull(server.services.get("chatroom"));
    }

    @Test
    public void testHandleRequest() {
        Server server = new Server();
        ChatRoom chatRoom = new ChatRoom();

        server.registerService("chatroom", chatRoom);

        String payload = "[{ \"method\": \"chatroom.new_message\", \"params\": { \"message\": \"Hello\" } }]";

        server.handle(payload);

        assertEquals(1, chatRoom.getMessageCount());
    }

    @Test
    public void testRandomCase() {
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

}