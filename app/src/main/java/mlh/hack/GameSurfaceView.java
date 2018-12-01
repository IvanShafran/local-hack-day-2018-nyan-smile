package mlh.hack;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final int speedMult = 4;

    private SurfaceHolder surfaceHolder = null;


    private Random random = new Random(System.currentTimeMillis());

    private Thread thread = null;

    private Paint backgroundPaint = new Paint();
    private Paint textPaint;

    // Record whether the child thread is running or not.
    private boolean threadRunning = false;

    private Canvas canvas = null;

    private int screenWidth = 0;

    private int screenHeight = 0;

    private volatile static boolean isSmiling = false;

    private volatile static boolean isBlink = false;

    private Bitmap background;
    private Bitmap backgroundNight;
    private Bitmap notSmileBlink;
    private Bitmap smileBlink;
    private Bitmap notSmileNotBlink;
    private Bitmap smileNotBlink;
    private Bitmap cloudOne;
    private int cloudOneX;
    private Bitmap cloudTwo;
    private int cloudTwoX = 100;
    private Bitmap cloudThree;
    private int cloudThreeX = 300;

    private Bitmap cake;
    private Bitmap cakePic;
    private Bitmap bottle;
    private int cakeX = -100;
    private boolean isCakeUp = false;

    private Bitmap nyanCat1;
    private Bitmap nyanCat2;
    private int nyanCatFrame = 0;
    private int nyanCatX = 30;

    private Bitmap dog1;
    private Bitmap dog2;
    private int dogFrame = 0;
    private boolean isDogUp = true;
    private int dogX = -300;

    private Bitmap road;
    private int roadX = 0;

    private int score = 0;

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

        // Create the textPaint object which will draw the text.
        textPaint = new Paint();
        textPaint.setTextSize(100);
        textPaint.setColor(Color.RED);

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


        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.background_night);
        backgroundNight = Bitmap.createScaledBitmap(raw, screenWidth, screenHeight, true);

        int sunSize = screenHeight / 4;
        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sun_not_smile_blink);
        notSmileBlink = Bitmap.createScaledBitmap(raw, sunSize, sunSize, true);

        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sun_not_smile_not_blink);
        notSmileNotBlink = Bitmap.createScaledBitmap(raw, sunSize, sunSize, true);

        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sun_smile_not_blink);
        smileNotBlink = Bitmap.createScaledBitmap(raw, sunSize, sunSize, true);

        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sun_smile_blink);
        smileBlink = Bitmap.createScaledBitmap(raw, sunSize, sunSize, true);

        int cloudOneWidth = screenWidth / 10;
        int cloudOneHeight = screenHeight / 10;
        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.cloud_1);
        cloudOne = Bitmap.createScaledBitmap(raw, cloudOneWidth, cloudOneHeight, true);

        int cloudTwoWidth = screenWidth / 20;
        int cloudTwoHeight = screenHeight / 20;
        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.cloud_2);
        cloudTwo = Bitmap.createScaledBitmap(raw, cloudTwoWidth, cloudTwoHeight, true);

        int cloudThreeWidth = screenWidth / 15;
        int cloudThreeHeight = screenHeight / 15;
        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.cloud_3);
        cloudThree = Bitmap.createScaledBitmap(raw, cloudThreeWidth, cloudThreeHeight, true);

        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.nyan_cat_1);
        nyanCat1 = Bitmap.createScaledBitmap(raw, screenWidth / 5, screenHeight / 5, true);

        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.nyan_cat_2);
        nyanCat2 = Bitmap.createScaledBitmap(raw, screenWidth / 5, screenHeight / 5, true);

        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.dog_1);
        dog1 = Bitmap.createScaledBitmap(raw, screenWidth / 5, screenHeight / 5, true);

        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.dog_2);
        dog2 = Bitmap.createScaledBitmap(raw, screenWidth / 5, screenHeight / 5, true);

        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.cake);
        cakePic = Bitmap.createScaledBitmap(raw, screenWidth / 10, screenHeight / 10, true);
        cake = cakePic;

        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.bottle);
        bottle = Bitmap.createScaledBitmap(raw, screenWidth / 10, screenHeight / 10, true);

        raw = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.road);
        road = Bitmap.createScaledBitmap(raw, screenWidth * 2, screenHeight / 20, true);
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
            drawRoad();
            drawClouds();
            drawSun();
            drawScore();
            drawCake();
            drawNyanCat();
            drawDog();

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

    private void drawClouds() {
        canvas.drawBitmap(cloudOne, cloudOneX, screenHeight / 10, null);
        cloudOneX -= 2 * speedMult;
        if (cloudOneX < -100) {
            cloudOneX = screenWidth;
        }

        canvas.drawBitmap(cloudTwo, cloudTwoX, screenHeight / 15, null);
        cloudTwoX -= 4 * speedMult;
        if (cloudTwoX < -100) {
            cloudTwoX = screenWidth;
        }

        canvas.drawBitmap(cloudThree, cloudThreeX, screenHeight / 5, null);
        cloudThreeX -= 3 * speedMult;
        if (cloudThreeX < -100) {
            cloudThreeX = screenWidth;
        }
    }

    private void drawBackground() {
        if (isBlink) {
            canvas.drawBitmap(backgroundNight, 0, 0, null);
        } else {
            canvas.drawBitmap(background, 0, 0, null);
        }
    }

    private void drawScore() {
        String text = "Score: " + score;
        canvas.drawText(text, 20, textPaint.getTextSize(), textPaint);
    }

    private void drawNyanCat() {
        float y = getYForRoad(isSmiling);

        nyanCatFrame += 1;
        nyanCatFrame %= 10;

        Bitmap nyanCat;
        if (nyanCatFrame < 5) {
            nyanCat = nyanCat1;
        } else {
            nyanCat = nyanCat2;
        }
        canvas.drawBitmap(nyanCat, nyanCatX, y, null);
    }

    private void drawCake() {
        float y = getYForRoad(isCakeUp) + screenHeight / 20;

        canvas.drawBitmap(cake, cakeX, y, null);

        float nyanCatHead = nyanCatX + nyanCat1.getWidth();
        if (nyanCatHead - 5 > cakeX && nyanCatX < cakeX && isSmiling == isCakeUp) {
            cakeX = -200;
            if (isBlink) {
                score += 2;
            } else {
                score += 1;
            }
            return;
        }

        cakeX -= 3 * speedMult;
        if (cakeX < -500) {
            cakeX = screenWidth;

            isCakeUp = random.nextBoolean();

            if (random.nextBoolean()) {
                cake = cakePic;
            } else {
                cake = bottle;
            }
        }
    }

    private void drawDog() {
        float y = getYForRoad(isDogUp);

        Bitmap dog;

        dogFrame += 1;
        dogFrame %= 10;
        if (dogFrame < 5) {
            dog = dog1;
        } else {
            dog = dog2;
        }

        canvas.drawBitmap(dog, dogX, y, null);

        float nyanCatHead = nyanCatX + nyanCat1.getWidth();
        if (nyanCatHead - 5 > dogX && nyanCatX < dogX && isSmiling == isDogUp) {
            dogX = -300;
            if (isBlink) {
                score -= 2;
            } else {
                score -= 1;
            }
            return;
        }

        dogX -= 4 * speedMult;
        if (dogX < -600) {
            dogX = screenWidth;
            isDogUp = random.nextBoolean();
        }
    }

    private void drawRoad() {
        canvas.drawBitmap(road, roadX, screenHeight * 0.65f, null);
        roadX -= 3 * speedMult;
        if (roadX < -screenWidth) {
            roadX = 0;
        }
    }

    private float getYForRoad(boolean isUp) {
        if (isUp) {
            return screenHeight * 0.45f;
        } else {
            return screenHeight * 0.68f;
        }
    }

}
