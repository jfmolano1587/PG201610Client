package com.uniandes.jfm.pg20161client;

import android.content.Context;
import android.provider.SyncStateContract;

import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Thanks to Prashi Developers http://stackoverflow.com/questions/28937785/http-response-with-media-httprequesthandler-android
 */
public class RegisterThread extends Thread {


    private boolean isRunning = false;

    private BasicHttpProcessor httpproc = null;
    private BasicHttpContext httpContext = null;
    private HttpService httpService = null;
    private HttpRequestHandlerRegistry registry = null;

    public RegisterThread(Context context) {
        super("192.168.0.21");

        httpproc = new BasicHttpProcessor();
        httpContext = new BasicHttpContext();
        httpproc.addInterceptor(new ResponseDate());
        httpproc.addInterceptor(new ResponseServer());
        httpproc.addInterceptor(new ResponseContent());
        httpproc.addInterceptor(new ResponseConnControl());
        httpService = new HttpService(httpproc,
                new DefaultConnectionReuseStrategy(),
                new DefaultHttpResponseFactory());
        registry = new HttpRequestHandlerRegistry();
        registry.register("*", new LoggingHandler(context));
        httpService.setHandlerResolver(registry);
    }

    @Override
    public void run() {
        super.run();

        try {
            ServerSocket serverSocket = new ServerSocket(8080);

            serverSocket.setReuseAddress(true);

            while (isRunning) {
                try {
                    final Socket socket = serverSocket.accept();

                    DefaultHttpServerConnection serverConnection = new DefaultHttpServerConnection();

                    serverConnection.bind(socket, new BasicHttpParams());

                    httpService.handleRequest(serverConnection, httpContext);

                    serverConnection.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void startThread() {
        isRunning = true;
        super.start();
    }

    public synchronized void stopThread() {
        isRunning = false;
    }

}
