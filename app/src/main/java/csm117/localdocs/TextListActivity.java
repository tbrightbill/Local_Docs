package csm117.localdocs;

/**
 * Created by Jae on 2/4/2016.
 */



// looking up things I might need to add
/*
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;


// Writing file
// in order to use internal storage to write some data in the file, call the openFileOutput() method
FileOutputStream fOut = openFileOutput("file name here", MODE_WORLD_READABLE);

// the method openFileOutput() returns as instance of FileOutputStream. So you receive it in the object of
// FileInputStream.
String str = "data";
fOut.write(str.getBytes());
fOut.close();

// Reading file
//  In order to read from the file you just created, call the openFileInput() method with the name of the file
FileInputStream fin = openFileInput(file);

// you can call read method to read one character at a time from the file and then you can print it.
int c;
String temp = "";
while ((c = fin.read()) != -1){
    temp = temp + Character.toString((char)c);
}

// string temp contains all the data of the file
fin.close();
 */


/*
public class TextListActivity extends activity{

    //get files in storage

    public ArrayList<String> GetFiles(String DirectoryPath){
        ArrayList<String> MyFiles = new ArrayList<String>();
        File f = new File(DirectoryPath);

        f.mkdirs();
        File[] files = f.listFiles();
        if (files.length == 0)
            return null;
        else {
            for (int i=0; i<files.length; i++)
                MyFiles.add(files[i].getName());
        }

        return MyFiles;
    }

    @Override
    public void onCreate() {
    // Other Code

        ListView lv;
        ArrayList<String> FilesInFolder = GetFiles("/sdcard/somefolder");
        lv = (ListView)findViewById(R.id.filelist);

        lv.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, FilesInFolder));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Clicking on items
            }
        });
    }
}


Read.setOnClickListener(new View.OnClickListener() {

            //private Context context;

            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), MessageBox.class);
                // TODO Auto-generated method stub
                //Intent intent = new Intent(context,MessageBox.class);
                 try{
                     FileInputStream fin = openFileInput(file);
                     int c;
                     String temp="";
                     while( (c = fin.read()) != -1){
                        temp = temp + Character.toString((char)c);
                        Intent in = new Intent(getApplicationContext(),data.class);

                        //String msg = null;
                        in.putExtra("Msg_Detail", temp);
                     startActivity(in);

                    // et.setText(temp);
                     Toast.makeText(getBaseContext(),"file read",
                     Toast.LENGTH_SHORT).show();
                     }


                  }catch(Exception e){

                  }
        }
    });}

     public void save(View view){
          data = tv.getText().toString();
          try {
             FileOutputStream fOut = openFileOutput(file,MODE_WORLD_READABLE);
             fOut.write(data.getBytes());
             fOut.close();
             Toast.makeText(getBaseContext(),"file saved",
             Toast.LENGTH_SHORT).show();
          } catch (Exception e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
          }
       }
**data.java**
setContentView(R.layout.data);
        et11 = (EditText)(findViewById(R.id.eText123));
        Intent intent = getIntent();
        String msg = intent.getStringExtra("Msg_Detail");
        //String msg = intent.getExtras().getString("Msg_Detail");
        ((EditText)findViewById(R.id.eText123)).setText(msg);
        //et11.setText(msg);





*/
