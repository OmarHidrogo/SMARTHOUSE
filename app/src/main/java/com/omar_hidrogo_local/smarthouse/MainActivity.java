package com.omar_hidrogo_local.smarthouse;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter btAdapter = null; //Adaptador Bluetooth

    private static MainActivity instance; //Se declara una instancia estatica  para MainActivity

    //Se declara un constructor publico instanciando
    public MainActivity(){
        instance = this;
    }

    //Se declara un getcontext para tener acceso al contexto de mainactivity
    public static Context getContext(){
        return  instance;
    }

    //Se declara variable publica  estatica final para el codigo de solicitud bluetooth
    public static final int CODIGO_SOLICITUD_PERMISO = 1;
    public static final int CODIGO_SOLICITUD_HABILITAR_BLUETOOTH = 0;

    private  Context context;
    public static  Activity activity;  //publica para acceso a la actividad

    private Toolbar toolbar;  //barra superior aplicacion
    private ConstructorDevices constructorDevices;
    private Splash_screen splash_screen;

    public Devices_controller devices_controller;

    //inicializar preferencias compartidas
    public static final SharedPreferences miprefInternet = getContext().getSharedPreferences("cInternet", Context.MODE_PRIVATE);
    public static final SharedPreferences miprefBluetooth = getContext().getSharedPreferences("cBluetooth", Context.MODE_PRIVATE);
    public static String cInternet = "";
    public static String cBluetooth= "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        activity = this;

        //entrar a las propiedades de la barra superios aplicacion
        toolbar =(Toolbar) findViewById(R.id.toolbarbar); // se establece contacto con el id del layout

        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        //quitar el titulo por defecto al actionbar
        if(toolbar != null){
            bar.setDisplayShowTitleEnabled(false); //se desabilita el titulo del actionbar por default
        }


        //inicializar preferencias compartidas

        // Se guarda la preferencia en string haciendo relacion con cInternet
        cInternet = miprefInternet.getString("cInternet", "");

        // Se guarda la preferencia en string haciendo relacion con cBluetooth
        cBluetooth = miprefBluetooth.getString("cBluetooth", "");



        //Si la preferencia compartida esta vacia se redireccionara a conectarse a un dispositivo llamando a un dialog
        if (cInternet.equals("") && cBluetooth.equals("")){
            AlertDialog.Builder messageConnection = new AlertDialog.Builder(MainActivity.this);
            messageConnection.setMessage("Para continuar por favor selecciona metodo de conexion para el control de su casa")
                    .setCancelable(true)
                    .setPositiveButton("Internet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //si dio clic en Internet se redirigira a la actividad Connection_internet
                            Intent intent = new Intent(MainActivity.this, Connection_internet.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("Bluetooth", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            habilitarBluetooth();

                        }
                    });
            AlertDialog titulo = messageConnection.create();
            titulo.setTitle("Alerta!");
            titulo.show();

        }else{
            //Toast.makeText(MainActivity.this, "Ingrese las conexiones a controlar", Toast.LENGTH_LONG).show();
            habilitarBluetooth();

        }
        return;

    }

    public void habilitarBluetooth(){
        solicitarPermiso();

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null){
            Toast.makeText(MainActivity.this, "Tu dispositivo no tiene Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if(!mBluetoothAdapter.isEnabled()){
            Intent habilitarBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(habilitarBluetoothIntent, CODIGO_SOLICITUD_HABILITAR_BLUETOOTH);
        }


    }
    public boolean chacarStatusPermiso(){
        //PREGUNTA SI EL PERMISO FUE OTORGADO EN LA APLICACION
        int resultado = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH);
        if(resultado == PackageManager.PERMISSION_GRANTED){
            return  true;
        }else {
            return  false;
        }
    }

    //solicitar el permiso al usuario
    public  void solicitarPermiso() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.BLUETOOTH)) {
            Toast.makeText(MainActivity.this, "El permiso ya fue otorgado, si deseas desactivarlo puedes ir a los ajustes de la aplicacion", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH}, CODIGO_SOLICITUD_PERMISO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CODIGO_SOLICITUD_PERMISO:
                if(chacarStatusPermiso()){
                    //Toast.makeText(MainActivity.this, "Ya esta activo el permiso para el Bluetooth", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "No esta activo el permiso para el Bluetooth", Toast.LENGTH_SHORT).show();
                }
                break;

        }

        if(cInternet.equals("") && cBluetooth.equals("")) {
            Intent intent = new Intent(MainActivity.this, Device_Lists.class);
            startActivity(intent);
            finish();

        }else
        {

            Intent intent = new Intent(MainActivity.this, Splash_screen.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ccasa:
                Intent intent = new Intent(this, Connection_internet.class);
                this.startActivity(intent);
                break;
            case R.id.nconexion:
                Intent intent2 = new Intent(this, Devices_controller.class);
                this.startActivity(intent2);
                break;
            case R.id.cbluetooth:
                Intent intent3 = new Intent(this, Device_Lists.class);
                this.startActivity(intent3);
                break;
            case R.id.cInternet:
                Intent intent5 = new Intent(this, Connection_internet.class);
                this.startActivity(intent5);
                break;
            case R.id.acerca:
                Intent intent4 = new Intent(this, Connection_internet.class);
                this.startActivity(intent4);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
