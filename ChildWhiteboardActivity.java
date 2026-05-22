package com.example.mathsprout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildWhiteboardActivity extends AppCompatActivity {

    private WhiteboardView whiteboardView;
    private CardView toolbarCard;
    private View c_colorBlack, c_colorRed, c_colorBlue;
    private SeekBar c_widthSeek;
    private ImageButton c_undoBtn, c_clearBtn;

    private String sessionCode;
    private DatabaseReference sessionRootRef, sessionRef, commandsRef, permissionsRef;
    private boolean canDraw = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_whiteboard);

        whiteboardView = findViewById(R.id.whiteboardView);
        toolbarCard = findViewById(R.id.toolbarCard);
        c_colorBlack = findViewById(R.id.c_colorBlack);
        c_colorRed = findViewById(R.id.c_colorRed);
        c_colorBlue = findViewById(R.id.c_colorBlue);
        c_widthSeek = findViewById(R.id.c_widthSeek);
        c_undoBtn = findViewById(R.id.c_undoBtn);
        c_clearBtn = findViewById(R.id.c_clearBtn);

        sessionCode = getIntent().getStringExtra("SESSION_CODE");
        if (sessionCode == null) {
            finish();
            return;
        }

        sessionRootRef = FirebaseDatabase.getInstance().getReference("whiteboards").child(sessionCode);
        sessionRef = sessionRootRef.child("strokes");
        commandsRef = sessionRootRef.child("commands");
        permissionsRef = sessionRootRef.child("permissions");

        setupSync();
        setupPermissions();
        setupToolbar();

        sessionRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(ChildWhiteboardActivity.this, "Session has ended by educator", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ChildWhiteboardActivity.this, ChildMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupSync() {
        whiteboardView.setStrokeListener((path, paint) -> {
            if (!canDraw) return;
            List<Float> pts = PathUtils.convertPathToPoints(path);
            Map<String, Object> stroke = new HashMap<>();
            stroke.put("points", pts);
            stroke.put("color", paint.getColor());
            stroke.put("strokeWidth", paint.getStrokeWidth());
            stroke.put("userType", "child");
            sessionRef.push().setValue(stroke);
        });

        sessionRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ds, String s) {
                Map<String, Object> map = (Map<String, Object>) ds.getValue();
                if (map == null || "child".equals(map.get("userType"))) return;

                List<Object> pRaw = (List<Object>) map.get("points");
                List<Float> pts = new ArrayList<>();
                if (pRaw != null) {
                    for (Object obj : pRaw) {
                        if (obj instanceof Number) {
                            pts.add(((Number) obj).floatValue());
                        }
                    }
                }

                Path p = PathUtils.convertPointsToPath(pts);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(((Number) map.get("color")).intValue());
                paint.setStrokeWidth(((Number) map.get("strokeWidth")).floatValue());
                whiteboardView.addRemoteStroke(p, paint);
            }
            @Override public void onChildChanged(@NonNull DataSnapshot ds, String s) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot ds) { whiteboardView.clearBoard(); }
            @Override public void onChildMoved(@NonNull DataSnapshot ds, String s) {}
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });

        commandsRef.addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(@NonNull DataSnapshot ds, String s) {
                String type = ds.child("type").getValue(String.class);
                if ("CLEAR".equals(type)) whiteboardView.clearBoard();
                if ("UNDO".equals(type)) whiteboardView.undo();
            }
            @Override public void onChildChanged(@NonNull DataSnapshot ds, String s) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot ds) {}
            @Override public void onChildMoved(@NonNull DataSnapshot ds, String s) {}
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void setupPermissions() {
        permissionsRef.child("canChildDraw").addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot ds) {
                canDraw = Boolean.TRUE.equals(ds.getValue(Boolean.class));
                whiteboardView.setEnabled(canDraw);
                if (toolbarCard != null) {
                    toolbarCard.setVisibility(canDraw ? View.VISIBLE : View.GONE);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void setupToolbar() {
        c_colorBlack.setOnClickListener(v -> whiteboardView.setColor(Color.BLACK));
        c_colorRed.setOnClickListener(v -> whiteboardView.setColor(Color.RED));
        c_colorBlue.setOnClickListener(v -> whiteboardView.setColor(Color.BLUE));
        c_widthSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean b) {
                whiteboardView.setStrokeWidth(Math.max(2f, p));
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });
        c_undoBtn.setOnClickListener(v -> whiteboardView.undo());
        c_clearBtn.setOnClickListener(v -> whiteboardView.clearBoard());
    }
}