# Lyra

> A Java-based Spotify agent that allows me to interact with Spotify programmatically.

## About

Lyra is an ambitious idea becoming real. The reason behind it starts with my bad experience as a Spotify user, listening on Xbox and controlling on my phone. In order to reduce friction, Lyra will not only allow me to control Spotify more smoothly, but also have its own identity as an actual smart agent.

## Current Version

**v0.2.5 — Devices**

The system recognizes available Spotify Connect devices and manages playback transfers between them without stopping the current playback.

### Current Features

* [x] Spotify authentication
* [x] OAuth callback server
* [x] Token management
* [x] Spotify API client
* [x] Resume playback
* [x] Pause playback
* [x] Skip to next track
* [x] Skip to previous track
* [x] Seek to position
* [x] Set volume
* [x] Set repeat mode
* [x] Toggle shuffle
* [x] Get available devices
* [x] Transfer playback

## Version History

### v0.0.1 — Authentication Start

Spotify OAuth was implemented.

### v0.0.5 — Server Complete

Callback local server was implemented, allowing the full flow of OAuth.

### v0.0.7 — Token Manager Complete

Token Manager class was implemented to give the system autonomy to save and load the right tokens.

### v0.1 — Authentication Complete

Full flow of Auth complete, allowing the system to successfully interact with the user's Spotify account.

### v0.2 — Playback Complete

Playback controls complete.

### v0.3 — Search Class Start

Search class implemented, allowing the system to search for tracks, albums, artists, and playlists.

### v0.4 — Search Classes Split

Search class split into four separate classes to properly divide responsibilities.

### v0.5 — Search Class Lists

Search classes now parse search results into lists, with each result represented as its own object.

### v0.6 — Play Track

Playback class now supports playing a specific track using its Spotify URI obtained from the track search results.


## Roadmap

### v0.7 — Queue

* [ ] View queue
* [ ] Add tracks to queue
* [ ] Remove tracks from queue

### v0.8 — Playlists

* [ ] Create playlists
* [ ] Delete playlists
* [ ] View playlists
* [ ] Add tracks to playlists
* [ ] Remove track from playlists

### Future

* [ ] User interface
* [ ] Device management
* [ ] LLM
* [ ] Voice to text system

## Technologies

* Java
* Maven
* Spotify Web API
* Java 21
* Git/GitHub

## Project Structure

```text
src/
└── main/
    └── java/
        └── lyra/
            ├── App.java
            ├── CallBackServer.java
            ├── ConfigLoader.java
            ├── Device.java
            ├── Playback.java
            ├── SearchAlbum.java
            ├── SearchArtist.java
            ├── SearchPlaylist.java
            ├── SearchTrack.java
            ├── SpotifyAuth.java
            └── TokenManager.java
```

**`SpotifyAuth`** handles Spotify OAuth authorization flow, generates the permission URL, exchange the successfully authorized code for tokens and parsing the tokens response.

**`TokenManager`** gives the system the autonomy to refresh the access token to interact with the account and store it to then load and use again.

**`ConfigLoader`** handles the loading of secrets and sensitive data to the system.

**`Device`** handles Spotify Connect devices and playback transfers between devices.

**`Playback`** provides controls for Spotify playback, including pause, resume, skipping, seeking, volume, repeat mode, and shuffle.

**`SearchAlbum`** searches albums by album or artist name, providing album information and its associated tracks.

**`SearchArtist`** searches artists by name, providing information about the artist, including genres, popularity, followers, and image.

**`SearchPlaylist`** searches public playlists by name, providing playlist information such as owner, description, cover, and track count.

**`SearchTrack`** searches tracks by track or artist name, providing information such as duration, artists, album, cover, URI, and availability.

## Setup

1. Clone the repository.
2. Configure your Spotify credentials.
3. Run the application inside a environment with Maven and the preferred JDK.

### Requirements

* Java 21.0.10
* Maven
* Spotify account
* A Spotify Developer application with a Client ID and Client Secret 
* An internet connection

### Configuration

The following configuration files must exist inside your repository for the system to work:

1. Spotify.properties -> Stores the app credentials.
2. Tokens.properties -> Stores the tokens after the succeeded Spotify OAuth authorization.

## Usage

The main class currently features a straightforward loop to control the playback and available devices.

Example:

```text
Playback Controls:
1. Pause
2. Resume
3. Next
4. Previous
5. Seek to position
6. Repeat mode
7. Volume
8. Shuffle
9. Quit
```
