package uk.ncl.giacomobergami.SumoOsmosisBridger.network_generators;

public class HostsAndVMs {
    public int n_hosts_per_edges;
    public int hosts_bandwidth;
    public int hosts_mips;
    public int hosts_pes;
    public int hosts_ram;
    public long hosts_storage;

    public int n_vm;
    public int vm_bw;
    public double vm_mips;
    public int vm_ram;
    public int vm_pes;
    public String vm_cloudletPolicy;
    public long vm_storage;

    public HostsAndVMs copy() {
        HostsAndVMs result = new HostsAndVMs();
        result.n_hosts_per_edges = n_hosts_per_edges;
        result.hosts_bandwidth = hosts_bandwidth;
        result.hosts_mips = hosts_mips;
        result.hosts_pes = hosts_pes;
        result.hosts_ram = hosts_ram;
        result.hosts_storage = hosts_storage;
        result.n_vm = n_vm;
        result.vm_bw = vm_bw;
        result.vm_mips = vm_mips;
        result.vm_ram = vm_ram;
        result.vm_pes = vm_pes;
        result.vm_cloudletPolicy = vm_cloudletPolicy;
        result.vm_storage = vm_storage;
        return result;
    }
    
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


}
