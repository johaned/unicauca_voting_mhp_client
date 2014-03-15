package org.university.unicauca.tdi.model;

import org.university.unicauca.tdi.app.VotingCore;

public interface Scene {
	public final int VK_RED=403;
	public final int VK_GREEN=404;
	public final int VK_YELLOW=405;
	public final int VK_BLUE=406;
	public final int VK_LEFT=37;
	public final int VK_RIGHT=39;
	public final int VK_UP=38;
	public final int VK_DOWN=40;
	public final int VK_OK=10;
	
	public abstract void initializer(VotingCore appXlet);
	public abstract void cleaner();
}
