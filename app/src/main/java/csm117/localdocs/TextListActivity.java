package csm117.localdocs;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.ArrayAdapter;

public class TextListActivity extends Activity {

    private File file = null;
    private List<String> myList;
    ListView listView;
    ArrayAdapter<String> adapter;
    private String[] strings = {"Hi", "Hello", "Good morning"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_list);

        // Obtain file names from internal storage
        myList = new ArrayList<>();
        file = new File(Environment.getDataDirectory().toString()); // Return the user data directory
        File[] list = file.listFiles();
        try {
            for(int i = 0; i < list.length; i++){
                myList.add(list[i].getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Display the list
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myList);
        listView.setAdapter(adapter);
        // Click on an item in the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                String message = "You clicked # " + position
                        + ", which is string: " + textView.getText().toString();
                Toast.makeText(TextListActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
        addTextToFile("Hello");
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
}

