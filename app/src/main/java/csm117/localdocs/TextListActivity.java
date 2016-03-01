package csm117.localdocs;

import android.view.MenuItem;
import android.view.ContextMenu;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Button;


public class TextListActivity extends Activity {

    /**
     * Return Intent extra
     */
    public static String FILE_CONTENT = "file_content";
    public static String FILE_NAME = "file_name";

    private File file = null;
    private List<String> myList;
    ListView listView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_list);
        Button listButton = (Button) findViewById(R.id.create_new_doc);

        /*
        //button on top to create a new file
        //requires user to press the button
        listButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String filename = "new_file";
                File selectedFile = new File(TextListActivity.this.getFilesDir(), filename);
                if (!selectedFile.exists()) {
                    // String content = "Hello world!";
                    String content = "";    // empty string
                    FileOutputStream outputStream;
                    try {
                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(content.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        */

        // create dummy file
        public void onClick(View v) {
            String filename = "new_file";
            File selectedFile = new File(TextListActivity.this.getFilesDir(), filename);
            if (!selectedFile.exists()) {
                // String content = "Hello world!";
                String content = "";    // empty string
                FileOutputStream outputStream;
                try {
                    outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(content.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Obtain file names from internal storage
        myList = new ArrayList<>();
        //file = new File(Environment.getDataDirectory().toString()); // Return the user data directory
        file = this.getFilesDir();
        File[] list = file.listFiles();
        try {
            for (File aList : list) {
                myList.add(aList.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Display the list
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myList);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        // Click on an item in the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                File selectedFile = new File(TextListActivity.this.getFilesDir(), textView.getText().toString());
                //Read text from file
                StringBuilder text = new StringBuilder();

                try {
                    BufferedReader br = new BufferedReader(new FileReader(selectedFile));
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    if (text.length() > 0)
                        text.deleteCharAt(text.length() - 1);
                    br.close();
                }
                catch (IOException e) {
                    //You'll need to add proper error handling here
                }

                //startActivity((new Intent(TextListActivity.this, Pop.class)));
                Intent intent = new Intent();
                intent.putExtra(FILE_CONTENT, text.toString());
                intent.putExtra(FILE_NAME, textView.getText().toString());

                // Set result and finish this Activity
                 setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        addTextToFile("Hello");
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.list_view) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            File[] list = file.listFiles();
            menu.setHeaderTitle(list[info.position].getName());
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_file_context, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.open:
                File[] list = file.listFiles();
                File selectedFile = list[info.position];
                //Read text from file
                StringBuilder text = new StringBuilder();

                try {
                    BufferedReader br = new BufferedReader(new FileReader(selectedFile));
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    if (text.length() > 0)
                        text.deleteCharAt(text.length() - 1);
                    br.close();
                }
                catch (IOException e) {
                    //You'll need to add proper error handling here
                }
                Intent intent = new Intent();
                intent.putExtra(FILE_CONTENT, text.toString());
                intent.putExtra(FILE_NAME, selectedFile.getName());

                // Set result and finish this Activity
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;
            case R.id.rename:
                //editNote(info.id);
                return true;
            case R.id.delete:
                //deleteNote(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    // create a file and add text to it
    public void addTextToFile(String text) {
        File logFile = new File("usbStorage/" + "MyFile.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void deleteAFile(String path) {
        File file = new File(path);
        boolean deleted = file.delete();
    }
}

