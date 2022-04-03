# Regex Argument Mod

This simple mod adds a regex argument to the Minecraft command system to let users run commands that match a regular expression. This is to be used in combination with the `regex-mod` build of my CommandAPI plugin.

## What this is

The Regex Argument Mod extends the existing Minecraft command system (Brigadier) by adding an additional argument for commands which accepts input that matches a provided regular expression. If a server were to also use this same argument and have additional commands that utilize this, it is possible to create custom commands with custom syntax (adhering to the limitations of regular expressions).

Using my [CommandAPI](https://github.com/JorelAli/CommandAPI) plugin, it's easy to create custom commands with custom arguments!

## Usage examples

- Hex color code argument
  ![Implementing a hex color code argument using a regex argument](./images/colormsg.gif)

  ```java
  new CommandAPICommand("colormsg")
      .withArguments(new RegexArgument("hexcolor", "^#?([a-f0-9]{6})$", "This is not a valid hex color!"))
      .withArguments(new GreedyStringArgument("message"))
      .executesPlayer((sender, args) -> {
          String hexColor = (String) args[0];
          String message = (String) args[1];
          sender.spigot().sendMessage(new ComponentBuilder(message).color(ChatColor.of(hexColor)).create());
      })
      .register();
  ```

- Password strength validation with a minimum of 8 characters, at least 1 uppercase letter, 1 lowercase letter, 1 number and 1 special character (source: [Stackoverflow](https://stackoverflow.com/a/21456918/4779071))
  ![Implementing a client-side password strength checker using a regex argument](./images/password.gif)

  ```java
  String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
  new CommandAPICommand("setpassword")
      .withArguments(new RegexArgument("password", passwordRegex, "This password is not strong enough!"))
      .executes((sender, args) -> {
          String password = (String) args[0];
          // TODO: Do something with password here
          sender.sendMessage("Password set as " + password.chars().map(c -> '*')
              .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
              .toString());
      })
      .register();
  ```

## Setup (with Spigot + CommandAPI)

(Coming soon!)

## License

This project is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
