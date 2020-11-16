package apc.mobprog.vaulthubmanager;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
public class MainActivity extends AppCompatActivity {

    final int Ver = Build.VERSION.SDK_INT;
    final String dlDir = "/sdcard/Download";
    final String keyDir = "/sdcard/Vaulthub";
    final String[] privatekeys = {keyDir + "/loginKeys/privateKey.key", keyDir + "/userKeys/privateKey.key"};
    final String[] publickeys = {keyDir + "/loginKeys/publicKey.key", keyDir + "/userKeys/publicKey.key"};
    final String apkPath = dlDir + "/Vaulthub.apk";
    final String name = "apc.mobprog.vaulthub";
    final Uri vaulthub = Uri.parse( "package:apc.mobprog.vaulthub" );
    final Uri manager = Uri.parse( "package:apc.mobprog.vaulthubmanager" );
    final Uri vaulthubSRC = Uri.parse( "https://github.com/walalang985/Vaulthub" );
    final Uri vaulthubDL = Uri.parse("https://vaulthub-dl.herokuapp.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        if(Ver > Build.VERSION_CODES.LOLLIPOP_MR1){
            if(!granted()){
                requestPermissions();
            }else {
                return;//disables the functionality if not granted
            }
        }
        Spinner spinner = findViewById( R.id.spinner );
        spinner.setAdapter( new ArrayAdapter<>( this, R.layout.support_simple_spinner_dropdown_item, handler.getItems() ) );
        Button exec = findViewById( R.id.button );
        exec.setOnClickListener( v -> doStuff( spinner.getSelectedItem().toString() ) );
    }
    public void doStuff(String selection){
        switch (selection){
            case "PLEASE SELECT AN OPERATION TO DO":
                return;
            case "DOWNLOAD VAULTHUB":
                if(new File( apkPath ).exists()){
                    Toast.makeText( getApplicationContext() , "File has already been downloaded", Toast.LENGTH_SHORT ).show();
                }
                else{
                    startActivity( new Intent(Intent.ACTION_VIEW, vaulthubDL) );
                }
                break;
            case "INSTALL VAULTHUB":
                PackageManager pm0 = getApplicationContext().getPackageManager();
                if(isInstalled( pm0 )){
                    Toast.makeText( getApplicationContext(),  "Application is already installed", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType( FileProvider.getUriForFile( getApplicationContext(), getApplicationContext().getPackageName() + ".provider", new File( apkPath ) ), "application/vnd.android.package-archive" );
                    intent.addFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION );
                    getApplicationContext().startActivity( intent );
                }
                break;
            case "LAUNCH VAULTHUB":
                PackageManager pm1 = getApplicationContext().getPackageManager();
                Intent intent0 = pm1.getLaunchIntentForPackage( name );
                if(isInstalled( pm1 )){
                    startActivity( intent0 );
                }else{
                    Toast.makeText( getApplicationContext(), "Vaulthub is not yet installed", Toast.LENGTH_SHORT ).show();
                }
                break;
            case "UNINSTALL VAULTHUB":
                PackageManager pm2 = getApplicationContext().getPackageManager();
                if(isInstalled( pm2 )){
                    startActivity( new Intent(Intent.ACTION_DELETE).setData( vaulthub ) );
                }
                else{
                    Toast.makeText( getApplicationContext(), "App not found", Toast.LENGTH_SHORT ).show();
                }
                break;
            case "GENERATE KEYS":
                PackageManager pm3 = getApplicationContext().getPackageManager();
                if(!isInstalled( pm3 )){
                    Toast.makeText( getApplicationContext(), "App is not yet installed", Toast.LENGTH_SHORT ).show();
                }else{
                    try{
                        KeyPairGenerator keygen0 = KeyPairGenerator.getInstance( "RSA" );
                        KeyPairGenerator keygen1 = KeyPairGenerator.getInstance( "RSA" );
                        keygen0.initialize(2048);
                        keygen1.initialize(2048);
                        KeyPair kp0 = keygen0.generateKeyPair();
                        KeyPair kp1 = keygen1.generateKeyPair();
                        File[] files = {new File( privatekeys[0] ), new File( privatekeys[1] ), new File( publickeys[0] ), new File( publickeys[1] )};
                        if(files[0]!=null && files[1]!=null&&files[2]!=null&&files[3]!=null){
                            for(int i = 0; i < files.length;i++){
                                files[i].getParentFile().mkdirs();
                            }
                        }
                        for (int i = 0; i < files.length;i++){
                            files[i].createNewFile();
                        }
                        ObjectOutputStream pub1,pub2,priv1,priv2;
                        priv1 = new ObjectOutputStream( new FileOutputStream( files[0] ) );
                        pub1 = new ObjectOutputStream( new FileOutputStream( files[2] ) );
                        priv2 = new ObjectOutputStream( new FileOutputStream( files[1] ) );
                        pub2 = new ObjectOutputStream( new FileOutputStream( files[3] ) );
                        priv1.writeObject( kp0.getPrivate() );
                        pub1.writeObject( kp0.getPublic() );
                        priv2.writeObject( kp1.getPrivate() );
                        pub2.writeObject( kp1.getPublic() );
                        pub1.close();
                        priv1.close();
                        pub2.close();
                        priv2.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case "UNINSTALL VAULTHUB MANAGER":
                startActivity( new Intent(Intent.ACTION_DELETE).setData( vaulthub ) );
                //wait at 15 seconds before uninstalling this
                new Handler().postDelayed( new Runnable() {
                    @Override
                    public void run() {
                        deleteDir();
                        startActivity( new Intent(Intent.ACTION_DELETE).setData( manager ));
                    }
                } , 3000);
                break;
            case "VIEW SOURCE CODE":
                startActivity( new Intent(Intent.ACTION_VIEW, vaulthubSRC) );
                break;
            case "DELETE GENERATED KEYS":
                deleteDir();
                break;
        }
    }
    public void requestPermissions(){
        ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.REQUEST_INSTALL_PACKAGES, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE}, 101 );
    }
    public boolean granted(){
        return ContextCompat.checkSelfPermission( this, Manifest.permission_group.STORAGE ) == PackageManager.PERMISSION_GRANTED;
    }
    private boolean isInstalled(@NonNull PackageManager pm){
        try{
            pm.getPackageInfo( name, 0 );
            return true;
        }catch (PackageManager.NameNotFoundException e){
            return false;
        }
    }
    public void deleteDir(){
        try {
            FileUtils.deleteDirectory( new File( keyDir ) );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class handler {
    private static String HEADER = "PLEASE SELECT AN OPERATION TO DO";
    private static String VAULTHUB_DOWNLOAD = "DOWNLOAD VAULTHUB";
    private static String VAULTHUB_INSTALL = "INSTALL VAULTHUB";
    private static String VAULTHUB_LAUNCH = "LAUNCH VAULTHUB";
    private static String VAULTHUB_UNINSTALL = "UNINSTALL VAULTHUB";
    private static String VAULTHUB_KEYGEN = "GENERATE KEYS";
    private static String VAULTHUB_DELETE_KEYGEN = "DELETE GENERATED KEYS";
    private static String VAULTHUB_MANAGER_UNINSTALL = "UNINSTALL VAULTHUB MANAGER";
    private static String VAULTHUB_SOURCE_CODE = "VIEW SOURCE CODE";
    @NonNull
    public static List<String> getItems(){
        List<String> list = new ArrayList<>();
        list.add( HEADER );
        list.add( VAULTHUB_DOWNLOAD );
        list.add( VAULTHUB_INSTALL );
        list.add( VAULTHUB_UNINSTALL );
        list.add( VAULTHUB_KEYGEN );
        list.add( VAULTHUB_DELETE_KEYGEN );
        list.add( VAULTHUB_LAUNCH );
        list.add( VAULTHUB_MANAGER_UNINSTALL );
        list.add( VAULTHUB_SOURCE_CODE );
        return list;
    }
}
