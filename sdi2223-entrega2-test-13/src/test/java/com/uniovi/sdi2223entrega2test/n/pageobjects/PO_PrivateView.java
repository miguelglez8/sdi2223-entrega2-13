package com.uniovi.sdi2223entrega2test.n.pageobjects;

import com.uniovi.sdi2223entrega2test.n.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class PO_PrivateView extends PO_NavView {
    static public void fillFormAddMark(WebDriver driver, int userOrder, String descriptionp, String scorep) {
        //Esperamos 5 segundo a que carge el DOM porque en algunos equipos falla
        SeleniumUtils.waitSeconds(driver, 5);
        //Seleccionamos el alumnos userOrder
        new Select(driver.findElement(By.id("user"))).selectByIndex(userOrder);
        //Rellenemos el campo de descripción
        WebElement description = driver.findElement(By.name("description"));
        description.clear();
        description.sendKeys(descriptionp);
        WebElement score = driver.findElement(By.name("score"));
        score.click();
        score.clear();
        score.sendKeys(scorep);
        By boton = By.className("btn");
        driver.findElement(boton).click();
    }

    static public void refactorLogging(WebDriver driver, String email, String password) {
        //Vamos al formulario de logueo.
        WebElement link = driver.findElement(By.linkText("Identifícate"));
        // Hacer clic en el enlace
        link.click();
        //Rellenamos el formulario
        PO_LoginView.fillLoginForm(driver, email, password);
    }

    static public void refactorLogout(WebDriver driver) {
        WebElement link = driver.findElement(By.linkText("Cerrar sesión"));
        link.click();
    }

    public static void clickElement(WebDriver driver, String s, int i) {
        List<WebElement> elements = PO_View.checkElementBy(driver, "free", s);
        elements.get(i).click();
    }
}
