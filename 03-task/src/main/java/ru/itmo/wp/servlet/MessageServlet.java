package ru.itmo.wp.servlet;

import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageServlet extends HttpServlet {

    private final Handler notFoundHandler = (request, response) -> response.sendError(HttpServletResponse.SC_NOT_FOUND);

    private final List<Message> messages = new CopyOnWriteArrayList<>();

    private void writeJsonAndFlush(HttpServletResponse response, Object o) throws IOException {
        response.getWriter().print(new Gson().toJson(o));
        response.getWriter().flush();
    }

    private final Map<String, Handler> handlers = Map.of(
            "/message/auth", (request, response) -> {
                String newName = request.getParameter("user");
                HttpSession session = request.getSession();
                if (newName != null) {
                    session.setAttribute("name", newName);
                }
                Optional<String> name = Optional.ofNullable((String) session.getAttribute("name"));
                writeJsonAndFlush(response, name.orElse(""));
            },
            "/message/findAll", (request, response) -> {
                writeJsonAndFlush(response, messages);
            },
            "/message/add", (request, response) -> {
                Optional<String> name = Optional.ofNullable((String) request.getSession().getAttribute("name"));
                if (name.isPresent()) {
                    messages.add(new Message(name.get(), request.getParameter("text")));
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }

            }
    );

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        handlers.getOrDefault(request.getRequestURI(), notFoundHandler).handle(request, response);
    }

    private class Message {
        public String user;
        public String text;

        public Message(String user, String text) {
            this.user = user;
            this.text = text;
        }

        public String getUser() {
            return user;
        }

        public String getText() {
            return text;
        }
    }

    @FunctionalInterface
    private interface Handler {
        void handle(HttpServletRequest request, HttpServletResponse response) throws IOException;
    }
}

