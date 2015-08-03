package com.dre.dungeonsxl.commands;

import java.util.concurrent.CopyOnWriteArrayList;

public class DCommandRoot {
	// Variables
	public static DCommandRoot root;

	public CopyOnWriteArrayList<DCommand> commands = new CopyOnWriteArrayList<DCommand>();

	// Commands
	public CMDCreate cmdCreate = new CMDCreate();
	public CMDSave cmdSave = new CMDSave();
	public CMDLeave cmdLeave = new CMDLeave();
	public CMDEscape cmdEscape = new CMDEscape();
	public CMDEdit cmdEdit = new CMDEdit();
	public CMDPortal cmdPortal = new CMDPortal();
	public CMDDeletePortal cmdDeletePortal = new CMDDeletePortal();
	public CMDChat cmdChat = new CMDChat();
	public CMDChatSpy cmdChatSpy = new CMDChatSpy();
	public CMDLives cmdLives = new CMDLives();
	public CMDList cmdList = new CMDList();
	public CMDUninvite cmdUninvite = new CMDUninvite();
	public CMDInvite cmdInvite = new CMDInvite();
	public CMDMsg cmdMsg = new CMDMsg();
	public CMDPlay cmdPlay = new CMDPlay();
	public CMDTest cmdTest = new CMDTest();
	public CMDHelp cmdHelp = new CMDHelp();
	public CMDReload cmdReload = new CMDReload();

	// Methods
	public DCommandRoot() {
		root = this;

		// Add Commands
		this.commands.add(cmdCreate);
		this.commands.add(cmdSave);
		this.commands.add(cmdLeave);
		this.commands.add(cmdEscape);
		this.commands.add(cmdEdit);
		this.commands.add(cmdPortal);
		this.commands.add(cmdDeletePortal);
		this.commands.add(cmdChat);
		this.commands.add(cmdChatSpy);
		this.commands.add(cmdLives);
		this.commands.add(cmdList);
		this.commands.add(cmdUninvite);
		this.commands.add(cmdInvite);
		this.commands.add(cmdMsg);
		this.commands.add(cmdPlay);
		this.commands.add(cmdTest);
		this.commands.add(cmdHelp);
		this.commands.add(cmdReload);
	}
}
