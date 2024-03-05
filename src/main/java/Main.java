import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Server {
    private Map<String, Object> services;

    public Server() {
        services = new HashMap<>();
    }

    public void registerService(String serviceName, Object service) {
        services.put(serviceName, service);
    }

    public void handle(String payload) {
        Gson gson = new Gson();
        Request[] requests = gson.fromJson(payload, Request[].class);

        for (Request request : requests) {
            String[] methodParts = request.method.split("\\.");
            String serviceName = methodParts[0];
            String methodName = methodParts[1];

            if (services.containsKey(serviceName)) {
                Object service = services.get(serviceName);
                invokeMethod(service, methodName, request.params);
            }
        }
    }

    private void invokeMethod(Object service, String methodName, Map<String, Object> params) {
        try {
            Class<?>[] paramTypes = null;
            Object[] paramValues = null;

            if (params != null) {
                int paramCount = params.size();
                paramTypes = new Class<?>[paramCount];
                paramValues = new Object[paramCount];

                int index = 0;
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    paramTypes[index] = entry.getValue().getClass();
                    paramTypes[index] = entry.getValue().getClass();
                    if (paramTypes[index] == Double.class) {
                        paramTypes[index] = int.class;
                        paramValues[index] = ((Double) entry.getValue()).intValue();
                    } else {
                        paramValues[index] = entry.getValue();
                    }
                    index++;
                }
            }

            service.getClass().getMethod(methodName, paramTypes).invoke(service, paramValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ChatRoom {
    private int messageCount;

    public ChatRoom() {
        messageCount = 0;
    }

    public void new_message(String message) {
        System.out.println("New message: " + message);
        messageCount++;
    }

    public int getMessageCount() {
        return messageCount;
    }
}

class MemberSystem {
    private Map<String, Integer> memberAges;

    public MemberSystem() {
        memberAges = new HashMap<>();
    }

    public void new_member(String email, int age) {
        System.out.println("New member: " + email + ", age: " + age);
        memberAges.put(email, (int) age);
    }
    public void remove_member(String email) {
//        System.out.println("Member removed: " + email);
//        memberAges.remove(email);
    }

    public int getAgeByEmail(String email) {
        return memberAges.getOrDefault(email, -1);
    }
}

class Request {
    public String method;
    public Map<String, Object> params;
}

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        ChatRoom chatRoom = new ChatRoom();
        MemberSystem memberSystem = new MemberSystem();

        server.registerService("chatroom", chatRoom);
        server.registerService("member", memberSystem);

        // TODO: Register the services above to the server object

        /************************************
         * !! DO NOT EDIT CONTENT BELOW !! *
         ************************************/

        // Sending 4 requests at once
        // Don't need to support method member.remove_member, just ignore it

        // [{
        // "method": "chatroom.new_message",
        // "params": { "message": "Foo" }
        // },
        // {
        // "method": "chatroom.new_message",
        // "params": { "message": "Bar" }
        // },
        // {
        // "method": "member.new_member",
        // "params": { "email": "jason@example.com", "age": 12 }
        // },
        // {
        // "method": "member.remove_member",
        // "params": { "email": "tony@example.com" }
        // }]

        String jsonPayload = "[{\"method\": \"chatroom.new_message\", \"params\": { \"message\": \"Foo\" }}, {\"method\": \"chatroom.new_message\", \"params\": { \"message\": \"Bar\" }}, {\"method\": \"member.new_member\", \"params\": { \"email\": \"jason@example.com\", \"age\": 12 }}, {\"method\": \"member.remove_member\", \"params\": { \"email\": \"tony@example.com\" }}]";

        server.handle(jsonPayload);

        System.out.println("Chat room message count: " + chatRoom.getMessageCount() + " (Expected: 2)");
        System.out.println("Jason's age: " + memberSystem.getAgeByEmail("jason@example.com") + " (Expected: 12)");
    }
}