package org.demo.cpabe;

import it.unisa.dia.gas.jpbc.Element;

/**
 * cipher
 */
public class BswabeCph {
	/*
	 * A ciphertext. Note that this library only handles encrypting a single
	 * group element, so if you want to encrypt something bigger, you will have
	 * to use that group element as a symmetric key for hybrid encryption (which
	 * you do yourself).
	 */
	/**
	 * C~
	 */
	public Element cs; /* G_T */
	/**
	 * C
	 */
	public Element c; /* G_1 */
	public BswabePolicy p;
}
