package at.fhtw.mctg;

import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.sampleapp.service.weather.WeatherService;

public class Main {
    public static void main(String[] args) {

        WeatherService service = new WeatherService();
        Request request = new Request();
        request.setMethod(Method.GET);
        System.out.println(service.handleRequest(request));
    }
}
