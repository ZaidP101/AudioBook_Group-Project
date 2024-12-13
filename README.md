# AudioBook Project

## Project Overview
**AudioBook** is an interactive digital audiobook player application. It offers users a Spotify-inspired interface to listen to audiobooks chapter by chapter while viewing accompanying images. This project is designed to demonstrate the practical application of data structures and algorithmic concepts learned during Semester 1 of the MCA program.
Note : Audio files not added, add audio files in .wav formate
![Img1](/images/img1.jpeg)
![Img2](/images/img2.jpeg)

### Developed By
- **Hemant Sharma**
- **Raiyan Farooqui**
- **Zaid Patel**


---

## Features
- Spotify-inspired UI for an intuitive experience.
- Audiobook playlist with a dynamic, scrollable list of chapters.
- Image display corresponding to each chapter.
- Play, pause, resume, next, and previous functionalities.
- Seek functionality with real-time tracking of current and total playback times.

---

## Data Structures Used
1. **ArrayList**  
   - **Purpose**: To store the list of audio files and image files.
   - **Usage**: Efficient retrieval and traversal of audio/image file paths.

2. **DefaultListModel**  
   - **Purpose**: To dynamically manage and render the playlist of audiobook chapters.
   - **Usage**: Updates the playlist displayed in the UI.

---

## Time Complexity of Functions

### Core Functions
| **Function**                     | **Description**                                      | **Time Complexity**          |
|-----------------------------------|------------------------------------------------------|-------------------------------|
| `loadFiles(String folderPath, String extension)` | Loads and filters files from a directory.           | **O(n)** (n = number of files in directory) |
| `playAudio(String filePath)`     | Plays the selected audio file.                       | **O(1)** (loading of audio is constant)      |
| `pauseAudio()`                   | Pauses the currently playing audio.                  | **O(1)**                                |
| `resumeAudio()`                  | Resumes a paused audio.                              | **O(1)**                                |
| `stopAudio()`                    | Stops the current audio and resets the state.        | **O(1)**                                |
| `moveToNextChapter()`            | Moves to the next chapter in the playlist.           | **O(1)**                                |
| `moveToPreviousChapter()`        | Moves to the previous chapter in the playlist.       | **O(1)**                                |
| `updateAudioAndImage()`          | Updates the UI with new chapter details.             | **O(1)**                                |

### Auxiliary Functions
| **Function**                     | **Description**                                      | **Time Complexity**          |
|-----------------------------------|------------------------------------------------------|-------------------------------|
| `getAudioName()`                 | Retrieves the name of the current audio file.        | **O(1)**                                |
| `updateImage()`                  | Updates the displayed image for the current chapter. | **O(1)**                                |
| `resetSeekBar()`                 | Resets the seek bar and labels to initial state.      | **O(1)**                                |
| `startTimer()`                   | Starts a timer for updating playback progress.       | **O(1)**                                |

---

## Technologies Used
- **Programming Language**: Java
- **Libraries**:
  - `javax.sound.sampled` for audio playback.
  - `javax.swing` for the graphical user interface (GUI).
  - `java.util` for managing collections and file operations.

---

## How to Run the Application
1. Clone the repository or download the source code.
2. Ensure the Java Development Kit (JDK) is installed (version 8 or higher).
3. Place your audiobook `.wav` files and images in the appropriate directories:
   - **Audio files**: `C:\\Users\\Sunil\\eclipse-workspace\\Audiobook\\audio`
   - **Image files**: `C:\\Users\\Sunil\\eclipse-workspace\\Audiobook\\images`
4. Compile and run the `AudioBookPlayer` class.

---

## Future Enhancements
- Add support for additional audio formats like `.mp3`.
- Implement bookmark functionality to save playback positions.
- Enhance UI responsiveness and scalability.
- Integrate cloud storage for audio and image files.

---

## License
This project is licensed under the MIT License. Feel free to use, modify, and distribute this project.

---

**Thank you for exploring AudioBook! Happy listening! 🎧**
