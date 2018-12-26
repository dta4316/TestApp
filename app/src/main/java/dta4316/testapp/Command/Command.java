package dta4316.testapp.Command;

import java.util.HashMap;
import java.util.Map;

public class Command {
    public interface ICommand {
        public void apply();
    }

    public static final class CommandFactory {
        private final Map<String, ICommand> commands;

        private CommandFactory() {
            commands = new HashMap<>();
        }

        public void AddCommand(final String name, final ICommand command) {
            commands.put(name, command);
        }

        public void ExecuteCommand(String name) {
            if (commands.containsKey(name)) {
                commands.get(name).apply();
            }
        }

        public void ExecuteAllCommand() {
            for (ICommand cmd:commands.values()) {
                cmd.apply();
            }
        }

        public static CommandFactory init() {
            final CommandFactory cf;
            cf = new CommandFactory();

            return cf;
        }
    }
}
