package apc.mobprog.vaulthubmanager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
public class MainActivity extends AppCompatActivity {
    final String dlDir = "/sdcard/Download";
    final String keyDir = "/sdcard/Vaulthub";
    final String[] privatekeys = {keyDir + "/loginKeys/privateKey.key", keyDir + "/userKeys/privateKey.key"};
    final String[] publickeys = {keyDir + "/loginKeys/publicKey.key", keyDir + "/userKeys/publicKey.key"};
    final String apkPath = dlDir + "/Vaulthub.apk";
    final String name = "apc.mobprog.vaulthub";
    final int Ver = Build.VERSION.SDK_INT;
    final Uri vaulthub = Uri.parse( "package:apc.mobprog.vaulthub" );
    final Uri manager = Uri.parse( "package:apc.mobprog.vaulthubmanager" );
    final Uri vaulthubSRC = Uri.parse( "https://github.com/walalang985/Vaulthub" );
    final Uri vaulthubDL = Uri.parse("https://download1513.mediafire.com/bgzdhankq4dg/miswrsfw7nwbmi5/Vaulthub.apk");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        if(Ver > Build.VERSION_CODES.LOLLIPOP_MR1){
            if(!granted()){
                requestPermissions();
            }else {
                return;
            }
        }
        Button deleteK = findViewById( R.id.delete ), dl = findViewById( R.id.dl ), install = findViewById( R.id.installer ), gen = findViewById( R.id.keygen ), uninstall = findViewById( R.id.remove ), launcher = findViewById( R.id.launch ), viewsrc = findViewById( R.id.code ), removethis = findViewById( R.id.removeThis );
        dl.setOnClickListener( v -> {
            if(new File( apkPath ).exists()){
                Toast.makeText( getApplicationContext() , "File has already been downloaded", Toast.LENGTH_SHORT ).show();
            }
            else{
                download();
            }
        } );
        install.setOnClickListener( v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType( FileProvider.getUriForFile( getApplicationContext(), getApplicationContext().getPackageName() + ".provider", new File( apkPath ) ), "application/vnd.android.package-archive" );
            intent.addFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION );
            startActivity( intent );


        } );
        deleteK.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File( "/sdcard/Vaulthub" );
                boolean delete = file.delete();
                Log.d("tag", Boolean.toString( delete ));
            }
        } );
        gen.setOnClickListener( v -> {
            PackageManager pm = getApplicationContext().getPackageManager();
            if(!isInstalled( pm )){
                Toast.makeText( getApplicationContext(), "App is not yet installed", Toast.LENGTH_SHORT ).show();
            }
            else{
                try{
                    KeyPairGenerator keygen0 = KeyPairGenerator.getInstance( "RSA" );
                    KeyPairGenerator keygen1 = KeyPairGenerator.getInstance( "RSA" );
                    keygen0.initialize(2048);
                    keygen1.initialize(2048);
                    KeyPair kp0 = keygen0.generateKeyPair();
                    KeyPair kp1 = keygen1.generateKeyPair();
                    //0 = login private key 1 = user private key 2 = login public key 3 = user public key
                    File[] files = {new File( privatekeys[0] ),new File( privatekeys[1] ), new File( publickeys[0] ), new File( publickeys[1] )};
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
                    Toast.makeText( getApplicationContext(),"Generated successfully" , Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } );
        uninstall.setOnClickListener( v -> {
            PackageManager pm = getApplicationContext().getPackageManager();
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData( vaulthub );
            startActivity( intent );
        } );
        launcher.setOnClickListener( v -> {
            PackageManager pm = getApplicationContext().getPackageManager();
            Intent intent = getPackageManager().getLaunchIntentForPackage( name );
            if(isInstalled( pm )){
                startActivity( intent );
            }else{
                Toast.makeText( getApplicationContext(), "Vaulthub is not yet installed", Toast.LENGTH_SHORT ).show();
            }
        } );
        viewsrc.setOnClickListener( v -> startActivity( new Intent(Intent.ACTION_VIEW,vaulthubSRC ) ) );
        removethis.setOnClickListener( v -> {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData( manager );
            startActivity( intent );
        } );
    }
    public void requestPermissions(){
        ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.REQUEST_INSTALL_PACKAGES, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE}, 101 );
    }
    public boolean granted(){
        if(ContextCompat.checkSelfPermission( this, Manifest.permission_group.STORAGE ) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            return false;
        }
    }
    private boolean isInstalled(@NonNull PackageManager pm){
        try{
            pm.getPackageInfo( name, 0 );
            return true;
        }catch (PackageManager.NameNotFoundException e){
            return false;
        }
    }
    public void download(){
        DownloadManager.Request request = new DownloadManager.Request( vaulthubDL );
        request.setAllowedNetworkTypes( DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI );
        request.setTitle( "Vaulthub.apk" );
        request.setDescription( "Downloading apk" );
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED );
        request.setDestinationInExternalPublicDir( Environment.DIRECTORY_DOWNLOADS, "Vaulthub.apk" );
        DownloadManager manager = (DownloadManager) getSystemService( Context.DOWNLOAD_SERVICE );
        manager.enqueue( request );
    }
    public boolean doKeysExist(){
        File[] files = {new File( privatekeys[0] ),new File( privatekeys[1] ), new File( publickeys[0] ), new File( publickeys[1] )};
        return files[0].exists() && files[1].exists() && files[2].exists() && files[3].exists();
    }
}
