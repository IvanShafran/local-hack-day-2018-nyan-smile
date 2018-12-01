package mlh.hack;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder surfaceHolder = null;

    private Thread thread = null;

    private Paint paint;

    // Record whether the child thread is running or not.
    private boolean threadRunning = false;

    private Canvas canvas = null;

    private float textX = 0;

    private float textY = 0;

    private String text = "Hello!";

    private int screenWidth = 0;

    private int screenHeight = 0;

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context);

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
            if (TextUtils.isEmpty(text)) {
                text = "Input text in above text box.";
            }

            long startTime = System.currentTimeMillis();

            textX += 100;

            textY += 100;

            if (textX > screenWidth) {
                textX = 0;
            }

            if (textY > screenHeight) {
                textY = 0;
            }

            drawText();

            long endTime = System.currentTimeMillis();

            long deltaTime = endTime - startTime;

            if (deltaTime < 200) {
                try {
                    Thread.sleep(200 - deltaTime);
                } catch (InterruptedException ex) {

                }

            }
        }
    }

    private void drawText() {
        int margin = 100;

        int left = margin;

        int top = margin;

        int right = screenWidth - margin;

        int bottom = screenHeight - margin;

        Rect rect = new Rect(left, top, right, bottom);

        // Only draw text on the specified rectangle area.
        canvas = surfaceHolder.lockCanvas(rect);

        // Draw the specify canvas background color.
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLUE);
        canvas.drawRect(rect, backgroundPaint);

        // Draw text in the canvas.
        canvas.drawText(text, textX, textY, paint);

        // Send message to main UI thread to update the drawing to the main view special area.
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
