package API;

import com.sun.net.httpserver.*;

import server.ServerMain;
import server.Session;
import server.User;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;



public class GetUser implements HttpHandler {
	

	@Override
    public void handle(HttpExchange t) throws IOException {
        // Determine the request method
        String method = t.getRequestMethod();
        //allow CORS
        Headers headers = t.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Content-Type, X-Requested-With, Authorization");
        headers.add("Access-Control-Max-Age", "3600");

        //handle OPTIONS method for CORS preflight
        if ("OPTIONS".equals(t.getRequestMethod())) {
            t.sendResponseHeaders(204, -1); // No content response for preflight
            return; // Stop further processing
        }
        if("GET".equalsIgnoreCase(method)) {
            String query = t.getRequestURI().getQuery();
            
            Map<String, String> queryParams = parseQuery(query);
            String authToken = queryParams.get("auth_token");
            String userID = queryParams.get("account_id");

            //System.out.println(authToken);
            //check for authToken and validate it
            
            boolean validToken = ServerMain.d.tokenValid(authToken);
  
            
            if(authToken == null || !validToken ) {
                String response = "Unauthorized access";
                t.sendResponseHeaders(401, response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
                return;
            }
            
            String path = t.getRequestURI().getPath();
            String[] parts = path.split("/");
            if(parts.length != 3) { 
                String response = "Invalid request";
                t.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
                return;
            }
            String accountId = userID; // Get the account ID part

            User user = ServerMain.d.getUserByID(accountId);
            if(user == null) {
                String response = "User not found "+accountId;
                t.sendResponseHeaders(404, response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
                return;
            }

            // generate respsonse
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("email", user.getEmail());
            jsonResponse.addProperty("account_id", user.getAccountID());
            jsonResponse.addProperty("first_name", user.getFirstName());
            jsonResponse.addProperty("last_name", user.getLastName());
            jsonResponse.addProperty("phone", user.getPhone());
            jsonResponse.addProperty("created_at", user.getCreatedAt());
            jsonResponse.addProperty("updated_at", user.getUpdatedAt());


            String response = jsonResponse.toString();
            
            // Send Response
            t.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        } else {
            String response = "HTTP method not supported";
            t.sendResponseHeaders(405, response.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }
	
    private Map<String, String> parseQuery(String query) {
        Map<String, String> queryPairs = new LinkedHashMap<>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                try {
					queryPairs.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.toString()), URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.toString()));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
        return queryPairs;
    }

    }