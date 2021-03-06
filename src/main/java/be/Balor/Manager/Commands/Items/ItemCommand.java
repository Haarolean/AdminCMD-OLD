/**
 * **********************************************************************
 * This file is part of AdminCmd.
 *
 * AdminCmd is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * AdminCmd is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 * **********************************************************************
 */
package be.Balor.Manager.Commands.Items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import be.Balor.Manager.Commands.CoreCommand;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public abstract class ItemCommand extends CoreCommand {

        /**
         *
         */
        public ItemCommand() {
                super();
                this.permParent = plugin.getPermissionLinker().getPermParent("admincmd.item.*");
        }

        /**
         * @param string
         * @param string2
         */
        public ItemCommand(final String cmd, final String permission) {
                this();
                this.cmdName = cmd;
                this.permNode = permission;

        }

        /**
         * Check if the material can be repaired
         *
         * @param mat
         * @return
         */
        protected static boolean isReparable(final Material mat) {
                return mat.getMaxDurability() > 0;
        }

        /**
         * Check if the item stack can be repaired
         *
         * @param stack
         * @return
         */
        protected static boolean isReparable(final ItemStack stack) {
                return isReparable(stack.getType()) && stack.getType().getMaxDurability() > stack.getDurability();
        }

}
