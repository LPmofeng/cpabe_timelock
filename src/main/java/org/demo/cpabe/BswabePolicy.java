package org.demo.cpabe;

import it.unisa.dia.gas.jpbc.Element;

import java.util.ArrayList;

public class BswabePolicy {
	/* serialized */
	
	/* k=1 if leaf, otherwise threshould */
	public int k;
	/* attribute string if leaf, otherwise null */
	public String attr;
	/**
	 * 表示Cy
	 */
	public Element c;			/* G_1 only for leaves */
	/**
	 * 表示_Cy
 	 */
	public Element cp;		/* G_1 only for leaves */
	/* array of BswabePolicy and length is 0 for leaves */
	public BswabePolicy[] children;
	
	/* only used during encryption */
	public BswabePolynomial q;

	/* only used during decription */
	public boolean satisfiable;
	public int min_leaves;
	public int attri;
	public ArrayList<Integer> satl = new ArrayList<Integer>();
}
