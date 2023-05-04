package com.uniovi.sdi2223entrega2test.n.pageobjects;

import com.uniovi.sdi2223entrega2test.n.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PO_HomeView extends PO_NavView {
    static public void checkWelcomeToPage(WebDriver driver, int language) {
        //Esperamos a que se cargue el saludo de bienvenida en Español
        SeleniumUtils.waitLoadElementsBy(driver, "text", p.getString("welcome.message", language),
                getTimeout());
    }

    static public List<WebElement> getWelcomeMessageText(WebDriver driver, int language) {
        //Esperamos a que se cargue el saludo de bienvenida en Español
        return SeleniumUtils.waitLoadElementsBy(driver, "text", p.getString("welcome.message", language),
                getTimeout());
    }

    static public void checkChangeLanguage(WebDriver driver, String textLanguage1, String textLanguage,
                                           int locale1, int locale2) {
        //Esperamos a que se cargue el saludo de bienvenida en Español
        PO_HomeView.checkWelcomeToPage(driver, locale1);
        //Cambiamos a segundo idioma
        changeLanguage(driver, textLanguage);
        //Comprobamos que el texto de bienvenida haya cambiado a segundo idioma
        PO_HomeView.checkWelcomeToPage(driver, locale2);
        //Volvemos a Español.
        changeLanguage(driver, textLanguage1);
        //Esperamos a que se cargue el saludo de bienvenida en Español
        PO_HomeView.checkWelcomeToPage(driver, locale1);
    }

    /**
     * Devueve las páginas de la web
     * @param driver
     * @param criterio
     * @return
     */
    public static int checkElementUl(WebDriver driver, String criterio) {
        // Encontrar el ul por su selector
        WebElement ul = driver.findElement(By.className(criterio));

        // Encontrar todos los elementos li dentro del ul
        List<WebElement> elementos = ul.findElements(By.cssSelector("li"));

        // devolver la lista de elementos
        return elementos.size();
    }

    /**
     * Devuelve todos los elementos de la tabla de ofertas
     * @param driver
     * @param criterio
     * @return
     */
    public static List<WebElement> checkElementTableBody(WebDriver driver, String criterio) {
        WebElement tbody = driver.findElement(By.id(criterio));

        // Encontrar todos los elementos tr dentro del tbody
        List<WebElement> elementos = tbody.findElements(By.tagName("tr"));

        // devolver la lista de elementos
        return elementos;
    }

}
