/************************************************************************
 * This file is part of AdminCmd.									
 *																		
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by	
 * the Free Software Foundation, either version 3 of the License, or		
 * (at your option) any later version.									
 *																		
 * AdminCmd is distributed in the hope that it will be useful,	
 * but WITHOUT ANY WARRANTY; without even the implied warranty of		
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the			
 * GNU General Public License for more details.							
 *																		
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd.  If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/
package be.Balor.Manager;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.Balor.bukkit.AdminCmd.ACHelper;


/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class CommandManager implements CommandExecutor {
	private HashMap<Command, ACCommands> commands = new HashMap<Command, ACCommands>();
	private static CommandManager instance = null;

	/**
	 * @return the instance
	 */
	public static CommandManager getInstance() {
		if (instance == null)
			instance = new CommandManager();
		return instance;
	}

	/**
	 * Register command
	 * 
	 * @param clazz
	 */
	public void registerCommand(Class<?> clazz) {
		try {
			ACCommands command = (ACCommands) clazz.newInstance();
			command.initializeCommand();
			command.registerBukkitPerm();
			command.getPluginCommand().setExecutor(this);
			commands.put(command.getPluginCommand(), command);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (CommandException e) {
			Logger.getLogger("Minecraft").info("[AdminCmd] " + e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender
	 * , org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {

			ACCommands cmd = null;
			if (commands.containsKey(command)
					&& (cmd = commands.get(command)).permissionCheck(sender) && cmd.argsCheck(args)) {
				ACHelper.getInstance().setSender(sender);
				cmd.execute(sender, args);
				return true;
			} else
				return false;
		} catch (Throwable t) {
			Logger.getLogger("Minecraft")
					.severe("The command "
							+ command.getName()
							+ " throw an Exception please report the log to this thread : http://forums.bukkit.org/threads/admn-gen-admincmd-5-5-6-time-give-tp-repair-heal-kill-warp-weather-multi-lingual-1000.10770");
			t.printStackTrace();
			return false;
		}
	}
}
