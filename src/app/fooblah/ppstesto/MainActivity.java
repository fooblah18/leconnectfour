package app.fooblah.ppstesto;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.llsx.pps.PpsManager;
import com.llsx.pps.session.SessionManager;

public class MainActivity extends Activity
{
    private Button button_board;
    private Button button_player;

    public PpsManager ppsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ppsManager      = new PpsManager(this, false, PpsManager.SESSION_MODE);

        button_board    = (Button) findViewById(R.id.selector_board);
        button_player   = (Button) findViewById(R.id.selector_player);

        button_board.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String deviceName = ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    
                ppsManager.setScreenType(PpsManager.PUBLIC);
                SessionManager.getInstance().setDeviceName(deviceName);
    
                gotoLobby();
            }
        });

        button_player.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder     = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater      inflater    = MainActivity.this.getLayoutInflater();
    
                final View          view        = inflater.inflate(R.layout.dialog_name, null);
    
                builder.setView(view);
    
                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        EditText text = (EditText)view.findViewById(R.id.dialog_name_text);
    
                        ppsManager.setScreenType(PpsManager.PRIVATE);
                        SessionManager.getInstance().setDeviceName(text.getText().toString());
    
                        gotoLobby();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
    
                    }
                });
                builder.setCancelable(false);
                builder.create().show();
            }
        });
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
    
    private void gotoLobby()
    {
        startActivity(new Intent(this, LobbyActivity.class));
        finish();
    }
}
