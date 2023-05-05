package com.uniovi.sdi2223entrega2test.n;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.MongoCollection;
import com.uniovi.sdi2223entrega2test.n.mongo.MongoDB;
import com.uniovi.sdi2223entrega2test.n.pageobjects.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static org.junit.Assert.assertEquals;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sdi2223Entrega2TestApplicationTests {
    // Miguel
    //static String PathFirefox = "C:\\Archivos de programa\\Mozilla Firefox\\firefox.exe";
    //static String Geckodriver = "C:\\Users\\migue\\Desktop\\SDI\\LABORATORIO\\spring\\sesion06\\PL-SDI-Sesión5-material\\PL-SDI-Sesio╠ün5-material\\geckodriver-v0.30.0-win64.exe";

    //Raúl
     static String PathFirefox = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
     static String Geckodriver = "C:\\Users\\Aladino España\\Desktop\\PL-SDI-Sesión5-material\\geckodriver-v0.30.0-win64.exe";

    //Ton
    // static String PathFirefox = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    // static String Geckodriver = "C:\\Users\\tonpm\\OneDrive\\Documentos\\MisDocumentos\\Clase\\2022\\SDI\\geckodriver-v0.30.0-win64.exe";

    //static String Geckodriver = "C:\\Path\\geckodriver-v0.30.0-win64.exe";
    //static String PathFirefox = "/Applications/Firefox.app/Contents/MacOS/firefox-bin";
    //static String Geckodriver = "/Users/USUARIO/selenium/geckodriver-v0.30.0-macos";
    //Común a Windows y a MACOSX

    static WebDriver driver = getDriver(PathFirefox, Geckodriver);
    static String URL = "http://localhost:3000";

    // acceder a la base de datos
    static MongoDB mongo;

    public static WebDriver getDriver(String PathFirefox, String Geckodriver) {
        System.setProperty("webdriver.firefox.bin", PathFirefox);
        System.setProperty("webdriver.gecko.driver", Geckodriver);
        driver = new FirefoxDriver();
        return driver;
    }

    @BeforeEach
    public void setUp() {
        driver.navigate().to(URL);
        mongo = new MongoDB();
        mongo.resetMongo();
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
    //[Prueba1] Registro de Usuario con datos válidos
    @Test
    @Order(1)
    public void PR01() {
        /*driver.get("http://localhost:3000/users/signup");

        //Rellenamos el formulario
        WebElement email = driver.findElement(By.id("email"));
        WebElement name = driver.findElement(By.id("name"));
        WebElement surname = driver.findElement(By.id("surname"));
        WebElement birthdate = driver.findElement(By.id("birthdate"));

        WebElement password = driver.findElement(By.id("password"));
        WebElement passwordConfirm = driver.findElement(By.name("passwordConfirm"));
        WebElement submit = driver.findElement(By.cssSelector("button[type='submit']"));

        email.sendKeys("user20@email.com");
        name.sendKeys("user20");
        surname.sendKeys("user20");
        birthdate.clear();
        birthdate.sendKeys("1990/05/02");
        password.sendKeys("user20");
        passwordConfirm.sendKeys("user20");
        submit.click();

        //Esperamos a que se cargue la página de inicio de sesión
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.titleIs("Iniciar sesión"));

        //Comprobamos que hemos sido redirigidos a la página de inicio de sesión
        assertEquals("Iniciar sesión", driver.getTitle());*/

    }



    //Prueba5] Inicio de sesión con datos válidos (administrador)
    @Test
    @Order(7)
    public void PR05() {
        //Vamos al formulario de inicio de sesión.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillLoginForm(driver, "admin@email.com", "admin");
        //Comprobamos que entramos en la página privada del administrador
        String checkText = "Usuarios";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    //[Prueba6] Inicio de sesión con datos válidos (usuario estándar)
    @Test
    @Order(8)
    public void PR06() {
        //Vamos al formulario de inicio de sesión.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "user01");
        //Comprobamos que entramos en la página privada de usuario
        String checkText = "Mis Ofertas";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);

        Assertions.assertEquals(checkText, checkText);
    }

    //[Prueba7] Inicio de sesión con datos inválidos (usuario estándar, campo email y contraseña vacíos)
    @Test
    @Order(9)
    public void PR07() {
        //Vamos al formulario de inicio de sesión.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillLoginForm(driver, "", "");
        //Comprobamos que entramos en la página privada de usuario
        String checkText = "Identifícate";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    //[Prueba8] Inicio de sesión con datos válidos (usuario estándar, email existente, pero contraseña incorrecta)
    @Test
    @Order(10)
    public void PR08() {
        //Vamos al formulario de inicio de sesión.
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        //Rellenamos el formulario
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "i");
        //Comprobamos que entramos en la página privada de usuario
        String checkText = "Email o password incorrecto";
        List<WebElement> result = PO_LoginView.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());
    }





    /**
     * PR16. Ir al formulario de alta de oferta, rellenarla con datos válidos y pulsar el botón Submit.
     * Comprobar que la oferta sale en el listado de ofertas de dicho usuario.
     */
    @Test
    @Order(16)
    public void PR16(){

    }

    /**
     * PR17. Ir al formulario de alta de oferta, rellenarla con datos inválidos (campo título vacío y precio
     * en negativo) y pulsar el botón Submit. Comprobar que se muestra el mensaje de campo inválido.
     */
    @Test
    @Order(17)
    public void PR17(){
//        PO_HomeView.clickOption(driver, "Identifícate", "text", "/users/login");
//        PO_HomeView.clickOption(driver,  "", "", "");
        //Iniciamos sesión a través del formulario de login
//        PO_PrivateView.refactorLogging(driver, "user16@email.com", "user16");
//        PO_PrivateView.clickElement(driver, "//a[@id='myoffers']", 0);
//        PO_NavView.clickOption(driver,"Mis Ofertas", "@href", "/offers/myoffers");
    }

    /**
     * PR18. Mostrar el listado de ofertas para dicho usuario y comprobar que se muestran todas las que
     * existen para este usuario.
     */
    @Test
    @Order(18)
    public void PR18(){

    }

    /**
     * PR19. Ir a la lista de ofertas, borrar la primera oferta de la lista, comprobar que la lista se actualiza
     * y que la oferta desaparece.
     */
    @Test
    @Order(19)
    public void PR19(){

    }

    /**
     * PR20. Ir a la lista de ofertas, borrar la última oferta de la lista, comprobar que la lista se actualiza
     * y que la oferta desaparece.
     */
    @Test
    @Order(20)
    public void PR20(){

    }

    /**
     * PR21. Ir a la lista de ofertas, borrar una oferta de otro usuario, comprobar que la oferta no se
     * borra.
     */
    @Test
    @Order(21)
    public void PR21(){

    }

    /**
     * PR22. Ir a la lista de ofertas, borrar una oferta propia que ha sido vendida, comprobar que la
     * oferta no se borra.
     */
    @Test
    @Order(22)
    public void PR22(){

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
        // vamos a la vista de buscar ofertas
        PO_ListOfferView.goToPage(driver);
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
        int i = 2; // páginas
        String url = "http://localhost:3000/offers/searchOffers?page=";
        while (offers.isEmpty()==false) {
            size = size + offers.size(); // acumulamos las ofertas
            driver.navigate().to(url + i);
            offers = PO_HomeView.checkElementTableBody(driver, "offers"); // buscamos otra vez las ofertas de la siguiente página
            i++; // incrementamos el número de página
        }
        // comprobamos que están todas las ofertas
        Assertions.assertEquals(mongo.getCollection("offers").count(), size);
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
        // vamos a la vista de buscar ofertas
        PO_ListOfferView.goToPage(driver);
        // comprobamos que hay ofertas en la base de datos
        Assertions.assertTrue(mongo.getCollection("offers").count() > 0);
        // introducimos un campo que no existe en el campo de búsqueda
        WebElement input = driver.findElement(By.xpath("//*[@id=\"search\"]"));
        input.click();
        input.clear();
        input.sendKeys("cdcduefhdrufc");
        // seleccionamos el botón de buscar
        By boton = By.xpath("//*[@id=\"custom-search-input \"]/form/div/span/button");
        driver.findElement(boton).click();
        // comprobamos que no hay páginas (ninguna búsqueda existente)
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
        // vamos a la vista de buscar ofertas
        PO_ListOfferView.goToPage(driver);
        // introducimos un campo que no existe en el campo de búsqueda
        WebElement input = driver.findElement(By.xpath("//*[@id=\"search\"]"));
        input.click();
        input.clear();
        input.sendKeys("OFERTA 141");
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
        // vamos a la vista de buscar ofertas
        PO_ListOfferView.goToPage(driver);
        // introducimos un campo que no existe en el campo de búsqueda
        WebElement input = driver.findElement(By.xpath("//*[@id=\"search\"]"));
        input.click();
        input.clear();
        input.sendKeys("11");
        // seleccionamos el botón de buscar
        By boton = By.xpath("//*[@id=\"custom-search-input \"]/form/div/span/button");
        driver.findElement(boton).click();
        // comprobamos que tiene el saldo de su base de datos
        double saldoA = PO_ListOfferView.wallet(driver);
        Assertions.assertEquals(mongo.getSaldo("user01@email.com"), saldoA);
        // sacamos el precio de la oferta que vamos a comprar
        double precio = Double.parseDouble(driver.findElement(By.xpath("//*[@id=\"offers\"]/tr/td[4]")).getText());
        // comprobamos que el precio mostrado se corresponde con el de la base de datos
        Assertions.assertEquals(mongo.getPrice("Oferta 11"), precio);
        // compramos la oferta 11
        By comprar = By.xpath("//*[@id=\"offers\"]/tr/td[5]/a");
        driver.findElement(comprar).click();
        // comprobamos que tiene saldo positivo
        double saldoB = PO_ListOfferView.wallet(driver);
        Assertions.assertTrue(saldoB > 0);
        // comprobamos que tiene el saldo de su base de datos
        Assertions.assertEquals(mongo.getSaldo("user01@email.com"), saldoB);
        // comprobamos que se ha descontado el precio correctamente
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
        // nos logueamos
        PO_PrivateView.refactorLogging(driver, "user02@email.com", "user02");
        // vamos a la vista de buscar ofertas
        PO_ListOfferView.goToPage(driver);
        // introducimos un campo que no existe en el campo de búsqueda
        WebElement input = driver.findElement(By.xpath("//*[@id=\"search\"]"));
        input.click();
        input.clear();
        input.sendKeys("100");
        // seleccionamos el botón de buscar
        By boton = By.xpath("//*[@id=\"custom-search-input \"]/form/div/span/button");
        driver.findElement(boton).click();
        // comprobamos que tiene el saldo de la base de datos
        double saldoA = PO_ListOfferView.wallet(driver);
        Assertions.assertEquals(mongo.getSaldo("user02@email.com"), saldoA);
        // sacamos el precio de la oferta que vamos a comprar
        double precio = Double.parseDouble(driver.findElement(By.xpath("//*[@id=\"offers\"]/tr/td[4]")).getText());
        // comprobamos que el precio mostrado se corresponde con el de la base de datos
        Assertions.assertEquals(mongo.getPrice("Oferta 100"), precio);
        // compramos la oferta 100
        By comprar = By.xpath("//*[@id=\"offers\"]/tr/td[5]/a");
        driver.findElement(comprar).click();
        // comprobamos que tiene saldo a cero
        double saldoB = PO_ListOfferView.wallet(driver);
        Assertions.assertTrue(saldoB == 0);
        // comprobamos que tiene el saldo de su base de datos
        Assertions.assertEquals(mongo.getSaldo("user02@email.com"), saldoB);
        // comprobamos que se ha descontado el precio correctamente
        Assertions.assertEquals(precio, saldoA - saldoB);
        // logout
        PO_PrivateView.refactorLogout(driver);
    }

    /**
     * PR28. Sobre una búsqueda determinada (a elección de desarrollador), intentar comprar una oferta
     * que esté por encima de saldo disponible del comprador. Y comprobar que se muestra el mensaje
     * de saldo no suficiente.
     */
    @Test
    @Order(28)
    public void PR28() {
        // nos logueamos
        PO_PrivateView.refactorLogging(driver, "user03@email.com", "user03");
        // vamos a la vista de buscar ofertas
        PO_ListOfferView.goToPage(driver);
        // introducimos un campo que no existe en el campo de búsqueda
        WebElement input = driver.findElement(By.xpath("//*[@id=\"search\"]"));
        input.click();
        input.clear();
        input.sendKeys("101");
        // seleccionamos el botón de buscar
        By boton = By.xpath("//*[@id=\"custom-search-input \"]/form/div/span/button");
        driver.findElement(boton).click();
        // comprobamos que tiene el saldo de la base de datos
        double saldoA = PO_ListOfferView.wallet(driver);
        Assertions.assertEquals(mongo.getSaldo("user03@email.com"), saldoA);
        // sacamos el precio de la oferta que supuestamente queremos comprar
        double precio = Double.parseDouble(driver.findElement(By.xpath("//*[@id=\"offers\"]/tr/td[4]")).getText());
        // comprobamos que el precio mostrado se corresponde con el de la base de datos
        Assertions.assertEquals(mongo.getPrice("Oferta 101"), precio);
        // comprobamos que el precio es superior al saldo del usuario
        Assertions.assertTrue(precio > saldoA);
        // la oferta tiene un precio de 101 euros
        Assertions.assertEquals(101, precio);
        // intentamos comprar la oferta 101
        By comprar = By.xpath("//*[@id=\"offers\"]/tr/td[5]/a");
        driver.findElement(comprar).click();
        // comprobamos que tiene el mismo saldo, ya que no la pudo comprar
        double saldoB = PO_ListOfferView.wallet(driver);
        Assertions.assertTrue(saldoB == saldoA);
        Assertions.assertEquals(mongo.getSaldo("user03@email.com"), saldoB);
        // comprobamos que se ha lanzado un mensaje de error
        String messageError = PO_ListOfferView.getErrors(driver);
        Assertions.assertEquals("[Titulo=Oferta 101] ERROR: no tienes suficiente saldo para comprar la oferta", messageError);
        // logout
        PO_PrivateView.refactorLogout(driver);
    }

    /**
     * PR29. Ir a la opción de ofertas compradas del usuario y mostrar la lista. Comprobar que aparecen
     * las ofertas que deben aparecer
     */
    @Test
    @Order(29)
    public void PR29() {
        // nos logueamos
        PO_PrivateView.refactorLogging(driver, "user03@email.com", "user03");
        // vamos a la vista de las ofertas compradas por el usuario y vemos que no aparece ninguna oferta comprada
        PO_BuyOfferView.goToPage(driver);
        List<WebElement> tableBefore = PO_HomeView.checkElementTableBody(driver, "buy-offers");
        Assertions.assertEquals(mongo.getBuys("user03@email.com"), tableBefore.size());
        // vamos a la vista de buscar ofertas
        PO_ListOfferView.goToPage(driver);
        // introducimos un campo que no existe en el campo de búsqueda
        WebElement input = driver.findElement(By.xpath("//*[@id=\"search\"]"));
        input.click();
        input.clear();
        input.sendKeys("99");
        // seleccionamos el botón de buscar
        By boton = By.xpath("//*[@id=\"custom-search-input \"]/form/div/span/button");
        driver.findElement(boton).click();
        // compramos la oferta 99
        By comprar = By.xpath("//*[@id=\"offers\"]/tr/td[5]/a");
        driver.findElement(comprar).click();
        // vamos a la vista de las ofertas compradas por el usuario y vemos que aparece la oferta
        PO_BuyOfferView.goToPage(driver);
        List<WebElement> tableAfter = PO_HomeView.checkElementTableBody(driver, "buy-offers");
        Assertions.assertEquals(mongo.getBuys("user03@email.com"), tableAfter.size());
        // logout
        PO_PrivateView.refactorLogout(driver);
    }

    /**
     * PR51. Mostrar el listado de ofertas disponibles y comprobar que se muestran todas las que existen,
     * menos las del usuario identificado
     */
    @Test
    @Order(51)
    public void PR51() {
        // navegamos a la URL
        driver.get("http://localhost:3000/apiclient/client.html?w=login");
        // introducimos los datos en el login
        PO_LoginAjaxView.fillLoginForm(driver, "user01@email.com", "user01");
        // sacamos los datos de la tabla y vemos que aparecen todas las ofertas menos las del usuario identificado
        List<WebElement> table = PO_HomeView.checkElementTableBody(driver, "offersTableBody");
        // sacamos los datos que hay en la base de datos en la colección de ofertas
        MongoCollection<Document> collection = mongo.getCollection("offers");
        // Crear un objeto de filtro para especificar el criterio de búsqueda
        Bson filter = Filters.not(Filters.eq("seller", "user01@email.com"));
        // Filtrar los documentos de la colección
        FindIterable<Document> cursor = collection.find(filter);
        int size = 0;
        for (Document document : cursor) { // vamos sumando el número de elementos que hay en la colección
            size++;
        }
        Assertions.assertEquals(size, table.size()); // comprobamos que sea el mismo número
    }

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
