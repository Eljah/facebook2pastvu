package com.example.facebook2pastvu;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SyncTask implements Runnable {
    @Override
    public void run() {
        try (Database db = new Database()) {
            String fbToken = db.getConfig("FB_TOKEN");
            String fbGroupId = db.getConfig("FB_GROUP_ID");
            String pastvuCity = db.getConfig("PASTVU_CITY");
            if (fbToken == null || fbGroupId == null || pastvuCity == null) {
                System.err.println("Missing config values");
                return;
            }

            Path downloadDir = Path.of("downloads");
            Files.createDirectories(downloadDir);

            FacebookClient fb = new FacebookClient(fbToken, fbGroupId);
            PastVuClient pv = new PastVuClient();
            ImageMatcher matcher = new ImageMatcher();

            List<FacebookPost> posts = fb.fetchPosts();
            for (FacebookPost post : posts) {
                for (String imageUrl : post.imageUrls()) {
                    Path local = fb.downloadImage(imageUrl, downloadDir);
                    List<PastVuPhoto> candidates = pv.searchByCity(pastvuCity);
                    for (PastVuPhoto pvPhoto : candidates) {
                        if (matcher.isDuplicate(local, pvPhoto.url())) {
                            fb.commentWithMatch(post.id(), pvPhoto.url());
                            db.saveResult(post.id(), imageUrl, pvPhoto.url(), "hash");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
