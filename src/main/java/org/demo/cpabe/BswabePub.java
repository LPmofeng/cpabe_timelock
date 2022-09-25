package org.demo.cpabe;

import it.unisa.dia.gas.jpbc.Element;

public class BswabePub {
    /*
     * A public key
     */
    public String pairingParametersFileName;
    public Element g;                /* G_1 */
    public Element h;                /* G_1 */
    public Element f;                /* G_1 */
    public Element gp;            /* G_2 */
    public Element g_hat_alpha;    /* G_T */

    public BswabePub() {
        this.pairingParametersFileName = "a.properties";
    }
}
