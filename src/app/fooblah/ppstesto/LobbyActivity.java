package app.fooblah.ppstesto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import app.fooblah.ppstesto.data.LobbyEntry;

import com.llsx.pps.PpsManager;
import com.llsx.pps.event.Event;
import com.llsx.pps.event.EventHandler;
import com.llsx.pps.event.EventManager;
import com.llsx.pps.session.SessionManager;

public class LobbyActivity extends Activity
{
    private class LobbyEvents implements EventHandler
    {
        @Override
        public void handleEvent(Event event)
        {
            switch (event.getType())
            {
            case Event.T_ADD_NEW_SESSION:
            {

                Map<String, Boolean>                sessions    = SessionManager.getInstance().getAvailableSessionsMap();
                Iterator<Entry<String, Boolean>>    sessionsIt  = sessions.entrySet().iterator();
                
                lobbyList.clear();
                
                while (sessionsIt.hasNext())
                {
                    Map.Entry<String, Boolean>  entry       = sessionsIt.next();
                    LobbyEntry                  newEntry    = new LobbyEntry();
                    
                    newEntry.Name       = entry.getKey();
                    newEntry.Private    = entry.getValue();
                    
                    lobbyList.add(newEntry);
                }
                
                lobbyAdapter.notifyDataSetChanged();
                
            }   break;
            }
        }
    }
    
    private ListView                    lobby_list;

    private ArrayList<LobbyEntry>       lobbyList;
    private ArrayAdapter<LobbyEntry>    lobbyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        lobbyList       = new ArrayList<LobbyEntry>();
        lobbyAdapter    = new ArrayAdapter<LobbyEntry>(this, android.R.layout.simple_list_item_1, lobbyList);

        lobby_list = (ListView) findViewById(R.id.lobby_list);
        lobby_list.setAdapter(lobbyAdapter);

        lobby_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // check if session exists
                LobbyEntry  entry = lobbyList.get(position);
                boolean     found = false;
                
                for (String sessionName : SessionManager.getInstance().getAvailableSessions())
                {
                    if (entry.Name == sessionName)
                    {
                        found = true;
                        break;
                    }
                }
                
                if (found)
                {
                    SessionManager.getInstance().setChosenSession(entry.Name);
                    
                    Intent intent = new Intent(LobbyActivity.this, LobbyInfoActivity.class);
                    intent.putExtra("SessionName", entry.Name);
                    
                    startActivity(intent);
                }
                else
                {
                    new AlertDialog.Builder(LobbyActivity.this)
    
                        .setTitle           ("Lobby not found")
                        .setMessage         ("I can't seem to find the lobby you want to join to..")
                        
                        .setPositiveButton  (android.R.string.ok, new OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                
                            }
                        })
                        .create             ()
                        .show               ();
                }
            }
        });
        
        EventManager.getInstance().setEventHandler(new LobbyEvents());
    }

    @Override
    protected void onPause()
    {        
        super.onPause();
        
        PpsManager.getInstance().stop();
    }
    @Override
    protected void onResume()
    {
        super.onResume();

        PpsManager.getInstance().setSessionMode(PpsManager.SESSION_MODE);
        PpsManager.getInstance().start();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_lobby, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if      (id == R.id.lobby_addLobby)
        {
            AlertDialog.Builder builder     = new AlertDialog.Builder(this);
            LayoutInflater      inflater    = this.getLayoutInflater();

            final View          view        = inflater.inflate(R.layout.dialog_new_lobby, null);
            
            builder.setView             (view);
            builder.setPositiveButton	("Create", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    try
                    {
                        EditText    editText    = (EditText) view.findViewById(R.id.dialog_new_lobby_text);
                        String      session     = SessionManager.getInstance().createSession(editText.getText().toString()); 
                        
                        if (session == null)
                        {
                            new AlertDialog.Builder(LobbyActivity.this)
                            
                                .setTitle           ("Error creating lobby!")
                                .setMessage         ("Lobby already exists! Please specify another name for your lobby.")
                                
                                .setPositiveButton  (android.R.string.ok, new OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        
                                    }
                                })
                                .create             ()
                                .show               ();
                        }
                        else
                        {
                            SessionManager.getInstance().setChosenSession(session);
                            

                            Intent intent = new Intent(LobbyActivity.this, LobbyInfoActivity.class);
                            intent.putExtra("SessionName", session);

                            startActivity(intent);
                        }
                    }
                    catch (Exception ex)
                    {
                        Log.e("ConnectFour", ex.getMessage(), ex);
                    }
                }
            });
            builder.setNegativeButton	("Cancel", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {

                }
            });
            
            builder.setCancelable       (true);
            
            builder.create              ();
            builder.show                ();
            
            return true;
        }
        else if (id == R.id.lobby_refresh)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
