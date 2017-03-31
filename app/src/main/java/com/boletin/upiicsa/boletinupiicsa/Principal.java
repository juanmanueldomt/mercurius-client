package com.boletin.upiicsa.boletinupiicsa;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Metodo Cargado al abrir la aplicacion
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Buscamos el WebView y le Asignamos Valores Personalizados
        myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        CookieManager.getInstance().setAcceptCookie(true);
        myWebView.setWebViewClient(
                //Cliente Web Personalizado con Inyeccion de CSS
                new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        // Inyectamos cuando lapagina haya terminado de cargarse
                        injectCSS();
                        super.onPageFinished(view, url);
                    }
                });
        //Creamos una Interfaz JavaScript
        //Para obtener los valores Correspondientes al nombre de usuario y correo
        final JavaScriptInterface n =new JavaScriptInterface(this);
        //La funcion interface se llamara AndroidFunction
        myWebView.addJavascriptInterface(n,"AndroidNav");
        //Cargamos la pagina.
        myWebView.loadUrl(Estructura.ROOT);
    }

    private void injectCSS() {
        //Inyeccion de CSS para quitar barra de navegacion.
        try {
            InputStream inputStream = getAssets().open("style.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            myWebView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //Si en el WebView Podemos Volver entonces volvemos; sino salimos de la aplicacion
            if(myWebView.canGoBack())
            myWebView.goBack();
            else
                super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
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

    private void loadpage(String pagina){
        myWebView.loadUrl(pagina);
    }

    private void loadpage(String pagina,String tag){
        myWebView.loadUrl(pagina+"?tag="+tag);
           }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //Seleccion de las diferentes categorias
        if (id == R.id.nav_Administrativo) {

            loadpage(Estructura.ROOT,"Administrativo");
        }
        else if(id == R.id.nav_Direccion)
            loadpage(Estructura.ROOT,"Direccion");
            else if (id == R.id.nav_Academico)
            loadpage(Estructura.ROOT,"Academico");
        else if(id == R.id.nav_Cultural)
            loadpage(Estructura.ROOT,"Cultural");
        else if(id == R.id.nav_Deportivo)
            loadpage(Estructura.ROOT,"Deportivo");
        else if(id == R.id.nav_Salud)
            loadpage(Estructura.ROOT,"Salud");
        else if(id == R.id.nav_Investigacion){
           loadpage(Estructura.ROOT,"Investigacion");
        }
        else if(id == R.id.logout) {
            loadpage(Estructura.LOGOUT);
            myWebView.clearHistory();
            TextView logtxt=(TextView) findViewById(R.id.nombre);
            logtxt.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    loadpage(Estructura.LOGIN);
                }
            });
            TextView regtxt=(TextView) findViewById(R.id.correo);
            regtxt.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    loadpage(Estructura.REGISTER);
                }
            });
            logtxt.setText("Ingresa");
            regtxt.setText("Registrate");
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void login(View v){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        loadpage(Estructura.LOGIN);
    }

    public class JavaScriptInterface{
        //Interfaz de JavaScript
        Context mContext;
        JavaScriptInterface(Context c){
            mContext=c;
        }
        //Metodo de Interfaz
        @JavascriptInterface
        public void setProperty(String iname,String imail) {
            final String nombre = iname;
            final String mail = imail;

            TextView name=(TextView) findViewById( R.id.nombre);
            name.setOnClickListener(null);
            name.setText(nombre);

            TextView correo=(TextView) findViewById( R.id.correo);
            correo.setOnClickListener(null);
            correo.setText(mail);
            Toast.makeText(mContext,imail,Toast.LENGTH_LONG);
        }
    }

    public class Estructura{
        final static String ROOT="http://10.42.0.1/mercurius/index.php";
        final static String LOGIN=ROOT+"/login.php";
        final static String EDITOR=ROOT+"/editor.php";
        final static String LOGOUT=ROOT+"/logout.php";
        final static String REGISTER=ROOT+"/register.php";


    }
}
