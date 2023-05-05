package com.uniovi.sdi2223entrega2test.n.pageobjects;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;

public class PO_RestApi extends PO_View {

    public static Response login(String user, String password, String LoginURL, RequestSpecification request) {
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", user);
        requestParams.put("password", password);
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        Response loginResponse = request.post(LoginURL);
        return loginResponse;
    }

    public static Response sendMessage(String user, String offer, String text, RequestSpecification request, String MessagesURL) {
        JSONObject messageRequestParams = new JSONObject();
        messageRequestParams.put("seller", user);
        messageRequestParams.put("offer", offer);
        messageRequestParams.put("text", text);
        request.header("Content-Type", "application/json");
        request.body(messageRequestParams.toJSONString());
        Response messagesResponse = request.post(MessagesURL);
        return messagesResponse;
    }
}
