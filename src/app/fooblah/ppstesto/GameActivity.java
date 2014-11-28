package app.fooblah.ppstesto;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import app.fooblah.ppstesto.data.Board;
import app.fooblah.ppstesto.game.Game;

public class GameActivity extends Activity implements SurfaceHolder.Callback
{
    SurfaceView surface;
    Thread      gameThread;

    Game        game;

    boolean     running;

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        surface = (SurfaceView) findViewById(R.id.gameSurface);
        surface.getHolder().addCallback(this);

        game        = new Game(surface);

        gameThread  = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SurfaceHolder holder = surface.getHolder();

                long curTime = 0;
                long oldTime;

                Paint clearPaint = new Paint();
                clearPaint.setColor(Color.WHITE);

                game.init();

                while (running)
                {
                    oldTime = curTime;
                    curTime = System.currentTimeMillis();

                    long delta = curTime - oldTime;

                    Canvas canvas = holder.lockCanvas();

                    if (canvas == null) continue;

                    canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), clearPaint);

                    game.update(delta);
                    game.draw(canvas);

                    holder.unlockCanvasAndPost(canvas);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id)
        {
        default:
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        running = true;
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        running = false;

        try
        {
            gameThread.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
