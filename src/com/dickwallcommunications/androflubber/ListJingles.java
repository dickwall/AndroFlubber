package com.dickwallcommunications.androflubber;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListJingles extends ListActivity {

	private String[] mJingles;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	// get a list of files from the Jingles directory
    	File jinglesDir = AndroFlubber.getJinglesDirectory();
    	
    	String[] jingles = jinglesDir.list();
    	mJingles = jingles;
    	
    	if (jingles.length == 0) {
    		showEmptyJinglesMessage(jinglesDir.getAbsolutePath());
    	}
	
		// Bind the results of the search into the list
		ListAdapter adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, jingles);
		
		setListAdapter(adapter);
    }

    /**
     * Override the onListItemClick to open the wiki note to view when it is
     * selected from the list.
     */
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
    	String jingleName = mJingles[position];
    	
    	File jingleFile = new File(AndroFlubber.getJinglesDirectory(), jingleName);
    	
    	MediaPlayer player = new MediaPlayer();
    	
    	player.reset();
    	try {
    		player.setDataSource(jingleFile.getAbsolutePath());
    		player.prepare();
    		player.start();
    		finish();
    	}
    	catch (IOException ex) {
        	Context context = getApplicationContext();
        	CharSequence text = "ERROR: Failed to play " + jingleName;
        	int duration = Toast.LENGTH_LONG;

        	Toast toast = Toast.makeText(context, text, duration);
        	toast.show();
    	}
    }
    
    private void showEmptyJinglesMessage(String path) {
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);  
    	  
    	alert.setTitle("No Jingles Available");  
    	alert.setMessage("To play jingles, please put some audio files in " + path);  
    	  
    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
    		public void onClick(DialogInterface dialog, int whichButton) {    
    			finish();
    		}  
    	});
    	  
    	alert.show();
    }
	
}
