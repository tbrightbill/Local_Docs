package csm117.localdocs;

/**
 * Created by Jae on 2/4/2016.
 */

public class TextListActivity extends activity{
    /*
    get files in storage
     */
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

