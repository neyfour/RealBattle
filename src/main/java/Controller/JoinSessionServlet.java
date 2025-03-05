/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import GameService.GameSession;
import GameService.GameSessionManager;
import jakarta.inject.Inject;
import  jakarta.json.Json;
import  jakarta.json.JsonObject;
import  jakarta.servlet.ServletException;
import  jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import  jakarta.servlet.http.HttpServletRequest;
import  jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="JoinSessionServlet",urlPatterns={"/api/sessions/join"})
public class JoinSessionServlet extends HttpServlet {
    
    @Inject
    private GameSessionManager sessionManager;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String sessionId = request.getParameter("sessionId");
        
        if (sessionId == null || sessionId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        GameSession session = sessionManager.getSession(sessionId);
        
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        JsonObject jsonResponse = Json.createObjectBuilder()
            .add("sessionId", session.getId())
            .add("status", session.getState().toString())
            .add("playerCount", session.getPlayers().size())
            .build();
        
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse.toString());
    }
}