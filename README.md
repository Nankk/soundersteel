

# Soundersteel

`soundersteel` is the tool for controlling multiple audio tracks in a single interface.
This is useful e.g. for live streaming or for hosting the tabletop role-playing game with audio effects.

![Soundersteel Logo](https://github.com/Nankk/soundersteel/blob/images/soundersteel-logo.png)


## Features

-   Load multiple audio tracks
-   Support WAV, MP3, and M4A
-   Project saving & loading
-   Region-based looping
-   Playback control with global hotkeys


## Download

Download the latest version from [Releases](https://github.com/Nankk/soundersteel/releases).
Windows and Linux versions are available.


## Launch the app

1.  Extract zip
2.  Move to the extracted directory and
    -   execute `./soundersteel` from the command line on Linux
    -   double click `soundersteel.exe`


## Screenshots


### Basic Usage

![Usage flow](https://github.com/Nankk/soundersteel/blob/images/usage-flow.gif)

Above shows:

-   Add files by DnD from file browser to Files section
-   Add & renaming the scene
-   Create tracks in the scene by DnD the file to tracks area
-   Adjust the volume of each track
-   Set the loop
    -   Start & end point of the region
    -   Loop mode: `Off`, `A-B`, and `Single shot`
        -   **`Off`:** Ignores A/B markers and plays till the end of the track only one time
        -   **`A-B`:** Jumps back to A-marker when the seeking reached to B-marker. When A-marker is not set, the audio is re-played from the beginning
        -   **`Single shot`:** Jumps back to the A marker and pauses track when the seeking reached to B-marker. This is useful for short sound effects like gun shot


### Saving and Loading the Project

![Usage flow](https://github.com/Nankk/soundersteel/blob/images/save-and-load-project.gif)

You can save & load current state to `.ssp` file.


## Global hotkeys

You can control the playback of the tracks using global hotkeys.
Following keys are available with modifier keys `Ctrl + Shift + Super (Win)`.
These functions can be reached even when the app lost it's focus or hide behind other windows.

-   **`Q`, `W`, `E`, `R`, `T`:** Toggle play/pause track #1-5 of current scene
-   **`A`, `S`, `D`, `F`, `G`:** Toggle play/pause track #6-10 of current scene
-   **`Z`:** Move to previous scene
-   **`X`:** Move to next scene

![Global hotkeys](https://github.com/Nankk/soundersteel/blob/images/global-hotkeys.png)
