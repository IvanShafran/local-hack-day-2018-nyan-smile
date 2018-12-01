package mlh.hack;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder surfaceHolder = null;

    private Thread thread = null;

    private Paint backgroundPaint = new Paint();
    private Paint paint;

    // Record whether the child thread is running or not.
    private boolean threadRunning = false;

    private Canvas canvas = null;

    private int screenWidth = 0;

    private int screenHeight = 0;

    private volatile static boolean isSmiling = false;

    private volatile static boolean isBlink = false;

    public static void setMimicks(boolean isSmilingNew, boolean isBlinkNew) {
        isSmiling = isSmilingNew;
        isBlink = isBlinkNew;
    }

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context);

        backgroundPaint.setColor(Color.BLUE);
        setFocusable(true);

        // Get SurfaceHolder object.
        surfaceHolder = this.getHolder();
        // Add current object as the callback listener.
        surfaceHolder.addCallback(this);

        // Create the paint object which will draw the text.
        paint = new Paint();
        paint.setTextSize(100);
        paint.setColor(Color.GREEN);

        // Set the SurfaceView object at the top of View object.
        setZOrderOnTop(true);

        //setBackgroundColor(Color.RED);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        // Create the child thread when SurfaceView is created.
        thread = new Thread(this);
        // Start to run the child thread.
        thread.start();
        // Set thread running flag to true.
        threadRunning = true;

        // Get screen width and height.
        screenHeight = getHeight();
        screenWidth = getWidth();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        // Set thread running flag to false when Surface is destroyed.
        // Then the thread will jump out the while loop and complete.
        threadRunning = false;
    }

    @Override
    public void run() {
        while (threadRunning) {
            // Only draw text on the specified rectangle area.
            canvas = surfaceHolder.lockCanvas();

            drawBackground();
            drawText();

            // Send message to main UI thread to update the drawing to the main view special area.
            surfaceHolder.unlockCanvasAndPost(canvas);
            try {
                Thread.sleep(16);
            } catch (InterruptedException ex) {

            }
        }
    }

    private void drawBackground() {
        Rect rect = new Rect(0, 0, screenWidth, screenHeight);

        canvas.drawRect(rect, backgroundPaint);
    }

    private void drawText() {
        String text = Boolean.valueOf(isSmiling) + " " + Boolean.valueOf(isBlink);
        // Draw text in the canvas.
        canvas.drawText(text, 100, 100, paint);
    }
}
