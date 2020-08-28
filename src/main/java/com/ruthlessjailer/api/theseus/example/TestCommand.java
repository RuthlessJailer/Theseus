package com.ruthlessjailer.api.theseus.example;

import com.ruthlessjailer.api.theseus.command.CommandBase;
import com.ruthlessjailer.api.theseus.command.SubCommand;
import com.ruthlessjailer.api.theseus.command.SuperiorCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * @author Vadim Hagedorn
 */
public class TestCommand extends CommandBase implements SuperiorCommand {

	public TestCommand() {
		super("test");
	}

	@Override
	protected void runCommand() {
//		this.sender.sendMessage(Chat.colorize(StringUtils.join(this.args, " ")));
		if (this.sender instanceof Player) {
			((Player) this.sender).setVelocity(((Player) this.sender).getVelocity().setY(15));
		}

		this.sender.sendMessage("YEET");
	}

	@SubCommand(inputArgs = "create|new %s<Name> %e", argTypes = Material.class)
	private void create(final String name, final Material test) {
		this.sender.sendMessage(name + " " + test);
	}

	@SubCommand(inputArgs = "create2|new2 %s<1243897AJKSHF__Sasfasf> %e", argTypes = Material.class)
	private void create2(final String name, final Material test) {
		this.sender.sendMessage("#2" + name + " " + test);
	}

	@SubCommand(inputArgs = "delete|remove %b %d<Number> %e", argTypes = Material.class)
	private void delete(final Boolean bool, final Double doub, final Material test) {
		this.sender.sendMessage(bool + " " + doub + " " + test);
	}

	@SubCommand(inputArgs = "delete2|remove2 %b %d<Number> %e", argTypes = Material.class)
	private void delete2(final Boolean bool, final Double doub, final Material test) {
		this.sender.sendMessage("#2" + bool + " " + doub + " " + test);
	}

	@SubCommand(inputArgs = "list", argTypes = {})
	private void list() {
		this.sender.sendMessage("listing");
	}

	@SubCommand(inputArgs = "listX", argTypes = {})
	private void listX() {
		this.sender.sendMessage("listingX");
	}

	@SubCommand(inputArgs = "list1", argTypes = {})
	private void list1() {
		this.sender.sendMessage("listing1");
	}

	@SubCommand(inputArgs = "list2", argTypes = {})
	private void list2() {
		this.sender.sendMessage("listing2");
	}

	@SubCommand(inputArgs = "list3", argTypes = {})
	private void list3() {
		this.sender.sendMessage("listing3");
	}

	@SubCommand(inputArgs = "list4", argTypes = {})
	private void list4() {
		this.sender.sendMessage("listing4");
	}

	@SubCommand(inputArgs = "list5", argTypes = {})
	private void list5() {
		this.sender.sendMessage("listing5");
	}

	@SubCommand(inputArgs = "list6", argTypes = {})
	private void list6() {
		this.sender.sendMessage("listing6");
	}

	@SubCommand(inputArgs = "list7", argTypes = {})
	private void list7() {
		this.sender.sendMessage("listing7");
	}

	@SubCommand(inputArgs = "listZ|listNUL", argTypes = {})
	private void listNUL() {
		this.sender.sendMessage("listing" + this.args[0].substring(5));
	}


}
