package com.vmpk.codequerst.Custom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.vmpk.codequerst.Activity.LevelQuizActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZigZagView extends View {
    private Paint linePaint, circlePaint, textPaint;
    private Path path;
    private static final int TOTAL_LEVELS = 100;
    private float verticalGap = 220f;

    private static class CirclePoint {
        float x, y;
        int level;
        CirclePoint(float x, float y, int level) {
            this.x = x; this.y = y; this.level = level;
        }
    }

    private final List<CirclePoint> circles = new ArrayList<>();
    private final Map<Integer, Boolean> levelStatus = new HashMap<>();

    private Context context;
    private boolean dataLoaded = false;

    public ZigZagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
        loadUserProgress();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(10f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);

        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(70f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        textPaint.setAntiAlias(true);

        path = new Path();
    }

    private void loadUserProgress() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            levelStatus.clear();
            levelStatus.put(1, true);
            dataLoaded = true;
            invalidate();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("userProgress")
                .child(user.getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                levelStatus.clear();
                for (int i = 1; i <= TOTAL_LEVELS; i++) {
                    Boolean completed = snapshot.child("level_" + i + "_complete").getValue(Boolean.class);
                    levelStatus.put(i, completed != null && completed);
                }

                // Ensure level 1 is always unlocked
                if (!levelStatus.containsKey(1)) {
                    levelStatus.put(1, true);
                }

                dataLoaded = true;
                invalidate(); // redraw the view so unlocked levels appear
            }

            @Override
            public void onCancelled(DatabaseError error) {
                dataLoaded = true;
                invalidate();
            }
        });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!dataLoaded) return;

        circles.clear();
        float width = getWidth();
        float leftX = width * 0.2f;
        float rightX = width * 0.8f;
        float midRightX = width * 0.7f;
        float midLeftX = width * 0.3f;
        float startY = getHeight() - 200f;

        path.reset();
        path.moveTo(leftX, startY);
        boolean moveRight = true;
        for (int i = 1; i < TOTAL_LEVELS; i++) {
            boolean isLong = (i % 2 != 0);
            float x = moveRight ? (isLong ? rightX : midRightX) : (isLong ? leftX : midLeftX);
            float y = startY - (verticalGap * i);
            path.lineTo(x, y);
            moveRight = !moveRight;
        }
        canvas.drawPath(path, linePaint);

        moveRight = false;
        for (int i = 0; i < TOTAL_LEVELS; i++) {
            boolean isLong = (i % 2 != 0);
            float x = moveRight ? (isLong ? rightX : midRightX) : (isLong ? leftX : midLeftX);
            float y = startY - (verticalGap * i);
            int levelNum = i + 1;

            boolean unlocked = (levelNum == 1) || levelStatus.getOrDefault(levelNum - 1, false);
            boolean completed = levelStatus.getOrDefault(levelNum, false);

            if (completed) circlePaint.setColor(Color.parseColor("#4CAF50")); // Green for completed
            else if (unlocked) circlePaint.setColor(Color.parseColor("#FF4081")); // Pink for unlocked
            else circlePaint.setColor(Color.GRAY); // Locked

            canvas.drawCircle(x, y, 90, circlePaint);
            canvas.drawText(String.valueOf(levelNum), x, y + 25, textPaint);
            circles.add(new CirclePoint(x, y, levelNum));
            moveRight = !moveRight;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!dataLoaded) return true;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();

            for (CirclePoint point : circles) {
                float dx = touchX - point.x;
                float dy = touchY - point.y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance <= 100) {
                    handleLevelClick(point.level);
                    return true;
                }
            }
        }
        return true;
    }

    private void handleLevelClick(int levelNumber) {
        if (levelNumber == 1 || levelStatus.getOrDefault(levelNumber - 1, false)) {
            Intent intent = new Intent(context, LevelQuizActivity.class);
            intent.putExtra("levelNumber", levelNumber);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredHeight = (int) (TOTAL_LEVELS * verticalGap + 800);
        int height = resolveSize(desiredHeight, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
