package org.cloudbus.osmosis.core;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.sdn.SDNHost;
import org.cloudbus.cloudsim.sdn.Switch;
import uk.ncl.giacomobergami.utils.structures.Union4;

public class NetworkNodeType extends Union4<Switch, SDNHost, SDNController, Switch> {

    type t;
    private NetworkNodeType() {super();}


    public static NetworkNodeType gateway(Switch left) {
        NetworkNodeType var = new NetworkNodeType();
        var.val1 = left;
        var.val2 = null;
        var.val3 = null;
        var.val4 = null;
        var.index = 0;
        var.t = type.Gateway;
        return var;
    }

    public static NetworkNodeType host(SDNHost right) {
        NetworkNodeType var = new NetworkNodeType();
        var.val1 = null;
        var.val2 = right;
        var.val3 = null;
        var.val4 = null;
        var.index = 1;
        var.t = type.Host;
        return var;
    }

    public static NetworkNodeType controller(SDNController right) {
        NetworkNodeType var = new NetworkNodeType();
        var.val1 = null;
        var.val3 = right;
        var.val2 = null;
        var.val4 = null;
        var.index = 2;
        var.t = type.Controller;
        return var;
    }

    public static NetworkNodeType switch_(Switch right) {
        NetworkNodeType var = new NetworkNodeType();
        var.val1 = null;
        var.val2 = null;
        var.val3 = null;
        var.val4 = right;
        var.index = 3;
        var.t = type.Switch;
        return var;
    }

    public type getT() {
        return t;
    }

    public enum type {
        Gateway(0),
        Host(1),
        Controller(2),
        Switch(3);

        int id;
        type(int i) {
            this.id = i;
        }
        public int GetID(){return id;}
        public boolean Compare(int i){return id == i;}
        public static type GetValue(int _id)  {
            for (type a : type.values()) {
                if (a.Compare(_id))
                    return a;
            }
            return null;
        }
    }
}
