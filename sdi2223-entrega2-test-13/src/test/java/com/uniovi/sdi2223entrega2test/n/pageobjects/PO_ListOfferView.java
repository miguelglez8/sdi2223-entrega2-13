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

    public static double wallet(WebDriver driver) {
        // formato: Precio: 100 €
        String text = driver.findElement(By.xpath("//*[@id=\"money\"]/a")).getText(); // texto del enlace
        // cogemos el segundo campo del string que es el saldo del usuario
        double precio = Double.parseDouble(text.split(" ")[1]);
        return precio; // devolvemos el saldo
    }
}
