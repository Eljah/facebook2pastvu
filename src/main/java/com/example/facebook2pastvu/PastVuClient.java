package com.example.facebook2pastvu;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PastVuClient {
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<PastVuPhoto> searchByCity(String city) throws IOException {
        String url = "https://pastvu.com/api2?method=photos.search&format=json&city=" + city;
        Request request = new Request.Builder().url(url).build();
        List<PastVuPhoto> result = new ArrayList<>();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("PastVu request failed: " + response);
            }
            JsonNode root = mapper.readTree(response.body().string());
            JsonNode photos = root.path("result").path("photos");
            for (JsonNode node : photos) {
                String id = node.get("_id").asText();
                String photoUrl = "https://pastvu.com/p/" + id;
                result.add(new PastVuPhoto(id, photoUrl));
            }
        }
        return result;
    }
}
