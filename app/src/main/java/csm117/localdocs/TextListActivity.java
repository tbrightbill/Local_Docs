package csm117.localdocs;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.ArrayAdapter;

// https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
// ListView

public class TextListActivity extends Activity {

    /**
     * Return Intent extra
     */
    public static String FILE_CONTENT = "file_content";

    private File file = null;
    private List<String> myList;
    ListView listView;
    ArrayAdapter<String> adapter;
    private String[] strings = {"Hi", "Hello", "Good morning"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_list);

        //create demo file
        String filename = "testfile";
        File selectedFile = new File(TextListActivity.this.getFilesDir(), filename);
        if (!selectedFile.exists()) {
            String content = "Hello world!";
            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(content.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // Obtain file names from internal storage
        myList = new ArrayList<>();
        //file = new File(Environment.getDataDirectory().toString()); // Return the user data directory
        file = this.getFilesDir();
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
                /*String message = "You clicked # " + position
                        + ", which is string: " + textView.getText().toString();
                Toast.makeText(TextListActivity.this, message, Toast.LENGTH_LONG).show();*/
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

                Intent intent = new Intent();
                intent.putExtra(FILE_CONTENT, text.toString());

                // Set result and finish this Activity
                setResult(Activity.RESULT_OK, intent);
                finish();
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
/*
    public void writeToFile() {
        try {
            // catches IOException below
            String TESTSTRING = new String("Hello Android");

       /* We have to use the openFileOutput()-method
       * the ActivityContext provides, to
       * protect your file from others and
       * This is done for security-reasons.
       * We chose MODE_WORLD_READABLE, because
       *  we have nothing to hide in our file */
  /*          FileOutputStream fOut = openFileOutput("samplefile.txt", MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            // Write the string to the file
            osw.write(TESTSTRING);

       /* ensure that everything is
        * really written out and close */
/*            osw.flush();
            osw.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
*/
/*    public void readFromFile()
    {
        try {
            String TESTSTRING = new String("Hello Android");
       /* We have to use the openFileInput()-method
        * the ActivityContext provides.
        * Again for security reasons with
        * openFileInput(...) */

  /*          FileInputStream fIn = openFileInput("samplefile.txt");
            InputStreamReader isr = new InputStreamReader(fIn);

        /* Prepare a char-Array that will
         * hold the chars we read back in. */
    /*        char[] inputBuffer = new char[TESTSTRING.length()];

            // Fill the Buffer with data from the file
            isr.read(inputBuffer);

            // Transform the chars to a String
            String readString = new String(inputBuffer);

            // Check if we read back the same chars that we had written out
            boolean isTheSame = TESTSTRING.equals(readString);

            Log.i("File Reading stuff", "success = " + isTheSame);

        } catch (IOException ioe)
        {ioe.printStackTrace();}
    }

//    String yourFilePath = context.getFilesDir() + "/" + "hello.txt";
//    File yourFile = new File( yourFilePath );
//
//    FileInputStream fis = context.openFileInput("hello.txt", Context.MODE_PRIVATE);
//    InputStreamReader isr = new InputStreamReader(fis);
//    BufferedReader bufferedReader = new BufferedReader(isr);
//    StringBuilder sb = new StringBuilder();
//    String line;
//    while(bufferedReader.readLine() != null) {
//        //sb.append(bufferedReader.readLine());
//        try{
//            line += bufferedReader.readLine();
//        }
//        catch(Exception)
//
//    }
/*
    // Read text from file
    public void ReadBtn(View v) {
        //reading text from file
        try {
            FileInputStream fileIn=openFileInput("mytextfile.txt");
            InputStreamReader InputRead= new InputStreamReader(fileIn);

            char[] inputBuffer= new char[READ_BLOCK_SIZE];
            String s="";
            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer,0,charRead);
                s += readstring;
            }
            InputRead.close();
            Toast.makeText(getBaseContext(), s,Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        myList = new ArrayList<>();

        //String root_sd = Environment.getInternalStorageDirectory().toString();
        //directory = new File(Environment.getDataDirectory() + "/RobotiumTestLog/");
        //file = new File(root_sd);
        file = new File(Environment.getDataDirectory().toString());
        File list[] = file.listFiles();

        for (int i = 0; i < list.length; i++){
            myList.add(list[i].getName());
        }
        //setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myList));
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myList));
    }

    protected void onListItemClick(ListView l, View v, int position, long id){
        //super.onListItemClick(l, v, position, id);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        File temp_file = new File(file, myList.get(position));

        if (!temp_file.isFile()){
            file = new File(file, myList.get(position));
            File list[] = file.listFiles();

            myList.clear();

            for (int i = 0; i < list.length; i++){
                myList.add(list[i].getName());
            }
            Toast.makeText(getApplicationContext(), file.toString(), Toast.LENGTH_LONG).show();
            listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myList));
            //setListAdapter(new ArrayAdapter<String>(this,android.R.Layout.simple_list_item_1, myList)));
        }
    }

    @Override
    public void onBackPressed() {
        String parent = file.getParent();
        file = new File( parent ) ;
        File list[] = file.listFiles();

        myList.clear();

        for( int i=0; i< list.length; i++)
        {
            myList.add( list[i].getName() );
        }
        Toast.makeText(getApplicationContext(), parent, Toast.LENGTH_LONG).show();
        //setListAdapter(new ArrayAdapter<String>(this,
          //      android.R.layout.simple_list_item_1, myList ));
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myList));
    }


    EditText textmsg;
    static final int READ_BLOCK_SIZE = 100;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_editor);
//        //setContentView(R.layout.activity_main);
//
//        //textmsg=(EditText)findViewById(R.id.editText1);
//    }

    // write text to file
    public void WriteBtn(View v) {
        // add-write text into file
        try {
            FileOutputStream fileout=openFileOutput("mytextfile.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.write(textmsg.getText().toString());
            outputWriter.close();

            //display file saved message
            Toast.makeText(getBaseContext(), "File saved successfully!",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}

