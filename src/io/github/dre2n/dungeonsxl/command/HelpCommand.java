package io.github.dre2n.dungeonsxl.command;

import java.util.ArrayList;
import java.util.List;

import io.github.dre2n.dungeonsxl.file.DMessages.Messages;
import io.github.dre2n.dungeonsxl.util.IntegerUtil;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.command.CommandSender;

public class HelpCommand extends DCommand {
	
	public HelpCommand() {
		setCommand("help");
		setMinArgs(0);
		setMaxArgs(1);
		setHelp(dMessages.getMessage(Messages.HELP_CMD_HELP));
		setPermission("dxl.help");
		setPlayerCommand(true);
		setConsoleCommand(true);
	}
	
	@Override
	public void onExecute(String[] args, CommandSender sender) {
		List<DCommand> dCommandList = plugin.getDCommands().getDCommands();
		ArrayList<DCommand> toSend = new ArrayList<DCommand>();
		
		int page = 1;
		if (args.length == 2) {
			page = IntegerUtil.parseInt(args[1], 1);
		}
		int send = 0;
		int max = 0;
		int min = 0;
		for (DCommand dCommand : dCommandList) {
			send++;
			if (send >= page * 5 - 4 && send <= page * 5) {
				min = page * 5 - 4;
				max = page * 5;
				toSend.add(dCommand);
			}
		}
		
		MessageUtil.sendPluginTag(sender, plugin);
		MessageUtil.sendCenteredMessage(sender, "&4&l[ &6" + min + "-" + max + " &4/&6 " + send + " &4|&6 " + page + " &4&l]");
		
		for (DCommand dCommand : toSend) {
			MessageUtil.sendMessage(sender, "&b" + dCommand.getCommand() + "&7 - " + dCommand.getHelp());
		}
	}
	
}
