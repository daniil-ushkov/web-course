package ru.itmo.wp.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StaticServlet extends HttpServlet {

    private Optional<File> getFile(String uri) {
        if (!uri.isEmpty() && uri.charAt(0) != '/') {
            uri = '/' + uri;
        }
        File file = new File(new File(getServletContext().getRealPath(""))
                .getParentFile()
                .getParentFile(), "/src/main/webapp/static" + uri);
        if (file.isFile()) {
            return Optional.of(file);
        }
        file = new File(getServletContext().getRealPath("/static" + uri));
        if (file.isFile()) {
            return Optional.of(file);
        }
        return Optional.empty();
    }

    private void writeFile(File file, OutputStream outputStream) {
        try {
            Files.copy(file.toPath(), outputStream);
        } catch (IOException e) {
            System.out.println(file.toPath() + " : copy failed");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] uris = request.getRequestURI().split("\\+");
        List<File> fileList = Arrays.stream(uris)
                .map(this::getFile)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        if (fileList.size() == uris.length) {
            response.setContentType(getContentTypeFromName(fileList.get(0).getName()));
            OutputStream outputStream = response.getOutputStream();
            fileList.forEach(file -> writeFile(file, outputStream));
            outputStream.flush();
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private String getContentTypeFromName(String name) {
        name = name.toLowerCase();

        if (name.endsWith(".png")) {
            return "image/png";
        }

        if (name.endsWith(".jpg")) {
            return "image/jpeg";
        }

        if (name.endsWith(".html")) {
            return "text/html";
        }

        if (name.endsWith(".css")) {
            return "text/css";
        }

        if (name.endsWith(".js")) {
            return "application/javascript";
        }

        throw new IllegalArgumentException("Can't find content type for '" + name + "'.");
    }
}
