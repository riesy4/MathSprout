package com.example.mathsprout;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.database.*;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EducatorLiveWhiteboardActivity extends AppCompatActivity {

    private WhiteboardView whiteboardView;
    private CardView qrPanel;
    private View e_colorBlack, e_colorRed, e_colorBlue;
    private SeekBar e_widthSeek;
    private Button allowChildBtn, showQrBtn, endSessionBtn;
    private ImageButton e_undoBtn, e_clearBtn;
    private TextView sessionCodeText;
    private ImageView qrImage;
    private DatabaseReference sessionRootRef, sessionRef, permissionsRef, commandsRef;
    private String sessionCode;
    private boolean isQrVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educator_live_whiteboard);

        whiteboardView = findViewById(R.id.whiteboardView);
        qrPanel = findViewById(R.id.qrPanel);
        sessionCodeText = findViewById(R.id.sessionCodeText);
        qrImage = findViewById(R.id.qrImage);
        e_colorBlack = findViewById(R.id.e_colorBlack);
        e_colorRed = findViewById(R.id.e_colorRed);
        e_colorBlue = findViewById(R.id.e_colorBlue);
        e_widthSeek = findViewById(R.id.e_widthSeek);
        e_undoBtn = findViewById(R.id.e_undoBtn);
        e_clearBtn = findViewById(R.id.e_clearBtn);
        allowChildBtn = findViewById(R.id.allowChildBtn);
        showQrBtn = findViewById(R.id.showQrBtn);
        endSessionBtn = findViewById(R.id.endSessionBtn);

        createSession();
        setupToolbar();
        setupSync();
    }

    private void createSession() {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference("whiteboards");
        String key = root.push().getKey();
        sessionCode = "MS-" + key.substring(0, 6).toUpperCase();
        sessionCodeText.setText("Session Code: " + sessionCode);

        sessionRootRef = root.child(sessionCode);
        sessionRef = sessionRootRef.child("strokes");
        permissionsRef = sessionRootRef.child("permissions");
        commandsRef = sessionRootRef.child("commands");

        permissionsRef.child("canChildDraw").setValue(false);
        generateQrCode();
    }

    private void setupSync() {
        whiteboardView.setStrokeListener((path, paint) -> {
            List<Float> pts = PathUtils.convertPathToPoints(path);
            Map<String, Object> stroke = new HashMap<>();
            stroke.put("points", pts);
            stroke.put("color", paint.getColor());
            stroke.put("strokeWidth", paint.getStrokeWidth());
            stroke.put("userType", "educator");
            sessionRef.push().setValue(stroke);
        });

        sessionRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ds, String s) {
                Map<String, Object> map = (Map<String, Object>) ds.getValue();
                if (map == null || "educator".equals(map.get("userType"))) return;

                // FIX: Use List<Object> and Number to avoid ClassCastException
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
                paint.setColor(((Number)map.get("color")).intValue());
                paint.setStrokeWidth(((Number)map.get("strokeWidth")).floatValue());
                whiteboardView.addRemoteStroke(p, paint);
            }
            @Override public void onChildChanged(@NonNull DataSnapshot ds, String s) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot ds) { whiteboardView.clearBoard(); }
            @Override public void onChildMoved(@NonNull DataSnapshot ds, String s) {}
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void setupToolbar() {
        e_colorBlack.setOnClickListener(v -> whiteboardView.setColor(Color.BLACK));
        e_colorRed.setOnClickListener(v -> whiteboardView.setColor(Color.RED));
        e_colorBlue.setOnClickListener(v -> whiteboardView.setColor(Color.BLUE));
        e_widthSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean b) { whiteboardView.setStrokeWidth(Math.max(2f, p)); }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });
        e_undoBtn.setOnClickListener(v -> { whiteboardView.undo(); broadcastCommand("UNDO"); });
        e_clearBtn.setOnClickListener(v -> { whiteboardView.clearBoard(); sessionRef.removeValue(); broadcastCommand("CLEAR"); });
        showQrBtn.setOnClickListener(v -> { isQrVisible = !isQrVisible; qrPanel.setVisibility(isQrVisible ? View.VISIBLE : View.GONE); });
        allowChildBtn.setOnClickListener(v -> {
            permissionsRef.child("canChildDraw").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override public void onDataChange(@NonNull DataSnapshot ds) {
                    boolean next = !Boolean.TRUE.equals(ds.getValue(Boolean.class));
                    permissionsRef.child("canChildDraw").setValue(next);
                    allowChildBtn.setText(next ? "Disable Kids" : "Allow Kids");
                }
                @Override public void onCancelled(@NonNull DatabaseError e) {}
            });
        });
        endSessionBtn.setOnClickListener(v -> { sessionRootRef.removeValue(); finish(); });
    }

    private void broadcastCommand(String type) {
        Map<String, Object> cmd = new HashMap<>();
        cmd.put("type", type);
        commandsRef.push().setValue(cmd);
    }

    private void generateQrCode() {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(sessionCode, com.google.zxing.BarcodeFormat.QR_CODE, 400, 400);
            qrImage.setImageBitmap(bitmap);
        } catch (Exception e) { e.printStackTrace(); }
    }
}