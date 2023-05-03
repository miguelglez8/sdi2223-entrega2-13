package com.uniovi.sdi2223entrega2test.n;

import com.uniovi.sdi2223entrega2test.n.pageobjects.PO_HomeView;
import com.uniovi.sdi2223entrega2test.n.pageobjects.PO_ListOfferView;
import com.uniovi.sdi2223entrega2test.n.pageobjects.PO_PrivateView;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sdi2223Entrega2TestApplicationTests {
    // Miguel (UO282337)
    static String PathFirefox = "C:\\Archivos de programa\\Mozilla Firefox\\firefox.exe";
    static String Geckodriver = "C:\\Users\\migue\\Desktop\\SDI\\LABORATORIO\\spring\\sesion06\\PL-SDI-Sesión5-material\\PL-SDI-Sesio╠ün5-material\\geckodriver-v0.30.0-win64.exe";

    //Ton
   // static String PathFirefox = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
   // static String Geckodriver = "C:\\Users\\tonpm\\OneDrive\\Documentos\\MisDocumentos\\Clase\\2022\\SDI\\geckodriver-v0.30.0-win64.exe";


    //static String Geckodriver = "C:\\Path\\geckodriver-v0.30.0-win64.exe";
    //static String PathFirefox = "/Applications/Firefox.app/Contents/MacOS/firefox-bin";
    //static String Geckodriver = "/Users/USUARIO/selenium/geckodriver-v0.30.0-macos";
    //Común a Windows y a MACOSX
    static WebDriver driver = getDriver(PathFirefox, Geckodriver);
    static String URL = "http://localhost:3000";
    public static WebDriver getDriver(String PathFirefox, String Geckodriver) {
        System.setProperty("webdriver.firefox.bin", PathFirefox);
        System.setProperty("webdriver.gecko.driver", Geckodriver);
        driver = new FirefoxDriver();
        return driver;
    }

    @BeforeEach
    public void setUp() {
        // driver.navigate().to(URL + "/initbd"); // recargar base de datos
        driver.navigate().to(URL);
    }

    //Después de cada prueba se borran las cookies del navegador
    @AfterEach
    public void tearDown() {
        driver.manage().deleteAllCookies();
    }

    //Antes de la primera prueba
    @BeforeAll
    static public void begin() {
    }

    //Al finalizar la última prueba
    @AfterAll
    static public void end() {
    //Cerramos el navegador al finalizar las pruebas
        driver.quit();
    }

    /**
     * PR23. Ir a la lista de ofertas, y buscar con el buscador vacío,
     * comprobar que aparecen todas las ofertas
     */
    @Test
    @Order(23)
    public void PR23() {
        // nos logueamos
        PO_PrivateView.refactorLogging(driver, "user01@email.com", "user01");
        PO_ListOfferView.goToPage(driver);
        // hay mas de una página
        Assertions.assertTrue(PO_HomeView.checkElementUl(driver, "pagination") > 0);
        // hay 5 ofertas en la tabla (en la primera página)
        Assertions.assertEquals(5, PO_HomeView.checkElementTableBody(driver, "offers").size());
        // introducimos un campo que no existe en el campo de búsqueda
        WebElement input = driver.findElement(By.xpath("//*[@id=\"search\"]"));
        input.click();
        input.clear();
        input.sendKeys("");
        // seleccionamos el botón de buscar
        By boton = By.xpath("//*[@id=\"custom-search-input \"]/form/div/span/button");
        driver.findElement(boton).click();
        List<WebElement> offers = PO_HomeView.checkElementTableBody(driver, "offers"); // ofertas
        int size = 0; // acumular todas las ofertas que hay
        int i = 1; // páginas
        while (offers.isEmpty()==false) {
            try {
                // existe la página
                WebElement enlace = driver.findElement(By.linkText(i + ""));
                enlace.click();
            } catch (org.openqa.selenium.NoSuchElementException e) {
                // no existen más páginas
                break;
            }
            size = size + offers.size(); // acumulamos las ofertas
            offers = PO_HomeView.checkElementTableBody(driver, "offers"); // buscamos otra vez las ofertas de la siguiente página
            i++; // incrementamos el número de página
        }
        // comprobamos que están todas las ofertas
        Assertions.assertEquals(150, size);
        // logout
        PO_PrivateView.refactorLogout(driver);
    }

    /**
     * PR24. Ir a la lista de ofertas, y buscar con el buscador un texto que no exista,
     * comprobamos que aparece la lista vacía
     */
    @Test
    @Order(24)
    public void PR24() {
        // nos logueamos
        PO_PrivateView.refactorLogging(driver, "user01@email.com", "user01");
        PO_ListOfferView.goToPage(driver);
        // hay mas de una página
        Assertions.assertTrue(PO_HomeView.checkElementUl(driver, "pagination") > 0);
        // hay 5 ofertas en la tabla (en la primera página)
        Assertions.assertEquals(5, PO_HomeView.checkElementTableBody(driver, "offers").size());
        // introducimos un campo que no existe en el campo de búsqueda
        WebElement input = driver.findElement(By.xpath("//*[@id=\"search\"]"));
        input.click();
        input.clear();
        input.sendKeys("cdcduefhdrufc");
        // seleccionamos el botón de buscar
        By boton = By.xpath("//*[@id=\"custom-search-input \"]/form/div/span/button");
        driver.findElement(boton).click();
        // comprobamos que no hay paginas
        Assertions.assertEquals(0, PO_HomeView.checkElementUl(driver, "pagination"));
        // no hay ninguna oferta en la tabla
        Assertions.assertEquals(0, PO_HomeView.checkElementTableBody(driver, "offers").size());
        // logout
        PO_PrivateView.refactorLogout(driver);
    }

    /**
     * PR25. Hacer una búsqueda escribiendo en el campo un texto en minúscula o mayúscula y
     * comprobar que se muestra la página que corresponde, con la lista de ofertas que contengan dicho
     * texto, independientemente que el título esté almacenado en minúsculas o mayúscula.
     */
    @Test
    @Order(25)
    public void PR25() {
        // nos logueamos
        PO_PrivateView.refactorLogging(driver, "user01@email.com", "user01");
        PO_ListOfferView.goToPage(driver);
        // hay mas de una página
        Assertions.assertTrue(PO_HomeView.checkElementUl(driver, "pagination") > 0);
        // hay 5 ofertas en la tabla (en la primera página)
        Assertions.assertEquals(5, PO_HomeView.checkElementTableBody(driver, "offers").size());
        // introducimos un campo que no existe en el campo de búsqueda
        WebElement input = driver.findElement(By.xpath("//*[@id=\"search\"]"));
        input.click();
        input.clear();
        input.sendKeys("141");
        // seleccionamos el botón de buscar
        By boton = By.xpath("//*[@id=\"custom-search-input \"]/form/div/span/button");
        driver.findElement(boton).click();
        // comprobamos que hay una sola página
        Assertions.assertEquals(1, PO_HomeView.checkElementUl(driver, "pagination"));
        // hay una oferta en la tabla (que es la 141)
        Assertions.assertEquals(1, PO_HomeView.checkElementTableBody(driver, "offers").size());
        // logout
        PO_PrivateView.refactorLogout(driver);
    }

    /**
     * PR26. Sobre una búsqueda determinada (a elección de desarrollador), comprar una oferta que
     * deja un saldo positivo en el contador del comprobador. Y comprobar que el contador se actualiza
     * correctamente en la vista del comprador
     */
    @Test
    @Order(26)
    public void PR26() {
        // nos logueamos
        PO_PrivateView.refactorLogging(driver, "user01@email.com", "user01");
        PO_ListOfferView.goToPage(driver);
        // introducimos un campo que no existe en el campo de búsqueda
        WebElement input = driver.findElement(By.xpath("//*[@id=\"search\"]"));
        input.click();
        input.clear();
        input.sendKeys("141");
        // seleccionamos el botón de buscar
        By boton = By.xpath("//*[@id=\"custom-search-input \"]/form/div/span/button");
        driver.findElement(boton).click();
        // comprobamos que tiene 100 euros
        double saldoA = PO_ListOfferView.wallet(driver);
        Assertions.assertEquals(100, saldoA);
        // compramos la oferta 141
        By comprar = By.xpath("//*[@id=\"offers\"]/tr/td[5]/a");
        driver.findElement(comprar).click();
        // sacamos el precio de la oferta que compramos
        double precio = Double.parseDouble(driver.findElement(By.xpath("//*[@id=\"offers\"]/tr/td[4]")).getText());
        // comprobamos que tiene saldo positivo
        double saldoB = PO_ListOfferView.wallet(driver);
        Assertions.assertTrue(saldoB > 0);
        Assertions.assertEquals(precio, saldoA - saldoB);
        // logout
        PO_PrivateView.refactorLogout(driver);
    }

    /**
     * PR27. Sobre una búsqueda determinada (a elección de desarrollador), comprar una oferta que
     * deja un saldo 0 en el contador del comprobador. Y comprobar que el contador se actualiza
     * correctamente en la vista del comprador.
     */
    @Test
    @Order(27)
    public void PR27() {
         /*
        // nos logueamos
        PO_PrivateView.refactorLogging(driver, "user03@email.com", "user01");
        // mostramos todas las ofertas
        driver.get("http://localhost:8090/offer/list?size=200");
        // introducimos un campo que existe en el campo de búsqueda
        WebElement input = driver.findElement(By.name("searchText"));
        input.click();
        input.clear();
        input.sendKeys("Oferta 43");
        // buscamos la oferta
        driver.findElement(By.xpath("//*[@id=\"main-container\"]/form/button")).click();
        // la intentamos comprar
        driver.findElement(By.xpath("//*[@id=\"tableOffers\"]/tbody/tr/td[6]/div/a")).click();
        double value = (Double.parseDouble(driver.findElement
                (By.xpath("//*[@id=\"myNavbar\"]/ul[2]/li[1]/h4")).getText()));
        // comprobamos que el marcador sigue igual (a 100) porque no se pudo comprar
        Assertions.assertEquals(value, 100);
        // seleccionamos el mensaje que aparece
        String textFail = driver.findElement(By.xpath("//*[@id=\"main-container\"]/div[1]/div/span")).getText();
        // comprobamos que se corresponde con el mensaje de saldo no suficiente
        Assertions.assertEquals("El precio de la oferta es superior a su saldo (Saldo no suficiente)", textFail);
        // logout
        PO_PrivateView.refactorLogout(driver, "logout");

          */
    }

    /**
     * PR28. Sobre una búsqueda determinada (a elección de desarrollador), intentar comprar una oferta
     * que esté por encima de saldo disponible del comprador. Y comprobar que se muestra el mensaje
     * de saldo no suficiente.
     */
    @Test
    @Order(28)
    public void PR28() {
        // login
         /*
        PO_PrivateView.refactorLogging(driver, "user04@email.com", "user01");
        // mostramos las ofertas
        driver.get("http://localhost:8090/offer/list?size=200");
        // buscamos por título
        WebElement input = driver.findElement(By.name("searchText"));
        input.click();
        input.clear();
        input.sendKeys("Oferta 6");
        // seleccionamos buscar
        driver.findElement(By.xpath("//*[@id=\"main-container\"]/form/button")).click();
        // la compramos
        driver.findElement(By.xpath("//*[@id=\"tableOffers\"]/tbody/tr/td[6]/div/a")).click();
        // vamos a la vista de ofertas compradas
        driver.get("http://localhost:8090/offer/listBuy");
        // seleccionamos todas las ofertas que aparecen
        List<WebElement> rows = driver.findElements(By.className("filas-listBuy-offers"));
        // vemos que solo puede haber una
        //Assertions.assertEquals(offersService.getOffers().stream()
        //        .filter(offer -> offer.isSold() && offer.getEmailComprador().equals("user04@email.com")).toList().size(), rows.size());
        // logout
        PO_PrivateView.refactorLogout(driver, "logout");

          */
    }

    /**
     * PR29. Ir a la opción de ofertas compradas del usuario y mostrar la lista. Comprobar que aparecen
     * las ofertas que deben aparecer
     */
    @Test
    @Order(29)
    public void PR29() {
        /*
        // login
        PO_PrivateView.refactorLogging(driver, "user04@email.com", "user01");
        // mostramos las ofertas
        driver.get("http://localhost:8090/offer/list?size=200");
        // buscamos por título
        WebElement input = driver.findElement(By.name("searchText"));
        input.click();
        input.clear();
        input.sendKeys("Oferta 6");
        // seleccionamos buscar
        driver.findElement(By.xpath("//*[@id=\"main-container\"]/form/button")).click();
        // la compramos
        driver.findElement(By.xpath("//*[@id=\"tableOffers\"]/tbody/tr/td[6]/div/a")).click();
        // vamos a la vista de ofertas compradas
        driver.get("http://localhost:8090/offer/listBuy");
        // seleccionamos todas las ofertas que aparecen
        List<WebElement> rows = driver.findElements(By.className("filas-listBuy-offers"));
        // vemos que solo puede haber una
        //Assertions.assertEquals(offersService.getOffers().stream()
        //        .filter(offer -> offer.isSold() && offer.getEmailComprador().equals("user04@email.com")).toList().size(), rows.size());
        // logout
        PO_PrivateView.refactorLogout(driver, "logout");

         */
    }

    // PARTE DE API-REST

    /* Ejemplos de pruebas de llamada a una API-REST */
    /* ---- Probamos a obtener lista de canciones sin token ---- */
    @Test
    @Order(49)
    public void PR49() {
         /*
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/songs";
        Response response = RestAssured.get(RestAssuredURL);
        Assertions.assertEquals(403, response.getStatusCode());
         */

    }

    // PARTE DE AJAX

    /**
     * PR51. Mostrar el listado de ofertas disponibles y comprobar que se muestran todas las que existen,
     * menos las del usuario identificado
     */
    @Test
    @Order(51)
    public void PR51() {
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "prueba1@prueba1.com");
        requestParams.put("password", "prueba1");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());
        // comprobar las ofertas ...
    }

    @Test
    @Order(50)
    public void PR50() {
         /*
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "prueba1@prueba1.com");
        requestParams.put("password", "prueba1");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());

          */
    }
}
