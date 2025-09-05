package com.casa.doodleapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.mlkit.common.MlKitException;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.vision.digitalink.*;

public class MainActivity extends AppCompatActivity {

    private DrawingView drawingView;
    private Button btnClear, btnShow, btnUndo, btnRedo;
    private DigitalInkRecognizer recognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize
        drawingView = findViewById(R.id.drawingView);
        btnClear = findViewById(R.id.btnClear);
        btnUndo = findViewById(R.id.btnUndo);
        btnRedo = findViewById(R.id.btnRedo);
        btnShow = findViewById(R.id.btnShow);

        // Initialize
        setupRecognizer();

        // Clear doodle
        btnClear.setOnClickListener(v -> drawingView.clear());

        // Undo/Redo
        btnUndo.setOnClickListener(v -> drawingView.undo());
        btnRedo.setOnClickListener(v -> drawingView.redo());

        // Clear doodle
        btnClear.setOnClickListener(v -> drawingView.clear());

        // Show recognized text
        btnShow.setOnClickListener(v -> recognizeInk());
    }





    // Setup Recognizer
    private void setupRecognizer() {
        DigitalInkRecognitionModelIdentifier modelIdentifier;
        try {
            modelIdentifier = DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US");
        } catch (MlKitException e) {
            Toast.makeText(this, "Model not found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        DigitalInkRecognitionModel model =
                DigitalInkRecognitionModel.builder(modelIdentifier).build();

        recognizer = DigitalInkRecognition.getClient(
                DigitalInkRecognizerOptions.builder(model).build()
        );

        RemoteModelManager.getInstance()
                .download(model, new DownloadConditions.Builder().build())
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Model downloaded", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Model download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Recognize Ink / Text
    private void recognizeInk() {
        if (recognizer == null) {
            Toast.makeText(this, "Recognizer not ready", Toast.LENGTH_SHORT).show();
            return;
        }

        Ink ink = drawingView.getInk();
        recognizer.recognize(ink)
                .addOnSuccessListener(result -> {
                    if (result.getCandidates().isEmpty()) {
                        Toast.makeText(this, "No text recognized", Toast.LENGTH_SHORT).show();
                    } else {
                        String text = result.getCandidates().get(0).getText();
                        Toast.makeText(this, "Recognized: " + text, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Recognition failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}