package com.enginious.snowblossom;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.enginious.snowblossom.activities.HomeActivity;
import com.google.protobuf.util.JsonFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import snowblossom.client.SnowBlossomClient;
import snowblossom.client.WalletUtil;
import snowblossom.lib.Globals;
import snowblossom.proto.WalletDatabase;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.btn_create_main)
    Button createWallet;

    @BindView(R.id.btn_import_main) Button importWallet;

    SharedPreferences prefs;


    private static final int READ_REQUEST_CODE = 42;
    private static final int MY_PERMISSIONS_REQUEST = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        prefs = getSharedPreferences("configs",MODE_PRIVATE);

        createWallet.setOnClickListener(this);

        importWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importWallet();
            }
        });
    }


    @Override
    public void onClick(View view) {
        Boolean is_config = getIntent().getBooleanExtra("config",false);

        if(is_config){

            String url = getIntent().getStringExtra("url");
            String port = getIntent().getStringExtra("port");

            url = url.trim();
            url = url.replace(" ", "");

            port = port.trim();
            port = port.replace(" ", "");
            if (url.isEmpty() || port.isEmpty()) {

                return;
            }

            int net = getIntent().getIntExtra("net",0);


            Globals.addCryptoProviderAndroid();
            TreeMap<String, String> configs = new TreeMap<>();
            configs.put("node_host", url);
            configs.put("node_port", port);
            if (net == 1) {
                configs.put("network", "mainnet");
            } else {
                configs.put("network", "testnet");
            }

            ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
            File internal_file =  contextWrapper.getDir(getFilesDir().getName(), Context.MODE_PRIVATE);

            File wallet_path = new File(internal_file, "wallet_db_testnet");

            String path = wallet_path.getAbsolutePath();

            prefs.edit().putString("wallet_path",path).apply();

            configs.put("wallet_path", wallet_path.getAbsolutePath());

            try {
                SnowBlossomClient client = WalletHelper.InitClient(configs);
                // moving to bottom navigation


                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("net", net);
                editor.putBoolean("config",true);
                editor.putString("url", url);
                editor.putString("port", port);
                editor.apply();

                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                // set the new task and clear flags
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
    public void importWallet(){


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST);

        } else {
            // Permission has already been granted
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            startActivityForResult(intent, READ_REQUEST_CODE);
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, READ_REQUEST_CODE);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            String filename = "";
            if (resultData != null) {
                try {
                    Uri uri = resultData.getData();


                        String mimeType = getContentResolver().getType(uri);
                        if (mimeType == null) {
                            String path = getPath(this, uri);
                            if (path == null) {

                                filename = FilenameUtils.getName(uri.toString());

                            } else {
                                File file = new File(path);
                                filename = file.getName();
                            }
                        } else {
                            Uri returnUri = resultData.getData();
                            Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                            returnCursor.moveToFirst();
                            filename = returnCursor.getString(nameIndex);
                            String size = Long.toString(returnCursor.getLong(sizeIndex));
                        }
                        String sourcePath = getExternalFilesDir(null).toString();
                        try {
                            File txt_file = new File(sourcePath + "/" + filename);
                            copyFileStream(txt_file, uri,this);

                            //creating wallet
                            Boolean is_config = getIntent().getBooleanExtra("config",false);

                            if(is_config){

                                String url = getIntent().getStringExtra("url");
                                String port = getIntent().getStringExtra("port");

                                url = url.trim();
                                url = url.replace(" ", "");

                                port = port.trim();
                                port = port.replace(" ", "");
                                if (url.isEmpty() || port.isEmpty()) {

                                    return;
                                }

                                int net = getIntent().getIntExtra("net",0);


                                Globals.addCryptoProviderAndroid();
                                TreeMap<String, String> configs = new TreeMap<>();
                                configs.put("node_host", url);
                                configs.put("node_port", port);
                                if (net == 1) {
                                    configs.put("network", "mainnet");
                                } else {
                                    configs.put("network", "testnet");
                                }

                                ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
                                File internal_file =  contextWrapper.getDir(getFilesDir().getName(), Context.MODE_PRIVATE);

                                File wallet_path = new File(internal_file, "wallet_db_testnet");

                                String path = wallet_path.getAbsolutePath();

                                prefs.edit().putString("wallet_path",path).apply();

                                configs.put("wallet_path", wallet_path.getAbsolutePath());

                                try {

                                    SnowBlossomClient client = WalletHelper.InitClient(configs);
                                    // Parsing txt file


                                    JsonFormat.Parser parser = JsonFormat.parser();
                                    WalletDatabase.Builder wallet_import = WalletDatabase.newBuilder();
                                    Reader input = new InputStreamReader(new FileInputStream(txt_file));
                                    parser.merge(input, wallet_import);

                                    WalletUtil.testWallet( wallet_import.build() );
                                    client.getPurse().mergeIn(wallet_import.build());

                                    client.printBasicStats(wallet_import.build());

                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putInt("net", net);
                                    editor.putBoolean("config",true);
                                    editor.putString("url", url);
                                    editor.putString("port", port);
                                    editor.apply();

                                    Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                                    // set the new task and clear flags
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                }catch (Exception e){
                                    e.printStackTrace();
                                }







                            }



                            //end creating wallet









                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
    public static String getPath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else
            if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private void copyFileStream(File dest, Uri uri, Context context)
            throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
            os.close();
        }
    }



}
