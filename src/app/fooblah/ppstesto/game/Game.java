package app.fooblah.ppstesto.game;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Canvas;

import app.fooblah.ppstesto.data.Board;

public class Game
{
    private Board   state;
    private View    parentView;

    private long    delta;
    private Paint   textPaint;

    private Paint   boardPaint;
    private Paint   chipPaint;

    private float   offsetX;
    private float   offsetY;

    public          Game            (View parentView)
    {
        this.state      = new Board();
        this.parentView = parentView;

        this.parentView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                // set events for this shit
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if ((event.getX() > offsetX && event.getX() <= offsetX + 420) &&
                        (event.getY() > offsetY && event.getY() <= offsetY + 490))
                    {
                        Log.i("GameGame", "Board area touched!");
                    }
                }

                return true;
            };
        });
    }

    public void     init            ()
    {
        delta = 0;

        textPaint = new Paint();

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(25);

        textPaint.setHinting(Paint.HINTING_ON);
        textPaint.setAntiAlias(true);

        boardPaint = new Paint();

        boardPaint.setColor(Color.LTGRAY);

        chipPaint = new Paint();

        chipPaint.setAntiAlias(true);
        chipPaint.setColor(Color.BLUE);
    }

    public void     draw            (Canvas canvas)
    {
        offsetX =  (canvas.getWidth () - 420) / 2;
        offsetY = ((canvas.getHeight() - 490) / 2) + 50;

        _drawBoard      (canvas);

        _drawPlayers    (canvas);
    }

    public void     update          (long delta)
    {
        this.delta = delta;
    }


    private void    _drawChips      (Canvas canvas)
    {
        for (int y = 0; y < 7; y++)
        {
            for (int x = 0; x < 6; x++)
            {
                canvas.drawCircle(x * 70 + 35, y * 70 + 35, 30, chipPaint);
            }
        }
    }

    private void    _drawBoard      (Canvas canvas)
    {
        canvas.save();

        canvas.translate(offsetX, offsetY);

        canvas.drawRect(0, 0, 420, 490, boardPaint);

        _drawChips(canvas);

        canvas.restore();
    }

    private void    _drawPlayers    (Canvas canvas)
    {
        canvas.save();

        canvas.translate(0, 40);

        canvas.drawText("Player 1: <P1 name>", 10,  0, textPaint);
        canvas.drawText("Player 2: <P2 name>", 10, 50, textPaint);

        canvas.restore();
    }

    private void    _onTap          (float x, float y)
    {

    }
}
