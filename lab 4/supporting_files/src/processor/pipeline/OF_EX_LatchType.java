package processor.pipeline;

import generic.Instruction;

public class OF_EX_LatchType {
	
	boolean EX_enable;
	Instruction instruction;
	boolean nop;
	
	public OF_EX_LatchType()              //constructor
	{
		EX_enable = false;
		nop=false;                  //setting nop false
	}

	public boolean isEX_enable() {             //To get state of latch
		return EX_enable;
	}

	public void setEX_enable(boolean ex_enable) {           //To set state of latch
		EX_enable = ex_enable;
	}
	
	public void setInstruction(Instruction instruction){            //To set new instruction
		this.instruction=instruction;
	}
	public Instruction getInstruction(){            //To get the instruction
		return this.instruction;
	}

	public boolean getIsNOP(){
		return  nop;
	}

	public void setIsNOP(boolean set){
		nop=set;
	}
}
