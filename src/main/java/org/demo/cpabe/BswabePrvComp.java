package org.demo.cpabe;

import it.unisa.dia.gas.jpbc.Element;

public class BswabePrvComp {
	/* these actually get serialized */
	public String attr;
	public Element d;					/* G_2 */
	public Element dp;				/* G_2 */
	
	/* only used during dec */
	public int used;
	public Element z;					/* G_1 */
	public Element zp;				/* G_1 */
}
