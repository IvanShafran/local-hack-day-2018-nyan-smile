package mlh.hack;

import android.content.Context;
import android.graphics.*;
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

    private Bitmap background;
    private Bitmap notSmileBlink;
    private Bitmap smileBlink;
    private Bitmap notSmileNotBlink;
    private Bitmap smileNotBlink;

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

    private void loadBitmapsIfNeeded() {
        if (background != null) {
            return;
        }

        Bitmap raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(raw, screenWidth, screenHeight, true);

        int sunSize = screenHeight / 4;
        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sun_not_smile_blink);
        notSmileBlink = Bitmap.createScaledBitmap(raw, sunSize, sunSize, true);

        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sun_not_smile_not_blink);
        notSmileNotBlink = Bitmap.createScaledBitmap(raw, sunSize, sunSize, true);

        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sun_smile_not_blink);
        smileNotBlink = Bitmap.createScaledBitmap(raw, sunSize, sunSize, true);

        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sun_smile_blink);
        smileBlink = Bitmap.createScaledBitmap(raw, sunSize, sunSize, true);
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

            loadBitmapsIfNeeded();
            drawBackground();
            drawSun();

            // Send message to main UI thread to update the drawing to the main view special area.
            surfaceHolder.unlockCanvasAndPost(canvas);
            try {
                Thread.sleep(16);
            } catch (InterruptedException ex) {

            }
        }
    }

    private void drawSun() {
        Bitmap sun = smileNotBlink;

        if (isSmiling && isBlink) {
            sun = smileBlink;
        }

        if (isSmiling && !isBlink) {
            sun = smileNotBlink;
        }

        if (!isSmiling && isBlink) {
            sun = notSmileBlink;
        }

        if (!isSmiling && !isBlink) {
            sun = notSmileNotBlink;
        }

        canvas.drawBitmap(sun, screenWidth - sun.getWidth(), 0, null);
    }

    private void drawBackground() {
        canvas.drawBitmap(background, 0, 0, null);
    }
}
