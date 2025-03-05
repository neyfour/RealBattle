package Controller;

import GameService.GameSession;
import GameService.GameSessionManager;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="CreateSessionServlet", urlPatterns={"/api/sessions/create"})
public class CreateSessionServlet extends HttpServlet {
    
    private GameSessionManager sessionManager;

    public CreateSessionServlet() {
        this.sessionManager = new GameSessionManager(); // Manually initialize
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        GameSession session = sessionManager.createSession();
        
        JsonObject jsonResponse = Json.createObjectBuilder()
            .add("sessionId", session.getId())
            .add("status", "created")
            .build();
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse.toString());
    }
}
