# Maze Runner (Java / libGDX)

A 2D pixel-art maze runner game built with **Java** and **libGDX**.  
Play through **5 campaign levels**: find the chest containing the key, fight enemies, and unlock doors to reach the next stage. You can also load and play **custom map files**.

<h2 align="center">Gameplay Preview</h2>
<p align="center">
  <img src="media/preview.gif" width="800">
</p>

## Features
- 5-level campaign progression (key → door unlock)
- Enemies + combat
- Custom map loading (play your own levels)
- 2D pixel-art aesthetic
- Windows desktop build available via **Releases**

## Controls
- Movement: Arrow keys
- Attack: A
- Pause/Menu: ESC

## Installation

### Windows

There is no installer required. Download and start playing immediately:

1. Go to the **Releases** section of this repository.
2. Download the latest `.zip` file.
3. Extract the archive. 
4. Run: MazeRunner.exe (**Do not move MazeRunner.exe out of its folder, the game requires the included assets/ directory.**)

Requirements: Windows 10 or newer. No additional installation required (Java runtime is bundled).

> If Windows shows a security warning, click **More Info → Run Anyway**.


### Linux

The Linux version runs via Java (no installer required).

1. Go to the **Releases** section of this repository.
2. Download the latest **MazeRunner-Linux-*.zip** file.
3. Extract the archive.
4. Open a terminal inside the extracted folder.
5. Make the launcher executable:
```bash
chmod +x run.sh
```
6. Start the game (**Do not move MazeRunner.jar out of its folder, the game requires the included assets/ directory.**):
```bash
./run.sh
```

Requirements: Java 17 or newer (Java 21 recommended). Check your Java installation.
```bash
java -version
```



## Tech Stack

- Java
- libGDX


## Run Locally (Development)

### Requirements
- Java (JDK 8+ recommended)

### Run

Windows (PowerShell):
```powershell
.\gradlew :desktop:run
```

Mac/Linux (Bash)
```bash
.\gradlew :desktop:run
```

## License

This project is for educational and demonstration purposes and licensed under the MIT License.


## Author

Felix Reibold (M.Sc.)

- GitHub: https://github.com/felixreibold
- LinkedIn: https://www.linkedin.com/in/felixreibold