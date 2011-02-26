package com.epicsagaonline.bukkit.permissions;

import java.io.File;
import java.util.List;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.data.Variables;
import org.anjocaido.groupmanager.dataholder.DataHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.epicsagaonline.bukkit.EnableError;
import com.epicsagaonline.bukkit.NotFoundError;


public class GroupManagerPermissionManager implements PermissionManager {
	GroupManager manager;
	File managerFile;
	long managerFileModDate;
	
	public GroupManagerPermissionManager(Server server)
		throws EnableError {
		PluginManager pm = server.getPluginManager();
		
		manager = (GroupManager)pm.getPlugin("GroupManager");
		if (manager == null) {
			throw new EnableError("GroupManager plugin doesn't " +
					"exist on this server. Please make sure GroupManager " +
					"exists in the plugins directory");
		}
		// make sure GroupManager gets enabled first
		if(!manager.isEnabled())
			server.getPluginManager().enablePlugin(manager);

		managerFile = new File(manager.getDataFolder(), "data.yml");
		managerFileModDate = managerFile.lastModified();
	}
	
	private void refreshManager() {
		if (managerFile.lastModified() <= managerFileModDate)
			return;
		
		manager.reload();
		managerFileModDate = managerFile.lastModified();
	}
	
	public void addUser(String user, List<String> permissions) {
		refreshManager();
		
		DataHolder data = manager.getData();
		User u = data.createUser(user);
		if (u == null)
			return;
		
		manager.commit();
	}

	public String getGroup(String playerName) throws NotFoundError {
		DataHolder data = manager.getData();

		if (!data.isUserDeclared(playerName)) {
			throw new NotFoundError("User not in file: "+playerName);
		}
		
		return data.getUser(playerName).getGroupName();
	}
	
	public VariableContainer getGroupVars(String group) throws NotFoundError {
		DataHolder data = manager.getData();
		
		if (!data.groupExists(group)) {
			throw new NotFoundError("Group not in file: "+group);
		}
		
		return new VariableContainerImpl(data.getGroup(group).getVariables());	
	}

	public VariableContainer getUserVars(String user) throws NotFoundError {
		DataHolder data = manager.getData();
		
		if (!data.isUserDeclared(user)) {
			throw new NotFoundError("User not in file: "+user);
		}
		
		return new VariableContainerImpl(manager.getData().getUser(user).getVariables());
	}

	public boolean has(Player player, String perm) {
		String name = player.getDisplayName().replaceAll("\u00A7.", "");
		
		DataHolder data = manager.getData();
		
		// do extra name checks, in case displayName is different
		if (!data.isUserDeclared(name)) {
			name = player.getName();
			if (!data.isUserDeclared(name)) {
				return false;
			}
		}
		return ((AnjoPermissionsHandler)manager.getPermissionHandler()).
			checkUserPermission(data.getUser(name), perm);
	}

	public boolean hasUser(String userName) {
		return manager.getData().isUserDeclared(userName);
	}

	
	private class VariableContainerImpl implements VariableContainer {
		private Variables vars;
		
		public VariableContainerImpl(Variables vars) {
			this.vars = vars;
		}

		public Boolean getBoolean(String variable) {
			return vars.getVarBoolean(variable);
		}

		public Double getDouble(String variable) {
			return vars.getVarDouble(variable);
		}

		public Integer getInteger(String variable) {
			return vars.getVarInteger(variable);
		}

		public Object getObject(String variable) {
			return vars.getVarObject(variable);
		}

		public String getString(String variable) {
			return vars.getVarString(variable);
		}

		public void set(String variable, Object val) {
			vars.addVar(variable, val);
		}
	}
}
