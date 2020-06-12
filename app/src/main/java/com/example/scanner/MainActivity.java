package com.example.scanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.scanner.roomdb.Document;
import com.example.scanner.roomdb.DocumentViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.scanner.utils.Constants.BaseDir.CARDDIR;
import static com.example.scanner.utils.Constants.BaseDir.PHOTODIR;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int PICK_IMAGE = 1;
    private static final int PERMISSIONS_REQUEST_CODE = 1001;
    String[] PERMISSIONS =
            {
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private DocumentViewModel viewModel;

    private RecyclerView mainRecyclerView;
    MainAdapter adapter;
    private Spinner categorySpin;
    private ClipboardManager clipboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        categorySpin = (Spinner) findViewById(R.id.categorySpin);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mainRecyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mainRecyclerView.setHasFixedSize(true);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAndRequestPermissions()) {
                    // Floating action button
                    ListView listView = new ListView(MainActivity.this);
                    List<String> options = new ArrayList<>();
                    options.add("QR Scan");
                    options.add("Bar Code Scan");
                    options.add("Docs Scan");
                    options.add("ID Card Scan");
                    options.add("ID Photo Scan");
                    options.add("Image to Text");

                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, options);
                    listView.setAdapter(adapter);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Select Option");
                    builder.setCancelable(true);
                    builder.setView(listView);

                    final AlertDialog dialog = builder.create();
                    dialog.show();

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (adapter.getItem(position).equals("QR Scan")) {
                                scanCode();
                            }
                            if (adapter.getItem(position).equals("Bar Code Scan")) {
                                scanCode();
                            }
                            if (adapter.getItem(position).equals("Docs Scan")) {
                                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                                startActivity(intent);
                            }
                            if (adapter.getItem(position).equals("ID Card Scan")) {
                                Intent intent = new Intent(MainActivity.this, IdCardActivity.class);
                                startActivity(intent);
                            }
                            if (adapter.getItem(position).equals("ID Photo Scan")) {
                                Intent intent = new Intent(MainActivity.this, ResizeActivity.class);
                                startActivity(intent);
                            }
                            if (adapter.getItem(position).equals("Image to Text")) {
                                Intent intent = new Intent(MainActivity.this, TextscanActivity.class);
                                startActivity(intent);
                            }
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        adapter = new MainAdapter();
        mainRecyclerView.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(DocumentViewModel.class);
        viewModel.documentGroup("", "").observe(this, new Observer<List<Document>>() {
            @Override
            public void onChanged(List<Document> documents) {
                adapter.setDocuments(documents);
            }
        });


        final ArrayAdapter<String> spinadapter = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_layout,
                getResources().getStringArray(R.array.category));
        spinadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpin.setAdapter(spinadapter);

        categorySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinadapter.getItem(position).equals("All Docs")) {
                    viewModel.documentGroup("", "").observe(MainActivity.this, new Observer<List<Document>>() {
                        @Override
                        public void onChanged(List<Document> documents) {
                            adapter.setDocuments(documents);
                        }
                    });
                }
                if (spinadapter.getItem(position).equals("Docs")) {
                    viewModel.documentGroup("1", "").observe(MainActivity.this, new Observer<List<Document>>() {
                        @Override
                        public void onChanged(List<Document> documents) {
                            adapter.setDocuments(documents);
                        }
                    });
                }
                if (spinadapter.getItem(position).equals("ID Card")) {
                    viewModel.documentGroup("2", "").observe(MainActivity.this, new Observer<List<Document>>() {
                        @Override
                        public void onChanged(List<Document> documents) {
                            adapter.setDocuments(documents);
                        }
                    });
                }
                if (spinadapter.getItem(position).equals("PDF")) {
                    viewModel.documentGroup("3", "").observe(MainActivity.this, new Observer<List<Document>>() {
                        @Override
                        public void onChanged(List<Document> documents) {
                            adapter.setDocuments(documents);
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss");
        final String timestamp = simpleDateFormat.format( new Date() );

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final String date = dateFormat.format( new Date() );

//        String filename = "SCANNED_STG_" + timestamp + ".png";
        String fileName = "1591845324930" + ".png";
        String filePath = CARDDIR + fileName;

//        Document document = new Document();
//        document.setName("Three");
//        document.setDate("17-06-2020");
//        document.setCategory("1");
//        document.setPath(filePath);
//        document.setPageCount(1);
//        document.setScanned(timestamp);
//        viewModel.saveDocument(document);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search...");
//
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.documentGroup("", query).observe(MainActivity.this, new Observer<List<Document>>() {
                    @Override
                    public void onChanged(List<Document> documents) {
                        adapter.setDocuments(documents);
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.documentGroup("", newText).observe(MainActivity.this, new Observer<List<Document>>() {
                    @Override
                    public void onChanged(List<Document> documents) {
                        adapter.setDocuments(documents);
                    }
                });
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.import_gallery:
                importFromGallery();
                break;

            case R.id.sort_by:
                sorting();
                break;
        }
        return true;
    }

    private void sorting() {
        ListView listView = new ListView(MainActivity.this);
        List<String> options = new ArrayList<>();
        options.add("By Date");
        options.add("By Name");
        options.add("Pdf");
        options.add("Docs");
        options.add("Id Card");

        final ArrayAdapter<String> adapterList = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapterList);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select Option");
        builder.setCancelable(true);
        builder.setView(listView);

        final AlertDialog dialog = builder.create();
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapterList.getItem(position).equals("By Date")) {
                    viewModel.documentByDate().observe(MainActivity.this, new Observer<List<Document>>() {
                        @Override
                        public void onChanged(List<Document> documents) {
                            adapter.setDocuments(documents);
                        }
                    });
                }
                if (adapterList.getItem(position).equals("By Name")) {
                    viewModel.documentByName().observe(MainActivity.this, new Observer<List<Document>>() {
                        @Override
                        public void onChanged(List<Document> documents) {
                            adapter.setDocuments(documents);
                        }
                    });
                }
                if (adapterList.getItem(position).equals("Pdf")) {
                    viewModel.documentGroup("3", "").observe(MainActivity.this, new Observer<List<Document>>() {
                        @Override
                        public void onChanged(List<Document> documents) {
                            adapter.setDocuments(documents);
                        }
                    });
                }
                if (adapterList.getItem(position).equals("Docs")) {
                    viewModel.documentGroup("1", "").observe(MainActivity.this, new Observer<List<Document>>() {
                        @Override
                        public void onChanged(List<Document> documents) {
                            adapter.setDocuments(documents);
                        }
                    });
                }
                if (adapterList.getItem(position).equals("Id Card")) {
                    viewModel.documentGroup("2", "").observe(MainActivity.this, new Observer<List<Document>>() {
                        @Override
                        public void onChanged(List<Document> documents) {
                            adapter.setDocuments(documents);
                        }
                    });
                }

                dialog.dismiss();
            }
        });
    }

    private void importFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.setCaptureActivity(CodescanActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning...");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    BitmapHelper.getInstance().setBitmap(selectedImage);

                    if (BitmapHelper.getInstance().getBitmap() == null) {
                        Toast.makeText(MainActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
                        startActivity(intent);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        }

        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Scanning Result");
                builder.setMessage(result.getContents());

                builder.setPositiveButton("FINISH", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        ClipData clipData = ClipData.newPlainText("data", result.getContents());
//                        clipboardManager.setPrimaryClip(clipData);
//                        Toast.makeText(MainActivity.this, "Copied", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                Toast.makeText(this, "No Results", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.starred_documents:
//                break;

            case R.id.file_conversion:
                Intent intent = new Intent(MainActivity.this, FileconvActivity.class);
                startActivity(intent);
                break;

//            case R.id.settings:
//                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
//                break;

            case R.id.feedback:
                break;

            case R.id.contact_us:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private boolean checkAndRequestPermissions() {
//        Check which permission are granted
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String appPermissions : PERMISSIONS)
        {
            if (ContextCompat.checkSelfPermission(this, appPermissions) != PackageManager.PERMISSION_GRANTED)
            {
                listPermissionsNeeded.add(appPermissions);
            }
        }

//        Ask for non-granted permissions
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    PERMISSIONS_REQUEST_CODE);
            return false;
        }

//        App has all permissions. Proceed ahead
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE)
        {
            HashMap<String, Integer> permissionResults = new HashMap<>();
            int deniedCount = 0;

//            Gather permission grant results
            for (int i=0; i<grantResults.length; i++)
            {
//                Add only permissions which are denied
                if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                {
                    permissionResults.put(permissions[i], grantResults[i]);
                    deniedCount++;
                }
            }

//            Check if all permissions are granted
            if (deniedCount == 0)
            {
                // Floating action button
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListView listView = new ListView(MainActivity.this);
                        List<String> options = new ArrayList<>();
                        options.add("QR Scan");
                        options.add("Bar Code Scan");
                        options.add("Docs Scan");
                        options.add("ID Card Scan");
                        options.add("ID Photo Scan");
                        options.add("Image to Text");

                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, options);
                        listView.setAdapter(adapter);

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Select Option");
                        builder.setCancelable(true);
                        builder.setView(listView);

                        final AlertDialog dialog = builder.create();
                        dialog.show();

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (adapter.getItem(position).equals("QR Scan")) {
                                    scanCode();
                                }
                                if (adapter.getItem(position).equals("Bar Code Scan")) {
                                    scanCode();
                                }
                                if (adapter.getItem(position).equals("Docs Scan")) {
                                    Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                                    startActivity(intent);
                                }
                                if (adapter.getItem(position).equals("ID Card Scan")) {
                                    Intent intent = new Intent(MainActivity.this, IdCardActivity.class);
                                    startActivity(intent);
                                }
                                if (adapter.getItem(position).equals("ID Photo Scan")) {
                                    Intent intent = new Intent(MainActivity.this, ResizeActivity.class);
                                    startActivity(intent);
                                }
                                if (adapter.getItem(position).equals("Image to Text")) {
                                    Intent intent = new Intent(MainActivity.this, TextscanActivity.class);
                                    startActivity(intent);
                                }
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }

