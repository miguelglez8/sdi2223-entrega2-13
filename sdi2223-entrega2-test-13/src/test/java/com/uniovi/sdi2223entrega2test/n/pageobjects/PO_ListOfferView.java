package com.uniovi.sdi2223entrega2test.n.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_ListOfferView {

    public static void searchText(WebDriver driver, String text) {
        // hacer
        // PO_NavView.selectDropdownById(driver,"gestionOfertasMenu","gestionOfertasDropdow","listAllOfferMenu");
        // Introducimos texto
        WebElement element = driver.findElement(By.name("search"));
        element.click();
        element.clear();
        element.sendKeys(text);
        // Seleccionamos el botón buscar
        driver.findElement(By.name("search")).click();
    }
    public static void buyOffer(WebDriver driver,String nameButton) {
        // haceR
        // PO_NavView.selectDropdownById(driver,"gestionOfertasMenu","gestionOfertasDropdow","listAllOfferMenu");
        By boton = By.id(nameButton);
        driver.findElement(boton).click();
    }

    public static String getMoney(WebDriver driver) {
        return driver.findElement(By.id("wallet")).getText();
    }

    public static String getTitleMessage(WebDriver driver) {
        return driver.findElement(By.name("title")).getText();
    }

    public static void goToPage(WebDriver driver) {
        //Vamos a offers.
        WebElement link = driver.findElement(By.linkText("Offers"));
        // Hacer clic en el enlace
        link.click();
        // vamos a list offers.
        WebElement link2 = driver.findElement(By.linkText("Busca ofertas"));
        // Hacer clic en el enlace
        link2.click();
    }
}
