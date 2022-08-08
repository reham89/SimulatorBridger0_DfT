package uk.ncl.giacomobergami.SumoOsmosisBridger.network_generators;

public class HostsAndVMs {
    public int n_hosts_per_edges;
    public int hosts_bandwidth;
    public int hosts_mips;
    public int hosts_pes;
    public int hosts_ram;
    public int hosts_storage;

    public int n_vm;
    public int vm_bw;
    public double vm_mips;
    public int vm_ram;
    public int vm_pes;
    public String vm_cloudletPolicy;
    public int vm_storage;
    
    void validate() {
        if (vm_bw > hosts_bandwidth)  {
            vm_bw = hosts_bandwidth;
            System.err.println("ERROR: the VM' bandwidth should be always less or equal than the hosts'. Making it the same...");
        }
        if (vm_pes > hosts_pes) {
            vm_pes = hosts_pes;
            System.err.println("ERROR: the VM' pes should be always less or equal than the hosts'");
        }
        if (vm_ram > hosts_ram) {
            vm_ram = hosts_ram;
            System.err.println("ERROR: the VM' ram should be always less or equal than the hosts'");
        }
        if (vm_storage > hosts_storage) {
            vm_storage = hosts_storage;
            System.err.println("ERROR: the VM' storage should be always less or equal than the hosts'");
        }
        if (vm_mips > hosts_mips) {
            vm_mips = hosts_mips;
            System.err.println("ERROR: the VM' mips should be always less or equal than the hosts'");
        }
    }

    public int getN_hosts_per_edges() {
        return n_hosts_per_edges;
    }

    public void setN_hosts_per_edges(int n_hosts_per_edges) {
        this.n_hosts_per_edges = n_hosts_per_edges;
    }

    public int getHosts_bandwidth() {
        return hosts_bandwidth;
    }

    public void setHosts_bandwidth(int hosts_bandwidth) {
        this.hosts_bandwidth = hosts_bandwidth;
    }

    public int getHosts_mips() {
        return hosts_mips;
    }

    public void setHosts_mips(int hosts_mips) {
        this.hosts_mips = hosts_mips;
    }

    public int getHosts_pes() {
        return hosts_pes;
    }

    public void setHosts_pes(int hosts_pes) {
        this.hosts_pes = hosts_pes;
    }

    public int getHosts_ram() {
        return hosts_ram;
    }

    public void setHosts_ram(int hosts_ram) {
        this.hosts_ram = hosts_ram;
    }

    public int getHosts_storage() {
        return hosts_storage;
    }

    public void setHosts_storage(int hosts_storage) {
        this.hosts_storage = hosts_storage;
    }

    public int getN_vm() {
        return n_vm;
    }

    public void setN_vm(int n_vm) {
        this.n_vm = n_vm;
    }

    public int getVm_bw() {
        return vm_bw;
    }

    public void setVm_bw(int vm_bw) {
        this.vm_bw = vm_bw;
    }

    public double getVm_mips() {
        return vm_mips;
    }

    public void setVm_mips(double vm_mips) {
        this.vm_mips = vm_mips;
    }

    public int getVm_ram() {
        return vm_ram;
    }

    public void setVm_ram(int vm_ram) {
        this.vm_ram = vm_ram;
    }

    public int getVm_pes() {
        return vm_pes;
    }

    public void setVm_pes(int vm_pes) {
        this.vm_pes = vm_pes;
    }

    public String getVm_cloudletPolicy() {
        return vm_cloudletPolicy;
    }

    public void setVm_cloudletPolicy(String vm_cloudletPolicy) {
        this.vm_cloudletPolicy = vm_cloudletPolicy;
    }

    public int getVm_storage() {
        return vm_storage;
    }

    public void setVm_storage(int vm_storage) {
        this.vm_storage = vm_storage;
    }
}
