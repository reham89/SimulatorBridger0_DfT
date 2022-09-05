package org.cloudbus.agent;

public interface Agent {
    String getName();
    void receiveMessage(AgentMessage message);
    void monitor();
    void analyze();
    void plan();
    void execute();
    default void setAgentProgram(Object program) {
        System.out.println("Program set to: " + program.toString());
    }
}
