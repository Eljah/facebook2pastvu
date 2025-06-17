package com.example.facebook2pastvu;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FacebookClient {
    private final String token;
    private final String groupId;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public FacebookClient(String token, String groupId) {
        this.token = token;
        this.groupId = groupId;
    }

    public List<FacebookPost> fetchPosts() throws IOException {
        List<FacebookPost> result = new ArrayList<>();
        String url = "https://graph.facebook.com/v17.0/" + groupId + "/feed?fields=id,message,attachments&access_token=" + token;
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            JsonNode root = mapper.readTree(response.body().string());
            for (JsonNode data : root.get("data")) {
                String id = data.get("id").asText();
                List<String> imgs = new ArrayList<>();
                JsonNode attachments = data.path("attachments").path("data");
                if (attachments.isArray()) {
                    for (JsonNode att : attachments) {
                        String picture = att.path("media").path("image").path("src").asText(null);
                        if (picture != null) {
                            imgs.add(picture);
                        }
                    }
                }
                result.add(new FacebookPost(id, imgs));
            }
        }
        return result;
    }

    public Path downloadImage(String url, Path dir) throws IOException {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to download " + url);
            }
            String[] parts = url.split("/");
            Path file = dir.resolve(parts[parts.length - 1].split("\\?")[0]);
            try (InputStream is = response.body().byteStream()) {
                Files.copy(is, file);
            }
            return file;
        }
    }

    public void commentWithMatch(String postId, String comment) throws IOException {
        String url = "https://graph.facebook.com/v17.0/" + postId + "/comments?message=" + comment + "&access_token=" + token;
        Request request = new Request.Builder().url(url).post(okhttp3.internal.Util.EMPTY_REQUEST).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to comment: " + response);
            }
        }
    }
}
