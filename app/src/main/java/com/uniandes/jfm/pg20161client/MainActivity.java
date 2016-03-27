package com.uniandes.jfm.pg20161client;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

//Info registro
/*
hostDir = "157.253.205.40"
portNum = "4000"
appId = "user"
*/

public class MainActivity extends ActionBarActivity {

    public final static String URL_MTC = "192.168.0.27";
    public final static String PUERTO_MTC = "4000";
    public final static String APP_ID = "user01";
    private boolean listaRed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listaRed = false;
    }

    public void llamar(View view) {
        System.out.println("- - - - - - - - BOTON - - - - - - - -");
        //Registro
        //new TareaRed().execute();
        new Thread(new Runnable() {
            public void run() {
                try {
                    //Crea URL
                    String url = "http://"+URL_MTC+":"+PUERTO_MTC+"/m2m/applications";
                    System.out.println("URL: " + url);
                    URL object = new URL(url);
                    //Crea Conexion
                    HttpURLConnection con = (HttpURLConnection) object.openConnection();
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestMethod("POST");
                    //Crea JSON
                    String dataI = "{\"application\":{\"appId\":\""+APP_ID+"\"}}";
                    System.out.println("Objeto a mandar: "+dataI);
                    //Manda JSON
                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write(dataI);
                    wr.flush();
                    //
                    //Se recibe
                    //
                    StringBuilder sb = new StringBuilder();
                    int HttpResult = con.getResponseCode();
                    System.out.println("Respuesta:");
                    System.out.println(HttpResult);
                    System.out.println("Respuesta esperada:");
                    System.out.println(HttpURLConnection.HTTP_OK);
                    if (HttpResult == HttpURLConnection.HTTP_CREATED) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                        System.out.println("- - - - - - - - - - - - - - - - - -");
                        System.out.println("" + sb.toString());
                        System.out.println("- - - - - - - - - - - - - - - - - -");
                    } else {
                        System.out.println(con.getResponseMessage());
                    }
                }
                catch(Exception e)
                {
                    System.out.println(e.fillInStackTrace());
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
