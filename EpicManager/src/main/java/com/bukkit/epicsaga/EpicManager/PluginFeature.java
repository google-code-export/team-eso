package com.bukkit.epicsaga.EpicManager;

public interface PluginFeature {
	/**
	 *
	 * @param em
	 * @throws EpicManager.EnableError on fatal initialization error
	 */
	void onEnable(EpicManager em) throws EpicManager.EnableError;
	void onDisable(EpicManager em);
}
