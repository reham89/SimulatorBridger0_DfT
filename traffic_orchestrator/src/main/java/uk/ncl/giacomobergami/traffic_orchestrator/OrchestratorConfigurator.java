package uk.ncl.giacomobergami.traffic_orchestrator;

public class OrchestratorConfigurator {
        public boolean clairvoyance;
        public boolean use_top_k_nearest_targets_randomOne;
        public boolean use_pareto_front;
        public boolean use_nearest_MEL_to_IoT;
        public boolean use_greedy_algorithm;
        public boolean do_thresholding;
        public boolean use_local_demand_forecast;
        public boolean ignore_cubic;
        public boolean reduce_to_one;
        public boolean update_after_flow;
        String experiment_name;
        String RSUCsvFile;
        String vehicleCSVFile;
        String RSUJsonFile;
        String vehiclejsonFile;
        String output_stats_folder;
        int use_top_k_nearest_targets;
        public double k1;
        public double k2;
        public double p1;
        public double p2;
        public double removal;
        public double addition;
        public double reset_rsu_communication_radius;
        public int reset_max_vehicle_communication;
        public String generateRSUAdjacencyList;
        public String updateRSUFields;

        public String getUpdateRSUFields() {
                return updateRSUFields;
        }

        public void setUpdateRSUFields(String updateRSUFields) {
                this.updateRSUFields = updateRSUFields;
        }

        public String getGenerateRSUAdjacencyList() {
                return generateRSUAdjacencyList;
        }

        public void setGenerateRSUAdjacencyList(String generateRSUAdjacencyList) {
                this.generateRSUAdjacencyList = generateRSUAdjacencyList;
        }

        public int getReset_max_vehicle_communication() {
                return reset_max_vehicle_communication;
        }

        public void setReset_max_vehicle_communication(int reset_max_vehicle_communication) {
                this.reset_max_vehicle_communication = reset_max_vehicle_communication;
        }

        public double getReset_rsu_communication_radius() {
                return reset_rsu_communication_radius;
        }

        public void setReset_rsu_communication_radius(double reset_rsu_communication_radius) {
                this.reset_rsu_communication_radius = reset_rsu_communication_radius;
        }

        public String getExperiment_name() {
                return experiment_name;
        }

        public void setExperiment_name(String experiment_name) {
                this.experiment_name = experiment_name;
        }

        public String getOutput_stats_folder() {
                return output_stats_folder;
        }

        public void setOutput_stats_folder(String output_stats_folder) {
                this.output_stats_folder = output_stats_folder;
        }

        public boolean isClairvoyance() {
                return clairvoyance;
        }

        public void setClairvoyance(boolean clairvoyance) {
                this.clairvoyance = clairvoyance;
        }

        public boolean isUse_top_k_nearest_targets_randomOne() {
                return use_top_k_nearest_targets_randomOne;
        }

        public void setUse_top_k_nearest_targets_randomOne(boolean use_top_k_nearest_targets_randomOne) {
                this.use_top_k_nearest_targets_randomOne = use_top_k_nearest_targets_randomOne;
        }

        public boolean isUse_pareto_front() {
                return use_pareto_front;
        }

        public void setUse_pareto_front(boolean use_pareto_front) {
                this.use_pareto_front = use_pareto_front;
        }

        public boolean isUse_nearest_MEL_to_IoT() {
                return use_nearest_MEL_to_IoT;
        }

        public void setUse_nearest_MEL_to_IoT(boolean use_nearest_MEL_to_IoT) {
                this.use_nearest_MEL_to_IoT = use_nearest_MEL_to_IoT;
        }

        public boolean isUse_greedy_algorithm() {
                return use_greedy_algorithm;
        }

        public void setUse_greedy_algorithm(boolean use_greedy_algorithm) {
                this.use_greedy_algorithm = use_greedy_algorithm;
        }

        public boolean isDo_thresholding() {
                return do_thresholding;
        }

        public void setDo_thresholding(boolean do_thresholding) {
                this.do_thresholding = do_thresholding;
        }

        public boolean isUse_local_demand_forecast() {
                return use_local_demand_forecast;
        }

        public void setUse_local_demand_forecast(boolean use_local_demand_forecast) {
                this.use_local_demand_forecast = use_local_demand_forecast;
        }

        public boolean isIgnore_cubic() {
                return ignore_cubic;
        }

        public void setIgnore_cubic(boolean ignore_cubic) {
                this.ignore_cubic = ignore_cubic;
        }

        public boolean isReduce_to_one() {
                return reduce_to_one;
        }

        public void setReduce_to_one(boolean reduce_to_one) {
                this.reduce_to_one = reduce_to_one;
        }

        public boolean isUpdate_after_flow() {
                return update_after_flow;
        }

        public void setUpdate_after_flow(boolean update_after_flow) {
                this.update_after_flow = update_after_flow;
        }

        public String getRSUCsvFile() {
                return RSUCsvFile;
        }

        public void setRSUCsvFile(String RSUCsvFile) {
                this.RSUCsvFile = RSUCsvFile;
        }

        public String getVehicleCSVFile() {
                return vehicleCSVFile;
        }

        public void setVehicleCSVFile(String vehicleCSVFile) {
                this.vehicleCSVFile = vehicleCSVFile;
        }

        public String getRSUJsonFile() {
                return RSUJsonFile;
        }

        public void setRSUJsonFile(String RSUJsonFile) {
                this.RSUJsonFile = RSUJsonFile;
        }

        public String getVehiclejsonFile() {
                return vehiclejsonFile;
        }

        public void setVehiclejsonFile(String vehiclejsonFile) {
                this.vehiclejsonFile = vehiclejsonFile;
        }

        public int getUse_top_k_nearest_targets() {
                return use_top_k_nearest_targets;
        }

        public void setUse_top_k_nearest_targets(int use_top_k_nearest_targets) {
                this.use_top_k_nearest_targets = use_top_k_nearest_targets;
        }

        public double getK1() {
                return k1;
        }

        public void setK1(double k1) {
                this.k1 = k1;
        }

        public double getK2() {
                return k2;
        }

        public void setK2(double k2) {
                this.k2 = k2;
        }

        public double getP1() {
                return p1;
        }

        public void setP1(double p1) {
                this.p1 = p1;
        }

        public double getP2() {
                return p2;
        }

        public void setP2(double p2) {
                this.p2 = p2;
        }

        public double getRemoval() {
                return removal;
        }

        public void setRemoval(double removal) {
                this.removal = removal;
        }

        public double getAddition() {
                return addition;
        }

        public void setAddition(double addition) {
                this.addition = addition;
        }
}