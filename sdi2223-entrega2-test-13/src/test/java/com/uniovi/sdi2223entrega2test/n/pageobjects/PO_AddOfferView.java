package com.uniovi.sdi2223entrega2test.n.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_AddOfferView {

    static public void fillAddForm(WebDriver driver, String titlep, String detailp, String pricep) {
        WebElement title = driver.findElement(By.name("title"));
        title.click();
        title.clear();
        title.sendKeys(titlep);
        WebElement detail = driver.findElement(By.name("detail"));
        detail.click();
        detail.clear();
        detail.sendKeys(detailp);
        WebElement price = driver.findElement(By.name("price"));
        price.click();
        price.clear();
        price.sendKeys(pricep);
        //Pulsar el boton de login.
        By boton = By.xpath("/html/body/div/form/div[4]/div/button");
        driver.findElement(boton).click();
    }
}
