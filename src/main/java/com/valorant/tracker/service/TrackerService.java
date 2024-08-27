package com.valorant.tracker.service;

import org.json.JSONArray;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.json.JSONObject;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class TrackerService {

    private final String base_url = "https://tracker.gg/valorant/profile/riot/";
    private final String totalMatchesURI = "/overview?season=all";
    private final String overviewURI = "/overview";
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36";
    private  final int MAX_RETRIES = 500;

    private ChromeOptions options = new ChromeOptions();

    public TrackerService(){
        options.addArguments("--headless"); // Run in headless mode
        options.addArguments("--user-agent=" + USER_AGENT);
    }

    public Integer scrapeTotalMatches(String user, String tag) {
        String url = base_url + user + "%23" + tag + totalMatchesURI;
        String data = "";

        WebDriver driver = null;

        try {
            URL seleniumServerUrl = new URL("http://selenium:4444/wd/hub");
            driver = new RemoteWebDriver(seleniumServerUrl, options);

            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Wait for up to 10 seconds
            wait.until(ExpectedConditions.presenceOfElementLocated(TrackerGGObject.MATCHES));

            data = driver.findElement(TrackerGGObject.MATCHES).getText();
            data = data.replace(" Matches", "");
            data = data.replace(",","");

            driver.quit();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Integer.parseInt(data);
    }

    public Integer scrapeActMatches(String user, String tag) {
        String url = base_url + user + "%23" + tag + overviewURI;
        String data = "";
        WebDriver driver = null;

        try {
            URL seleniumServerUrl = new URL("http://selenium:4444/wd/hub");
            driver = new RemoteWebDriver(seleniumServerUrl, options);
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // Wait for up to 10 seconds
            wait.until(ExpectedConditions.presenceOfElementLocated(TrackerGGObject.MATCHES));

            data = driver.findElement(TrackerGGObject.MATCHES).getText();
            data = data.replace(" Matches", "");
            data = data.replace(",","");

            driver.quit();
        } catch( Exception e) {
            e.printStackTrace();
        }

        return Integer.parseInt(data);
    }

    public String scrapeBottomFrags(String user, String tag, Integer limitParam, Integer startLimit, Integer sleep){
        int page = startLimit;
        int bottomFrags = 0;
        int limit = limitParam;

        WebDriver driver = null;

        try {
            URL seleniumServerUrl = new URL("http://selenium:4444/wd/hub");
            driver = new RemoteWebDriver(seleniumServerUrl, options);


            while (true) {

                String url = "https://api.tracker.gg/api/v2/valorant/standard/matches/riot/" + user + "%23" + tag + "?type=competitive&season=&agent=all&map=all&next=" + page;

                driver.get(url);

                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

                JSONObject jsonObject;
                try {
                    jsonObject =
                            new JSONObject(
                                    wait.until(ExpectedConditions.presenceOfElementLocated(TrackerGGObject.JSON_RESPONSE)).getText()
                            );
                } catch (Exception e) {
                    try {
                        wait.until(ExpectedConditions.presenceOfElementLocated(TrackerGGObject.BLOCKED));
                        driver.quit();
                        return "You've been blocked. Please wait 24hrs";
                    } catch (Exception e2) {
                        String exit = driver.getTitle();
                        driver.quit();

                        return exit;
                    }
                }

                JSONArray matches = jsonObject.getJSONObject("data").getJSONArray("matches");

                if (matches.length() == 0) {
                    // No more matches, break out of the loop
                    break;
                }

                for (int i = 0; i < matches.length(); i++) {
                    JSONObject match = matches.getJSONObject(i);
                    JSONArray segments = match.getJSONArray("segments");

                    for (int j = 0; j < segments.length(); j++) {
                        JSONObject segment = segments.getJSONObject(j);
                        JSONObject stats = segment.getJSONObject("stats");
                        JSONObject placement = stats.getJSONObject("placement");

                        if (placement.getInt("value") == 10) {
                            bottomFrags += 1;
                        }

                    }
                }
                page++;
            }

            driver.quit();
        } catch ( Exception e ){
            e.printStackTrace();
        }

        return Integer.toString(bottomFrags);
    }

}
