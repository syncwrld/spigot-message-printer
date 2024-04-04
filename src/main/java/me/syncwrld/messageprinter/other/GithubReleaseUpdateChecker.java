package me.syncwrld.messageprinter.other;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import me.syncwrld.messageprinter.other.http.Request;

@Data
public class GithubReleaseUpdateChecker {

  private final String repositoryOwner;
  private final String repositoryName;
  private final String currentVersion;

  private String latestVersionTag;
  private String githubRepositoryURL = "https://api.github.com/repos/$$user$$/$$repo$$";
  private List<String> modifications;

  public GithubReleaseUpdateChecker(
      String repositoryOwner, String repositoryName, String currentVersion) {
    this.repositoryOwner = repositoryOwner;
    this.repositoryName = repositoryName;
    this.currentVersion = currentVersion.replace("v", "");

    this.githubRepositoryURL =
        githubRepositoryURL
            .replace("$$user$$", repositoryOwner)
            .replace("$$repo$$", repositoryName);

    this.modifications = getModifications();
    this.latestVersionTag = getLatestVersion().replace("v", "");
  }

  public String getLatestVersion() {
    final String releasesURL = githubRepositoryURL + "/releases";
    Request<String> request = new Request<>(releasesURL);

    if (!request.execute()) {
      throw new RuntimeException(
          "Cannot get latest version for update checker: " + request.getException());
    }

    final String data = request.getResponse().getData();
    JsonArray json = new JsonParser().parse(data).getAsJsonArray();

    String tagName = json.get(0).getAsJsonObject().get("tag_name").getAsString();

    if (tagName == null) {
      throw new RuntimeException(
          "Please, first create at least one release in your repository ("
              + githubRepositoryURL
              + ") to use update checker.");
    }

    return tagName;
  }

  public List<String> getModifications() {
    final JsonObject releaseJson = getReleaseData(getLatestReleaseID());
    final String body = releaseJson.get("body").getAsString();
    List<String> changelog = new ArrayList<>();

    String[] changelogUnformatted = body.replace("\r", "").split("\n");

    for (String line : changelogUnformatted) {
      if (!line.isEmpty()) {
        if (!line.startsWith("-")) {
          line = "- " + line;
        }

        changelog.add(line.replace("-", "•"));
      }
    }

    return changelog;
  }

  private String getLatestReleaseID() {
    final String releasesURL = githubRepositoryURL + "/releases";
    Request<String> request = new Request<>(releasesURL);

    if (!request.execute()) {
      throw new RuntimeException(
          "Cannot get latest version for update checker: " + request.getException());
    }

    final String data = request.getResponse().getData();
    final JsonParser jsonParser = new JsonParser();
    JsonArray json = jsonParser.parse(data).getAsJsonArray();

    JsonElement releaseIDObj = json.get(0).getAsJsonObject().get("id");

    if (releaseIDObj == null) {
      throw new RuntimeException(
          "Please, first create at least one release in your repository ("
              + githubRepositoryURL
              + ") to use update checker.");
    }

    return releaseIDObj.getAsString();
  }

  public String getLatestVersionDownloadURL() {
    final String latestReleaseID = getLatestReleaseID();
    final JsonObject releaseJson = getReleaseData(latestReleaseID);

    return releaseJson
        .get("assets")
        .getAsJsonArray()
        .get(0)
        .getAsJsonObject()
        .get("browser_download_url")
        .getAsString();
  }

  private JsonObject getReleaseData(String releaseID) {
    final String releaseInfoURL = githubRepositoryURL + "/releases/" + releaseID;
    Request<String> releaseRequest = new Request<>(releaseInfoURL);

    if (!releaseRequest.execute()) {
      throw new RuntimeException(
          "Cannot get release information for update checker: " + releaseRequest.getException());
    }

    final String releaseData = releaseRequest.getResponse().getData();
    return new JsonParser().parse(releaseData).getAsJsonObject();
  }

  public boolean isUpdateAvailable() {
    return !currentVersion.equals(latestVersionTag);
  }
}
