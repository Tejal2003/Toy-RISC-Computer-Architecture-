package generic;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import generic.*;
import processor.pipeline.*;
import processor.Clock;
import processor.Processor;

public class Simulator {
		
	static MainMemory newMemory = new MainMemory();
	static RegisterFile register_file = new RegisterFile(); 
	static EventQueue eventQueue = new EventQueue(); 
	static ArrayList<Integer> objInstruction = new ArrayList<Integer>();
	static Processor processor;
	static boolean simulationComplete;
	static FileInputStream input_stream = null;
	static DataInputStream data_input_stream = null;
	static int number_of_instructions = 0; 
	static int number_of_dynamic_instructions = 0; 
	static int number_of_OF_stalls = 0; 
	static int number_of_wrong_branches = 0; 

	public static void setupSimulation(String assemblyProgramFile, Processor p)
	{
		Simulator.processor = p;
		try
		{
			loadProgram(assemblyProgramFile);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		simulationComplete = false;
	}
	
	static void loadProgram(String assemblyProgramFile) throws IOException
	{
		/*
		 * TODO
		 * 1. load the program into memory according to the program layout described
		 *    in the ISA specification
		 * 2. set PC to the address of the first instruction in the main
		 * 3. set the following registers:
		 *     x0 = 0
		 *     x1 = 65535
		 *     x2 = 65535
		 */
		int index = 0;
		int firstCodeAddress=0;
		try
		{
			input_stream = new FileInputStream(assemblyProgramFile);
			data_input_stream = new DataInputStream(input_stream);
			while(data_input_stream.available()>0) 
			{
				int instruction = data_input_stream.readInt();
				if(index==0)
				{
					firstCodeAddress = instruction;
					index++;
					continue;
				}
				else
				{
					if(index>=firstCodeAddress)
					{
						number_of_instructions++;
					}
					newMemory.memory[index-1] = instruction; /* storing instruction in memory */
					index++;
				}
			}
		}
		catch(Exception e)
		{
			Misc.printErrorAndExit("objectProgramFile not read: error\n"+e.toString());
		}

		try
		{
			data_input_stream.close();
			input_stream.close();
		}
		catch (Exception e)
		{
			Misc.printErrorAndExit("Inputstream not closed: error\n"+e.toString());
		}

		register_file.setProgramCounter(firstCodeAddress);

		register_file.setValue(0,0);
		for(int i=3;i<31;i++)
		{
			register_file.setValue(i,0);
		}
		register_file.setValue(1,65535);
		register_file.setValue(2,65535);

		processor.setMainMemory(newMemory);
		processor.setRegisterFile(register_file);
        //System.out.println(processor.getRegisterFile().getProgramCounter());
        //String output = processor.getMainMemory().getContentsAsString(0, 15);
        //System.out.println(output);
	}
			
	public static void simulate()
	{	int x;
		while(simulationComplete == false)
		{	
			eventQueue.processEvents();
			processor.getRWUnit().performRW();
			// Clock.incrementClock();
			processor.getMAUnit().performMA();
			// Clock.incrementClock();
			processor.getEXUnit().performEX();
			// Clock.incrementClock();
			processor.getOFUnit().performOF();
			// Clock.incrementClock();
			processor.getIFUnit().performIF();
			Clock.incrementClock();
			x++;

			// TODO
			// set statistics
			
		Statistics.setNumberOfCycles(Clock.getCurrentTime());
		Statistics.setNumberOfStaticInstructions(number_of_instructions);
		Statistics.setNumberOfInstructions(number_of_dynamic_instructions);
		Statistics.setNumberOfOFStalls(number_of_OF_stalls);
		Statistics.setNumberOfWrongBranches(number_of_wrong_branches);
		}


		//Statistics
		// System.out.print("#cycles = "+Statistics.getNumberOfCycles()+"\n");
		// System.out.print("#wrong branches = "+Statistics.getNumberOfBranchTaken()+"\n");
		// System.out.print("#stops = "+(Statistics.getNumberOfInstructions() - Statistics.getNumberOfRegisterWriteInstructions())+"\n");


	}

	public static void increment_dynamic_instructions()
	{
		number_of_dynamic_instructions++;
	}

	public static void increment_OF_stalls()
	{
		number_of_OF_stalls++;
	}

	public static void increment_wrong_branches()
	{
		number_of_wrong_branches++;
	}
	
	public static void setSimulationComplete(boolean value)
	{
		simulationComplete = value;
	}

	public static EventQueue getEventQueue()
	{ 
		return eventQueue;
	}
}
