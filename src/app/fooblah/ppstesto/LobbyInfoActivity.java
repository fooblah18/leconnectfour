package app.fooblah.ppstesto;

import com.llsx.pps.PpsManager;
import com.llsx.pps.session.SessionManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class LobbyInfoActivity extends Activity
{
    private String sessionName = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_lobby_info);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            sessionName = extras.getString("SessionName");
        }
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
        getMenuInflater().inflate(R.menu.menu_lobby_info, menu);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if      (id == R.id.start_game      )
        {
            startActivity(new Intent(this, GameActivity.class));
            finish();

            return true;
        }
        else if (id == R.id.lobby_info_lock )
        {
            if (SessionManager.getInstance().isSessionLocked(sessionName))
            {
                SessionManager.getInstance().unlockSession  (sessionName);
            }
            else
            {
                SessionManager.getInstance().lockSession    (sessionName);
            }
            
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
