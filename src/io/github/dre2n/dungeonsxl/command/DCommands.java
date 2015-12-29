package io.github.dre2n.dungeonsxl.command;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DCommands {
	
	private CopyOnWriteArrayList<DCommand> dCommands = new CopyOnWriteArrayList<DCommand>();
	
	// Methods
	public DCommands() {
		// Add Commands
		dCommands.add(new HelpCommand());
		dCommands.add(new BreakCommand());
		dCommands.add(new ChatCommand());
		dCommands.add(new ChatSpyCommand());
		dCommands.add(new CreateCommand());
		dCommands.add(new EditCommand());
		dCommands.add(new EscapeCommand());
		dCommands.add(new InviteCommand());
		dCommands.add(new LeaveCommand());
		dCommands.add(new ListCommand());
		dCommands.add(new LivesCommand());
		dCommands.add(new MainCommand());
		dCommands.add(new UninviteCommand());
		dCommands.add(new MsgCommand());
		dCommands.add(new PlayCommand());
		dCommands.add(new PortalCommand());
		dCommands.add(new DeletePortalCommand());
		dCommands.add(new ReloadCommand());
		dCommands.add(new SaveCommand());
		dCommands.add(new TestCommand());
	}
	
	/**
	 * @param command
	 * usually the first command variable
	 */
	public DCommand getDCommand(String command) {
		for (DCommand dCommand : dCommands) {
			if (dCommand.getCommand().equals(command)) {
				return dCommand;
			}
		}
		
		return null;
	}
	
	/**
	 * @return the dCommands
	 */
	public List<DCommand> getDCommands() {
		return dCommands;
	}
	
}
