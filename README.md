## Dynamic IOTSimOsmosisRES

  1. ```SimulatorBridger-core```: shared dependencies among the projects.
  2. ```SimulatorBridger-traffic_information_collector```: this phase runs the traffic simulator and collects the data from it. This also digests the output of the simulation, by identifying which are the IoT nodes and which are the Edge nodes through which the former are going to interact with.
  3. ```SimulatorBridger-central_agent_planner```: this provides the theoretical omniscent algorithm, that can schedule the time as required. This, depending on the network simulator of choice, also generates potential network connectivity information based on the IoT and Edge information provided by the simulation.
  4. ```SumoOsmosisBridger```: an example bridging all of the simulations together with **Dynamic IoTSimOsmosisRES**