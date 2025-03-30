# BossBarNotify

A lightweight Minecraft Spigot plugin that displays configurable boss bars with custom messages to all online players.

![BossBarNotify](https://img.shields.io/badge/Minecraft-1.21+-brightgreen)
![Version](https://img.shields.io/badge/Version-1.0-blue)

## Features

- Create multiple boss bar messages with different colors and styles
- Configure how long each message should be displayed
- Randomize the display order of messages
- World whitelist option to only show boss bars in specific worlds
- Multi-language support (English and Spanish)
- Full in-game command support for configuration
- Progress bar that shows the remaining time

## Requirements

- Minecraft Server with Spigot/Paper 1.13 or higher
- Java 8 or higher

## Installation

1. Download the latest release from [here](https://github.com/PasteDev/BossBarNotify/releases)
2. Place the JAR file in your server's `plugins` folder
3. Start/restart your server
4. Configure the plugin via the generated `config.yml` file or in-game commands

## Usage

After installation, the plugin will automatically display the configured boss bars to all online players. The default configuration includes an example boss bar message.

### Commands

- `/bbnotify help` - Displays help information
- `/bbnotify reload` - Reloads the plugin configuration
- `/bbnotify create <id> <color> <style> <time> <message>` - Creates a new boss bar
- `/bbnotify edit <id> <property> <value>` - Edits an existing boss bar
- `/bbnotify delete <id>` - Deletes a boss bar
- `/bbnotify list` - Lists all configured boss bars

### Command Aliases

- `/bbn` - Short alias for `/bbnotify`

### Permissions

- `bossbarnotify.command` - Access to BossBarNotify commands
- `bossbarnotify.reload` - Allows reloading the plugin configuration
- `bossbarnotify.create` - Allows creating new boss bars
- `bossbarnotify.edit` - Allows editing existing boss bars
- `bossbarnotify.delete` - Allows deleting boss bars
- `bossbarnotify.list` - Allows listing all boss bars

## Configuration

### Main Settings

```yaml
# The language for messages (en, es)
Language: en

BossBarMessages:
  # Enable or disable the boss bar messages
  Enabled: true
  
  # If enabled, boss bars will only show in the worlds listed below
  World_Whitelist_Enabled: false
  
  # Show messages in random order
  Random_Order: false
  
  # Hide the progress bar
  No_Progress: false
  
  # List of worlds where boss bars will show (if World_Whitelist_Enabled is true)
  World_Whitelist:
    - world
```

### Message Format

```yaml
Messages:
  UniqueID:
    Message: '&e&l(!) &fYour message with &ecolor &fcodes'
    Color: YELLOW  # BLUE, GREEN, PINK, PURPLE, RED, WHITE, YELLOW
    Style: SOLID   # SOLID, SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20
    Time: 10       # Display time in seconds
```

## Creating Boss Bars

### In-Game

Use the command: `/bbnotify create <id> <color> <style> <time> <message>`

Example:
```
/bbnotify create welcome BLUE SOLID 15 &b&lWelcome! &fThanks for joining the server!
```

### Through Config

Add a new entry to the `BossBarMessages.Messages` section in `config.yml`:

```yaml
Messages:
  welcome:
    Message: '&b&lWelcome! &fThanks for joining the server!'
    Color: BLUE
    Style: SOLID
    Time: 15
```

## Valid Colors

- `BLUE`
- `GREEN`
- `PINK`
- `PURPLE`
- `RED`
- `WHITE`
- `YELLOW`

## Valid Styles

- `SOLID` (or `0` or `6`)
- `SEGMENTED_6` (or `1`)
- `SEGMENTED_10` (or `2`)
- `SEGMENTED_12` (or `3`)
- `SEGMENTED_20` (or `4`)

## Support

If you encounter any issues or have questions, please [create an issue](https://github.com/PasteDev/BossBarNotify/issues) on GitHub.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

- MrPastelitoo_
