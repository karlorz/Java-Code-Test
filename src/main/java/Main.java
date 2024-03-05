import com.google.gson.Gson;
import java.util.HashMap;
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
        // Handle JSON payload, which might contain one or more requests.
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
                    paramValues[index] = entry.getValue();

                    // Check if the parameter type is Double, convert it to int
                    if (paramTypes[index] == Double.class) {
                        paramTypes[index] = int.class;
                        paramValues[index] = ((Double) entry.getValue()).intValue();
                    }

                    index++;
                }
            }

            // Invoke the method on the service object using reflection
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

    // Get the message count
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
        memberAges.put(email, age);
    }

    // Remove a member by email (not implemented)
    public void remove_member(String email) {
        // Not implemented
    }

    // Get the age of a member by email
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

        // Register the chatRoom and memberSystem services to the server
        server.registerService("chatroom", chatRoom);
        server.registerService("member", memberSystem);

        // JSON payload containing multiple requests
        String jsonPayload = "[{\"method\": \"chatroom.new_message\", \"params\": { \"message\": \"Foo\" }}, {\"method\": \"chatroom.new_message\", \"params\": { \"message\": \"Bar\" }}, {\"method\": \"member.new_member\", \"params\": { \"email\": \"jason@example.com\", \"age\": 12 }}, {\"method\": \"member.remove_member\", \"params\": { \"email\": \"tony@example.com\" }}]";

        // Handle the JSON payload
        server.handle(jsonPayload);

        // Print the results
        System.out.println("Chat room message count: " + chatRoom.getMessageCount() + " (Expected: 2)");
        System.out.println("Jason's age: " + memberSystem.getAgeByEmail("jason@example.com") + " (Expected: 12)");
    }
}