package com.uniovi.sdi2223entrega2test.n.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PO_BuyOfferView {

    /**
     * Va a la página de lista de ofertas compradas
     * @param driver
     */
    public static void goToPage(WebDriver driver) {
        //Vamos a offers.
        WebElement link = driver.findElement(By.linkText("Offers"));
        // Hacer clic en el enlace
        link.click();
        // vamos a list offers buy.
        WebElement link2 = driver.findElement(By.linkText("Ofertas compradas"));
        // Hacer clic en el enlace
        link2.click();
    }
}
