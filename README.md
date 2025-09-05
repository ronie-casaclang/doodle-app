# DoodleApp
An Android app built in Java that allows users to draw doodles or handwritten text on screen and converts it into recognized text using Google ML Kit Digital Ink Recognition.


### Features
- Draw freely on a canvas (DrawingView).
- Clear the canvas anytime.
- Convert doodles/handwriting into text.
- Recognized text displayed on screen.
- Uses ML Kit Digital Ink Recognition for high accuracy handwriting recognition.


# Setup Guide

### 1: Add ML Kit Dependency
dependencies { <br>
   <tab> implementation 'com.google.mlkit:digital-ink-recognition:18.1.0' <br>
}


### 2. Update MainActivity.java
initRecognizer(); funtion implementation <br>