//            Alteast one or all permissions are denied
            else
            {
                for (Map.Entry<String, Integer> entry : permissionResults.entrySet())
                {
                    String permName = entry.getKey();
                    int permResult = entry.getValue();

//                    permission is denied (this is the first time, when "never ask again" is not checked)
//                    so ask again explaining the usage of permission
//                    shouldShowRequestPermissionRationale will return true
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permName))
                    {
//                        Show dialog of explanation
                        showDialog("", "This app needs Camera and Storage permissions to work without any problems.",
                                "Yes, Grant permissions",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        checkAndRequestPermissions();
                                    }
                                },
                                "No, Exit app", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        finish();
                                    }
                                }, false);
                    }

//                    permission is denied (this is the first time, when "never ask again" is checked)
//                    shouldShowRequestPermissionRationale will return false
                    else
                    {
//                        Ask user to go to settings and manually allow permissions
                        showDialog("",
                                "You have denied some permissions. Allow all permissions at [Setting] > [Permissions]",
                                "Go to Settings",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();

//                                        Go to Settings
//                                        Intent intent = null;
//                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//                                            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                                                    Uri.fromParts("package", getPackageName(), null));
//                                        }
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(intent);
//                                        finish();

                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }
                                },
                                "No, Exit app", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        finish();
                                    }
                                }, false);
                        break;
                    }
                }
            }
        }
    }

    public AlertDialog showDialog(String title, String msg, String positiveLabel,
                                  DialogInterface.OnClickListener positiveOnClick,
                                  String negativeLabel, DialogInterface.OnClickListener negativeOnClick,
                                  boolean isCancelAble)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setCancelable(isCancelAble);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLabel, positiveOnClick);
        builder.setNegativeButton(negativeLabel, negativeOnClick);

        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }
}
