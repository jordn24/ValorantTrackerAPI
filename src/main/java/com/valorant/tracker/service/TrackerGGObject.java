package com.valorant.tracker.service;

import org.openqa.selenium.By;

public class TrackerGGObject {

    public final static By MATCHES = By.className("matches");
    public final static By JSON_RESPONSE = By.tagName("pre");
    public final static By BLOCKED = By.cssSelector("span[data-translate='error']");

}
